'''
Created on Oct 5, 2013

@author: Sandeep Kotha
'''

import web
from portlets.collector import Collector 
from portlets import stowwn
from portlets.host import Host
from copy import deepcopy
from util import config
from util import auth
from util import credential
from util.credential import credkey
from util.authclient import get_authclient
from datetime import datetime
from engines import hponeview
import time
import base64
import traceback
from util.stringcrypt import str_decrypt
from logging import getLogger
from requests import HTTPError
import json
from  util.scheduletasks import *
import engines.vc_engine as vc_engine
from vmware.vcenter import vCenter,ManagedObjectRef
from threading import Timer
from operator import itemgetter

from urlparse import urlparse
from core.uim import UIManager

from util import threadpool

def getBaseline(hostname,vcguid):
    import util.catalog as catalog
    '''
    getBaseline functon will check if host is managed by HP oneview.
    If host is oneview manged then with host_uuid we get the data from database.
    If there is a job already scheduled for that host return the job details.
    If a job is being applied get request will be sucessfull but cannot schedule a job
    '''    
    
    try:
        dc = Collector.get_collector()
        log.debug("the value of getCollector form collector is : %s ", dc)
        host_uuid = dc.host.hardware.systemInfo.uuid
        log.debug("Host UUID  from host hardware systeminfo uuid is = %s", host_uuid)
    except:
        log.exception("There was an error while trying to get host uuid from collector") 
        try:
            vc = [vc for vc in catalog.get_all() if isinstance(vc, vCenter) and vc.sc.about.instanceUuid.lower() == vcguid]
            vc= vc[0]
            log.debug("The vc value is : %s",vc)
            host_uuid = None
            for x in vc.retreive_host_list():
                if x.obj.value == hostname:
                    for prop in x.propSet:
                        log.debug("props == %s, attribs = %s", prop, dir(prop))
                        if hasattr(prop, 'name') and \
                           prop.name == "hardware.systemInfo.uuid" and \
                           hasattr(prop, 'val'):
                            log.debug("Moref success")
                            host_uuid = prop.val
                            log.debug("Host UUID  from vcenter is = %s", host_uuid)
                            break
        except:
            log.exception('error iterating over vc list')
            raise        
    returnData = {}
    memberData = []
    tempuri = None
    taskinfo_id = None
    
    returnData['firmwareBaseLineModels'] = None
    #dc = Collector.get_collector()
    #host_uuid = dc.host.hardware.systemInfo.uuid
    log.debug("Host UUID = %s", host_uuid)
    ovClient = Host.findOVClient(host_uuid)
    
   
    if ovClient:
        log.debug("server profile for server UUID %s = %s", host_uuid, ovClient.getServerProfilesbyUuids([host_uuid]))
        serverProfile = ovClient.getServerProfilesbyUuids([host_uuid])[host_uuid]
        
        returnData['isHostFusionManaged'] = True
        returnData['message'] = None
        
        
        #A check has to be made  if firmwarebaseliuri exists for that particluar uuid
        if serverProfile['firmware'].has_key('firmwareBaselineUri'):
            if serverProfile['firmware']['firmwareBaselineUri'] != '':
                returnData['currentFirmwareBaseLine'] = ovClient.getovBaseline(serverProfile['firmware']['firmwareBaselineUri'])['baselineShortName'] 
            else:
                returnData['currentFirmwareBaseLine'] = None
                returnData['message'] = "Hosts firmware is manually managed"
                return returnData

        baselineObj = ovClient.getAllBaselines()
        
        
        if returnData.has_key('firmwareBaseLineModels'):
            for obj in baselineObj:
                #if obj['baselineShortName'] != None and obj['uri'] != serverProfile['firmware']['firmwareBaselineUri']:
                if obj['baselineShortName'] != None:
                    temp={}
                    temp['baseLineSPPName'] = obj['baselineShortName']
                    temp['baselineDescription'] = obj['description']
                    temp['baseLineURI'] =  obj['uri']
                    temp['baseLineName'] = obj['name']
                    memberData.append(temp)
        
        returnData['firmwareBaseLineModels'] = memberData
        log.debug("gatherring data not complete yet : %s ", returnData)
        try: 
            ret =get_task_schedule_from_db('firmware_jobs', host_uuid = host_uuid)
            log.debug("data returned from db is : %s", ret)
            if(len(ret) == 1):
                for r in ret:
                    if r['status'] == 'Applying Baseline' or r['status'] == 'scheduled':
                        returnData['baseLineChangeStatus'] = r['status']
                        returnData['putHostInMaintenanceMode'] = r['enter_maintenance_mode']
                        returnData['powerOnHostOnceBaseLineIsApplied'] = r['restart']
                        returnData['exitMaintenanceModeOnceBaseLineIsApplied'] = r['exit_maintenance_mode']
                        returnData['dateAndTimeToApplyBaseLine'] = r['scheduledtime'].strftime('%a %b %d %H:%M:%S %Y')
                        tempuri = r['baseline_uri']
                        taskinfo_id = r['task_info_id']
                        
                    else:
                        returnData['baseLineChangeStatus']="Not Scheduled"
                        returnData['putHostInMaintenanceMode']=False
                        returnData['powerOnHostOnceBaseLineIsApplied']=False
                        returnData['exitMaintenanceModeOnceBaseLineIsApplied']=False
                        returnData['dateAndTimeToApplyBaseLine']=None
                        returnData['currentlySelectedBaseLine'] = None
            
            else:
                returnData['baseLineChangeStatus']="Not Scheduled"
                returnData['putHostInMaintenanceMode']=False
                returnData['powerOnHostOnceBaseLineIsApplied']=False
                returnData['exitMaintenanceModeOnceBaseLineIsApplied']=False
                returnData['dateAndTimeToApplyBaseLine']=None
                returnData['currentlySelectedBaseLine'] = None
            
            if(tempuri != None):
                for tempObj in baselineObj:
                    if tempuri == tempObj['uri']:
                        returnData['currentlySelectedBaseLine'] = tempObj['baselineShortName']
                        break

            
            log.debug("FINAL RESPONSE FOR GET Request is : %s", returnData)
            return returnData
        except:
            log.exception("unable to communicate with DB")
    else :
        log.debug('getBaseline is returning None')
        return None
    

