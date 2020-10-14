'''
Created on Mar 30, 2011

@author: islamm
#  This is the common services client engine.
#  It drives common services discovery when it starts

'''

#from hp.mgmt.event import HPEvent
#import hp.mgmt.cs_event

#from engines import misc
from threading import Thread
from time import sleep
from logging import getLogger
log = getLogger(__name__)
from util import catalog, config
from vmware.vcenter import vCenter
from core.cs import CommonServicesServerError, CommonServices
from engines.vc_engine import vc_engine
from engines.threadpool import threadpool
from core.classes import discovery_entity
from Queue import Queue
from core.uim import UIManager
from core.csevent import CSEvent

import string
import time, base64

from copy import deepcopy

from urlparse import urlparse

forwardableEvents = ['hpcs.event.wbem', 'hpcs.event.wbem', 'hpcs.event.cseries.status', 'hpcs.event.cseries.hardware']
wbemKeys = [ 'EnclosureName', 'EventThreshold', 
             'ProbableCauseDescription', 'Description', 
             'RecommendedActions', 'ActualEventThreshold',
             'Summary', 'BladeName', 'BladeBay' ]

def valid_host(host):
    if host:
        thishost = host.strip()
        if ' ' not in thishost and thishost != '0.0.0.0' and thishost != '':
            return True
    log.debug('HOST IS NOT VALID %s, SKIPPING DISCOVERY...', host)
    return False

