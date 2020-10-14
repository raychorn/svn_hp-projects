'''
Created on Oct 18, 2011

@author: IslamM
'''
from threading import Thread
import web, os
from web.wsgiserver import CherryPyWSGIServer
from suds.sudsobject import Object as SudsObject

from logging import getLogger
log = getLogger(__name__)

from util import catalog
from util.config import config

import json

from portlets.host import Host
from portlets.cva import cva
from portlets.network import Network
from portlets import cluster
from engines.deployment_connector import ALDeploymentConnector
from vmware.vcenter import vCenter
from time import sleep
from engines.firmware import HostFirmware
from util.smartcomponent import get_scm
from portlets.cluster import ClusterSwFwDetail
from portlets.collector import Collector, ClusterCollector
from urlparse import urlparse
from urllib import unquote
from core.uim import UIManager
import portlets.collector
import time

urls = [
    '/debug/(.*)', 'wdebug',
    '/debug/', 'wdebug',
    '/services/clusterswfwdetail', 'ClusterSwFwDetail',
    '/services/', 'wservices',
    '/services/(.*)', 'wservices',     
    '/deployment/', 'deployment',
    '/firmware/', 'firmware',
    '/firmware', 'firmware',
    '/firmware/jobs', 'firmware_jobs',
    '/firmware/jobs/', 'firmware_jobs',
    '/smart_components', 'smart_components',
    '/settings/(.+)', 'Settings',
    '/uidcontrol', 'UIDControl',
    '/cva/summary(/|)', 'CVASummary',
    '/cva/summary/(.+)(/|)', 'CVASummary',
]


service_dispatcher = {'hostsummary': Host.summary,
                      'hostdetail': Host.detail,
                      'hoststatus': Host.hoststatus,
                      'networksummary': Network.summary,
                      'networkdetail': Network.detail,
                      'hostswfwsummary': Host.swfwsummary,
                      'hostswfwdetail': Host.swfwdetail,
                      'hostinfrasummary': Host.infrasummary,
                      'hostinfradetail': Host.infradetail,
                      'hostlaunchtools': Host.launchtools,
                      'hostsimlaunchinfo': Host.simlaunchinfo,
                      'hostcommsummary': Host.commsummary,
                      'clustersummary': cluster.summary,
                      'clusterdetail': cluster.detail,
                      'clusterinfrasummary': cluster.infrasummary,
                      'clusterinfradetail': cluster.infradetail,
                      'clusterswfwsummary': cluster.swfwsummary,   
                      'clusterlaunchtools': cluster.launchtools,
                      'clusterstatus': cluster.clusterstatus,
                      'cvasummary': cva.summary,
                      'cvadetail': cva.summary,
                      'getsummary': cva.getsummary,
                      'getdetail': cva.getsummary,
                      'vdisksummary': cva.getvdisk,
                      'vdiskdetail': cva.getvdisk,
                      'vdisksummary2': cva.putvdisk,
                      'vdiskdetail2': cva.putvdisk,
                      'driversummary': cva.postdriver,
                      'driverdetail': cva.postdriver,
                       }

class SudsEncoder(json.JSONEncoder):
    '''
    A JSON Encoder that understands suds object types.
    '''
    def default(self, obj):
        if isinstance(obj, SudsObject):
            d = dict(__classname__=obj.__class__.__name__)
            for k in obj.__keylist__:
                d[k] = obj.__dict__[k]
            return d
        return json.JSONEncoder.default(self, obj)

class CVASummary:
    def GET(self, item=None):
        from portlets import cva

        s = web.session

        try:
            data = s.cva_summary_data
        except AttributeError:
            data = s.cva_summary_data = cva.cva.summary_test()

        return json.dumps(data)

    def DELETE(self, uuid, item=None):
        from portlets import cva

        s = web.session

        try:
            __data__ = s.cva_summary_data
        except AttributeError:
            __data__ = {}
            
        uuid = uuid.split('/')[0]
        for name,items in __data__.iteritems():
            i = 0
            for item in items:
                if (item.has_key('uuid') and (str(item['uuid']) == str(uuid))):
                    del items[i]
                i += 1
        return json.dumps(__data__)
    
    def POST(self, item=None):
        from portlets import cva

        s = web.session

        try:
            __data__ = s.cva_summary_data
        except AttributeError:
            __data__ = {}
            
        data = web.input()
        if (len(str(data['uuid'])) > 0):
            for name,items in __data__.iteritems():
                for item in items:
                    if (item.has_key('uuid') and (str(item['uuid']) == str(data['uuid']))):
                        for k,v in data.iteritems():
                            if (k != 'uuid'):
                                item[k] = v
        else:
            import uuid
            item = {'uuid':str(uuid.uuid4())}
            for k,v in data.iteritems():
                if (k != 'uuid'):
                    item[k] = v
            __data__['vdisks'].append(item)
            s.cva_summary_data = __data__
        return json.dumps(__data__)
    