def setMaintenanceMode(vc, hostMoref, onoff):
    '''
    :param - onoff: boolean value. If set to True, the host is put into
    maintenance mode. If False, it is made to come out of maintenance mode.
    :return - a Task object representing the enter/exit maintenance mode 
    task
    '''
    assert(vc)
    if onoff:
        return vc.vim.EnterMaintenanceMode_Task(hostMoref, 0)
    else:
        return vc.vim.ExitMaintenanceMode_Task(hostMoref, 0)


def addScheduledTask(inputdata,vcguid):
    '''
    This function is called when a POST call is amde to schedule an firmware update job for fusion host.
    @param inputdata : The data passed from ui to schedulethe job.
    @param vcgui : serverGuid 
    
    If no job is scheduled for the host then we adda new task to DB.
    If a job is already scheduled for the host hen we remove the existing job and add a new one.
    we create and post ic4vc tasks,news feeds and vcenter tasks.
    In case of exception we post error to ic4vc tasks,news feeds and vcenter tasks.
    '''
    try:
        update = False
        log.debug("The input data in add scheduled task is : %s ",inputdata)
        returnData={}
        dc = Collector.get_collector()
        
        uuid=dc.host.hardware.systemInfo.uuid

        task_name = 'Firmware Baseline'
        taskid = None
        start_time = time.time()
        user = dc.vcntr.get_current_session_username(dc.sessionId)
                
        objpath = 'moref=' + dc.moref + '&' + 'serverGuid=' + dc.serverGuid
        log.debug("the objPath is : %s",objpath)  
        uim_url = urlparse(config.config().get_uim_root())        
        uimgr = UIManager(port=uim_url.port, protocol=uim_url.scheme )
        
        result = get_task_schedule_from_db("firmware_jobs",host_uuid=uuid)
        
        if(result):
            if result[0]['status'] == 'scheduled':
                cancel_pending_task(result[0]['id'],"firmware_jobs")
                taskid = str(result[0]['task_info_id'])
                update = True

            elif result[0]['status'] == 'Applying Baseline':
                returnData['message'] = "failure"
                returnData['errorMessage'] = "Scheduled Baseline is in progress.  Cannot update the baseline."
                returnData['baseLineChangeStatus']="Applying Baseline"
                return returnData
        
        now_flag = inputdata['nowFlag']
        date_str = inputdata['dateAndTimeToApplyBaseLine']
        dt_obj = datetime.strptime(date_str, "%m %d %Y %H:%M:%S")
         
        task_date_str = inputdata['scheduledTimeForTasks']
        if update != True:
            desc = "Firmware baseline update task created"
            taskid_ret_list = uimgr.create_task(objpath, task_name, [], start_time, "QUEUED", user, desc,  [])
            taskid = str(taskid_ret_list['ids'][0])
            log.debug("the task id for tasks is : %s",taskid)
            #desc = 'firmware baseline update is scheduled at '+task_date_str
            #uimgr.post_event(objpath, desc, 'User Action', 'INFORMATION', time.time())
            
        else:
            desc = "A scheduled firmware baseline job has been updated to " +task_date_str
            
            uimgr.update_task(objpath,taskid, task_name, [],start_time, time.time(), "QUEUED", user, desc, [])        
            
            uimgr.post_event(objpath, desc, 'User Action', 'INFORMATION', time.time())
        
        addTaskResult = add_new_task("firmware_jobs", dt_obj, now_flag, host_uuid=uuid,
                baseline_uri = inputdata['firmwareBaseLineModels'][0]['baseLineURI'],
                restart=inputdata['powerOnHostOnceBaseLineIsApplied'],
                enter_maintenance_mode=inputdata['putHostInMaintenanceMode'],
                exit_maintenance_mode=inputdata['exitMaintenanceModeOnceBaseLineIsApplied'],
                vcguid = vcguid,
                task_info_id = int(taskid)
                )
        log.debug("addtaskresult is : %s",addTaskResult)
        if(addTaskResult):
            #Added to DB and returned an unique identifier for the task
            returnData['message'] = "success"
            returnData['baseLineChangeStatus']="Job Scheduled"
            desc = 'Firmware baseline update is scheduled at '+task_date_str
            uimgr.update_task(objpath,taskid, task_name, [],start_time, time.time(), "QUEUED", user, desc, [])
            uimgr.post_event(objpath, desc, 'User Action', 'INFORMATION', time.time())
            log.debug("return message from addScheduleTasks is : success")
            
            
        else :
            returnData['message'] = "failure"
            returnData['errorMessage'] = " Unable to scheduled a job in the past.Please synchronize time"
            returnData['baseLineChangeStatus']="Not scheduled"
            log.debug("return message from addscheduleTasks is : failure")
            desc = " Unable to scheduled a job in the past.Please synchronize time."
            uimgr.update_task(objpath,taskid, task_name, [],start_time, time.time(), 'ERROR', user, desc, [])
            #some problem and data not added to DB
    except:
        if update != True:
            returnData['message'] = "failure"
            returnData['errorMessage'] = "Unable to schedule a new task"
            desc = "Unable to schedule a new task"
            returnData['baseLineChangeStatus']="Scheduled"
        else:
            returnData['message'] = "failure"
            returnData['errorMessage'] = "Deleted scheduled task and unable to schedule a new task"
            desc = "Deleted Scheduled task and unable to schedule a new task"
            returnData['baseLineChangeStatus']="Not Scheduled"

        uimgr.update_task(objpath,taskid, task_name, [],start_time, time.time(), 'ERROR', user, desc, [])
        log.exception("Unable to schedule a new task")
        
    log.debug("RETURN DATA IS  : %s",returnData)
    return returnData
    