class csc_engine:
    def __init__(self, un, pw, webcfg, decoder=None):
        self.events_enabled = True
        self.events = None
        self.csconfig = {}
        
        self.un = un
        self.pw = pw
        self.cs_addr = webcfg.address
        self.cs_proto = "http" if webcfg.useSSL == 'false' else "https"
        self.cs_port = webcfg.port
        self.cs_webcfg = webcfg
        
        self.decoder = decoder
        self.entities = {}
        self.ent_Q = Queue()
        self.de_list = {'node':{},}
        
        self.poll = Thread(name="csc_engine.csc_engine.host_poll", target=self.run)
        self.poll.daemon = True

        self.events_thread = Thread(name="csc_engine.csc_engine.events_poll", target=self.run_events_poller)
        self.events_thread.daemon = True
        
        self.uim = urlparse(config.config().get_uim_root())
        self.event_poll = int(config.config().get_server_config().get('event_poll', '60'))
        self.device_poll = int(config.config().get_server_config().get('device_poll', '300'))
        self.cs_discovery_poll = 60

        catalog.insert(self, 'engines.csc_engine')
      
    def start(self):
        self.get_cs_config()
        self.get_cs_discovery_list()
        self.create_entity_list()
        self.poll.start()
        self.events_thread.start()

    def get_cs_discovery_poll(self):
        return (self.cs_discovery_poll+30) if self.cs_discovery_poll < self.device_poll else self.device_poll
                     
    def run(self):
        while True:
            try:
                self.start_cs_discovery()
                self.cs_discovery_poll = self.get_cs_discovery_poll()
                #sleep(self.cs_discovery_poll)
                sleep(self.device_poll)
            except Exception as e:
                log.exception('Exception in run for host %s', (str(e)))

    def add_password_to_cs(self, un, pw, pwtype, host):
        cs = CommonServices(self.decode(self.un), self.decode(self.pw), self.cs_addr, self.cs_proto, self.cs_port )
        if not cs.get_password(un,pwtype, host):
            return cs.add_password(un, pw, pwtype, host)

    def del_password_from_cs(self, pwid, username, pwtype, host):
        cs = CommonServices(self.decode(self.un), self.decode(self.pw), self.cs_addr, self.cs_proto, self.cs_port )
        return cs.delete_password(pwid, username, pwtype, host) 

    def update_password(self, pwid, un, pw, pwtype, host):
        cs = CommonServices(self.decode(self.un), self.decode(self.pw), self.cs_addr, self.cs_proto, self.cs_port )
        return cs.update_password(pwid, un, pw, pwtype, host)

    def associate_ilo(self, host):
        self.ent_Q.put(discovery_entity(host, 'ilo'))

    def send_vcenter_list(self, vcenters, decode=None):
        cs = CommonServices(self.decode(self.un), self.decode(self.pw), self.cs_addr, self.cs_proto, self.cs_port )
        for vc in vcenters:
            try:
                if decode:
                    vcun = decode(vc.username)
                    vcpw = decode(vc.password)
                else:
                    vcun = vc.username
                    vcpw = vc.password
                cs.add_password(vcun, vcpw, 'vCenter', vc.ip)
            except CommonServicesServerError as e:
                if '409' == e.status :
                    cs.delete_password(id=e.response, username=vcun, type='vCenter', host=vc.ip)
                    cs.add_password(username=vcun, password=vcpw, type='vCenter', host=vc.ip)
                else:
                    log.exception('Exception adding vcenter to CS: %s', str(e))
            
    def run_events_poller(self):        
        while True:
            try:
                self.do_events()
                sleep(self.event_poll)
            except Exception as e:
                log.exception('Exception in run_events_poller %s', (str(e)))
    
    def decode(self, value):
        return self.decoder.decode(value) if self.decoder else base64.decodestring(value)
    
    def get_cs_discovery_list(self):
        cs = CommonServices(self.decode(self.un), self.decode(self.pw), self.cs_addr, self.cs_proto, self.cs_port )
        try:
            self.de_list = cs.get_discovery_engine()
            log.debug(self.de_list)
        except Exception as e:
            log.exception('Exception in getting discovery engine list, error %s', str(e))
            
    def get_cs_config(self):
        cs = CommonServices(self.decode(self.un), self.decode(self.pw), self.cs_addr, self.cs_proto, self.cs_port )
        try:
            self.csconfig = cs.get_config()
        except:
            log.exception('error getting cs_config')
    
    def configure_cs(self):
        cs_plugin_config = {'plugins': {'plugin':[
            {'_name':'hp.plugins.discovery'},
            {'_name':'hp.plugins.vcm'},
            {'_name':'hp.plugins.oa'},
            {'_name':'hp.plugins.ilo'},
            {'_name':'hp.plugins.server'},
            {'_name':'hp.plugins.tinyvcenter'}] } }
        cs = CommonServices(self.decode(self.un), self.decode(self.pw), self.cs_addr, self.cs_proto, self.cs_port )
        cs.set_config(cs_plugin_config)

    def already_discovered(self, host):
        cs = CommonServices(self.decode(self.un), self.decode(self.pw), self.cs_addr, self.cs_proto, self.cs_port )
        try:
            data = cs.discovery_status(host)
            log.debug(data)
            return data
        except Exception as e:
            log.exception('Exception %s in getting status, error %s', host, str(e))
            return False

    def get_cs_entity_list(self, entity_type):
        cs = CommonServices(self.decode(self.un), self.decode(self.pw), self.cs_addr, self.cs_proto, self.cs_port )
        data = cs.get_entity(entity_type)
        return data
        
    def get_hostinfo(self, infotype, uuid):
        cs = CommonServices(self.decode(self.un), self.decode(self.pw), self.cs_addr, self.cs_proto, self.cs_port )
        if infotype == 'OA':
            log.debug('WASHY: OA uuid = %s', uuid)
            data = cs.get_me('oa', uuid)
            #log.debug("OA data for %s = %s", uuid, data)
            return data
        if not self.valid_meid(uuid):
            return {}
        data = {}
        if infotype == 'vc':
            log.debug("this is in cscengine.py infotype is vc")
            data = cs.get_host_vc(uuid)
            log.debug("data value for vc is : %s",data);
        elif infotype == 'oa' :        
            data = cs.get_me(infotype, uuid)
            
            # Put OA syslogs in a usable format
            syslog = data['syslog'].split('\n')
            data['syslog'] = syslog
            
            # make a combined oa info, status & network structure for easier display in UI
            data['oa_info_status']=[]
            for x in range( min( len(data['oa_info']['oaInfo']), len(data['oa_status']['oaStatus']) ) ) :
                oa_is = {}
                oa_is['oa_info'] = deepcopy(data['oa_info']['oaInfo'][x])                    
                oa_is['oa_status'] = deepcopy(data['oa_status']['oaStatus'][x])
                try :
                    oa_is['oa_network'] = deepcopy(data['oa_network']['oaNetworkInfo'][x])
                except :
                    log.exception("Exception getting oa network information from index %d", x)
                    
                if not oa_is['oa_status']['bayNumber'] :    # OA sets bayNumber to 0 when module is removed - Goofy!
                    oa_is['oa_status']['bayNumber'] = x+1
                    
                if data['oa_info']['oaInfo'][x]['bayNumber'] :    # The c3000 only have one oa - skip if bay is 0
                    data['oa_info_status'].append(oa_is)
        else:
            data = cs.get_me(infotype, uuid)
        return data

    def delete_hostinfo(self, infotype, uuid, ip_address):
        if ip_address in self.entities :
            self.entites[ip_address].discovered = False
        cs = CommonServices(self.decode(self.un), self.decode(self.pw), self.cs_addr, self.cs_proto, self.cs_port )        
        cs.delete_me(infotype, uuid)

    def get_discovery_node(self, host):        
        for n in self.de_list['node']:
            if host == n['address']:
                return n
        return None
    
    def in_discovery_engine_list(self, host):
        return self.get_discovery_node(host)

    def de_node_undiscoverd(self, de_node):
        if de_node['uuid'] and de_node['type']:
                return True
        return False
    
    def get_discovery_plugin(self):
        plugins = self.csconfig.get('plugins').get('plugin', []) 
        for plugin in plugins:
            dis = plugin.get('discovery', '')
            if dis:
                return dis
        return {}

    def node_has_wrong_type(self, de_node, entity):
        entity_type = getattr(entity, 'entity_type')
        if entity_type == 'HostSystem':
            entity_type = 'server'
        return de_node.get('type') != entity_type 
        
    def cs_has_given_up(self, de_node):
        discovery_plugin = self.get_discovery_plugin()
        maxretries = discovery_plugin.get('retry', None)
        if de_node['retry'] == maxretries and self.de_node_undiscoverd(de_node):
            return True
        elif de_node['type'] == 'unknown':
            return True
        return False
            
    def create_entity_list(self):
        vce = catalog.get_all(vc_engine)
        while not vce or not vce[0].host_discovery_done():
            time.sleep(5)

        log.debug('Generating list of host to discover by CS')
        vcs = catalog.get_all(vCenter)
        for vc in vcs:
            if valid_host(vc.host):
                self.entities[vc.host]  = discovery_entity(vc.host, 'tinyvcenter')
                hosts = vc.get_hosts()
                for host in hosts:
                    log.debug('Adding host %s to entity list', host.name)
                    if valid_host(host.name):
                        self.entities[host.name]  = discovery_entity(host.name, host._type)
                        if hasattr(host, 'config') and hasattr(host.config, 'ipmi') and hasattr(host.config.ipmi, 'bmcIpAddress') and host.config.ipmi.bmcIpAddress:
                            if valid_host(host.config.ipmi.bmcIpAddress):
                                log.debug('Adding iLO %s to entity list', host.config.ipmi.bmcIpAddress)
                                self.add_password_to_cs(host.config.ipmi.login, host.config.ipmi.password, 'iLO', host.config.ipmi.bmcIpAddress)
                                self.entities[host.config.ipmi.bmcIpAddress]  = discovery_entity(host.config.ipmi.bmcIpAddress, 'ilo')

    def update_entity_list(self):
        self.get_cs_discovery_list()
        while not self.ent_Q.empty():
            ent = self.ent_Q.get()

            node_in_de = self.get_discovery_node(ent.address)            
            if (not ent.discovered and not node_in_de) or \
                    (node_in_de and self.node_has_wrong_type(node_in_de, ent)) or \
                    (node_in_de and self.cs_has_given_up(node_in_de)):

                if not ent.address in self.entities:
                    log.debug('Adding entity %s to entity list', ent.address)
                    if valid_host(ent.address):
                        self.entities[ent.address] = ent

        # Need to do the following because there may be updates
        vcs = catalog.get_all(vCenter)
        for vc in vcs:
            if not self.in_discovery_engine_list(vc.host):
                if not vc.host in self.entities: 
                    if valid_host(vc.host):
                        self.entities[vc.host]  = discovery_entity(vc.host, 'tinyvcenter')
            hosts = vc.get_hosts()
            for host in hosts:                  
                if not self.in_discovery_engine_list(host.name):
                    if not host.name in self.entities:
                        log.debug('Adding host %s to entity list', host.name)
                        if valid_host(host.name):
                            self.entities[host.name]  = discovery_entity(host.name, host._type)
                if hasattr(host, 'config') and hasattr(host.config, 'ipmi') and hasattr(host.config.ipmi, 'bmcIpAddress') and host.config.ipmi.bmcIpAddress:
                    if not self.in_discovery_engine_list(host.config.ipmi.bmcIpAddress):
                        if not host.config.ipmi.bmcIpAddress in self.entities:
                            log.debug('Adding iLO %s to entity list', host.config.ipmi.bmcIpAddress)
                            self.add_password_to_cs(host.config.ipmi.login, host.config.ipmi.password, 'iLO', host.config.ipmi.bmcIpAddress)
                            if valid_host(host.config.ipmi.bmcIpAddress):
                                self.entities[host.config.ipmi.bmcIpAddress]  = discovery_entity(host.config.ipmi.bmcIpAddress, 'ilo')
                            
            for host in hosts:
                try:
                    serverdata = self.get_hostinfo('server', getattr(host, 'uuid', None) )
                    iloaddr = serverdata.get('data',{}).get('summary',{}).get('iLOAddress', None)
                    if not iloaddr in self.entities:
                        if valid_host(iloaddr):
                            self.entities[iloaddr]  = discovery_entity(iloaddr, 'ilo')
                except:
                    log.exception('This host has not been discovered yet')
                try:
                    ilodata = self.get_hostinfo('ilo', getattr(host, 'uuid', None))
                    oa_addr = ilodata.get('data', {}).get('get_oa_info') 
                    if not oa_addr in self.entities:
                        if valid_host(oa_addr):
                            self.entities[oa_addr] = discovery_entity(oa_addr, 'oa')
                except:
                    log.exception('This ilo has not been discovered yet')
                
    def start_cs_discovery(self):
        log.debug('Starting cs discovery')
        task_q = catalog.get_all(threadpool)[0].get_q()
        self.update_entity_list()
        for key in self.entities:
            entity = self.entities[key]
            if not entity.discovered:
                task_q.put( (self.discover_entity, (entity,)) )
                time.sleep(1)

    def discover_entity(self, entity):
        if entity.entity_type == 'HostSystem':
            param = {'plugin': [{'_name':"hp.plugins.server"}]}
        elif entity.entity_type == 'ilo':
            param = {'plugin': [{'_name':"hp.plugins.ilo"}]}
        elif entity.entity_type == 'oa':
            param = {'plugin': [{'_name':"hp.plugins.oa"}]}
        elif entity.entity_type == 'vcm':
            param = {'plugin': [{'_name':"hp.plugins.vcm"}]}
        elif entity.entity_type == 'tinyvcenter':
            param = {'plugin': [{'_name':"hp.plugins.tinyvcenter"}]}
        else :
            log.error("Unhandled entity type: %s", entity.entity_type)
            return

        cs = CommonServices(self.decode(self.un), self.decode(self.pw), self.cs_addr, self.cs_proto, self.cs_port )
        try:
            log.info('Discovering entity %s', entity.address)
            data = cs.discover(entity.address, param)
            self.update_entity_Q(data)
            entity.discovered = True
            log.info('Discovered entity %s', entity.address)
        except CommonServicesServerError as e :
            log.error("Error discovering entity for %s: %s", entity.address, str(e))
        except :
            log.exception('Exception in poll for entity %s', entity.address)
    
    def update_entity_Q(self, data):               
        if data['__classname__'].endswith('Server_Entity'):
            if data.has_key('data') and data['data'].has_key('summary') and data['data']['summary'].has_key('iLOAddress'):
                ilo_address = data['data']['summary']['iLOAddress']
                log.debug("Adding iLO %s to entity queue", ilo_address)
                self.ent_Q.put( discovery_entity(ilo_address, 'ilo') )
        elif data['__classname__'].endswith( 'iLO_Entity' ):
            if data.has_key('data') and data['data'].has_key('get_oa_info'):                    
                oa_address = data['data']['get_oa_info']['ipaddress']
                log.debug("Adding OA %s to entity queue", oa_address)
                self.ent_Q.put(discovery_entity(oa_address, 'oa'))
        elif data['__classname__'].endswith( 'OA_Entity' ):            
            if data.has_key('vcm') and len(data['vcm']['vcmUrl']) and (data['vcm']['vcmUrl'] != 'empty'):
                vcm_url = urlparse(data['vcm']['vcmUrl']) 
                log.debug("Adding vcm %s to entity queue", vcm_url.hostname)
                self.ent_Q.put(discovery_entity(vcm_url.hostname, 'vcm'))
        elif data['__classname__'].endswith( 'VirtualConnect_Entity' ):
            # stop at VC entitys, nothing else to look at
            return
        elif data['__classname__'].endswith( 'TinyVCenter_Entity' ):
            # do nothing when we discover a vcenter
            return
        else :
            log.error("Unknown classname in update_entity_Q: %s", data['__classname__'])

    def get_event_no(self, which='max'):
        try :
            cs = CommonServices(self.decode(self.un), self.decode(self.pw), self.cs_addr, self.cs_proto, self.cs_port )
            events = cs.get_event('last')
        except CommonServicesServerError as e:
            log.error('Exception in get_event_no %s: %s', which, e)
            raise
            
        if which == 'min':
            return events['_min']
        return events['_max']

    def managed_host(self, uuid, name=None):
        vcs = catalog.get_all(vCenter)
        for vc in vcs:
            hosts = vc.get_hosts()
            for host in hosts:
                if host.hardware.systemInfo.uuid == uuid or host.name == name:
                    return host, vc.get_about().instanceUuid
        return None, None

    # Return value True indicates new events were retrieved
    # Return value False indicates no new events were retrieved
    def poll_events(self):
        cs = CommonServices(self.decode(self.un), self.decode(self.pw), self.cs_addr, self.cs_proto, self.cs_port )
        if self.events:
            maxev = self.get_event_no()
            if self.events['_max'] != maxev:
                try:
                    which = str(self.events['_max'] + 1)
                    self.events = cs.get_event(which)
                    return True
                except CommonServicesServerError as e:
                    self.events = None
                    log.exception('Exception in poll_events max+1 %s: %s', which, e)
        else:
            minev = self.get_event_no('min')
            try :
                self.events = cs.get_event(str(minev))
                return True
            except CommonServicesServerError as e:
                log.error('Exception in poll_events min %s: %s', str(minev), e)
        return False
    
    def valid_meid(self, id):
        return id and id.lower() not in ['0000000-0000-0000-0000-000000000000', '2e2e2e2e-2e2e-2e2e-2e2e-2e2e2e2e2e2e']

    def post_newsfeed(self, host, vcguid, message, srouce, severity, timestamp):
        obj_path = 'moref='+ host._type + ":" + host.value + "&" + 'serverGuid=' + vcguid
        try: 
            uimgr = UIManager(port=self.uim.port, protocol=self.uim.scheme)
            uimgr.post_event(obj_path, evobj.get_message(), evobj.detail_source(), evobj.severity(), evobj.timestamp())
        except Exception as err:
            log.exception('Error posting event to ui manager, %s', err)

    def do_events(self):
        if self.events_enabled:
            try:
                if self.poll_events():
                    for ev in self.events['meEvent']:
                        log.debug('Received Event: eventID: %s eventName: %s', ev['eventID'], ev['eventName'])
                        if ev['eventName'] == 'hpcs.event.EntityDestroyed':
                            continue
                        if ev['eventName'] == 'hpcs.event.ConnectionLost':
                            log.debug('Connection lost to ip=%s, entity=%s, id=%s', str(ev['eventSourceIP']), str(ev['eventReferencedDataModel']), str(ev['eventReferencedManagedElementID']))
                        
                        entity = None
                        try:
                            entity = self.get_hostinfo(ev['eventReferencedDataModel'], ev['eventReferencedManagedElementID'])
                        except:
                            log.exception('Unable to get entity information, may be the entity is not discovered yet')
                            # May be we need to post a newsfeed
                            try:
                                host, vcguid = self.managed_host(ev['eventReferencedManagedElementID'])
                                if host and vcguid:
                                    self.post_newsfeed(host, vcguid, ev['eventText'])
                                else:
                                    log.debug("Unable to post newsfeed because Host or vCenter UUID is None for event %s", str(ev))
                            except:
                                log.exception('Unable to post newsfeed')
                        
                        if not entity:
                            log.error('No entity found for event: ' + str(ev))
                            continue
                        evt = CSEvent.create(ev, entity)
                        if evt:
                            if entity['__classname__'].endswith('OA_Entity'):
                                for uuid in evt.get_affected_blades():
                                    host, vcguid = self.managed_host(uuid)
                                    if host:
                                        self.forward_event(host, vcguid, evt)
                                        self.force_update_entity(ev['eventReferencedDataModel'], ev['eventReferencedManagedElementID'])
                            else:
                                host, vcguid = self.managed_host(evt.entity_uuid(), evt.source())
                                if host:
                                    self.forward_event(host, vcguid, evt)
                                    self.force_update_entity(ev['eventReferencedDataModel'], ev['eventReferencedManagedElementID'])
            except Exception:               
                log.exception('Exception in do_event: ' + str(ev['eventReferencedManagedElementID']))

    def fix_kvpairs(self,ev):
        for kv in ev['keyValuePairs']['keyValuePair']:
            if kv['value'] and not isinstance(kv['value'], str): 
                kv['value'] = str(kv['value'])
            if kv['value']:
                kv['value'] = kv['value'] if all(c in string.printable for c in kv['value']) else 'non-printable'

    def create_event_detail_string(self, ev):
        detail = []
        
        self.fix_kvpairs(ev)
        
        if ev['eventName'].startswith('hpcs.event.snmp'):
            for kv in ev['keyValuePairs']['keyValuePair']:
                if kv['value'].startswith(''):
                    s  = '%s(%s)=%s' %(kv['altkey'].strip(), kv['key'].strip(), kv['value'].strip())
                    detail.append(s)
        elif ev['eventName'].startswith('hpcs.event.wbem'):
            for kv in ev['keyValuePairs']['keyValuePair']:
                if kv['key'] in wbemKeys and kv['value']:
                    s = '%s=%s' %(kv['key'].strip(), kv['value'].strip())
                    detail.append(s)
        return ', '.join(detail)

    def post_newsfeed(self, host, vcguid, msg ):
        obj_path = 'moref='+ host._type + ":" + host.value + "&" + 'serverGuid=' + vcguid
        try: 

            uimgr = UIManager(port=self.uim.port, protocol=self.uim.scheme)
            uimgr.post_event(obj_path, msg, host.name, 'INFORMATION', time.time())
        except Exception as err:
            log.exception('Error posting event to ui manager, %s', err)
    
    def forward_event(self, host, vcguid, evobj):
        obj_path = 'moref='+ host._type + ":" + host.value + "&" + 'serverGuid=' + vcguid
        try: 

            uimgr = UIManager(port=self.uim.port, protocol=self.uim.scheme)
            uimgr.post_event(obj_path, evobj.get_message(), evobj.detail_source(), evobj.severity(), evobj.timestamp())
        except Exception as err:
            log.exception('Error posting event to ui manager, %s', err)
        
        try:
            if (evobj.forwardable()): 
                vcs = catalog.get_all(vCenter)
                for vc in vcs:
                    if vc.has_host(host.moref()):
                        vc.log_event(host, evobj)
        except Exception as err:
            log.exception('Error logging event to vCenter, %s', err)
    
    def stop(self):
        self.event_poll = 0
        self.device_poll = 0
        
    def get_passwords(self):
        cs = CommonServices(self.decode(self.un), self.decode(self.pw), self.cs_addr, self.cs_proto, self.cs_port )
        return cs.get_passwords()

    def get_password(self, un, pwtype, host):
        cs = CommonServices(self.decode(self.un), self.decode(self.pw), self.cs_addr, self.cs_proto, self.cs_port )
        return cs.get_password(un, pwtype, host)
    
    def entity_control(self, me_name, me_id, device, cmd):
        cs = CommonServices(self.decode(self.un), self.decode(self.pw), self.cs_addr, self.cs_proto, self.cs_port )
        return cs.entity_control(me_name, me_id, device, cmd)
    
    def force_update_entity(self, me_name, me_id):
        log.debug('force_update_entity')
        cs = CommonServices(self.decode(self.un), self.decode(self.pw), self.cs_addr, self.cs_proto, self.cs_port )
        return cs.updatenow(me_name, me_id)

    def delete_entity(self, me_name, me_id):
        log.debug('deleting entity...')
        cs = CommonServices(self.decode(self.un), self.decode(self.pw), self.cs_addr, self.cs_proto, self.cs_port )
        return cs.delete_me(me_name, me_id)
        