class wservices:
    def GET(self, item=None):
        #session.any_auth()
        log.debug('GET wservices %s', item)
        if item not in service_dispatcher :
            raise web.notfound()
                
        data = service_dispatcher[item]()        
        
        return json.dumps(data, cls=SudsEncoder)        

class UIDControl:
    def PUT(self):
        log.debug('uidcontrol')
        dc = Collector.get_collector()
        cstate = dc.ilo.get('data', {}).get('get_uid_status', {}).get('uid', 'off')
        log.debug('Current UID state: %s', cstate)
        setstate = 'off' 
        if cstate.lower() == 'off':
            setstate = 'on'
        log.debug('Setting UID state to: %s', setstate)
        dc.csce.entity_control('ilo', dc.ilo['uuid'], 'uid', setstate)
        taskQ = catalog.lookup('ic4vc-server-threadpool').get_q()
        taskQ.put((dc.csce.force_update_entity, ('ilo', dc.ilo['uuid'])))
        taskQ.put((dc.delayed_update, (30, [True, False])))

        uim_url = urlparse(config().get_uim_root())        
        uimgr = UIManager(port=uim_url.port, protocol=uim_url.scheme )
        desc = 'UID State Toggled from ' + cstate + ' to ' + setstate
        objpath = 'moref=' + dc.moref + '&' + 'serverGuid=' + dc.serverGuid
        uimgr.post_event(objpath, desc, 'User Action', 'INFORMATION', time.time())

class wdebug:
    def GET(self, item=None):
        #session.any_auth()
        log.debug('Getting catalog item=%s', item)
        if item == 'catalog':
            q = web.input()
            if not q.get('item'):
                keys = catalog.keys()
                keys.sort()
                return keys
            else:
                web.http.expires(0)
                item = q.get('item')
                val = catalog.lookup(item)
                return  val.__dict__.items() if val else None

class deployment:

    def get_dc(self):
        try:
            return [dc for dc in catalog.get_all() if isinstance(dc, ALDeploymentConnector)][0]
        except:
            return None

    def refresh(self):
        ALDeploymentConnector.create()

    def clear(self):
        dc = self.get_dc()
        if dc:
            dc.clear_finished_jobs()

    def updateFolders(self, folders):
        dc = self.get_dc()
        if dc:
            dc.updateJobFolders(folders)
            return {}
        return {}

    def GET(self):
        log.debug("calling get_dc()")
        dc = self.get_dc()
        q = web.input()
        log.debug(q)
        vc_guid = q.serverGuid.lower()

        if dc:
            log.debug("in if dc:")
            web.header('Content-Type', 'text/javascript')
            web.http.expires(0)

            deployment_obj = {}
            log.debug("calling dc.getJobFolders()")
            deployment_obj['JobFolders'] = dc.getJobFolders()
            log.debug("calling dc.getManagedNodes()")
            deployment_obj['ManagedNodes'] = dc.getManagedNodes()
            log.debug("calling dc.getDcStatus()")
            deployment_obj['Status'] = dc.getDcStatus()
            try:
                vc = [vc for vc in catalog.get_all() if isinstance(vc, vCenter) and vc.sc.about.instanceUuid.lower()==vc_guid]
            except:
                log.debug('error iterating over vc list')
            vc = vc[0]

            log.debug("calling vc.retreive_cluster_list()")
            clusters = vc.retreive_cluster_list()
            names = []
            for cluster in clusters:
                try:
                    for prop in cluster.propSet:
                        if prop.name == 'name':
                            names.append(prop.val)
                except:
                    pass

            deployment_obj['Clusters'] = names

            log.debug("vc.retrieve_datacenter_list()")
            datacenters = vc.retrieve_datacenter_list()
            names = []
            for datacenter in datacenters:
                try:
                    for prop in datacenter.propSet:
                        if prop.name == 'name':
                            names.append(prop.val)
                except:
                    pass

            deployment_obj['Datacenters'] = names

        else:
            deployment_obj = {}
            deployment_obj['Status'] = {'errno':'-1', 'message':'The deployment service is starting.'}
        log.debug(json.dumps(deployment_obj))
        return json.dumps(deployment_obj)

    def POST(self):
        log.debug('In POST...')
        web.header('Content-Type', 'text/javascript')
        web.http.expires(0)

        q = web.input()
        item = ''
        if hasattr(q, 'action'):
            item = q.action

        if item == '':
            deployment_data = json.loads(web.data())
            dc = self.get_dc()
            if not dc:
                raise web.notfound()
            dc.begin_run_deployment(deployment_data)
            log.debug('Calling dc.begin_run_deployment')
            obj = {}
            obj['Status'] = {'errno':'8', 'message':'Started job(s)'}
            # just return something or the jquery callback won't fire.
            log.debug('returning empty object')
            return json.dumps(obj)
        elif item == 'refresh':
            self.refresh()
            sleep(1)
            while True:
                if self.get_dc():
                    return self.GET()
                sleep(1)
        elif item == 'clear':
            self.clear()
            return self.GET()

        elif item == 'updateFolders':
            log.debug('Calling self.updateFolders')
            folders = json.loads(web.data())
            result = self.updateFolders(folders)
            return json.dumps(result)

        return ""

