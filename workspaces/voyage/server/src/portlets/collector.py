'''
Created on Oct 25, 2011

@author: IslamM
'''

from engines import vc_engine 
from engines.csc_engine import csc_engine
from core.cs import CommonServicesServerError
from util import catalog
import time
from portlets import get_webinput
import threading
from util import auth
from util import config
import uuid

from logging import getLogger
log = getLogger(__name__)

c_lock = threading.Lock()
cc_lock = threading.Lock()

class Collector:
    def __init__(self, moref, serverGuid, sessionID):
        self.moref, self.serverGuid, self.sessionId = moref, serverGuid, sessionID
        self.host_detail = None
        self.ds = None
        self.dvpg = None 
        self.vdvsw = None
        self.vms = []
        self.server = None
        self.ilo = None 
        self.oa = None
        self.vcm = None
        self.host = None
        
        self.vcntr = vc_engine.get_vcenter(self.moref)
        if self.vcntr:
            self.host = self.vcntr.get_obj(self.moref)
            
        self.csce = catalog.get_all(csc_engine)[0]        

        self.time_stamp = 0
        self.lastaccess = 0

    def initialize(self):
        if not self.vcntr:
            self.vcntr = vc_engine.get_vcenter(self.moref)
            if self.vcntr:
                self.host = self.vcntr.get_obj(self.moref)
            self.time_stamp = 0
            self.lastaccess = 0

    def ilo_address(self) :
    
        address = None
        if self.server :
            address = self.server.get('data', {}).get('summary',{}).get('iLOAddress', None)            
        if self.ilo and not address :
            address = self.ilo.get('address', {}).get('ipv4', None)            
        if self.host and not address:
            if hasattr(self.host,'associated_ilo') :
                address = self.host.associated_ilo
            
        return address
        
    def collect_host_detail(self, force):   
        if not self.vcntr :
            self.vcntr = vc_engine.get_vcenter(self.moref)
        if self.vcntr :
            self.host_detail = self.vcntr.get_host_details(self.moref, force)
    
    def collect_ds(self):        
        if not self.vcntr :
            self.vcntr = vc_engine.get_vcenter(self.moref)
        if self.vcntr :
            self.ds = self.vcntr.get_ds_for_host(self.moref)

    def collect_dvpg(self):          
        if not self.vcntr :
            self.vcntr = vc_engine.get_vcenter(self.moref)
        if self.vcntr :
            self.dvpg = self.vcntr.get_dvpg()

    def collect_vdvsw(self):         
        if not self.vcntr :
            self.vcntr = vc_engine.get_vcenter(self.moref)
        if self.vcntr :
            self.vdvsw = self.vcntr.get_vdvsw()

    def collect_vms(self):        
        if not self.vcntr :
            self.vcntr = vc_engine.get_vcenter(self.moref)
        if self.vcntr :
            self.vms = self.vcntr.get_vms_for_host(self.moref)

    def collect_vcenter_data_vms_and_ds(self, force=False):
        if not self.vms or force:
            self.collect_vms()        
        if not self.ds or force:
            self.collect_ds()
        
    def collect_vcenter_data(self, force=False): 
        if not self.host_detail or force:
            self.collect_host_detail(force)
            taskQ = catalog.lookup('ic4vc-server-threadpool').get_q()
            taskQ.put( (self.collect_vcenter_data_ex, (force,) ))

    def collect_vcenter_data_ex(self, force=False):     
        if not self.dvpg or force:
            self.collect_dvpg()
        if not self.vdvsw or force:
            self.collect_vdvsw()

        taskQ = catalog.lookup('ic4vc-server-threadpool').get_q()
        taskQ.put( (self.collect_vcenter_data_vms_and_ds, (force,) ))
                        
    def collect_server(self):
        try:
            self.server = self.csce.get_hostinfo('server', self.host.hardware.systemInfo.uuid)
        except CommonServicesServerError as e :
            log.error("Error collecting server_entity for %s: %s", self.moref, str(e))
        except:
            log.exception('error getting server_entity for %s', self.moref)
            
        if self.server and self.server.get('status', 'disabled') == 'disabled':
            self.csce.force_update_entity('server', self.host.hardware.systemInfo.uuid)

    def collect_ilo(self):
        try:
            self.ilo = self.csce.get_hostinfo('ilo', self.host.hardware.systemInfo.uuid)
        except CommonServicesServerError as e :
            if e.status == '408' :  #CS isn't talking to this entity anymore and it should be rediscovered
                log.error("Lost communication with ilo_entity for %s - deleting", self.moref)
                try :
                    self.csce.delete_hostinfo('ilo', self.host.hardware.systemInfo.uuid, self.ilo_address())
                except :
                    log.exception("Error deleting ilo_entity for %s %s", self.moref, self.host.hardware.systemInfo.uuid)
                self.ilo = None
            else :
                log.error("Error collecting ilo_entity for %s: %s", self.moref, str(e))
        except:
            log.exception('error getting ilo_entity for %s', self.moref)

        if self.ilo: 
            if (self.ilo.get('status', 'disabled') == 'disabled') or \
                    (self.ilo.get('data', {}).get('get_oa_info', None) and \
                     (self.ilo.get('containedby', {}).get('uuid', None)) == None):
                self.csce.force_update_entity('ilo', self.host.hardware.systemInfo.uuid)

    def collect_oa(self):    
        if not self.ilo :
            log.error("Error collecting oa_entity.  No iLO for %s", self.moref)
            return
        oa_uuid = None
        try:        
            containedby = self.ilo.get('containedby', {}).get('uuid', [])
            if containedby:
                oa_uuid = containedby[0]
                self.oa = self.csce.get_hostinfo('oa', oa_uuid)                
        except:
            log.exception('error getting oa_entity for %s', self.moref)

        if self.oa:
            if self.oa.get('status', 'disabled') == 'disabled' and oa_uuid:
                self.csce.force_update_entity('oa', self.host.hardware.systemInfo.uuid)

            
    def collect_vcm(self):             
        try:            
            if self.oa and self.oa.has_key('vcm') and len(self.oa['vcm']['vcmUrl']):                
                self.vcm = self.csce.get_hostinfo('vc', self.host.hardware.systemInfo.uuid)
        except:
            log.exception('error getting vc_entity for %s', self.moref)
        
        if self.vcm: 
            if self.vcm.get('status', 'disabled')   == 'disabled':
                vcd_uuid = uuid.UUID(self.vcm.get('domain'))
                self.csce.force_update_entity('vcdomain', vcd_uuid)

    def collect_server_and_ilo_from_cs(self, force=False):
        if not self.server or force: 
            self.collect_server()
        if not self.ilo or force: 
            self.collect_ilo()

    def collect_cs_data(self, force=False):        
        if not self.oa or force: 
            self.collect_oa()
        if not self.vcm or force: 
            self.collect_vcm()        
        
    def update(self):
        force = False
        if not self.vcntr:
            self.initialize()
        self.vcntr.update()
        poll_interval = int(config.config().get_server_config().get('device_poll', '300'))      
        et = time.time() - self.time_stamp
        if et > poll_interval:
            force = True
        self.collect_vcenter_data(force)
        self.collect_server_and_ilo_from_cs(force)
        taskQ = catalog.lookup('ic4vc-server-threadpool').get_q()
        taskQ.put( (self.collect_cs_data, (force,) ))
        
        if force:
            self.time_stamp = time.time()

    def delayed_update(self, sec, updatecs = None, updatevcenter = None):
        time.sleep(sec)
        if updatecs:
            self.collect_cs_data(True)
        if updatevcenter:
            self.collect_vcenter_data(True)
    
    @staticmethod
    def get_collector():
        moref, serverGuid, sessionId = get_webinput()
        #log.debug(" in get_collector of collector \n moref:%s \n serverGuid : %s \n sessionId : %s\n", moref, serverGuid, sessionId)
        with c_lock: 
            datacollector = catalog.lookup('%s:%s' %(sessionId, moref) )
            #log.debug("the dataCollector value from catalog lookup is : %s ",datacollector) 
            if not datacollector:
                datacollector = Collector(moref, serverGuid, sessionId)
                datacollector.role = auth.get_effective_role(sessionId, moref, serverGuid)
                catalog.insert(datacollector, '%s:%s' %(sessionId, moref) )
                datacollector.update()
            else:
                poll_interval = int(config.config().get_server_config().get('device_poll', '300'))      
                et = time.time() - datacollector.time_stamp
                if et > poll_interval:
                    taskQ = catalog.lookup('ic4vc-server-threadpool').get_q()
                    taskQ.put( (datacollector.update, tuple() ))
                                
            datacollector.lastaccess = time.time()
        return datacollector

