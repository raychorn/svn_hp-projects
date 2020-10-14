'''
Created on Oct 25, 2011

@author: IslamM
'''
import web
import httplib2
import json
from portlets.collector import Collector 
from portlets import stowwn
from copy import deepcopy
from util import config
from util import auth
import string
from engines import vc_engine 
from engines.csc_engine import csc_engine
from datetime import datetime

from logging import getLogger
log = getLogger(__name__)

class cva:
    @staticmethod
    def summary_test(dcollector=None):
        '''
        {vDisk:'vDisk1', Accelarated:true, OperationalStatus:true, MIP:'35%',SSD:'SSD1'}, 
        {vDisk:'vDisk2', Accelarated:false, OperationalStatus:false, MIP:'20%',SSD:'SSD2'}]; 
        '''
        import uuid
        make_vm = lambda vmname,vdiskname,accelarated,operationalStatus,mip:{'config':{'hardware.device':[{'__class__':{'__name__':'VirtualDisk'},'backing':{'datastore':{'value':111}},'deviceInfo.label':'label1'}]},'name':vmname,'vdisk':vdiskname,'accelarated':accelarated,'operationalStatus':operationalStatus,'mip':mip,'uuid':str(uuid.uuid4()),'guest':{'guestOperationsReady':True,'guestFullName':'fullname1','ipAddress':'127.0.0.1'}}
        vdisks = {}
        vdisk_list = []
        datacollector = {'ds':[{'value':111,'name':'name1','info.vmfs.extent':[{'diskName':'disk1'}]}],'host_detail':{'config':{'storageDevice':{'scsiLun':[{'canonicalName':'disk1','displayName':'name1','ssd':True}]}}}}
        for this_vm in [make_vm('vm1','vDisk1',True,True,0.25),make_vm('vm2','vDisk2',False,True,0.15),make_vm('vm3','vDisk3',False,False,0.45)] :
            for device in this_vm['config']['hardware.device'] :
                vdisk = {}
                if device['__class__']['__name__'] == 'VirtualDisk' :
                    for this_ds in datacollector['ds'] :
                        if this_ds['value'] == device['backing']['datastore']['value'] :
                            vdisk['datastore'] = this_ds['name']
                            vdisk['logical'] = this_ds['info.vmfs.extent'][0]['diskName']
                            for lun in datacollector['host_detail']['config']['storageDevice']['scsiLun'] :
                                if vdisk['logical'] == lun['canonicalName'] :
                                    vdisk['logical_name'] = lun['displayName']
                                    if lun['ssd'] == True :
                                        vdisk['ssd'] = "SSD"
                                    else :
                                        vdisk['ssd'] = "non-SSD"
                                    break
                            break
                    vdisk['uuid'] = this_vm['uuid']
                    vdisk['vm'] = this_vm['name']
                    vdisk['vdisk'] = this_vm['vdisk']
                    vdisk['accelarated'] = this_vm['accelarated']
                    vdisk['operationalStatus'] = this_vm['operationalStatus']
                    vdisk['mip'] = this_vm['mip']
                    vdisk['label'] = device['deviceInfo.label']
                    if (this_vm['guest']['guestOperationsReady'] == True) :
                        vdisk['os'] = this_vm['guest']['guestFullName']
                        vdisk['ip'] = this_vm['guest']['ipAddress']
                    vdisk_list.append(vdisk)
        vdisks['vdisks'] = vdisk_list
        return vdisks
    
    @staticmethod
    def summary(dcollector=None):
        print "IN CVA"
        #h = httplib2.Http()
        #resp, content = h.request("http://10.10.10.101:9001")
        #content_to_dict = json.loads(content)
        #print "%s" % content_to_dict['data']
        datacollector = Collector.get_collector()
        hostname = datacollector.host_detail.name
        #config_manager = datacollector.hds
        #print (str(config_manager))
        vdisks = {}
        vdisk_list = []
        for this_vm in datacollector.vms :
            #print (str(this_vm.guest))
            for device in this_vm.config.hardware.device :
                vdisk = {}
                #print (str(device))
                if device.__class__.__name__ == 'VirtualDisk' :
                    for this_ds in datacollector.ds :
                        #print(str(this_ds.info))
                        if this_ds.value == device.backing.datastore.value :
                            vdisk['datastore'] = this_ds.name
                            vdisk['logical'] = this_ds.info.vmfs.extent[0].diskName
                            for lun in datacollector.host_detail.config.storageDevice.scsiLun :
                                if vdisk['logical'] == lun.canonicalName :
                                    vdisk['logical_name'] = lun.displayName
                                    if lun.ssd == True :
                                        vdisk['ssd'] = "SSD"
                                    else :
                                        vdisk['ssd'] = "non-SSD"
                                    break
                            break
                    vdisk['vm'] = this_vm.name
                    vdisk['label'] = device.deviceInfo.label
                    if (this_vm.guest.guestOperationsReady == True) :
                        vdisk['os'] = this_vm.guest.guestFullName
                        vdisk['ip'] = this_vm.guest.ipAddress
                    vdisk_list.append(vdisk)
        vdisks['vdisks'] = vdisk_list
        print (str(vdisks))
            
        return vdisks
    
    #Used to get detailed information about vdisks.
    @staticmethod
    def getsummary(dcollector=None):
        print "GET SUMMARY"
        datacollector = Collector.get_collector()
        hostname = datacollector.host_detail.name
        driver = False                    
        vdisks = {}
        vdisk_list = []
           
        for this_vm in datacollector.vms :
            #print (str(this_vm.name))            
            for device in this_vm.config.hardware.device :
                vdisk = {}
                #print (str(device))
                
                if device.__class__.__name__ == 'VirtualDisk' :
                    for this_ds in datacollector.ds :
                        #print(str(this_ds.name))                        
                        if this_ds.value == device.backing.datastore.value :
                            vdisk['datastore'] = this_ds.name
                            vdisk['datastore_capacity'] = this_ds.info.vmfs.capacity
                            vdisk['logical'] = this_ds.info.vmfs.extent[0].diskName
                            vdisk['id'] = this_ds.info.vmfs.uuid
                            for lun in datacollector.host_detail.config.storageDevice.scsiLun :
                                if vdisk['logical'] == lun.canonicalName :
                                    vdisk['logical_name'] = lun.displayName
                                    #print(str(lun.displayName))                                    
                                    if lun.ssd == True :
                                        vdisk['ssd'] = "SSD"
                                    else :
                                        vdisk['ssd'] = "non-SSD"                                    
                                    break
                            break
                    #driver status
                    vdisk['driver_status'] = False
                    '''
                    if driver :
                        vdisk['driver_status'] = True
                        print "Driver is installed."
                    else:
                        print "Driver is not installed."
                    '''
                    
                    vdisk['accel_status'] = "Disabled"
                    
                    '''
                    if (this_vm.config.flags.disableAcceleration == True) :
                        print "vDisk acceleration is disabled."
                    else :
                        print "vDisk acceleration is enabled."
                    '''
                    
                    vdisk['op_status'] = "Disabled"
                    vdisk['accel_on_ssd_name'] = "Disabled"                    
                    vdisk['vm'] = this_vm.name
                    vdisk['label'] = device.deviceInfo.label
                    
                    '''
                    if (this_vm.guest.guestOperationsReady == True) :
                        vdisk['os'] = this_vm.guest.guestFullName
                        vdisk['ip'] = this_vm.guest.ipAddress
                    '''
                    
                    vdisk_list.append(vdisk)
                    
        vdisks['vdisks'] = vdisk_list        
        print (str(vdisks))
        
        return vdisks
    
    #Used to get acceleration information for the vDisks to populate a vDisk Update form. 
    @staticmethod
    def getvdisk(dcollector=None):
        print "GET VDISK ACCELERATION INFO."
        vdisks = {}
        vdisk_info = []
        vdisk = {}
        
        #Detailed info about vdisks
        #Current cache capacity in case they want to change it.
        vdisk['cache_capacity'] = "100 Mb"
        #CVA state which they could change.
        vdisk['cva_state'] = "data..."
        #Potential cache datastores in case they want to change the cache for the specified vdisk.
        vdisk['cache_datastores'] = "datastore 1..."
        #Max size for the current vDisk (limited by remaining datastore space).
        vdisk['vdisk_max_size'] = "100 Mb"
        
        vdisk_info.append(vdisk)
        vdisks['vdisks'] = vdisk_info
        print (str(vdisks))
        
        return vdisks
    
    #Used to update acceleration information for the vDisks.
    @staticmethod
    def putvdisk(dcollector=None):
        print "UPDATE VDISK ACCELERATION INFO."
        vdisks = {}
        vdisk_info = []
        vdisk = {}
        
        #Detailed info about vdisks
        #Change current cache capacity.
        vdisk['cache_capacity'] = "New Value"
        #Change CVA state.
        vdisk['cva_state'] = "Change State"
        #Change cache datastores.
        vdisk['cache_datastores'] = "Update Datastores"
        #Change max size for the current vDisk (limited by remaining datastore space).
        vdisk['vdisk_max_size'] = "Update Max Size"
        vdisks['completion_status'] = "Done!"
        
        vdisk_info.append(vdisk)
        vdisks['vdisks'] = vdisk_info        
        print (str(vdisks))       
        
        return vdisks
    
    #Used to install or upgrade VM driver.
    @staticmethod
    def postdriver(dcollector=None):
        print "INSTALL OR UPGRADE VM DRIVER"
        drivers = {}
        driver_info = []
        
        driver = {}
        #Post Driver
        driver['driver'] = "Post Driver Here"
        #completion status
        driver['completion_status'] = False
        
        driver_info.append(driver)
        drivers['drivers'] = driver_info
        print (str(drivers))       
        
        return drivers