class firmware:

    def get_vc(self, serverGuid):
        vcs = [vc for vc in catalog.get_all() if isinstance(vc, vCenter) and vc.sc.about.instanceUuid.lower()==serverGuid.lower()]
        if len(vcs) == 1:
            return vcs[0]
        else:
            return None

    def get_host(self, serverGuid, moref):
        vc = self.get_vc(serverGuid)
        if not vc:
            return None

        hosts = [host for host in vc.hosts if host.moref().lower()==moref.lower()]
        if len(hosts) == 1:
            return hosts[0]
        else:
            return None

    def get_hosts(self, serverGuid, moref):
        vc = [vc for vc in catalog.get_all() if isinstance(vc, vCenter) and vc.sc.about.instanceUuid.lower()==serverGuid.lower()]
        vc = vc[0]
        clusters = vc.retreive_cluster_list()
        hosts = []
        for cluster in clusters:
            mob = cluster.obj._type + ':' + cluster.obj.value
            if mob.lower()==moref.lower():
                for prop in cluster.propSet:
                    if prop.name == 'host':
                        for host in prop.val[0]:
                            mob = host._type + ':' + host.value
                            hosts.append(self.get_host(serverGuid, mob))

        return hosts

    def POST(self):
        web.header('Content-Type', 'text/javascript')
        web.http.expires(0)

        input = json.loads(web.data())
        q = web.input()

        if not hasattr(q, 'moref') or q.moref == '':
            raise web.badrequest()
        if not hasattr(q, 'serverGuid') or q.serverGuid == '':
            raise web.badrequest()

        if not 'package_url' in input:
            raise web.badrequest()
        
        log.debug('firmware POST %s %s', q.moref, input['package_url'])
        
        if q.moref.split(':')[0] == 'HostSystem':
            item = self.get_host(q.serverGuid, q.moref)
            if not item:
                raise web.notfound()
            # Check to see if the firmware object has been created. Create it if not.
            try:
                fw = getattr(item, 'host_firmware')
            except AttributeError:
                setattr(item, 'host_firmware', HostFirmware(item, q.serverGuid))

        elif q.moref.split(':')[0] == 'ClusterComputeResource':
            item = self.get_hosts(q.serverGuid, q.moref)
            for host in item:
                try:
                    fw = getattr(host, 'host_firmware')
                except AttributeError:
                    setattr(host, 'host_firmware', HostFirmware(host, q.serverGuid))
        else:
            raise web.notfound()

        if item:
            package_url = input['package_url']
            options = input['options']
            if q.moref.split(':')[0] == 'ClusterComputeResource':
                data = []
                for host in item:
                    # Only execute the requested job on the selected hosts.
                    if host.name in input['hosts']:
                        log.debug('firmware queuing job for host %s', host.name)
                        result = host.host_firmware.queue_firmware_job(package_url, options)
                        try :
                            desc = 'Firmware job queued - ' + input['package_url']
                            objpath = 'moref=' + q.moref + '&' + 'serverGuid=' + q.serverGuid   
                            log.debug('Newsfeed ' + desc)
                            uim_url = urlparse(config().get_uim_root())        
                            uimgr = UIManager(port=uim_url.port, protocol=uim_url.scheme, host = uim_url.hostname )
                            uimgr.post_event(objpath, desc, 'User Action', 'INFORMATION', time.time())
                        except :
                            log.exception("Error generating newsfeed for firmware job queued")
                    else:
                        result = host.host_firmware.get_firmware_jobs()
                    
                    data.append({'Host': host.name, 'FirmwareJobs': result})
                #json.dump(data, open('firmware_resp.json', 'w+'), indent=4)    #debug  
                return json.dumps(data)
            else:            
                result = item.host_firmware.queue_firmware_job(package_url, options)
                
                try :
                    desc = 'Firmware job queued - ' + input['package_url']
                    objpath = 'moref=' + q.moref + '&' + 'serverGuid=' + q.serverGuid   
                    log.debug('Newsfeed ' + desc)
                    uim_url = urlparse(config().get_uim_root())        
                    uimgr = UIManager(port=uim_url.port, protocol=uim_url.scheme, host = uim_url.hostname )
                    uimgr.post_event(objpath, desc, 'User Action', 'INFORMATION', time.time())
                except :
                    log.exception("Error generating newsfeed for firmware job queued")
                
                return json.dumps(result)
            return None
        else:
            raise web.notfound()

    def DELETE(self):

        web.header('Content-Type', 'text/javascript')
        web.http.expires(0)

        input = json.loads(web.data())
        q = web.input()
        
        if not hasattr(q, 'moref') or q.moref == '':
            raise web.badrequest()
        if not hasattr(q, 'serverGuid') or q.serverGuid == '':
            raise web.badrequest()

        if not 'package_url' in input:
            raise web.badrequest()

        log.debug('firmware DELETE job %s %s', q.moref, input['package_url'])            
        
        if q.moref.split(':')[0] == 'HostSystem':
            item = self.get_host(q.serverGuid, q.moref)
            if not item:
                raise web.notfound()
            # Check to see if the firmware object has been created. Create it if not.
            try:
                fw = getattr(item, 'host_firmware')
            except AttributeError:
                setattr(item, 'host_firmware', HostFirmware(item, q.serverGuid))

        elif q.moref.split(':')[0] == 'ClusterComputeResource':
            item = self.get_hosts(q.serverGuid, q.moref)
            for host in item:
                try:
                    fw = getattr(host, 'host_firmware')
                except AttributeError:
                    setattr(host, 'host_firmware', HostFirmware(host, q.serverGuid))
        else:
            raise web.notfound()

        if item:
            if q.moref.split(':')[0] == 'ClusterComputeResource':
                json_str = '['
                for host in item:
                    if host.name in input['hosts']:
                        result = host.host_firmware.delete_queued_job(input['package_url'])
                        if not result :
                            raise web.notfound()
                        json_str += '{"Host":' + '"' + host.name + '", "FirmwareJobs":'
                        json_str += json.dumps(result) + '},'
                    else:
                        result = host.host_firmware.get_firmware_jobs()
                        json_str += '{"Host":' + '"' + host.name + '", "FirmwareJobs":'
                        json_str += json.dumps(result) + '},'

                return json_str + ']'
            else:
                result = item.host_firmware.delete_queued_job(input['package_url'])
                if not result :
                    raise web.notfound()
                return json.dumps(result)
            return None
        else:
            raise web.notfound()