def delScheduledTask(inputdata):
    '''
    This function cancels a  scheduled job.
    @param inputdata : tasks parameters passed to delete job frmo db 
    we updat and post ic4vc tasks,news feeds and vcenter tasks.
    In case of exception we update and post error to ic4vc tasks,news feeds and vcenter tasks.
    '''
    try:
        returnData={}
        dc = Collector.get_collector()
        
        uuid=dc.host.hardware.systemInfo.uuid
        
        task_name = 'Firmware Baseline'
        taskid = None
        start_time = time.time()
        user = dc.vcntr.get_current_session_username(dc.sessionId)
                
        objpath = 'moref=' + dc.moref + '&' + 'serverGuid=' + dc.serverGuid
        log.debug("moref value is  : %s",dc.moref) 
        log.debug("object path is : %s",objpath)
        uim_url = urlparse(config.config().get_uim_root())        
        uimgr = UIManager(port=uim_url.port, protocol=uim_url.scheme )
        
        result = get_task_schedule_from_db("firmware_jobs",host_uuid=uuid)
        
        if(result):
            if result[0]['status'] == 'Applying Baseline':
                returnData['message'] = "failure"
                returnData['baseLineChangeStatus'] = 'Applying Baseline'
                returnData['errorMessage'] = "Scheduled Baseline is in progress. Cannot update the baseline."
                log.debug("return message is : Job not deleted from database")
                return returnData
                
            elif result[0]['status'] == 'scheduled':
                taskid = str(result[0]['task_info_id'])
                jobDeleted = cancel_pending_task(result[0]['id'],"firmware_jobs")
                if jobDeleted:
                    returnData['message'] = "success"
                    returnData['baseLineChangeStatus']="Not scheduled"
                    log.debug("return message is : success")
                    desc = "Firmware Baseline Update job Deleted"
                    uimgr.update_task(objpath,taskid, task_name, [],start_time, time.time(), "CANCELLED", user, desc, [])
                    uimgr.post_event(objpath, desc, 'User Action', 'INFORMATION', time.time())
                    
                else:
                    returnData['message'] = "failure"
                    returnData['errorMessage'] = "Unable to delete job"
                    returnData['baseLineChangeStatus']="Job Scheduled"
                    log.debug("return message from deletescheduleTasks is : Job not deleted from database")
        else:
            returnData['message'] = "failure"
            returnData['errorMessage'] = "There are no scheduled jobs for this host"
            returnData['baseLineChangeStatus']="Not scheduled"
            log.debug("return message from deletescheduleTasks is : failure")
    except:
         returnData['message'] = "failure"
         returnData['errorMessage'] = "There are no scheduled jobs for this host"
         returnData['baseLineChangeStatus']="Not scheduled"
         log.exception("unable to delete the scheduled job")
        
    return returnData
    
    

