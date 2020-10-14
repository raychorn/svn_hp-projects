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
import re
from portlets.host import Host
from portlets.network import Network
from portlets import cluster
from engines.deployment_connector import ALDeploymentConnector
from engines import hostFirmwareBaseline
from vmware.vcenter import vCenter, ManagedObjectRef
from time import sleep
from datetime import datetime
from engines.firmware import HostFirmware
from util.smartcomponent import get_scm
from portlets.cluster import ClusterSwFwDetail
from portlets.collector import Collector, ClusterCollector
from urlparse import urlparse
from urllib import unquote
from core.uim import UIManager
from core.powercontrol import PowerControl
from core.uidcontrol import UIDControl
from core.rediscover import RediscoverNode
from core.supportdata import SupportData
import portlets.collector
import time
from util import credential
from core.icsp import ICSPJob
from urlparse import urlparse
from core.cs import CommonServicesServerError
import hponeview
from util.authclient import get_authclient
import traceback
from requests.exceptions import *
web.config.debug = True
import util.scheduletasks as scheduletasks
from portlets import enclosure
urls = [
    '/test', 'Test',
    '/test/(.*)', 'Test',
    '/debug/(.*)', 'wdebug',
    '/debug/', 'wdebug',
    '/services/clusterswfwdetail', 'ClusterSwFwDetail',
    '/services/', 'wservices',
    '/services/(.*)', 'wservices',
    '/deployment/', 'deployment',  # polling jobs
    '/firmware/', 'firmware',
    '/firmware', 'firmware',
    '/firmware/jobs', 'firmware_jobs',  # polling jobs
    '/firmware/jobs/', 'firmware_jobs',
    '/smart_components', 'smart_components',
    '/settings/(.+)', 'Settings',
    '/uidcontrol', 'uid_handler',
    '/supportdata', 'sd_handler',
    '/powercontrol', 'pc_handler',
    '/rediscovernode', 'rediscover_handler',
    '/icsp', 'ICSP',
    '/icsp/', 'ICSP',
    '/credentials', 'Credentials',
    '/credentials/', 'Credentials',
    'hponeview/(.+)', 'HPOneView',
    '/firmwarebaseline', 'firmwarebaseline',
    '/clusterhosts', 'clusterhosts',
    '/enclosures', 'Enclosures',
    '/enclosures/(.*)', 'Enclosures',
    '/enclosure/(.+)/(.+)', 'Enclosures',
    '/enclosurehosts', 'EnclosureHosts',
    '/enclosurehosts/(.*)', 'EnclosureHosts',
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

class wservices:
    def GET(self, item=None):
        # session.any_auth()
        log.debug('GET wservices %s', item)
        if item not in service_dispatcher :
            raise web.notfound()
        
        import cProfile, pprint, StringIO
        from util import ioTimeDeco
        
        def profile(func):
            def wrapper(*args, **kwargs):
                prof = cProfile.Profile()
                retval = prof.runcall(func, *args, **kwargs)
                fOut = StringIO.StringIO()
                pprint.pprint(prof.getstats(),fOut)
                log.debug(fOut.getvalue())
                return retval
            return wrapper        

        @ioTimeDeco.analyze('total::__service_dispatcher__')
        def __service_dispatcher__(__item__):
            return service_dispatcher[__item__]()

        log.info('(+++) item is "%s".' % (item))
        if (item == 'networkdetail'):
            ioTimeDeco.ioTimeAnalysis.__init__()
            data = __service_dispatcher__(item)
            results = ioTimeDeco.ioTimeAnalysis.ioTimeAnalysisReport(logger=None)
            log.debug(results)
        else:
            data = service_dispatcher[item]()

        return json.dumps(data, cls=SudsEncoder)

class wdebug:
    def GET(self, item=None):
        # session.any_auth()
        log.debug('Getting catalog item=%s', item)
        if item == 'catalog':
            q = web.input()
            if not q.get('item'):
                keys = catalog.keys()
                keys.sort()
                log.debug("wdebug key = %s", keys)
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
                vc = [vc for vc in catalog.get_all() if isinstance(vc, vCenter) and vc.sc.about.instanceUuid.lower() == vc_guid]
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
        vcs = [vc for vc in catalog.get_all() if isinstance(vc, vCenter) and vc.sc.about.instanceUuid.lower() == serverGuid.lower()]
        if len(vcs) == 1:
            return vcs[0]
        else:
            return None

    def get_host(self, serverGuid, moref):
        vc = self.get_vc(serverGuid)
        if not vc:
            return None

        hosts = [host for host in vc.hosts if host.moref().lower() == moref.lower()]
        if len(hosts) == 1:
            return hosts[0]
        else:
            return None

    def get_hosts(self, serverGuid, moref):
        vc = [vc for vc in catalog.get_all() if isinstance(vc, vCenter) and vc.sc.about.instanceUuid.lower() == serverGuid.lower()]
        vc = vc[0]
        clusters = vc.retreive_cluster_list()
        hosts = []
        for cluster in clusters:
            mob = cluster.obj._type + ':' + cluster.obj.value
            if mob.lower() == moref.lower():
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
            dc = Collector.get_collector()
            item = self.get_host(q.serverGuid, q.moref)
            if not item:
                raise web.notfound()
            # Check to see if the firmware object has been created. Create it if not.
            try:
                fw = getattr(item, 'host_firmware')
            except AttributeError:
                setattr(item, 'host_firmware', HostFirmware(item, q.serverGuid, q.moref, dc))

        elif q.moref.split(':')[0] == 'ClusterComputeResource':
            dc = ClusterCollector.get_collector()
            item = self.get_hosts(q.serverGuid, q.moref)
            for host in item:
                try:
                    fw = getattr(host, 'host_firmware')
                except AttributeError:
                    setattr(host, 'host_firmware', HostFirmware(host, q.serverGuid, q.moref, dc))
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
                            uimgr = UIManager(port=uim_url.port, protocol=uim_url.scheme, host=uim_url.hostname)
                            uimgr.post_event(objpath, desc, 'User Action', 'INFORMATION', time.time())
                        except :
                            log.exception("Error generating newsfeed for firmware job queued")
                    else:
                        result = host.host_firmware.get_firmware_jobs()

                    data.append({'Host': host.name, 'FirmwareJobs': result})
                # json.dump(data, open('firmware_resp.json', 'w+'), indent=4)    #debug
                return json.dumps(data)
            else:
                result = item.host_firmware.queue_firmware_job(package_url, options)

                try :
                    desc = 'Firmware job queued - ' + input['package_url']
                    objpath = 'moref=' + q.moref + '&' + 'serverGuid=' + q.serverGuid
                    log.debug('Newsfeed ' + desc)
                    uim_url = urlparse(config().get_uim_root())
                    uimgr = UIManager(port=uim_url.port, protocol=uim_url.scheme, host=uim_url.hostname)
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
            dc = Collector.get_collector()
            item = self.get_host(q.serverGuid, q.moref)
            if not item:
                raise web.notfound()
            # Check to see if the firmware object has been created. Create it if not.
            try:
                fw = getattr(item, 'host_firmware')
            except AttributeError:
                setattr(item, 'host_firmware', HostFirmware(item, q.serverGuid, q.moref, dc))

        elif q.moref.split(':')[0] == 'ClusterComputeResource':
            dc = ClusterCollector.get_collector()
            item = self.get_hosts(q.serverGuid, q.moref)
            for host in item:
                try:
                    fw = getattr(host, 'host_firmware')
                except AttributeError:
                    setattr(host, 'host_firmware', HostFirmware(host, q.serverGuid, q.moref, dc))
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
        vcs = [vc for vc in catalog.get_all() if isinstance(vc, vCenter) and vc.sc.about.instanceUuid.lower() == serverGuid.lower()]
        if len(vcs) == 1:
            return vcs[0]
        else:
            return None

    def get_host(self, serverGuid, moref):
        vc = self.get_vc(serverGuid)
        if not vc:
            return None

        hosts = [host for host in vc.hosts if host.moref().lower() == moref.lower()]
        if len(hosts) == 1:
            return hosts[0]
        else:
            return None


    def GET(self):

        q = web.input()
        if not hasattr(q, 'moref') or q.moref == '':
            log.error("firmware_jobs GET missing moref")
            raise web.badrequest()
        if not hasattr(q, 'serverGuid') or q.serverGuid == '':
            log.error("firmware_jobs GET missing serverGuid")
            raise web.badrequest()

        log.debug("firmware_jobs GET %s", q.moref)

        morefType = q.moref.split(':')[0]        
        
        web.header('Content-Type', 'text/javascript')
        web.http.expires(0)

        if morefType == 'ClusterComputeResource':
            data = []
            dc = ClusterCollector.get_collector()
            
            for host in dc.hosts:
                if host:
                    i = {}
                    i['Host'] = host.name
                    
                    uuid = host.hardware.systemInfo.uuid                    
                    host_data = {}
                    
                    # Check to see the this host even has the HP providers installed
                    # If not then don't even try to collect firmware information
                    Host.add_provider_bundle_status(host_data, dc.servers.get(uuid, None))        
                    if host_data.get('hp_bundle_status', None) :
                        log.debug("firmware_jobs GET creating HostFirmware for cluster host %s", host.name)
                        
                        if not getattr(host, 'host_firmware', None) :                        
                            setattr(host, 'host_firmware', HostFirmware(host, q.serverGuid, q.moref, dc))
                    
                        log.debug("firmware_jobs GET firmware jobs for host %s", host.name)
                        i['FirmwareJobs'] = host.host_firmware.get_firmware_jobs()
                        
                    else :
                        log.debug("firmware_jobs GET HP Bundle not available for cluster host %s", host.name)
                        i['FirmwareJobs'] = {'Error': (2, 'The HP ESXi Offline Bundle for VMware is not installed or not available on this host.')}
                        
                    data.append(i)
                    
            log.debug("firmware_jobs GET returning jobs for cluster %s : %s", q.moref, json.dumps(data))
            return json.dumps(data)
            
        elif morefType == 'HostSystem':
            dc = Collector.get_collector()
            host = self.get_host(q.serverGuid, q.moref)
            if not host :
                log.error("firmware_jobs GET Unable to find host %s", q.moref)
                raise web.notfound()
            # Check to see if the firmware object has been created. Create it if not.
            log.debug("firmware_jobs GET creating HostFirmware for host %s", host.name)
            
            if not getattr(host, 'host_firmware', None) :
                setattr(host, 'host_firmware', HostFirmware(host, q.serverGuid, q.moref, dc))
                
            jobs = host.host_firmware.get_firmware_jobs()
            log.debug("firmware_jobs GET returning jobs for host %s : %s", q.moref, json.dumps(jobs))
            return json.dumps(jobs)
            
        else:
            log.error("firmware_jobs GET unknown moref type %s", morefType)
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
            obj['message'] = 'Component upload failed: ' + str(err)

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
            kv = item.split('=')
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
            iloaddr = dc.server.get('data', {}).get('summary', {}).get('iLOAddress', None) if dc.server else None
            if not iloaddr:
                iloaddr = dc.ilo.get('address', {}).get('ipv4', None) if dc.ilo else None
                if not iloaddr:
                    iloaddr = getattr(dc.host, 'associated_ilo', None)
            vmwhost = getattr(dc.host_detail, 'name', None)

            # csce = catalog.lookup('engines.csc_engine')
            # if iloaddr:
            #    ilopw = csce.get_password('', 'iLO', iloaddr)
            # if vmwhost:
            #    hostpw = csce.get_password('', 'ProLiant Server', vmwhost)

            if iloaddr:
                ilopw = self.find_pw_for_host_and_type(iloaddr, 'iLO')
                log.debug('ILO PASSWORD!!! %s', ilopw)
                #if not ilopw:
                 #   ilopw = self.find_global_pw('iLO')

            if vmwhost:
                hostpw = self.find_pw_for_host_and_type(vmwhost, 'ProLiant Server')
                if hostpw:
                    Host.add_provider_bundle_status(hostpw,dc.server)
                log.debug('HOST PASSWORD!!! %s', hostpw)
                #if not hostpw:
                #    hostpw = self.find_global_pw('ProLiant Server')
                
            #ilopw.get('epassword', '')
            #hostpw.get('epassword','')
            pwlist = []
            if ilopw:
                pwlist.append({'host': ilopw['host'], 'username': ilopw['username'], 'password': ilopw.get('epassword', ''), 'type': 'iLO'})
            else:
                pwlist.append({'host': iloaddr, 'username': '', 'password': '', 'type': 'iLO'})
            if hostpw:
                pwlist.append({'host': hostpw['host'], 'username': hostpw['username'], 'password': hostpw.get('epassword', ''), 'type': 'ProLiant Server','hp_bundle_status':hostpw['hp_bundle_status']})
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
                    (data.has_key('host') and pw['host'] == data['host'] and \
                    data.has_key('type') and pw['type'] == data['type']):
                return pw
        return None

    def find_pw_for_host_and_type(self, host, pwtype):
        csce = catalog.lookup('engines.csc_engine')
        pwlist = csce.get_passwords()
        log.debug('looking for password %s, %s', host, pwtype)
        for pw in pwlist:
            log.debug('Found password %s, %s', pw['host'], pw['type'])
            if (pw['type'] == pwtype and pw['host'] == host):
                return pw
        return None

    def find_global_pw(self, pwtype):
        csce = catalog.lookup('engines.csc_engine')
        pwlist = csce.get_passwords()
        for pw in pwlist:
            if (pw['type'] == pwtype and pw['host'] == '*'):
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
        log.debug("Service = %s, opera = %s, data = %s", service, oper, data)
        if service in ('password', 'vcpassword'):
            csce = catalog.lookup('engines.csc_engine')
            vce = catalog.lookup('ic4vc-vc_engine')
            if oper == 'del':
                log.debug("****Credential DEL")
                try:
                    log.debug("Dumping delete data: %s", data)
                    if 'id' in data:
                        credential.delete(credid = data['id'])
                    else:
                        credential.delete(credusername = data['username'], ip =  data['host'])
                except credential.NoSuchCredError:
                    log.debug("Traceback %s", traceback.format_exc())
                    log.error('Error deleting credential: Unable to find credential')
                    raise web.notfound('Unable to find credential.')
                '''
                pw = self.find_pw_in_cspw_list(data)
                if pw:
                    csce.del_password_from_cs(pw['id'], pw['username'], pw['type'], pw['host'])
                    if pw['type'] == 'vCenter':
                        vce.delete_vc(pw)
                else :
                    log.error('Error deleting credential: Unable to find credential')
                    raise web.notfound('Unable to find credential.')
                '''
            elif oper == 'edit':# edit DOES NOT WORK because if the username is changed it will not find the cred
                log.debug("Credential EDIT")
                try:
                    credential.change( ip=data['host'], credtype=data['type'],
                            credusername = data['username'], 
                            credpassword_new = data['password'],
                            credusername_new = data['username'], 
                            ip_new=data['host'])
                    '''
                    credential.change( ip=data['host'], credtype=data['type'],  credusername = data['username'], credpassword_new = data['password'],
                                    credusername_new = data['username'], ip_new=data['host'])
                    '''
                except credential.NoSuchCredError:
                    log.error('Error changing password: Unable to find existing credentials')
                    raise web.notfound('Unable to find credential.')
                
                '''
                pw = self.find_pw_in_cspw_list(data)  # edit DOES NOT WORK because if the username is changed it will not find the cred
                if pw:
                    if data['password'] != '****************' and data['password'].strip() != '' :
                        csce.update_password(pw['id'], data['username'], data['password'], data['type'], data['host'])
                        if pw['type'] == 'vCenter':
                            vce.edit_vc(data)
                    else:
                        log.error('Error editing credential: No Password')
                        raise web.forbidden(data='The password must be set.')
                else:
                    log.error("Error editing credential: not found")
                    raise web.notfound('Unable to find credential.')
                '''
            elif oper == 'add':
                log.debug("Credential ADD")
                try:
                    credential.add(credusername = data['username'], 
                        credtype = data['type'],ip =  data['host'], credpassword = data['password'])
                except credential.DuplicateCredError:
                    raise web.conflict('Credential already exists.')
                '''
                if data['type'] == 'HP SIM':
                    pw = self.pwtype_exists(data['type'])
                    if pw:
                        log.error("Error adding credential: Only one HP SIM credential allowed")
                        raise web.forbidden(data='Only one HP SIM entry is allowed')

                pw = self.find_pw_in_cspw_list(data)
                if not pw:
                    try :
                        csce.add_password_to_cs(data['username'], data['password'], data['type'], data['host'])
                    except:
                        log.exception("Error adding credential: Error adding credential for vCenter %s", data['host'])
                        raise web.internalerror("Error adding credential")
                else:
                    log.debug("Password is %s for data = %s", pw, data)
                    log.error("Error adding credential: Credential already exists")
                    raise web.conflict('Credential already exists.')

                if data['type'] == 'vCenter':
                    runAfterSavingConfig.append((vce.add_vc, (data,)))
                '''
            else:
                log.error("Error in editing credential: operation '%s' not supported.", oper)
                raise web.notfound('Operation Not Supported')

            if oper != 'del':
                if data['type'] == 'HP SIM':
                    runAfterSavingConfig.append((ALDeploymentConnector.create, tuple()))
                if data['type'] == 'iLO' and data['host'] != '*':
                    csce.associate_ilo(data['host'])

            # log.debug(data)
        elif service == 'config':
            if oper == 'edit':
                cfg = config()
                srvcfg = cfg.get_server_config()
                if data['param_name'] == 'HP SIM Port':
                    srvcfg['hpsim']['port'] = data['param_value']
                    runAfterSavingConfig.append((ALDeploymentConnector.create, tuple()))
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
                if data['host'] != '*' and data['password'] != '****************' and data['password'].strip() != '' :
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
                if data['password'] != '****************' and data['password'].strip() != '' and data['username'].strip() != '' :
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
                                try:
                                    csce.add_password_to_cs(data['username'], data['password'], data['type'], iloaddress)
                                except CommonServicesServerError as e:
                                    if e.status == '409':
                                        try:
                                            csce.update_password(e.response, data['username'], data['password'], data['type'], iloaddress)
                                        except:
                                            log.error("Error editing cluster properties: No iLO address for host '%s'", host.name)
                                            continue
                                creds_set += 1
                            else :
                                log.error("Error editing cluster properties: No iLO address for host '%s'", host.name)

                        if not creds_set :
                            log.error('Error editing cluster properties: No hosts set of %d.', len(dc.hosts))
                            raise web.notfound('Unable to set any hosts.')
                        if creds_set != len(dc.hosts) :
                            log.error('Error editing cluster properties: Only set %d of %d hosts.', creds_set, len(dc.hosts))
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

class sd_handler:
    def GET(self, item=None):
        # session.any_auth()
        log.debug('GET customerdata %s', item)
        sd = SupportData()
        return sd.get_support_data()

class uid_handler:
    def GET(self):
        data = 'Unknown'
        try:
            uidc = UIDControl()
            data = uidc.get_current_state()
        except:
            log.exception('Unable to get UID state')
        return json.dumps({"UID": data})

    def PUT(self):
        log.debug('uid_handler')
        try:
            uidc = UIDControl()
            try:
                uidc.start_toggle_uid()
            except:
                log.exception('Failed to toggle uid successfully, attempting to set task to failed state')
                uidc.fail_task()
        except:
            log.exception('Unable to create UIDControl object')

class pc_handler:
    def GET(self):
        data = 'Unknown'
        try:
            pc = PowerControl()
            data = pc.get_current_state()
        except:
            log.exception('Unable to get power state')

        return json.dumps({"power": data})

    def PUT(self):
        log.debug('pc_handler')
        try:
            pc = PowerControl()
            try:
                pc.start_toggle_power()
            except:
                log.exception('Failed to toggle power, attempting to set task to failed state')
                pc.fail_task()
        except:
            log.exception('Unable to create PowerControl object')

class rediscover_handler:
    def GET(self):
        data = {}
        try:
            rd = RediscoverNode()
            data = rd.get_devices()
        except:
            log.exception('Unable to get devices associated with this host')
        return json.dumps(data)

    def PUT(self):
        log.debug('rediscovering node')
        # data = json.loads(web.data())
        
        # devices = data.get('devices')
        try:
            rn = RediscoverNode()
            try:
                rn.start_rediscovery()
            except:
                log.exception('Failed to rediscover node, attempting to set task to failed state')
                rn.fail_task()
        except:
            log.exception('Unable to create RediscoverNode object')

class Test:
    def GET(self,vcguid=None):
        web.header('Content-Type', 'text/plain')
        try:
            vc = [vc for vc in catalog.get_all() if isinstance(vc, vCenter) ]#and vc.sc.about.instanceUuid.lower() == vcguid.lower()]
            vc = vc[0]
            for x in vc.retreive_host_list():
                try:
                    log.debug('Host.obj.value = %s', x.obj.value)
                except:
                    log.exception('Unable to log host itself')
                for prop in x.propSet:
                    #log.debug("props == %s, attribs = %s", prop, dir(prop))
                    if hasattr(prop, 'name') and hasattr(prop, 'val'):
                        #log.debug("Host propname = %s and propval = %s", prop.name, prop.val)
                        pass
        except:
            log.exception('error iterating over vc list')
            raise
        return urls
        #return 'Hello ' + __name__
        #        params = web.input()
        #        if hasattr(params, 'uuid'):
        #            print params.uuid
        #            guid, _type, _name = params.uuid.split(':')
        #vcs = [vc for vc in catalog.get_all() if isinstance(vc, vCenter) ]  # and vc.sc.about.instanceUuid.lower() == guid.lower()
        #print vcs
        #if len(vcs) == 1:
            #vc = vcs[0]
        #return vc.test2()
    def POST(self,vcguid=None):
        log.debug("vcguid = %s", vcguid)
        vcs = [vc.sc.about.instanceUuid.lower() for vc in catalog.get_all() if isinstance(vc, vCenter)]
        log.debug("vCenters = %s", vcs)
        csce = catalog.lookup('engines.csc_engine')
        ret = enclosure.getEnclosureList(vcguid=vcguid, csce=csce)
        return json.dumps(ret)
        

class createvctaskhandler:
    def GET(self, item=None):
        print 'Creating vcenter task'
        vc = catalog.get_all(vCenter)[0]
        print 'Got vCenter'
        vc.create_task()

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
            q = web.input()
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
        try:
            log.info("scheduler initialization ...")
            #scheduletasks.add_task_type_and_handler(Host.applyBaseline, "firmware_jobs")
            scheduletasks.add_task_type_and_handler(hostFirmwareBaseline.applyBaseline, "firmware_jobs")
            scheduletasks.init(config = config())
            #log.debug("config data is : %s",self.cfg )
        except:
            log.error("Traceback %s", traceback.format_exc())


    def startmywebapp(self):
        self.app = mywebapp(urls, globals())
        timeout = int(self.server_cfg.get('webserver', {}).get('timeout', 60))
        rqs = int(self.server_cfg.get('webserver', {}).get('req_queue', 10))
        nthreads = int(self.server_cfg.get('webserver', {}).get('num_threads', 20))
        self.app.add_processor(self.start_collection)
        self.app.run(self.cfg.get_cert_info(), self.server_address, timeout=timeout, rqs=rqs, nthreads=nthreads)

    def stop(self):
        # This is where you will need to do all the cleanup
        self.app.server.stop()
        pass


class ICSP:
    '''
    Handles Insight Control Server Provisioning jobs
    '''    
            
    class BuildJobInfo:
        def _get_vc(self, serverGuid):
            vcs = [vc for vc in catalog.get_all() if isinstance(vc, vCenter) 
                   and vc.sc.about.instanceUuid.lower() == serverGuid.lower()]
            if len(vcs) == 1:
                return vcs[0]
            else:
                return None


        def __init__(self, obj_dict):
            self.username = obj_dict['serverProvisioningHostUserName']
            self.password = obj_dict['serverProvisioningHostPassword']
            self.vcenter = self._get_vc(obj_dict['serverGuid'])
            self.vcenterfoldertype, self.vcenterfolder = \
            obj_dict['uuid'].split(':')[1:3]
            self.baseUrl = obj_dict['serverProvisioningHost']
            self.relUrl = obj_dict['serverProvisioningJobId']
            self.job_id = obj_dict['serverProvisioningJobId'].split('/')[-1]
            self.host = obj_dict['host']
            
    class AllTasks(Thread):
        def __init__(self, job_info):
            super(ICSP.AllTasks, self).__init__()
            self.job_info = job_info            

        def _actual(self):
            log.info("ICSP Monitoring job %s", self.job_info.job_id)
            then = datetime.now()
            if (not self.job_info.vcenter):
                log.error("Error no vCenter in ICSP job %s", self.job_info.job_id)
                return None
            depserv = ICSPJob(self.job_info.baseUrl)
            log.debug("ICSP job %s logging in to %s", self.job_info.job_id, self.job_info.baseUrl)
            if (depserv.login(userName=self.job_info.username,
                                 password=self.job_info.password)):                 
                # Login success
                log.debug("ICSP job %s logged in to %s", self.job_info.job_id, self.job_info.baseUrl)
                first = True
                vCenter_tasks = {}                

                
                while True:
                    now = datetime.now()
                    thread_life = now - then
                    if thread_life.seconds > 7200 :
                        log.error("ICSP expiring job %s", self.job_info.job_id)
                        break
                    sleep(15)
                    log.debug("ICSP getting status of job %s", self.job_info.job_id)
                    job_status = depserv.pingJob(self.job_info.job_id)                    
                    log.debug("ICSP job %s status %s", self.job_info.job_id, job_status)
                    progress = job_status['progress']
                    complete = job_status['complete']
                    log.debug('ICSP job %s progress %s', self.job_info.job_id, progress)
                    log.debug('ICSP job %s complete %s', self.job_info.job_id, complete)
                    log.debug('ICSP job %s jobServerInfo %s', self.job_info.job_id, job_status['jobServerInfo'])
                    
                    if first:  # now create task
                        first = False                                                
                        for key in job_status['jobServerInfo']:
                            
                            try:
                                log.debug("ICSP job %s creating vCenter task for server %s", self.job_info.job_id, key)
                                vCenter_tasks[key] = self.job_info.vcenter.createICSPTask(self.job_info.vcenterfoldertype, self.job_info.vcenterfolder)
                                for host in self.job_info.host :
                                    if host.get('jobServerUri', None) == key :
                                        description = "Deploying to " + host.get('hostIP','Proliant Server')
                                        self.job_info.vcenter.setTaskDescription(vCenter_tasks[key], description)
                            except:
                                log.exception("ICSP Error in job %s creating vCenter task for server %s", self.job_info.job_id, key)                                
                                                        
                        continue
                        
                    else :
                        
                        for key in vCenter_tasks:  # now, update                        
                            vCenter_task = vCenter_tasks[key]                        
                            if progress and key in progress:                            
                                try:
                                    log.debug("ICSP job %s updateing vCenter task for server %s to 'running'", self.job_info.job_id, key)
                                    job = progress[key]                                
                                    self.job_info.vcenter.updateICSPTask(vCenter_task, "running", job['jobCompletedSteps'], job['jobTotalSteps'])
                                except:
                                    log.exception("ICSP Error in job %s updating vCenter task for server %s to 'running'", self.job_info.job_id, key)                                                                
                                                 
                     
                            elif complete and key in complete:
                                log.debug("ICSP job %s for server %s completed", self.job_info.job_id, key)
                                uim_completion_state = "UNKNOWN"
                                try:                                    
                                    if job_status['jobServerInfo'][key][u'jobStatusOnServer'] == u'FAILURE_STATUS':
                                        message = complete[key]['jobMessage']
                                        log.error("ICSP job %s for server %s failed: %s", self.job_info.job_id, key, message)
                                        uim_completion_state = "ERROR"                                        
                                        self.job_info.vcenter.updateICSPTask(vCenter_task, "error", 1, 1, message)
                                        
                                    else:
                                        log.info("ICSP job %s for server %s succeeded with state %s", self.job_info.job_id, key, job_status['jobServerInfo'][key][u'jobStatusOnServer'])
                                        uim_completion_state = "SUCCESS"                                    
                                        self.job_info.vcenter.updateICSPTask(vCenter_task, "success", 1, 1)                                    
                                        log.info("ICSP job %s waiting for server %s to fully boot", self.job_info.job_id, key)
                                        sleep(45)                                                
                                        
                                        for host in self.job_info.host :
                                            if host.get('jobServerUri', None) == key :
                                                # ESXi can come up on a different ip
                                                # so must check with Altair to see if the server came up on a 
                                                # new IP.
                                                
                                                ip = host['hostIP']
                                                if host.get('DHCP', None) :
                                                    serverInfo = depserv.serverInfo(key)
                                                    if ip != serverInfo['managementIP'] :
                                                        log.info("ICSP job %s server %s changed ip from %s to %s", self.job_info.job_id, key, host['hostIP'], serverInfo['managementIP'])
                                                        ip = serverInfo['managementIP']
                                                target_type = self.job_info.vcenterfoldertype
                                                target_name = self.job_info.vcenterfolder
                                                host_username = host['username']
                                                host_password = host['password']
                                                log.info("ICSP job %s adding server %s at %s to %s", self.job_info.job_id, key, ip, target_name)
                                                self.job_info.vcenter.addHostTovCenter(ip, target_type, target_name, host_username, host_password)                                    
                                        
                                except :
                                    log.exception("ICSP Error in job %s completing vCenter task for server %s", self.job_info.job_id, key)
                                                            
                            else :
                                log.error("ICSP Error in job %s server %s not in progress or complete.", self.job_info.job_id, key)
                                
                        if not progress and complete:  # all jobs done
                            log.info("ICSP job %s complete", self.job_info.job_id)
                            break                    
            else :
                log.error("ICSP job %s Error logging in to %s", self.job_info.job_id, self.job_info.baseUrl)
                
            log.info("ICSP No longer Monitoring job %s", self.job_info.job_id)
            
                
        def _test(self):
            if (not self.job_info.vcenter):
                return None
            taskInfo = self.job_info.vcenter.createICSPTask(self.job_info.vcenterfoldertype,
                                                                     self.job_info.vcenterfolder)
            sleep(5)
            self.job_info.vcenter.updateICSPTask(taskInfo, "running", 5, 12)
            sleep(5)
            self.job_info.vcenter.updateICSPTask(taskInfo, "error", 5, 12)

        def run(self):
            try:
                self._actual()
                #self._test()
            except :
                log.exception("Error in ICSP Task Montior thread")
            
            

    def _getVC(self, serverGuid):
            vcs = [vc for vc in catalog.get_all() if isinstance(vc, vCenter) 
                   and vc.sc.about.instanceUuid.lower() == serverGuid.lower()]
            if len(vcs) == 1:
                return vcs[0]
            else:
                return None
                
    def _getVC2(self):
            vcs = [vc for vc in catalog.get_all() if isinstance(vc, vCenter)]
            if len(vcs) == 1:
                return vcs[0]
            else:
                return None
                
    def POST(self):
        '''
        Expects a JSON body
        '''
        log.debug("ICSP POST")
        q = web.input()
        log.debug("ICSP POST query string %s", q)
        try:
            inputdata = web.data()
            log.debug("ICSP POST Request Body %s", inputdata)
            obj_dict = json.loads(inputdata)
            job_info = ICSP.BuildJobInfo(obj_dict)
            a = ICSP.AllTasks(job_info)
            a.start()
        except:            
            log.exception('Error in ICSP POST')
            raise            
        

    def GET(self):
        
        log.debug("ICSP GET")
        q = web.input()
        log.debug("ICSP query string %s", q)
        
        try:
            params = web.input()
            if hasattr(params, 'uuid'):                
                log.debug('ICSP:GET - uuid params: %s', params.uuid)
                _guid, _type, _name = params.uuid.split(':')                
                vc = self._getVC(_guid)
                x = vc.getClusterOrDatacenterName(_type, _name)
                x = {'objRefName':x}
                return json.dumps(x)
            else:
                vc = self._getVC2()
                vc.test()
        except :            
            log.exception("Error in ICSP GET")
            raise web.notfound()

class HPOneView:
    def managed_servers_query(self, query):
        uuid_list = json.loads(query['uuids'])
        hpov_creds = credential.get(credtype='hponeview')
        uuid_truth = []
        for x in hpov_creds:
            client = get_authclient().get(x)
            if  client:
                uuid_truth.append((client, client.isServerManaged(uuid_list)))
        

    def GET(self, service):
        if (service == 'managedservers'):
            self.managed_servers_query(query=web.input()) #expecting a list of uuids

class Enclosures:
    '''
    class to handle HP enclosure requests.
    '''
    def GET(self, subservice, uuid=None):
        '''
        Handle HTTP requests to return list of enclosures.
        @param uuid: Handles on None right now. Returns a JSON object,
        as specified in enlosures.getEnclosureList
        '''
        log.debug("Enclosures GET subservice = %s UUID = %s", 
                subservice, uuid)
        q = web.input()
        try:
            serverGuid, sessionId = q.serverGuid, q.sessionId
        except AttributeError:
            raise web.badrequest()
        if subservice == 'list':
            if uuid is None:
                ret= enclosure.getEnclosureList(vcguid = serverGuid, 
                        csce = catalog.lookup('engines.csc_engine'))
                lst= [{"uuid":uuid, "name":name} for uuid, name in ret.items()]
                web.header('Content-Type', 'application/json')
                return json.dumps({"enclosures":lst})
        elif subservice == 'summary':
            assert(uuid is not None)
            try:
                web.header('Content-Type', 'application/json')
                ret =  json.dumps(enclosure.getOaSummary(vcguid = serverGuid, 
                            oa_uuid = uuid, 
                            csce = catalog.lookup('engines.csc_engine')
                        ), 
                        sort_keys=True, indent=4, separators=(',', ': '))
                log.debug("Enclosure summary GET = %s", ret)
                return ret
            except:
                ex = 'Enclosure summary failure.'
                log.exception(ex)
                raise web.internalerror(ex)
        else:
            ex = 'Enclosure summary failure. Unhandled subservice %s.' % subservice
            log.error(ex)
            raise web.internalerror(ex)

class EnclosureHosts:
    '''
    class to handle HP enclosure requests for hosts for an enclosure.
    '''
    def GET(self, subservice):
        '''
        Handle HTTP requests to return list of hosts for an enclosures.
        @param uuid: Handles on None right now. Returns a JSON object,
        as specified in enlosures.getEnclosureList
        '''
        subservice, enclosureguid = tuple(subservice.split('/'))
        log.debug("Enclosure hosts GET subservice = %s UUID = %s", 
                subservice, enclosureguid)
        q = web.input()
        try:
            serverGuid, sessionId = q.serverGuid, q.sessionId
        except AttributeError:
            raise web.badrequest()
        if subservice == 'list':
            try :
                hosts = enclosure.getEnclosureHosts(vcguid = serverGuid, enclosureguid=enclosureguid, csce = catalog.lookup('engines.csc_engine'))
            except :
                log.exception("Error getting enclosure hosts for enclosure %s", enclosureguid)
                raise
            web.header('Content-Type', 'application/json')
            return json.dumps({"hosts":hosts})
        else:
            ex = 'Enclosure hosts failure. Unhandled subservice %s.' % subservice
            log.error(ex)
            raise web.internalerror(ex)

class clusterhosts:
    def get_vc(self, serverGuid):
        vcs = [vc for vc in catalog.get_all() if isinstance(vc, vCenter) and vc.sc.about.instanceUuid.lower() == serverGuid.lower()]
        if len(vcs) == 1:
            return vcs[0]
        else:
            return None

    def get_host(self, serverGuid, moref):
        vc = self.get_vc(serverGuid)
        if not vc:
            return None

        hosts = [host for host in vc.hosts if host.moref().lower() == moref.lower()]
        if len(hosts) == 1:
            return hosts[0]
        else:
            return None

    def get_hosts(self, serverGuid, moref):
        vc = [vc for vc in catalog.get_all() if isinstance(vc, vCenter) and vc.sc.about.instanceUuid.lower() == serverGuid.lower()]
        vc = vc[0]
        clusters = vc.retreive_cluster_list()
        hosts = []
        for cluster in clusters:
            mob = cluster.obj._type + ':' + cluster.obj.value
            if mob.lower() == moref.lower():
                for prop in cluster.propSet:
                    if prop.name == 'host':
                        for host in prop.val[0]:
                            mob = host._type + ':' + host.value
                            hosts.append(self.get_host(serverGuid, mob))

        return hosts

    def GET(self):
        q = web.input()
        log.debug("clusterhosts: web input is : %s",q)
        web.header('Content-Type', 'text/javascript')
        web.http.expires(0)

        import util.catalog as catalog

        if q.moref.split(':')[0] == 'ClusterComputeResource':
            item = self.get_hosts(q.serverGuid, q.moref)

        vcguid = str(q.serverGuid).lower()
        vc = [vc for vc in catalog.get_all() if isinstance(vc, vCenter) and vc.sc.about.instanceUuid.lower() == vcguid]
        vc= vc[0]
        hosts = []
        for x in vc.retreive_host_list():
            for prop in x.propSet:
                if hasattr(prop, 'name') and prop.name == "hardware.systemInfo.uuid" and hasattr(prop, 'val'):
                    host_uuid = prop.val
                    try:
                        ovClient = Host.findOVClient(host_uuid)
                    except Exception, ex:
                        from util.exceptions import formattedException
                        info_string = formattedException(details=ex)
                    if ovClient:
                        ovVCGuid = ovClient.getServerProfilesbyUuids([host_uuid])
                        log.debug("server profile for server UUID %s = %s", host_uuid, ovVCGuid)
                        serverProfile = ovClient.getServerProfilesbyUuids([host_uuid])[host_uuid]
                        
                        if serverProfile['firmware'].has_key('firmwareBaselineUri'):
                            if serverProfile['firmware']['firmwareBaselineUri'] != '':
                                ov_currentFirmwareBaseLine = ovClient.getovBaseline(serverProfile['firmware']['firmwareBaselineUri'])['baselineShortName'] 
                            else:
                                ov_currentFirmwareBaseLine = None
                    host.append({'host_uuid':host_uuid,'currentbaseline':ov_currentFirmwareBaseLine,'vcguid':ovVCGuid})
                    break
        
        obj = {'hosts':hosts}
        
        return json.dumps(obj)    

class firmwarebaseline:
    def GET(self):
        q = web.input()
        log.debug("web input for get baseline is : %s",q)
        web.header('Content-Type', 'text/javascript')
        web.http.expires(0)
        
        hostName = q.moref.lower()
        vcguid = q.serverGuid.lower()
        temp = hostName.split(":")
        hostName = temp[1]
        #baseObj = Host.getBaseline(hostName,vcguid)
        baseObj = hostFirmwareBaseline.getBaseline(hostName,vcguid)
        #TODO: retrieve existing baselines from Fusion
        return json.dumps(baseObj)    

    def POST(self):
        web.header('Content-Type', 'text/javascript')
        web.http.expires(0)

        input = json.loads(web.data())
        q = web.input()
        vcguid = q.serverGuid.lower()
        if input.has_key('action'):            
            #retObj = Host.delScheduledTask(input)
            retObj = hostFirmwareBaseline.delScheduledTask(input)
        else:
            #retObj = Host.addScheduledTask(input,vcguid)
            retObj = hostFirmwareBaseline.addScheduledTask(input,vcguid)

        return json.dumps(retObj)
        
        
class Credentials:
    def _process_successful_login(self, client_obj, tiup):
        key = credential.add(ip = tiup['ip'] if (tiup.has_key('ip')) else tiup['host'] if (tiup.has_key('host')) else '', credtype = tiup['type'], 
                credusername = tiup['username'], 
                credpassword = tiup['password'])
        _authclnt = get_authclient()
        _authclnt.add(auth = key, client = client_obj)

    def _process_credential_query(self, query_data):
        if (query_data.has_key('username')):
            credusername = query_data['username']
        else:
            credusername = None

        if (query_data.has_key('host')):
            host = query_data['host']
        else:
            host = None
        if (query_data.has_key('type')):
            credtype = query_data['type']
        else:
            credtype = None
        all_hpovs = credential.get(credtype = credtype, 
                credusername = credusername, ip = host)
        log.debug('All existing HP One Views %s', all_hpovs)
        ret = {"pwdb":all_hpovs}
        log.debug('All existing HP One Views %s', json.dumps(ret))
        return json.dumps(ret)

    def _verify_creds(self, host, username, password, credtype):
        if (credtype == 'hponeview'):
            hp_one_view = hponeview.hponeview(host, verify=False)
            try:
                hp_one_view.login(username = username, password = password)
            except ConnectionError:
                raise credential.NoSuchHostError(host,credtype,username)
            return hp_one_view

    def add(self, obj_dict):
        try:
            ip = obj_dict['ip'] if (obj_dict.has_key('ip')) else obj_dict['host'] if (obj_dict.has_key('host')) else ''
            name = obj_dict['username']
            passwd = obj_dict['password']
            credtype = obj_dict['type']
            web.header('Content-Type', 'application/json')
            ret = self._verify_creds(host = ip, username = name, 
                    password = passwd, credtype = credtype)
            self._process_successful_login(client_obj = ret, 
                            tiup = obj_dict)
            web.ctx.status = '200'
            return #ret['auth']
        except credential.DuplicateCredError as e:
            web.ctx.status = '200'
            return #json.dumps({'auth':hp_one_view.headers['auth']})
        except credential.NoSuchHostError as e:
            web.ctx.status = '404'
            return json.dumps({'error':'Invalid host.'})
        except (ValueError, KeyError)  as ve:
            web.ctx.status = '400'
            web.header('Content-Type', 'application/json')
            log.debug("Traceback %s", traceback.format_exc())
            return json.dumps({"errormessage":"Not acceptable data format or missing information in the data.",
                    "errorcode":web.ctx.status})
        except HTTPError as he:
            log.debug("HPOneView HTTP error = %s", str(he.response))
            resp = str(he.response)
            m = re.search("<Response \[([0-9]+)\]>", resp)
            errcode = str(m.group(1))
            log.debug("HPOneView error code = %s", errcode)
            web.ctx.status = errcode
            if (errcode == '404'):
                log.debug("Traceback %s", traceback.format_exc())
                return json.dumps({"errormessage":"HP One View service not found",
                "errorcode":web.ctx.status})
            else:
                log.debug("Traceback %s", traceback.format_exc())
                return json.dumps({"errormessage":"Unknown error ",
                    "errorcode":web.ctx.status})

    def delete(self, obj_dict):
        try:
            ip = obj_dict['ip'] if (obj_dict.has_key('ip')) else obj_dict['host'] if (obj_dict.has_key('host')) else ''
            name = obj_dict['username']
            credtype = obj_dict['type']
            web.header('Content-Type', 'application/json')
            credential.delete(ip=ip, credtype=credtype, credusername = name)
            web.ctx.status = '200'
            return #json.dumps({'error':None})
        except credential.NoSuchCredError as e:
            web.ctx.status = '404'
            log.debug("Traceback %s", traceback.format_exc())
            return json.dumps({"errormessage":"No such credential",
                "errorcode":web.ctx.status})
        except (ValueError, KeyError)  as ve:
            web.ctx.status = '400'
            web.header('Content-Type', 'application/json')
            log.debug("Traceback %s", traceback.format_exc())
            return json.dumps({"errormessage":"Not acceptable data format or missing information in the data.",
                    "errorcode":web.ctx.status})

    '''
    def edit(self, obj_dict): # DO NOT USE (Legacy)
        try:
            web.header('Content-Type', 'application/json')
            assert(obj_dict['current'] != obj_dict['new'])
            ret_add = self.add(obj_dict = obj_dict['new'])
            if (web.ctx.status != '200'):
                return ret_add
            ret_del = self.delete(obj_dict = obj_dict['current'])
            if (web.ctx.status != '200'):
                self.delete(obj_dict = obj_dict['new'])
                return ret_del
            return json.dumps({"error":None})
        except AssertionError as ae:
            errmsg = "New credentials same as old"
            log.exception(errmsg + " %s", ae)
        except (ValueError, KeyError)  as ve:
            web.ctx.status = '400'
            web.header('Content-Type', 'application/json')
            log.debug("Traceback %s", traceback.format_exc())
            return json.dumps({"error":"Not acceptable data format or missing information in the data.",
                    "http_error_code":web.ctx.status})
        except HTTPError as he:
            log.debug("HPOneView HTTP error = %s", str(he.response))
            resp = str(he.response)
            m = re.search("<Response \[([0-9]+)\]>", resp)
            errcode = str(m.group(1))
            log.debug("HPOneView error code = %s", errcode)
            web.ctx.status = errcode
            if (errcode == '404'):
                log.debug("Traceback %s", traceback.format_exc())
                return json.dumps({"error":"HP One View service not found",
                "http_error_code":web.ctx.status})
            else:
                log.debug("Traceback %s", traceback.format_exc())
                return json.dumps({"error":"Unknown error ",
                    "details":hp_one_view.last_response,
                    "http_error_code":web.ctx.status})
    '''
        
    def GET(self, service=None):
        '''
        For a GET, the legitimate values for service are 'login',
        '''
        log.debug("HPOneView GET")
        '''
        Returns a JSON array of the format 
        [{"ip":"<Fusion IP1>", "username":"<username1"},
         {"ip":"<Fusion IP2>", "username":"<username2"},
         ...]
        '''
        query_data = web.input()
        web.header('Content-Type', 'application/json')
        return self._process_credential_query(query_data)

    def POST(self):
        '''
        handles POSTing to HP OneView
        :param service -  valid values are 'login'
        '''
        '''
        For login service, currently  handles ADDing, EDITing and 
        DELETE-ing creds
        the message body expected is of the following JSON object format 
        with the following fields 
        - (for adding)
        {
            "ip":"16.83.122.166",
            "username":"Administrator",
            "password":"*********",
            "action":"add"
        }
        :return : {"auth":"<auth token>"}
         - (for deleting)
        {
            "ip":"16.83.122.166",
            "username":"Administrator",
            "action":"delete"
        }
        :return : {"error":null}) upon success
                  {"error":"No such credential",
                    "http_error_code":"404"} for non-existent creds
                  {"error":"Not acceptable data format or missing information in the data.",
                "http_error_code":"400"} for bad data format or missing data in the POST body
         - (for editing) : NOTE: uim.py does not support an EDIT function
        currently
        {
            "current": {
                    "ip":"16.83.122.166",
                    "username":"Administrator"
                },
            "new": {
                "ip":"16.83.122.166",
                    "username":"Administrator",
                    "password":"*********"
            },
            "action":"edit"
        }
        :return : {"error":null}) upon success
                  {"error":"No such credential",
                    "http_error_code":"404"} for non-existent 'current' cred
                  {"error":"Not acceptable data format or missing information in the data.",
                    "http_error_code":"400"} for bad data format or missing data in the POST body
                  {"error":"HP One View service not found",
                    "http_error_code":"404"}) if HP One View service could not be contacted
         '''
        try:
            inputdata = web.data()
            log.debug("Credentials POST Request Body %s", inputdata)
            obj_dict = json.loads(inputdata)
            log.debug("Credentials POST Request Body (JSON deserialized) %s", obj_dict)
            action = getattr(self, obj_dict['action'])
            assert(obj_dict['action']== 'add' or obj_dict['action']== 'delete')
            return action(obj_dict)
        except (ValueError, KeyError, AssertionError)  as ve:
            log.debug("Traceback %s", traceback.format_exc())
            web.ctx.status = '400'
            web.header('Content-Type', 'application/json')
            return json.dumps({"error":"Not acceptable data format or missing information in the data.",
                    "http_error_code":web.ctx.status})
        except HTTPError as he:
            log.debug("HPOneView HTTP error = %s", str(he.response))
            resp = str(he.response)
            m = re.search("<Response \[([0-9]+)\]>", resp)
            errcode = str(m.group(1))
            log.debug("HPOneView error code = %s", errcode)
            web.ctx.status = errcode
            if (errcode == '404'):
                log.debug("Traceback %s", traceback.format_exc())
                return json.dumps({"error":"HP One View service not found",
                "http_error_code":web.ctx.status})
            else:
                log.debug("Traceback %s", traceback.format_exc())
                return json.dumps({"error":"Unknown error ",
                    "details":hp_one_view.last_response,
                "http_error_code":web.ctx.status})

def start():
    cfg = config()
    server_root = cfg.get_server_root()
    server_url = urlparse(server_root)
    server_address = (server_url.hostname, server_url.port)
    app = mywebapp(urls, globals())
    #TODO: ADD_TASK_HANDLER,CREATE_DB,RESCAN_DB  FROM scheule tasks should be added at  this level
    app.run(cfg.get_cert_info(), server_address)