class firmware_jobs:

    def get_vc(self, serverGuid):
        vcs = [vc for vc in catalog.get_all() if isinstance(vc, vCenter) and vc.sc.about.instanceUuid.lower()==serverGuid.lower()]
        if len(vcs) == 1:
            return vcs[0]
        else:
            return None

    def get_host(self, serverGuid, moref):
        vc = self.get_vc(serverGuid)
        if not vc:
            return None

        hosts = [host for host in vc.hosts if host.moref().lower()==moref.lower()]
        if len(hosts) == 1:
            return hosts[0]
        else:
            return None

    def get_hosts(self, serverGuid, moref):
        vc = [vc for vc in catalog.get_all() if isinstance(vc, vCenter) and vc.sc.about.instanceUuid.lower()==serverGuid.lower()]
        vc = vc[0]
        clusters = vc.retreive_cluster_list()
        hosts = []
        for cluster in clusters:
            mob = cluster.obj._type + ':' + cluster.obj.value
            if mob.lower()==moref.lower():
                for prop in cluster.propSet:
                    if prop.name == 'host':
                        for host in prop.val[0]:
                            mob = host._type + ':' + host.value
                            h = self.get_host(serverGuid, mob)
                            if h :
                                hosts.append(h)

        return hosts

    def GET(self):
        
        q = web.input()
        if not hasattr(q, 'moref') or q.moref == '':
            raise web.badrequest()
        if not hasattr(q, 'serverGuid') or q.serverGuid == '':
            raise web.badrequest()

        log.debug("firmware_jobs GET %s", q.moref)
        
        if q.moref.split(':')[0] == 'HostSystem':
            item = self.get_host(q.serverGuid, q.moref)
            if not item:
                raise web.notfound()
            # Check to see if the firmware object has been created. Create it if not.
            try:
                fw = getattr(item, 'host_firmware')
            except AttributeError:
                setattr(item, 'host_firmware', HostFirmware(item, q.serverGuid))

        elif q.moref.split(':')[0] == 'ClusterComputeResource':
            item = self.get_hosts(q.serverGuid, q.moref)
            for host in item:
                try:
                    fw = getattr(host, 'host_firmware')
                except AttributeError:
                    setattr(host, 'host_firmware', HostFirmware(host, q.serverGuid))

        else:
            raise web.notfound()

        if item:
            web.header('Content-Type', 'text/javascript')
            web.http.expires(0)

            if q.moref.split(':')[0] == 'ClusterComputeResource':
            
                data = []
                for host in item:
                    if host:
                        i = {}
                        i['Host'] = host.name                        
                        i['FirmwareJobs'] = host.host_firmware.get_firmware_jobs()
                        data.append(i)
                return json.dumps(data)
            else:
                jobs = item.host_firmware.get_firmware_jobs()
                return json.dumps(jobs)
        else:
            raise web.notfound()