def _getHostMoref(vc, uuid):
    '''
    Returns the actual Managed Object Reference for this host
    (the kind that vCenter can understand)
    '''
    for x in vc.retreive_host_list():
        for prop in x.propSet:
            log.debug("props == %s, attribs = %s", prop, dir(prop))
            if hasattr(prop, 'name') and prop.name == "hardware.systemInfo.uuid" and hasattr(prop, 'val') and prop.val == uuid:
                log.debug("Moref success")
                return x.obj
    log.error("Could not create moref for this host")



def applyBaseline(identifier,vcguid, host_uuid, baseline_uri, restart, enter_maintenance_mode, exit_maintenance_mode , task_info_id): 
    '''
    Any values returned from this method will need to be handled via a Queue for best results.
    '''
    import util.catalog as catalog
    log.debug("Parameters fro applying a baseline uri : (host_uuid = %s, baseline_uri = %s, restart = %s, enter_maintenance_mode = %s, exit_maintenance_mode = %s,task_info_id=%s)" %(host_uuid, baseline_uri, restart, enter_maintenance_mode, exit_maintenance_mode,task_info_id))
    
    '''
    1)Check if the host is managed by oneview client based on hostuuid.
    2)Get the server Profile based on host uuid
    3)Enter host maintainence mode
    4)power off server
    5)update the server profile with approprate uri
    6)make a rest call to update the firmware baseline.
    7)poll to know when applying baseline is finished 
      polling fr every 30 seconds to know the percentComplete of updation
      If process does not complete in 40 minutes polling stops updating status as interuptted
    8)power on server
    9)if required exit maintainence mode.
    '''
    try:
        vc = [vc for vc in catalog.get_all() if isinstance(vc, vCenter) and vc.sc.about.instanceUuid.lower() == vcguid.lower()]    
    except:
        log.exception('error iterating over vc list')
        raise
    #hostMoref = hostFirmwareBaseline._getHostMoref(vc = vc[0], uuid = host_uuid)
    try:
        hostMoref = _getHostMoref(vc = vc[0], uuid = host_uuid)
        log.debug("hostMoref value is : %s",hostMoref)
               
        ovClient = Host.findOVClient(host_uuid)
        taskId = identifier
        task_name = 'Firmware Baseline'
        
        
        host_task_state = ['Unknown','Suspended','Terminated','Killed','Error','Warning','Interrupted']
        
        start_time = time.time()
        log.debug("starttime of the job is : %s",start_time)
        user1  = vc[0].username
        
        user  = vc[0].decode(user1) 
        log.debug("the username on vcenter is : %s",user)
        moref = str(hostMoref._type)+":"+str(hostMoref.value)
        log.debug("moref value after concatenation is : %s ",moref)
        objpath = 'moref=' + moref + '&' + 'serverGuid=' + vcguid.upper()
        uim_url = urlparse(config.config().get_uim_root())
        uimgr = UIManager(port=uim_url.port, protocol=uim_url.scheme )
        
        taskid=str(task_info_id)
        desc = "Firmware update job is in progress" 
        uimgr.update_task(objpath,taskid, task_name, [],start_time, None, "RUNNING", user, desc, [])
        uimgr.post_event(objpath, desc, 'User Action', 'INFORMATION', time.time())
        vcenterTask = vc[0].createFirmwareUpdateTask(hostMoref,"root")
        if ovClient:
            #managed by HPonevew so proceed to next step
            serverProfileData = ovClient.getServerProfilesbyUuids([host_uuid])[host_uuid]
            taskComplete = False
        
            log.debug("the task identifier is : %s",type(identifier))
            update_task_status(identifier,"Applying Baseline")
            
            vc[0].updateFirmwareUpdateTask(vcenterTask,'running',0)
            desc = "Applying Firmware Baseline task started" 
            uimgr.update_task(objpath,taskid, task_name, [],start_time, None,'RUNNING', user, desc, [])
            
            if enter_maintenance_mode :
                try:
                    retval = setMaintenanceMode(vc[0], hostMoref, True)
                    log.debug("enter maintenance mode retrn val is  : %s",retval)
                    moderesult = monitorMaintenanceModeTasks(vc[0],retval,host_uuid)
                    if moderesult == 'Success':
                        desc = "Entering maintenance mode" 
                        uimgr.update_task(objpath,taskid, task_name, [],start_time, None, 'RUNNING', user, desc, [])
                        vc[0].updateFirmwareUpdateTask(vcenterTask,'running',5)            
                        log.debug("host has entered maintainence mode")
                    else :
                        update_task_status(identifier,"interrupted")
                        vc[0].updateFirmwareUpdateTask(vcenterTask,'error',0)
                        desc = 'unable to set host in maintenance mode'
                        uimgr.update_task(objpath,taskid, task_name, [],start_time, time.time(), 'ERROR', user, desc, [])
                        uimgr.post_event(objpath, desc, 'User Action', 'error', time.time())
                        log.exception('unable to set host in maintenance mode')
                        return
                except:
                    update_task_status(identifier,"interrupted")
                    vc[0].updateFirmwareUpdateTask(vcenterTask,'error',0)
                    desc = "Error occured while entering maintenance mode" 
                    uimgr.update_task(objpath,taskid, task_name, [],start_time, time.time(), 'ERROR', user, desc, [])
                    desc = "Firmware baseline update was interupted"
                    uimgr.post_event(objpath, desc, 'User Action', 'error', time.time())
                    log.exception('unable to set host in maintenance mode')
                    return
                    
            
            time.sleep(5)
            
            try:
                log.debug("power off host ")
                desc = "Power off Host" 
                uimgr.update_task(objpath,taskid, task_name, [],start_time, None, 'RUNNING', user, desc, [])
                vc[0].updateFirmwareUpdateTask(vcenterTask,'running',15)
                ovClient.setServerPowerState( host_uuid, "Off", "PressAndHold", refresh = True)
            except:
                update_task_status(identifier,"interrupted")
                vc[0].updateFirmwareUpdateTask(vcenterTask,'error',0)
                desc = "Error occured while power off host operation" 
                uimgr.update_task(objpath,taskid, task_name, [],start_time, time.time(), 'ERROR', user, desc, [])
                desc = "Firmware baseline update was interupted"
                uimgr.post_event(objpath, desc, 'User Action', 'error', time.time())
                log.exception('unable to power off the host')
                return

                
            time.sleep(30)
            log.debug("host has been powered off")
            
            try:
                if serverProfileData['firmware'].has_key('firmwareBaselineUri'):
                    serverProfileData['firmware']['firmwareBaselineUri'] = baseline_uri
                
                retVal = ovClient.updateServerProfile(serverProfileData)
                log.debug("the task status of update server profile is  : %s", retVal)
                
                desc = "Updating firmware baseline"
                uimgr.update_task(objpath,taskid, task_name, [],start_time, None, 'RUNNING', user, desc, [])
                
                time.sleep(20)
                
                taskStatus = ovClient.getTaskStatusFromUri(retVal['uri'])
                log.debug("result from getTaskstatusfrmouri is : %s",json.dumps(taskStatus))
                if taskStatus['percentComplete'] != 100:
                    taskComplete = False
                else:
                    taskComplete = True
                
                counter = 0
                log.debug("taskcomplete status is : %s",taskComplete)
                vc[0].updateFirmwareUpdateTask(vcenterTask,'running',25)
                while taskComplete == False:
                    time.sleep(30)
                    log.debug("the log counter value for applying baseline is : %d",counter)
                    taskProgress = ovClient.getTaskStatusFromUri(retVal['uri'])
                    if taskProgress['percentComplete'] == 100 and taskProgress['taskState'] == 'Completed':
                        taskComplete = True
                    else:
                        taskComplete = False
                        log.debug("Applying baseline task completion Percentage : %s",taskProgress['percentComplete'])
                        log.debug("Applying baseline task status is : %s",taskProgress['taskState'])

                        if taskProgress['taskState'] in host_task_state:
                            break
                        counter += 1

                        
                log.debug("After polling loop the task status of applying baseline is : %s",taskComplete)
                if taskComplete:
                    restartDone = False
                    try:
                         
                        hostServerhardawre = ovClient.getServersbyUuids([host_uuid])[host_uuid]
                        powerStateValue = hostServerhardawre['powerState']
                        #powerStateValue = ovClient.getServerPowerState(host_uuid)
                        log.debug("current power state of host is : %s",powerStateValue)
                        if powerStateValue == 'Off':
                            if restart :
                                try:
                                    restart_task_status = ovClient.setServerPowerState( host_uuid, "On", "MomentaryPress", refresh = True)
                                    while True:
                                        taskProgress = ovClient.getTaskStatusFromUri(restart_task_status['uri'])
                                        if taskProgress['percentComplete'] == 100 and taskProgress['taskState'] == 'Completed':
                                            log.debug("restarting server  : %s",taskProgress['percentComplete'])
                                            powerStateValue = 'On'
                                            break
                                        else:
                                            log.debug("restarting server  : %s",taskProgress['percentComplete'])
                                            if taskProgress['taskState'] in host_task_state:
                                                log.debug("restarting server  interuppted")
                                                break
                                            time.sleep(30)
                                            
                                    if powerStateValue == 'On':
                                        log.debug("host is powered on now")
                                        uimgr.update_task(objpath,taskid, task_name, [],start_time,None, 'RUNNING', user, desc, [])
                                        vc[0].updateFirmwareUpdateTask(vcenterTask,'running',80)
                                    else:
                                        update_task_status(identifier,"interrupted")
                                        vc[0].updateFirmwareUpdateTask(vcenterTask,'error',0)
                                        desc = "Firmware baseline update was interupted. Unable to power on the host"
                                        uimgr.post_event(objpath, desc, 'User Action', 'error', time.time())
                                        uimgr.update_task(objpath,taskid, task_name, [],start_time, time.time(), 'ERROR', user, desc, [])
                                        log.exception('unable to power on the host')
                                except:
                                    update_task_status(identifier,"interrupted")
                                    vc[0].updateFirmwareUpdateTask(vcenterTask,'error',0)
                                    desc = "Firmware baseline update was interupted. Unable to power on the host"
                                    uimgr.post_event(objpath, desc, 'User Action', 'error', time.time())
                                    uimgr.update_task(objpath,taskid, task_name, [],start_time, time.time(), 'ERROR', user, desc, [])
                                    log.exception('unable to power on the host')
                                    return
                                    
                        if exit_maintenance_mode :
                            try:
                                counter  = 0 
                                host_powerState = None
                                
                                mob = ManagedObjectRef(hostMoref._type, hostMoref.value)
                                while True:
                                    connectionInfo = vc[0].vim.QueryHostConnectionInfo(mob)
                                    
                                    host_runtime_info = connectionInfo.host.runtime
                                    
                                    if host_runtime_info.powerState  != 'poweredOn' and counter < 31:
                                        counter += 1
                                        log.debug(" The power state is : %s",host_runtime_info.powerState)
                                        log.debug("counter value for polling of power state is : %s",counter) 
                                        time.sleep(30)
                                    elif host_runtime_info.powerState  == 'poweredOn' :
                                        log.debug(" The power state is : %s",host_runtime_info.powerState)
                                        host_powerState = 'poweredOn'
                                        break
                                    elif counter > 30 :
                                        log.debug("The power state is : %s",host_runtime_info.powerState)
                                        log.debug("The to restart server has exceeded 15 minutes") 
                                        break
                                
                                time.sleep(30)
                                 
                                if host_powerState == 'poweredOn':
                                    hostConnectionInfo = vc[0].vim.QueryHostConnectionInfo(mob)
                                    
                                    inMaintenanceMode = hostConnectionInfo.host.runtime.inMaintenanceMode
                                    
                                    if inMaintenanceMode:
                                     
                                        mode_task_id = setMaintenanceMode(vc[0], hostMoref, False)
                                        
                                        mode_result = monitorMaintenanceModeTasks(vc[0],mode_task_id,host_uuid)
                                        if mode_result == 'Success':
                                            uimgr.update_task(objpath,taskid, task_name, [],start_time, None, 'RUNNING', user, desc, [])
                                            vc[0].updateFirmwareUpdateTask(vcenterTask,'running',90)
                                            log.debug("host has exited maintainence mode") 
                                        else:
                                            update_task_status(identifier,"interrupted")
                                            vc[0].updateFirmwareUpdateTask(vcenterTask,'error',0)
                                            desc = "Updating firmware Baseline task interrupted. Host unable to exit maintenance mode."
                                            uimgr.post_event(objpath, desc, 'User Action', 'error', time.time())
                                            uimgr.update_task(objpath,taskid, task_name, [],start_time, time.time(), 'ERROR', user, desc, [])
                                            log.exception('unable to get host out of maintenance mode')
                                            return
                                            
                                    else:
                                        uimgr.update_task(objpath,taskid, task_name, [],start_time, None, 'RUNNING', user, desc, [])
                                        vc[0].updateFirmwareUpdateTask(vcenterTask,'running',90)
                                        log.debug("host has exited maintainence mode") 
                                    
                                else:
                                    log.exception("unable to get powerstate of host in vcenter")
                                    raise
                                
                            except:
                                update_task_status(identifier,"interrupted")
                                vc[0].updateFirmwareUpdateTask(vcenterTask,'error',0)
                                desc = "Updating firmware Baseline task interrupted. Host unable to exit maintenance mode."
                                uimgr.post_event(objpath, desc, 'User Action', 'error', time.time())
                                uimgr.update_task(objpath,taskid, task_name, [],start_time, time.time(), 'ERROR', user, desc, [])
                                log.exception('Unable to get host out of maintenance mode')
                                return
                        
                        update_task_status(taskId,"Sucessfully Updated firmware Baseline")
                        vc[0].updateFirmwareUpdateTask(vcenterTask,'success',100)
                        desc = "Firmware baseline updated sucessfully."
                        uimgr.update_task(objpath,taskid, task_name, [],start_time, time.time(), 'COMPLETED', user, desc, [])
                        uimgr.post_event(objpath, desc, 'User Action', 'info', time.time())
                        
                    except:
                        log.debug("unable to get the power state of host from fusion")
                        update_task_status(taskId,"Interuptted")
                        vc[0].updateFirmwareUpdateTask(vcenterTask,'error',0)
                        desc = "Firmware baseline update was interupted. Unable to get the power state of host."
                        uimgr.post_event(objpath, desc, 'User Action', 'error', time.time())
                        uimgr.update_task(objpath,taskid, task_name, [],start_time, time.time(), 'ERROR', user, desc, [])
                        log.exception("unable to get the power state of host from fusion")
                    
                else:
                    update_task_status(identifier,"interupted")
                    desc = "Firmware baseline update was interupted"
                    uimgr.post_event(objpath, desc, 'User Action', 'error', time.time())
                    vc[0].updateFirmwareUpdateTask(vcenterTask,'error',0)
                    uimgr.update_task(objpath,taskid, task_name, [],start_time, time.time(), 'ERROR', user, desc, [])
            except:
                update_task_status(identifier,"interrupted")
                desc = "Firmware baseline update was interupted."
                uimgr.post_event(objpath, desc, 'User Action', 'error', time.time())
                vc[0].updateFirmwareUpdateTask(vcenterTask,'error',0)
                uimgr.update_task(objpath,taskid, task_name, [],start_time, time.time(), 'ERROR', user, desc, [])
                log.exception('unable to update firmware baseline')
                return
        else:
            #pass
            #not managed by HP oneview
            log.exception('Host is not managed by HP oneview, cannot apply firmware baseline')
            raise 
        
    except:
        update_task_status(identifier,"interrupted")
        vc[0].updateFirmwareUpdateTask(vcenterTask,'error',0)
        desc = "Error occured while updating firmware baseline" 
        uimgr.update_task(objpath,taskid, task_name, [],start_time, time.time(), 'ERROR', user, desc, [])
        desc = "Firmware baseline update was interupted"
        uimgr.post_event(objpath, desc, 'User Action', 'error', time.time())
        log.exception('unable to start firmware baseline update')
        return
    