class ClusterCollector():
    def __init__(self, moref, serverGuid, sessionID):
        self.moref, self.serverGuid, self.sessionId = moref, serverGuid, sessionID
        
        self.host_uuids = []
        self.hosts = []        
        self.servers = {}
        self.ilos = {}
        self.host_details = {}
        self.oas = []
        self.time_stamp = 0 # Forces update the first time
        self.lastaccess = 0
        
        self.vcntr = vc_engine.get_vcenter_for_obj(self.moref)
        if self.vcntr :
            self.cluster = self.vcntr.get_obj(self.moref)
        self.csce = catalog.get_all(csc_engine)[0]
    
    def initialize(self):
        self.time_stamp = 0 # Forces update the first time
        self.lastaccess = 0
        
        self.vcntr = vc_engine.get_vcenter_for_obj(self.moref)
        if self.vcntr :
            self.cluster = self.vcntr.get_obj(self.moref)

    def get_host(self, uuid) :
        for host in self.hosts :
            if uuid == host.hardware.systemInfo.uuid :
                return host
                
        return None
        
    def ilo_address(self, uuid) :
        
        host = self.get_host(uuid)
        server = self.servers.get(uuid, None)
        ilo = self.ilos.get(uuid, None)
    
        address = None
        if server :
            address = server.get('data', {}).get('summary',{}).get('iLOAddress', None)            
        if ilo and not address :
            address = ilo.get('address', {}).get('ipv4', None)            
        if host and not address:
            if hasattr(host,'associated_ilo') :
                address = host.associated_ilo
                
        return address
        
    def build_host_list(self) :
        self.hosts = []
        self.host_uuids = []
        if not self.vcntr :
            self.vcntr = vc_engine.get_vcenter_for_obj(self.moref)
        
        if self.cluster.host and self.vcntr :
            for h in self.cluster.host.ManagedObjectReference:
                host = self.vcntr.get_obj('%s:%s' %(h._type, h.value))
                if host:
                    self.hosts.append(host)
                    if host.hardware.systemInfo.uuid not in self.host_uuids:
                        self.host_uuids.append(host.hardware.systemInfo.uuid)

    def oa_in_list(self, uuid):
        luuid = uuid.lower()
        for oa in self.oas:
            if luuid == oa['uuid'].lower() :
                return True
        return False

    def build_oa_list(self) :
        oa_uuids = []
        for h in self.hosts:
            try:
                ilo = self.csce.get_hostinfo('ilo', h.hardware.systemInfo.uuid)
                
                for uuid in ilo['containedby']['uuid']: 
                    if uuid not in oa_uuids:
                        oa_uuids.append(uuid)

            except CommonServicesServerError as e :
                log.error("Error getting iLO_entity for %s: %s", h.hardware.systemInfo.uuid, str(e))
            except:
                log.exception('error getting iLO_entity for %s', h.hardware.systemInfo.uuid)
                        
        for uuid in oa_uuids:
            if not self.oa_in_list(uuid):
                try :
                    oa = self.csce.get_hostinfo('oa', uuid)
                    if oa:
                        self.oas.append(oa)
                except CommonServicesServerError as e :
                    log.error("Error getting oa_entity for %s: %s", uuid, str(e))
                except:
                    log.exception('error getting oa_entity for %s', uuid)
                        
                        
    def collect_host_details(self) :   
        self.host_details = {}
        for host in self.hosts :
            try :
                log.debug('Collecting cluster host_detail %s %s', host.hardware.systemInfo.uuid, host.moref())
                self.host_details[host.hardware.systemInfo.uuid] = self.vcntr.get_host_details(host.moref())
            except:
                log.exception('error getting cluster host_detail %s', host.hardware.systemInfo.uuid)        
                
    def collect_servers(self) :
        self.servers = {}   # Clear the old data - servers may have been removed from the cluster
        log.debug("Cluster: %s, host count: %d", self.cluster.name, len(self.hosts))
        for host in self.hosts :            
            try:
                log.debug('Collecting cluster server_entity %s', host.hardware.systemInfo.uuid)
                self.servers[host.hardware.systemInfo.uuid] = self.csce.get_hostinfo('server', host.hardware.systemInfo.uuid)
            except CommonServicesServerError as e :
                    log.error("Error getting cluster server_entity for %s: %s", host.hardware.systemInfo.uuid, str(e))
            except:
                log.exception('error getting cluster server_entity %s', host.hardware.systemInfo.uuid)

    def collect_ilos(self):        
        self.ilos = {}
        for host in self.hosts :
            try:
                log.debug('Collecting cluster iLO %s', host.hardware.systemInfo.uuid)
                self.ilos[host.hardware.systemInfo.uuid] = self.csce.get_hostinfo('ilo', host.hardware.systemInfo.uuid)
            except CommonServicesServerError as e :
                    log.error("Error getting cluster iLO_entity for %s: %s", host.hardware.systemInfo.uuid, str(e))
            except:
                log.exception('error getting cluster ilo_entity %s', host.hardware.systemInfo.uuid)

    def collect_vcenter_data(self, force) :
        if not self.hosts or force :
            self.build_host_list()
            taskQ = catalog.lookup('ic4vc-server-threadpool').get_q()
            taskQ.put( (self.collect_host_details, () ))            
                
    def collect_cs_data(self, force):
        if force:
            taskQ = catalog.lookup('ic4vc-server-threadpool').get_q()
            taskQ.put( (self.build_oa_list, () ))            
            taskQ.put( (self.collect_servers, () ))            
            taskQ.put( (self.collect_ilos, () ))            

    def cluster_changed(self, newcluster):
        if self.cluster and newcluster:
            if self.cluster.name != newcluster.name:
                return True
            elif self.cluster.host and newcluster.host: 
                if len(newcluster.host.ManagedObjectReference) != len(self.cluster.host.ManagedObjectReference):
                    return True
            elif self.cluster.host or newcluster.host:
                return True
        else:
            return True
        return False
            
    def update(self):
        force = False
        if not self.vcntr:
            self.initialize()

        if not self.vcntr :
            log.error("Unable to initialize vCenter")
            return

        self.vcntr.update()
        cluster = self.vcntr.get_obj(self.moref)
        if self.cluster_changed(cluster):
            log.debug('Forcing update ...')
            self.cluster = self.vcntr.get_obj(self.moref)
            force = True

        poll_interval = int(config.config().get_server_config().get('device_poll', '300'))      
        et = time.time() - self.time_stamp
        if et > poll_interval:
            force = True
        self.collect_vcenter_data(force)

        taskQ = catalog.lookup('ic4vc-server-threadpool').get_q()
        taskQ.put( (self.collect_cs_data, (force,) ))
        if force:
            self.time_stamp = time.time()

    @staticmethod
    def get_collector():
        moref, serverGuid, sessionId = get_webinput()
        with cc_lock :
            datacollector = catalog.lookup('%s:%s' %(sessionId, moref) )
            if not datacollector:
                datacollector = ClusterCollector(moref, serverGuid, sessionId)
                datacollector.role = auth.get_effective_role(sessionId, moref, serverGuid)
                catalog.insert(datacollector, '%s:%s' %(sessionId, moref) )
                datacollector.update()
            else:
                poll_interval = int(config.config().get_server_config().get('device_poll', '300'))      
                et = time.time() - datacollector.time_stamp
                if et > poll_interval:
                    datacollector.update()                    

            datacollector.lastaccess = time.time()
        return datacollector

removecollectorlock = threading.Lock()
def remove_old_collectors():
    with removecollectorlock:
        collectors = catalog.get_all(Collector)
        ccollectors = catalog.get_all(ClusterCollector)
        #poll_interval = int(config.config().get_server_config().get('device_poll', '300'))      
    
        for c in collectors:
            et_since_lastaccess = time.time() - c.lastaccess
            if et_since_lastaccess > 900:
                log.debug('Removing old collector...')
                catalog.remove(c)
    
        for c in ccollectors:
            et_since_lastaccess = time.time() - c.lastaccess
            if et_since_lastaccess > 900:
                log.debug('Removing old cluster collector...')
                catalog.remove(c)

def remove_collector(key):
    #print key
    with removecollectorlock:
        c = catalog.lookup(key)
        #print c
        catalog.remove(c)