class smart_components:
    def GET(self):
        log.debug("smart_components GET")
        q = web.input()

        web.header('Content-Type', 'text/javascript')
        web.http.expires(0)

        scm = get_scm()
        obj = {
            'components': scm.json()
        }
        return json.dumps(obj)

    def POST(self):

        # We have to use the text/html type here even though the format is json. This is so that
        # we can load the data in a hidden frame and parse it as javascript.
        web.header('Content-Type', 'text/html')
        web.http.expires(0)
        inputdata = json.loads(web.data())
        log.debug("smart_components POST %s", inputdata['filename'])
        # This is key - make sure we don't treat binary files as unicode. Also make sure to
        # use the binary file mode (wb) when writing the file.
        obj = {}
        try:
            filename = inputdata['filename']
            scm = get_scm()

            if not os.path.exists('static/sc_share'):
                os.makedirs('static/sc_share')

            import shutil
            shutil.copyfile('../uim/static/sc_share/' + filename, 'static/sc_share/' + filename)
            scm.add_component(filename)

            obj['status'] = '0'
            obj['message'] = 'Component upload successful'
            obj['components'] = scm.json()

        except Exception as err:
            log.exception("Error saving smart component '%s'", inputdata)
            obj['status'] = '-1'
            obj['message'] = 'Component upload failed: '+str(err)

        return json.dumps(obj)

    def DELETE(self):
        web.header('Content-Type', 'text/javascript')
        web.http.expires(0)

        input = json.loads(web.data())
        q = web.input()
        
        if not 'sc_filename' in input:
            raise web.badrequest()

        log.debug("smart_components DELETE %s", input['sc_filename'])
        
        scm = get_scm()
        scm.delete_component(input['sc_filename'])

        obj = {
            'components': scm.json()
        }
        return json.dumps(obj)