# Task states: error, queued, running, success

def monitorMaintenanceModeTasks(vc,tasks, uuid):
    taskObj = None
    result = None    
    add_tasks = [tasks['value']]
    log.debug("the add_tasks value is : %s",add_tasks)
    counter = 0
    while True:
        counter += 1        
        vc_tasks = []
        # Create a new collector each time around.
        collector = vc.client.service.CreateCollectorForTasks(vc.sc.taskManager, None)
        while True:
            # Read tasks in 100 page blocks.vSphere API filtering doesn't work, so we have to just get everything.
            next_tasks = vc.client.service.ReadNextTasks(collector, 100)
            #log.debug("the next_tasks value is : %s",next_tasks)
            if not next_tasks:
                break;
            vc_tasks = vc_tasks + next_tasks

        #log.debug(" the vc_tasks value after while loop is : %s",vc_tasks)
        # Get only tasks we care about.
        vc_tasks = [task for task in vc_tasks if task.key in add_tasks]
        log.debug("The vc_tasks value is : %s",vc_tasks)
        has_running = False
        for task in vc_tasks:
            log.debug( task )
            taskObj = task
            if task.state == 'running' or task.state == 'queued':
                result = 'In progress'
                has_running = True
            elif task.state == 'error':
                result = task.error.localizedMessage
            elif task.state == 'success':
                result = 'Success'
            else:
                result = 'Unknown'

        if not has_running or counter > 10:
            break
        else:
            time.sleep(30)

    if taskObj.state == 'running':
        vc.vim.CancelTask(taskObj.task)
    
    log.debug("The return result is : %s",result)
    return result

    