class Settings:
    def parse_data(self):
        op = ''
        obj = {}
        d = web.data()
        data = web.data().split('&')
        input = web.input()        
        
        for item in data:
            kv =  item.split('=')
            kv[1] = kv[1].replace('+', ' ')
            if kv[0] == 'oper':
                op = unquote(kv[1])
            else:
                obj[kv[0]] = unquote(kv[1])
        return op, obj
    
    def GET(self, service):
        if service == 'config':
            cfg = config()
            srvcfg = cfg.get_server_config()
            data = []
            data.append({'param_name': 'HP SIM Port', 'param_value': srvcfg['hpsim']['port']})
            data.append({'param_name': 'Device Poll Interval', 'param_value': srvcfg['device_poll']})
            data.append({'param_name': 'Event Poll Interval', 'param_value': srvcfg['event_poll']})
            data.append({'param_name': 'Power Cost', 'param_value': srvcfg['power_cost']})
            data.append({'param_name': 'Virtual Connect Uplink Over Subscription Factor', 'param_value': srvcfg.get('bottleneck_oversub', '1.2')})
            return json.dumps({'srvcfg' : data})
        elif service == 'hostproperties':
            hostpw = None
            ilopw = None
            dc = Collector.get_collector()
            iloaddr = dc.server.get('data', {}).get('summary',{}).get('iLOAddress', None) if dc.server else None
            if not iloaddr:
                iloaddr = dc.ilo.get('address', {}).get('ipv4', None) if dc.ilo else None
                if not iloaddr:
                    iloaddr = getattr(dc.host,'associated_ilo', None)
            vmwhost = getattr(dc.host_detail, 'name', None)
            
            #csce = catalog.lookup('engines.csc_engine')
            #if iloaddr:
            #    ilopw = csce.get_password('', 'iLO', iloaddr)
            #if vmwhost:
            #    hostpw = csce.get_password('', 'ProLiant Server', vmwhost)

            if iloaddr:
                ilopw = self.find_pw_for_host_and_type(iloaddr, 'iLO')
                if not ilopw:
                    ilopw = self.find_global_pw('iLO')

            if vmwhost:
                hostpw = self.find_pw_for_host_and_type(vmwhost, 'ProLiant Server')
                if not hostpw:
                    hostpw = self.find_global_pw('ProLiant Server')
                
            pwlist = []
            if ilopw:
                pwlist.append({'host': ilopw['host'], 'username': ilopw['username'], 'password': '', 'type': 'iLO'})
            else:
                pwlist.append({'host': iloaddr, 'username': '', 'password': '', 'type': 'iLO'})
            if hostpw:
                pwlist.append({'host': hostpw['host'], 'username': hostpw['username'], 'password': '', 'type': 'ProLiant Server'})
            else:
                pwlist.append({'host': vmwhost, 'username': '', 'password': '', 'type': 'ProLiant Server'})
                
            return json.dumps({'hostprop': pwlist})

        elif service == 'clusterproperties':
            cprops = []
            cprops.append({'username': '', 'password': '', 'type': 'iLO'})
            return json.dumps({'clusterprop': cprops})
        elif service == 'password':
            csce = catalog.lookup('engines.csc_engine')    
            pwlist = csce.get_passwords()
            pwlist = [pw for pw in pwlist if (pw['type'] not in ('HP Common Services', 'vCenter'))]
            return json.dumps(pwlist)

    def find_pw_in_cspw_list(self, data):
        csce = catalog.lookup('engines.csc_engine')
        pwlist = csce.get_passwords()
        for pw in pwlist:
            if (data.has_key('id') and pw['id'] == data['id']) or \
                    (data.has_key('username') and pw['username'] == data['username'] and \
                     data.has_key('type') and pw['type'] == data['type'] and \
                     data.has_key('host') and pw['host'] == data['host'] ):
                return pw
        return None

    def find_pw_for_host_and_type(self, host, pwtype):
        csce = catalog.lookup('engines.csc_engine')
        pwlist = csce.get_passwords()
        log.debug('looking for password %s, %s', host, pwtype)
        for pw in pwlist:
            if (pw['type'] == pwtype and pw['host'] == host ):
                return pw
        return None

    def find_global_pw(self, pwtype):
        csce = catalog.lookup('engines.csc_engine')
        pwlist = csce.get_passwords()
        for pw in pwlist:
            if (  pw['type'] == pwtype and pw['host'] == '*' ):
                return pw
        return None

    def pwtype_exists(self, pwtype):
        csce = catalog.lookup('engines.csc_engine')
        pwlist = csce.get_passwords()
        for pw in pwlist:
            if (pw['type'] == pwtype):
                return pw
        return None
        
    def POST(self, service):
        runAfterSavingConfig = []
        oper, data = self.parse_data()
        if service in ('password', 'vcpassword'):
            csce = catalog.lookup('engines.csc_engine')
            vce = catalog.lookup('ic4vc-vc_engine')
            if oper == 'del':
                log.debug("****Credential DEL")
                pw = self.find_pw_in_cspw_list(data)
                if pw:
                    csce.del_password_from_cs(pw['id'], pw['username'], pw['type'], pw['host'])
                    if pw['type'] == 'vCenter':
                        vce.delete_vc(pw)                    
                else :
                    log.error('Error deleting credential: Unable to find credential')
                    raise web.notfound('Unable to find credential.')
                    
            elif oper == 'edit':    
                log.debug("Credential EDIT")
                pw = self.find_pw_in_cspw_list(data)    # edit DOES NOT WORK because if the username is changed it will not find the cred
                if pw:
                    if data['password'] != '****************' and data['password'].strip() !='' :                   
                        csce.update_password(pw['id'], data['username'], data['password'], data['type'], data['host'])
                        if pw['type'] == 'vCenter':
                            vce.edit_vc(data)
                    else:
                        log.error('Error editing credential: No Password')
                        raise web.forbidden(data='The password must be set.')
                else:
                    log.error("Error editing credential: not found")
                    raise web.notfound('Unable to find credential.')
                    
            elif oper == 'add':
                log.debug("Credential ADD")
                if data['type'] == 'HP SIM':                
                    pw = self.pwtype_exists(data['type'])
                    if pw:
                        log.error("Error adding credential: Only one HP SIM credential allowed")
                        raise web.forbidden(data = 'Only one HP SIM entry is allowed')

                pw = self.find_pw_in_cspw_list(data)
                if not pw:
                    try :
                        csce.add_password_to_cs(data['username'], data['password'], data['type'], data['host'])
                    except: 
                        log.exception("Error adding credential: Error adding credential for vCenter %s", data['host'])
                        raise web.internalerror("Error adding credential")
                else:
                    log.error("Error adding credential: Credential already exists")
                    raise web.conflict('Credential already exists.')
                
                if data['type'] == 'vCenter':
                    runAfterSavingConfig.append( (vce.add_vc, (data,)) )
            else:
                log.error("Error in editing credential: operation '%s' not supported.", oper)
                raise web.notfound('Operation Not Supported')
            
            if oper != 'del':
                if data['type'] == 'HP SIM': 
                    runAfterSavingConfig.append( (ALDeploymentConnector.create, tuple() ) )
                if data['type'] == 'iLO' and data['host'] != '*':
                    csce.associate_ilo(data['host'])
                
            #log.debug(data)
        elif service == 'config':
            if oper == 'edit':
                cfg = config()
                srvcfg = cfg.get_server_config()
                if data['param_name'] == 'HP SIM Port':
                    srvcfg['hpsim']['port'] = data['param_value']
                    runAfterSavingConfig.append( (ALDeploymentConnector.create, tuple() ) )
                elif data['param_name'] == 'Device Poll Interval':
                    srvcfg['device_poll'] = data['param_value']
                elif data['param_name'] == 'Event Poll Interval':
                    srvcfg['event_poll'] = data['param_value']
                elif data['param_name'] == 'Power Cost':
                    srvcfg['power_cost'] = data['param_value']
                elif data['param_name'] == 'Virtual Connect Uplink Over Subscription Factor':
                    srvcfg['bottleneck_oversub'] = data['param_value']
                cfg.set_server_config(srvcfg)
            else:
                log.error("Error editing config: operation '%s' not supported.", oper)
                raise web.notfound('This operation is not supported')
        elif service == 'hostproperties':
            if oper == 'edit':
                if data['host'] != '*' and data['password'] != '****************' and data['password'].strip() !='' :
                    dc = Collector.get_collector()
                    if dc.host:
                        csce = catalog.lookup('engines.csc_engine')
                        vce = catalog.lookup('ic4vc-vc_engine')
                        csce.add_password_to_cs(data['username'], data['password'], data['type'], data['host'])
                        if data['type'] == 'iLO':
                            csce.associate_ilo(data['host'])
                            setattr(dc.host, 'associated_ilo', data['host'])
                    else:
                        log.error("Error editing host properties: host has not been discovered yet.")
                        raise web.notfound('Host has not been discovered yet.')
                else:
                    if data['host'] == '*' :
                        log.error("Error editing host properties: host=* Cannot set global from host.")
                        raise web.forbidden(data='Global credentials cannot be set here.')
                    elif data['password'] == '****************' or data['password'].strip() == '' :
                        log.error("Error setting host properties - password is blank or *.")
                        raise web.forbidden(data='The password must be set.')
                    else :
                        log.error("Error editing host properties - unknown error.")
                        raise web.forbidden(data='Error editing host properties.')
            else:
                log.error("Error in editing host properties: operation '%s' not supported.", oper)
                raise web.notfound('This operation is not supported')
        elif service == 'clusterproperties':
            log.debug('clusterproperties')
            if oper == 'edit':
                log.debug('clusterproperties edit')
                if data['password'] != '****************' and data['password'].strip() !='' and data['username'].strip() != '' :
                    dc = ClusterCollector.get_collector()
                    csce = catalog.lookup('engines.csc_engine')
                    if data['type'] == 'iLO':
                        log.debug('clusterproperties edit iLO')   
                        creds_set = 0
                        if not len(dc.hosts) :
                            log.error("Error editing cluster properties: Cluster %s is empty", dc.cluster.name)
                            raise web.notfound("No hosts in this cluster.")
                        for host in dc.hosts :
                            log.debug("Cluster Properties setting credentials for host '%s'", host.name)
                            iloaddress = dc.ilo_address(host.hardware.systemInfo.uuid)
                            if iloaddress:
                                csce.add_password_to_cs(data['username'], data['password'], data['type'], iloaddress)
                                creds_set+=1
                            else :
                                log.error("Error editing cluster properties: No iLO address for host '%s'", host.name)
                                
                        if not creds_set :
                            log.error('Error editing cluster properties: No hosts set of %d.', len(dc.hosts) )
                            raise web.notfound('Unable to set any hosts.')
                        if creds_set != len(dc.hosts) :
                            log.error('Error editing cluster properties: Only set %d of %d hosts.', creds_set, len(dc.hosts) )
                            raise web.notfound('Only set %d of %d hosts.' % (creds_set, len(dc.hosts)))
                    else :
                        log.error("Error in editing cluster properties: type '%s' not supported.", data['type'])
                        raise web.notfound('This type is not supported')
                else:
                    if data['username'].strip() == '' :
                        log.error("Error editing cluster properties: username is blank.")
                        raise web.forbidden(data='Username cannot be blank.')
                    elif data['password'] == '****************' or data['password'].strip() == '' :
                        log.error("Error setting cluster properties - password is blank or *.")
                        raise web.forbidden(data='The password must be set.')
                    else :
                        log.error("Error editing cluster properties - unknown error.")
                        raise web.forbidden(data='Error editing cluster properties.')                    
            else:
                log.error("Error in editing cluster properties: operation '%s' not supported.", oper)
                raise web.notfound('This operation is not supported')
        else:
            log.error("Error in Settings: service '%s' not supported", service)
            raise web.notfound('Service not supported')
        
        taskQ = catalog.lookup('ic4vc-server-threadpool').get_q()
        for f in runAfterSavingConfig:
            taskQ.put(f)
            
        return 
        
class mywebapp(web.application):
    def run(self, certinfo, server_address=('0.0.0.0', 8080), timeout=900, rqs=10, nthreads=20, *middleware):
        func = self.wsgifunc(*middleware)
        altaddress = ('localhost', server_address[1])
        self.server = CherryPyWSGIServer(altaddress, func, timeout=timeout, request_queue_size=rqs, numthreads=nthreads)
        self.server.thread_name = 'HPIC4VCServer'
        log.info('https://%s:%d/' % altaddress)
        try:
            self.server.start()
        except KeyboardInterrupt:
            self.server.stop()
            
class ServerWebApp:
    def __init__(self):
        self.cfg = config()
        self.server_cfg = self.cfg.get_server_config()
        self.server_root = self.cfg.get_server_root()
        self.server_url = urlparse(self.server_root)        
        self.proto = self.server_url.scheme
        log.debug(self.server_url.port)
        self.server_address = (self.server_url.hostname, self.server_url.port)
        catalog.insert(self, 'ServerWebApp')

    def start_collection(self, handler):
        try:
            portlets.collector.remove_old_collectors()
            q =  web.input()
            if hasattr(q, 'moref'): 
                if q.moref.startswith('HostSystem'):
                    Collector.get_collector()
                elif q.moref.startswith('ClusterComputeResource'):
                    ClusterCollector.get_collector()
        except:
            log.exception('error start_collection')
        return handler()
                
    def start(self):
        self.mywebappthread = Thread(name='server-ServerWebApp', target=self.startmywebapp)
        self.mywebappthread.daemon = True
        self.mywebappthread.start()
        
    def startmywebapp(self):
        self.app = mywebapp(urls, globals())
        timeout = int(self.server_cfg.get('webserver', {}).get('timeout', 60))
        rqs = int(self.server_cfg.get('webserver', {}).get('req_queue', 10))
        nthreads = int(self.server_cfg.get('webserver', {}).get('num_threads', 20))
        self.app.add_processor(self.start_collection)
        self.app.run(self.cfg.get_cert_info(), self.server_address, timeout = timeout, rqs=rqs, nthreads=nthreads)
    
    def stop(self):
        # This is where you will need to do all the cleanup
        self.app.server.stop()
        pass
        
def start():
    cfg = config()
    server_root = cfg.get_server_root()
    server_url = urlparse(server_root)
    server_address = (server_url.hostname, server_url.port)
    app = mywebapp(urls, globals())
    app.run(cfg.get_cert_info(), server_address)

if (__name__ == '__main__'):
    app = web.application(urls, globals())
    session = web.session.Session(app, web.session.DiskStore('sessions'), initializer={})
    app.run()
