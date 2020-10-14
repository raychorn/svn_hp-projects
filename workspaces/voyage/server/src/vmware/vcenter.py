#####################################################
#
# vcenter.py
#
# Copyright 2011 Hewlett-Packard Development Company, L.P.
#
# Hewlett-Packard and the Hewlett-Packard logo are trademarks of
# Hewlett-Packard Development Company, L.P. in the U.S. and/or other countries.
#
# Confidential computer software. Valid license from Hewlett-Packard required
# for possession, use or copying. Consistent with FAR 12.211 and 12.212,
# Commercial Computer Software, Computer Software Documentation, and Technical
# Data for Commercial Items are licensed to the U.S. Government under
# vendor's standard commercial license.
#
# Author:
#    Mohammed M. Islam
# 
# Description:
#    Classes for accessing the VMWare Web Services
#
#####################################################

import logging
log = logging.getLogger(__name__)

import suds
from suds.client import Client
from suds.sax.parser import Parser
from suds.umx.basic import Basic as bumx
import httplib2, urllib2
#Done to allow testing via: python vcenter.py -t vcenter -u user -p pass
if __name__ == '__main__':
    import sys
    sys.path.append('../')
    
from util import catalog

from util.resource import resource
from vmware import vmwobj

_PREFIX = 'ns0'

from uuid import UUID
import base64
from suds.sax.element import Element, Attribute
from suds.plugin import MessagePlugin
from suds.sudsobject import Object
from threading import Thread, Lock
import time
from M2Crypto import SSL

# from engines.deployment_connector import ALDeploymentConnector

def KeyAnyValue(key, value, xsitype):
    '''
    Generate the XML for a KeyAnyValue as defined in VMware's WSDL.
    This is necessary because SUDS apparently can't construct a proper
    KeyAnyValue because the 'value' part is typed as xsd:any
    '''
    
    kav = Element('arguments')
    kav.append(Element('key').setText(key))
    val = Element('value').setText(str(value))

    attr = Attribute('type', xsitype)
    attr.prefix = 'xsi'
    val.append(attr)
    kav.append(val)

    return kav

class AddAttr(MessagePlugin):
    def __init__(self, nodename, attrname, attrvalue):
        self.nodename = nodename
        self.attrname = attrname
        self.attrvalue = attrvalue
        
    def addAttributeForValue(self, node):
        if node.name == self.nodename:
            node.set(self.attrname, self.attrvalue)
            
    def marshalled(self, context):
        context.envelope.walk(self.addAttributeForValue)

class ManagedObjectRef(suds.sudsobject.Property):
#    ManagedObjectRef Class.  The suds SOAP library can not handle xml 
#    having an element that starts with "_".  Managed Object Reference has
    """Custom class to replace the suds generated class, which lacks _type."""
    def __init__(self, _type, value):
        suds.sudsobject.Property.__init__(self, value)
        self._type = _type
        
class vCenter:    
    service_reference = ManagedObjectRef('ServiceInstance', 'ServiceInstance')

    prop_set_host_minimal = ['name', 'hardware.systemInfo.uuid']
    prop_set_host_detials = ['overallStatus', 'hardware', 'name', 'config.ipmi', 'config.network', 'config.product',
                'config.storageDevice.hostBusAdapter', 'config.storageDevice.multipathInfo.lun',
                'vm']

    prop_set_vm = ['name' , 'config.uuid', 'config.hardware' ]
    prop_set_dvportgroup = ['name', 'configStatus', 'config', 'host']
    prop_set_cluster = ['name', 'configStatus', 'host']
    prop_set_datastore = ['name', 'info', 'host']
    prop_set_dvswitch = ['name', 'configStatus', 'config']
    
    def __init__(self, host, wsdl=None, decoder=None, eventing_enable=True):
        self.soapsessions = {}
        self.initializing = True
        self.host = host
        self.url = 'https://%s/sdk' % host
        if not wsdl:
            wsdl = 'file://' + resource('vmware/wsdl41/vimService.wsdl')
        self.wsdl = wsdl
        self.hosts = []
        self.clusters = []
        self.lastlistupdatetime = 0
        self.updateduration = 0
        self.session = None
        self.sc = None
        self.decoder = decoder
        self.host_discovery_done = False
        self.eventing_enable = eventing_enable
        self.last_event_key = 0
        self.standby_log = {}
        self.running = False
        log.debug("vCenter __init__ host: %s", host)

        self.host_update_lock = Lock()
        self.cluster_update_lock = Lock()
        self.retrieve_properties_lock = Lock()
        self.retrieve_list_lock = Lock()
        self.vcenterupdatelock = Lock()
        self.sudslock = Lock()
        self.get_vms_for_host_lock = Lock()
        self.get_ds_for_host_lock = Lock()
        
    def decode(self, value):
        return self.decoder.decode(value) if self.decoder else base64.decodestring(value)

    def get_api_versions(self):
        "Queries the VCenter for it's current version and list of prior versions in vim25"
        log.debug('Getting VCenter Version')
        try :
            http = httplib2.Http()
            http.disable_ssl_certificate_validation = True
            rawdata = http.request('https://%s/sdk/vimServiceVersions.xml' % (self.host), method="GET")
            xml = Parser().parse(string=rawdata[1]).root()
            data = bumx().process(xml)
            ver = []
            for ns in data.namespace:
                if hasattr(ns, 'priorVersions'):
                    ver += ns.priorVersions.version
            self.prior_versions = ver
        except Exception as e :
            log.exception("Failed to parse VCenter version: ", str(e))
        
    def setup(self):
        self.client = Client(self.wsdl)        
        self.client.set_options(location=self.url)
        self.client.set_options(timeout=36000)
        self.vim = self.client.service
        self.entire_datacenter_trav_spec = self.build_trav_spec()
        self.initializing = False
        if self.eventing_enable :
            self.eventSetup()

    def eventSetup(self) :
        '''Setup and start the thread to wait for vCenter events'''
        
        self.thread = Thread(name='vCenter.events:%s' % self.host, target=self.wait_for_events)
        self.thread.daemon = True
        self.thread.start()

    def eventfilter(self):
        evfilter = self.factory('EventFilterSpec')
        evfilter.alarm = None  # Looks like optional params need to be set to None so that they are not included in the SOAP request
        evfilter.scheduledTask = None
        evfilter.eventTypeId += [
            'vim.event.EnteredStandbyModeEvent',
            'vim.event.DrsEnteredStandbyModeEvent',
            'vim.event.ExitedStandbyModeEvent',
            'vim.event.DrsExitedStandbyModeEvent',
        ]        

        with self.sudslock:
            evcollector = self.vim.CreateCollectorForEvents(self.sc.eventManager, evfilter)
            self.vim.SetCollectorPageSize(evcollector, 100)

        ps = self.factory('PropertySpec')
        ps.all = False
        ps.type = evcollector._type
        ps.pathSet = [ 'latestPage' ]

        obj = self.factory('ObjectSpec')
        obj.obj = evcollector
        obj.skip = False

        pf = self.factory('PropertyFilterSpec')
        pf.propSet = [ps]
        pf.objectSet = [obj]
        return pf        
        
    def wait_for_events(self) :
        '''Wait for events from vCenter'''
        
        filter = None
        version = None
        
        self.running = True
        while self.running :
            time.sleep(60)
            log.debug("Waiting for vCenter Events")
            if not self.connected():
                log.debug("vCenter %s not connected skipping wait for events", self.host)
                time.sleep(30)
                continue
                
            if not filter:
                try:           
                    log.debug("Building Event Filter")
                    ef = self.eventfilter()           
                    log.debug("Calling CreateFilter")
                    with self.sudslock:
                        filter = self.vim.CreateFilter(self.sc.propertyCollector, ef, False)
                    log.debug("Created new event filter: %s", filter)
                except :
                    log.exception('Error creating event filter')
                    self.comm_error()
                    time.sleep(30)
                    continue
            
            try:                
                log.debug("WaitForUpdates: version=%s", version)
                with self.sudslock:
                    updates = self.vim.CheckForUpdates(self.sc.propertyCollector, version)
            # except urllib2.URLError:
            #    log.error('urllib2.URLError in self.vim.WaitForUpdates')
            #    continue
            except Exception:
                self.comm_error()
                log.exception('Exception waiting for updates')
                filter = None
                version = None                
                continue
            
            if updates :    
                version = updates.version
                for update in updates.filterSet:
                    for obj in update.objectSet:
                        self.process_update(obj)                            
            
        self.running = False

    def process_events(self, content):
        '''
        Process events from vCenter
        '''
        try:
            evlist = content.changeSet[0].val.Event
            evlist.reverse()
        except AttributeError:
            # If the event list is empty, content.changeSet[0].val will
            # be a Text node with a value of ""
            evlist = []

        for event in evlist:
            if event.key <= self.last_event_key:
                continue
            self.last_event_key = event.key
            event_name = event.__class__.__name__
            log.debug('Received vCenter Event %s', event_name)
            
            # Look up the morefs in the event:
            # example:
            #
            # (EventEx){
            #   host = (HostEventArgument) {
            #       name = "foobar"
            #       host = (ManagedObjectReference) {
            #           value = "host-1234"
            #           _type = "HostSystem"
            #       }
            #   }
            # }
            #
            # So, the moref is event.thing.thing.
            
            # Look for events related to hosts     could be any of ('datacenter', 'computeResource', 'host', 'vm', 'ds', 'net', 'dvs')
            r = getattr(event, 'host', None)        
            if r:
                r = getattr(r, 'host', None)
                if r:  
                    moref = '%s:%s' % (r._type, r.value)
                    log.debug("Received Event %s for host %s", event_name, moref)
                    host = self.get_host(moref)
                    if not host :
                        log.error('Error processing event %s for host %s: Host not found', event_name, moref)
                        continue
                    if 'EnteredStandbyMode' in event_name :
                        self.standby_log.setdefault(moref, {})['entered_standby_mode'] = event.createdTime
                        self.standby_log.setdefault(moref, {})['exited_standby_mode'] = None
                        log.debug("Event Host %s entered standby mode at %s", host.name, event.createdTime)
                    elif 'ExitedStandbyMode' in event_name :
                        self.standby_log.setdefault(moref, {})['exited_standby_mode'] = event.createdTime
                        log.debug("Event Host %s exited standby mode at %s", host.name, event.createdTime)
                    

                        
    def process_update(self, content):
        '''
        Process updates from VCenter
        '''
        log.debug("Processing vCenter Updates type: %s", content.obj._type)
        if content.obj._type == 'EventHistoryCollector':
            return self.process_events(content)

            
    def factory(self, name, prefix=_PREFIX):
        '''
        Create and instance of an object named in the WSDL

        @type name: string
        @param name: Name of the object to create.
        @type prefix: string
        @param prefix: The namespace in which the object resides.
        @rtype: object
        @return: The newly created object
        '''
        name = "%s:%s" % (prefix, name)
        return self.client.factory.create(name)
        
    def login(self, username, password) :
        self.username = username
        self.password = password
        log.debug("logging in to vCenter %s", self.host)
        try:
            with self.sudslock:
                self.sc = self.vim.RetrieveServiceContent(self.service_reference)
                self.session = self.vim.Login(self.sc.sessionManager, self.decode(username), self.decode(password))
        except:
            log.exception('Error in login')
            self.comm_error()

    def connect(self):
        log.debug('In connect...')
        try:
            if not self.sc:
                log.debug('Logging on to the vCenter...')
                with self.sudslock:
                    self.sc = self.vim.RetrieveServiceContent(self.service_reference)
                    self.session = self.vim.Login(self.sc.sessionManager, self.decode(self.username), self.decode(self.password))
        except Exception, e:
            log.exception("Error connecting to vCenter %s", self.host)
            self.comm_error(e)

    def connected(self):
        return self.session != None

    def keep_alive(self):
        log.debug('In keep_alive...')
        try:
            if not self.connected():
                log.debug('Re-connecting session')
                self.comm_error()
                self.connect()
            if self.connected():
                log.debug('keep_alive() calling update')
                self.update()
            # ext = self.vim.FindExtension(self.sc.extensionManager, 'com.hp.ic4vc')
            # log.debug('Extension found: %s', str(ext['key']))
        except Exception, e:
            log.exception('Exception connecting to vCenter %s in keep_alive', self.host)
            self.comm_error(e)

    def get_about(self):
        return self.sc.about
    
    def comm_error(self, e=None):        
        self.session = None
        self.sc = None
        self.client.options.transport.cookiejar.clear()        

    def trav_spec(self, name, _type, path, skip=False, selectset=[]):
        t = self.factory('TraversalSpec')
        t.name, t.type, t.path, t.skip = name, _type, path, skip
        for name in selectset:
            ss = self.factory('SelectionSpec')
            ss.name = name
            t.selectSet.append(ss)
        return t

    def prop_spec(self, _type, pathset, _all=False):
        p = self.factory('PropertySpec')
        p.all, p.type, p.pathSet = _all, _type, pathset
        return p
    
    def build_trav_spec(self):
        rp_vm = self.trav_spec('rpToVm', 'ResourcePool', 'vm')
        rp_rp = self.trav_spec('rpToRp', 'ResourcePool', 'resourcePool', selectset=['rpToVm', 'rpToRp'])
        cr_rp = self.trav_spec('crToRp', 'ComputeResource', 'resourcePool', selectset=['rpToVm', 'rpToRp'])
        cr_host = self.trav_spec('crToH', 'ComputeResource', 'host')
        cc_host = self.trav_spec('ccToH', 'ClusterComputeResource', 'host')
        dc_hf = self.trav_spec('dcToHf', 'Datacenter', 'hostFolder', selectset=['visitFolders'])
        dc_vmf = self.trav_spec('dcToVmf', 'Datacenter', 'vmFolder', selectset=['visitFolders'])
        dc_nwf = self.trav_spec('dcToNwf', 'Datacenter', 'networkFolder', selectset=['visitFolders'])
        dc_ds = self.trav_spec('dcToDs', 'Datacenter', 'datastore', selectset=['visitFolders'])
        host_vm = self.trav_spec('HToVm', 'HostSystem', 'vm', selectset=['visitFolders'])
        vf = self.trav_spec('visitFolders', 'Folder', 'childEntity', selectset=[
                'visitFolders',
                'dcToHf',
                'dcToVmf',
                'dcToNwf',
                'dcToDs',
                'crToH',
                'ccToH',
                'crToRp',
                'HToVm',
                'rpToVm',
                ])
        return [vf,
                dc_vmf,
                dc_hf,
                dc_nwf,
                dc_ds,
                cr_host,
                cc_host,
                cr_rp,
                rp_rp,
                host_vm,
                rp_vm
                ]

    def propfilter(self, ManagedEntityType, ManagedEntityProporties, startType=None, startValue=None):
        '''
        Create a property filter for collecting all the ManagedEntityType in the vcenter
        '''
        
        obj_spec = self.factory('ObjectSpec')
        obj_spec.skip = False
        if not startType:
            obj_spec.obj = self.sc.rootFolder
            ts = self.entire_datacenter_trav_spec
            obj_spec.selectSet = ts
        else:
            obj_spec.obj = ManagedObjectRef(startType, startValue)
            
        
        prop_filter_spec = self.factory('PropertyFilterSpec')
        prop_filter_spec.objectSet = [obj_spec]

        prop_set = []
        prop_set.append(self.prop_spec(ManagedEntityType, ManagedEntityProporties))
        prop_filter_spec.propSet = prop_set

        return prop_filter_spec
    
    def retrieve_list(self, ManagedEntityType, ManagedEntityProporties):
        with self.retrieve_list_lock:
            content = []
            pf = self.propfilter(ManagedEntityType, ManagedEntityProporties)
            try:
                with self.sudslock:
                    content = self.vim.RetrieveProperties(self.sc.propertyCollector, [pf])
            except:
                self.comm_error()
                log.exception('Exception in retrive_list')
            return content

    def retrieve_properties_incontext(self, session, ManagedEntityType, ManagedEntityProporties, startObjType, startObjValue):
        handler = urllib2.HTTPCookieProcessor()
        handler.cookiejar.set_cookie(self.mkcookie('vmware_soap_session', session))

        # Now, create a SOAP client based off a previously created SOAP client
        # (ie: we don't want to parse the WSDL again), and set the transport
        thisclient = self.client.clone()
        thisclient.set_options(location=self.url)
        thisclient.options.transport.urlopener = urllib2.build_opener(handler)
        sc = thisclient.service.RetriveProperties(self.service_reference)
        
        pf = self.propfilter(ManagedEntityType, ManagedEntityProporties, startObjType, startObjValue)
        
        content = thisclient.service.RetrieveProperties(sc, [pf])
        obj = vmwobj.factory(content[0].obj)
        obj.setprop(content[0].propSet)
        try:
            obj.hardware.systemInfo.uuid = str(UUID(bytes=UUID(obj.hardware.systemInfo.uuid).bytes_le))
        except:
            log.error('Error processing vcenter UUID')
        return obj

    def retrieve_properties(self, ManagedEntityType, ManagedEntityProporties, startObjType=None, startObjValue=None):
        with self.retrieve_properties_lock:
            content = None
            pf = self.propfilter(ManagedEntityType, ManagedEntityProporties, startObjType, startObjValue)
            try:
                with self.sudslock:
                    content = self.vim.RetrieveProperties(self.sc.propertyCollector, [pf])
            except:
                log.exception('Error in retrieve_properties')
                self.comm_error()
            if content:
                try:
                    obj = vmwobj.factory(content[0].obj)
                    obj.setprop(content[0].propSet)
                    return obj
                except:
                    log.exception("propSet failed for %s:%s", startObjType, startObjValue)
                    return None
        return None    
                
    def retreive_host_list(self):
        # prop_set_minimal=['name', 'hardware.systemInfo.uuid', 'config.ipmi']
        content = self.retrieve_list(vmwobj.host.mo_type, vmwobj.host.pset_minimal)
        return content

    def retreive_cluster_list(self):
        content = self.retrieve_list(vmwobj.cluster.mo_type, vmwobj.cluster.pset_detail)
        return content

    def retrieve_datacenter_list(self):
        content = self.retrieve_list(vmwobj.datacenter.mo_type, vmwobj.datacenter.pset_detail)
        return content

    def retrieve_dc_host_folder(self, dc_val):
        content = None
        pf = self.propfilter('Datacenter', ['hostFolder'], 'Datacenter', dc_val)
        try:
            with self.sudslock:
                content = self.vim.RetrieveProperties(self.sc.propertyCollector, [pf])
        except:
            log.exception('Error in retrieve_dc_host_folder')
            self.comm_error()
        if content:
            return content[0].propSet[0].val
        return None


    def get_ds_for_host(self, moref):
        with self.get_ds_for_host_lock:
            host = self.get_host(moref)
            dslist = []
            if hasattr(host, 'datastore') and hasattr(host.datastore, 'ManagedObjectReference') and isinstance(host.datastore.ManagedObjectReference, list):
                for ds_moref in host.datastore.ManagedObjectReference:
                    if vmwobj.ds.mo_type and ds_moref.value:
                        content = self.retrieve_properties(vmwobj.ds.mo_type, vmwobj.ds.pset_detail, vmwobj.ds.mo_type, ds_moref.value)
                        if content:
                            dslist.append(content)
            return dslist
    
    def get_dvpg(self):
        dvpgs = []
        content = self.retrieve_list(vmwobj.dvpg.mo_type, vmwobj.dvpg.pset_detail)
        for dvpg in content :
            obj = vmwobj.factory(dvpg.obj)        
            obj.setprop(dvpg.propSet)
            dvpgs.append(obj)
        return dvpgs
    
    def get_vdvsw(self):
        vdvsws = []
        content = self.retrieve_list(vmwobj.vdvsw.mo_type, vmwobj.vdvsw.pset_detail)
        for s in content :
            obj = vmwobj.factory(s.obj)        
            obj.setprop(s.propSet)
            vdvsws.append(obj)
        return vdvsws

    def get_vms_for_host(self, moref):
        with self.get_vms_for_host_lock:
            host = self.get_host(moref)
            vmlist = []
            if hasattr(host, 'vm') and hasattr(host.vm, 'ManagedObjectReference') and isinstance(host.vm.ManagedObjectReference, list):
                for vm_moref in host.vm.ManagedObjectReference:
                    if vmwobj.vm.mo_type and vm_moref.value:
                        content = self.retrieve_properties(vmwobj.vm.mo_type, vmwobj.vm.pset_detail, vmwobj.vm.mo_type, vm_moref.value)
                        if content:
                            vmlist.append(content)
            return vmlist

    def get_host_list(self):
        hostlist = []
        hosts = self.retreive_host_list()
        for host in hosts:
            h = vmwobj.factory(host.obj)
            h.setprop(host.propSet)
            hostlist.append(h)
        return hostlist

    def update_host_list(self):
        temphostlist = self.get_host_list()
        with self.host_update_lock:
            self.hosts = temphostlist
            
    def generate_host_list(self):
        with self.host_update_lock:
            self.hosts = self.get_host_list()
            self.host_discovery_done = True

    def get_cluster_list(self):
        clusters = self.retreive_cluster_list()
        clusterlist = []
        for cluster in clusters:
            c = vmwobj.factory(cluster.obj)
            c.setprop(cluster.propSet)
            clusterlist.append(c)
        return clusterlist
    
    def update_cluster_list(self):
        tempclusterlist = self.get_cluster_list()
        with self.cluster_update_lock:
            self.clusters = tempclusterlist
    
    def get_dc_list(self):
        dcs = self.retrieve_datacenter_list()
        dclist = []
        for dc in dcs:
            c = vmwobj.factory(dc.obj)
            c.setprop(dc.propSet)
            dclist.append(c)
        return dclist
    
    def update(self):
        try:
            st = time.time()
            log.debug('Updating...time=%s', time.time())
            if not self.connected():
                log.debug('Re-connecting session...')
                self.comm_error()
                self.connect()

            if self.connected():
                log.debug('Connected, getting vcenterupdatelock...')
                with self.vcenterupdatelock:
                    listupdate_et = time.time() - self.lastlistupdatetime 
                    if (listupdate_et) > (self.updateduration * 30):
                        log.debug('Elapsed time is too long. Updating, elapsetime = %s, updateduration = %s', listupdate_et, self.updateduration)
                        self.update_host_list()
                        self.update_cluster_list()
                        self.lastlistupdatetime = time.time()
                        updur = time.time() - st
                        updur = updur if updur else 1
                        self.updateduration = 30 if updur > 30 else updur
                log.debug('updated, vcenterupdatelock released...')
            else:
                log.error('Unable to connect to vCenter: %s', self.host)
            log.debug('Updated...time=%s', time.time())
        except:
            log.exception('Error updating vCenter information')

    def generate_cluster_list(self):
        with self.cluster_update_lock:
            self.clusters = self.get_cluster_list()
        
    def get_host_details(self, moref, force=False):
        content = catalog.lookup(moref)
        if not content or force:
            try:
                content = self.retrieve_properties(vmwobj.host.mo_type, vmwobj.host.pset_detail, vmwobj.host.mo_type, moref.split(':')[-1])
                catalog.insert(content, moref)
            except Exception as e:
                log.exception("error in vcenter %s", str(e)) 
                self.comm_error(e)
        return content
    
    def has_host(self, host):
        with self.host_update_lock:
            for h in self.hosts:
                if h.moref() == host:
                    return True
        return False

    def has_obj(self, obj):
        items = []
        if obj.startswith('HostSystem'):
            with self.host_update_lock:
                items = self.hosts
        elif obj.startswith('ClusterComputeResource'):
            with self.cluster_update_lock:
                items = self.clusters
            
        for item in items :
            if item.moref() == obj:
                return True
        return False

    def get_obj(self, obj):
        items = []
        if obj.startswith('HostSystem'):
            with self.host_update_lock:
                items = self.hosts
        elif obj.startswith('ClusterComputeResource'):
            with self.cluster_update_lock:
                items = self.clusters
        for item in items:
            if item.moref() == obj:
                return item
        return None
    
    def get_hosts(self):
        thislist = []
        with self.host_update_lock:
            thislist = self.hosts
        return thislist
    
    def get_host(self, moref):
        with self.host_update_lock:
            for h in self.hosts:
                if h.moref() == moref:
                    return h
        return None

    def arglist(self, kvpairs):
        args = []
        for arg in kvpairs:
            obj = Object()
            obj.key = arg['key']
            obj.value = arg['value']
            args.append(obj)
        return args
        
    def log_event(self, mob, event):
        ev = self.factory('EventEx')
        if mob._type == 'HostSystem':
            ev.host = self.factory('HostEventArgument')
            ev.host.host = ManagedObjectRef(mob._type, mob.value)
            ev.host.name = mob.value
        elif mob._type == 'VirtualMachine':
            ev.vm = self.factory('VmEventArgument')
            ev.vm.vm = ManagedObjectRef(mob._type, mob.value)
            ev.vm.name = mob.value
        elif mob._type == 'DataCenter':
            ev.datacenter = self.factory('DataCenterEventArgument')
            ev.datacenter.datacenter = ManagedObjectRef(mob._type, mob.value)
            ev.datacenter.name = mob.value
        else:
            log.debug("failed log_event(%s, %s)", mob, event.message())
        # ev.message = message
        ev.userName = "HP Insight Management"
        ev.eventTypeId = event.event_type_id()
        with self.sudslock:
            ev.createdTime = self.vim.CurrentTime(self.service_reference)
        ev.arguments = self.arglist(event.get_args())
        self.client.set_options(plugins=[AddAttr('value', 'xsi:type', 'xsd:string')])
        try:
            with self.sudslock:
                self.vim.PostEvent(self.sc.eventManager, ev)
        except:
            self.comm_error()
            log.exception('Error in log event')
        self.client.set_options(plugins=[])

    def create_task(self):
        self.datacenters = self.retrieve_datacenter_list()        
        moref = ManagedObjectRef('Datacenter', 'datacenter-2')
        with self.sudslock:
            vcTask = self.vim.CreateTask(self.sc.taskManager, moref, 'com.hp.insight.server.deploy', 'Administrator', True)
        return vcTask

    def createFirmwareUpdateTask(self, hostMoref, username=None):
        '''
        @param hostMoref: a vmware moref for the host for which the task is to be added
        @param username: a string representing the user who started the task.
        @returns a vCenter Task object on success
        ''' 
        with self.sudslock:
            taskid = self.vim.CreateTask(self.sc.taskManager, hostMoref,
                   "com.hp.insight.server.FirmwareUpdateTask",username, False)
        return taskid
        
    def updateFirmwareUpdateTask(self, taskInfo, status, percentComplete,
                        message=None):
        '''
        updates a vCenter task, given a status code
        @param taskInfo: the object returned by #createFirmwareUpdateTask
        @param 
        '''
        log.debug("Updating Server Deployment vCenter task to %s", status)
        statusInfo = self.factory('TaskInfoState')
        currStatus = None
        assert(status in ['running','error','success'])
        if status == 'running':
            currStatus = statusInfo.running
        elif status == 'error':
            currStatus = statusInfo.error
        elif status == 'success':
            currStatus = statusInfo.success
        locMethodFault = None
        log.debug('status = %s', currStatus)
        if taskInfo.state != currStatus: #change of status, update task
            if currStatus == statusInfo.success:
                log.debug("Updating task status to success")
                with self.sudslock:
                    self.vim.UpdateProgress(taskInfo.task, 100)
            elif currStatus == statusInfo.error:
                log.debug('Updating task status to error')
                if not message :
                    message = "Error occurred while updating firmware for host"
                lm = self.factory('LocalizableMessage')
                lm.message = message
                lm.key = 'com.hp.insight.server.FaultDescription'  
                extendedFault = self.factory('ExtendedFault')
                extendedFault.faultTypeId = "com.hp.insight.server.FirmwareUpdateFault"
                extendedFault.faultMessage = lm
                locMethodFault = self.factory('LocalizedMethodFault')
                locMethodFault.localizedMessage = message
                locMethodFault.fault = extendedFault

            with self.sudslock:
                ret = self.vim.SetTaskState(taskInfo.task, currStatus, None,
                                            locMethodFault) 
        if currStatus == statusInfo.running:
            with self.sudslock:
                self.vim.UpdateProgress(taskInfo.task, percentComplete)
        return ret
            
    def createICSPTask(self, target_type, target_name, username=None):
        '''
        returns a vCenter Task ID on success
        '''        
        
        log.info('creating Server Deployment vCenter task for %s, %s', target_type, target_name)        
        log.debug('Getting moref')
        moref = self.getClusterOrDatacenterMoref(target_type, target_name)
        log.debug('moref %s', moref)
        task = None
        try :
            with self.sudslock:
                task = self.vim.CreateTask(self.sc.taskManager, moref,
                                       'com.hp.insight.server.Deploy',
                                       username, False)
        
        except:
            log.exception('Error creating Server Deployment vCenter task for %s %s', target_type, target_name)
            
        return task

    def updateICSPTask(self, taskInfo, status, jobResultCompletedSteps,
                        jobResultTotalSteps, message=None):
        '''
        updates a vCenter task, given a status code
        '''
        log.info("Updating Server Deployment vCenter task to %s", status)
        statusInfo = self.factory('TaskInfoState')
        currStatus = None
        if status == 'running':
            currStatus = statusInfo.running
        elif status == 'error':
            currStatus = statusInfo.error
        elif status == 'success':
            currStatus = statusInfo.success
        locMethodFault = None
        log.debug('Before set task')
        log.debug('currStatus %s', currStatus)
        if taskInfo.state != currStatus: #change of status, update task
            if currStatus == statusInfo.success:
                log.debug("updating task status to success")
                with self.sudslock:
                    self.vim.UpdateProgress(taskInfo.task, 100)
            elif currStatus == statusInfo.error:
                log.debug('updating task status to error')
                if not message :
                    message = "Error occurred while deploying ESXi image on a HP ProLiant server"
                lm = self.factory('LocalizableMessage')
                lm.message = message
                lm.key = 'com.hp.insight.server.FaultDescription'  #Fake key just to make it happy. Shouldnt be needed when message is used instead of a localized message
                extendedFault = self.factory('ExtendedFault')
                extendedFault.faultTypeId = "com.hp.insight.server.DeployFault"
                extendedFault.faultMessage = lm
                locMethodFault = self.factory('LocalizedMethodFault')
                locMethodFault.localizedMessage = message
                locMethodFault.fault = extendedFault

            with self.sudslock:
                ret = self.vim.SetTaskState(taskInfo.task, currStatus, None, locMethodFault)
                
        log.debug('After set task')
        if currStatus == statusInfo.running:
            percentComplete = 0
            if jobResultTotalSteps :
                percentComplete = int(float(jobResultCompletedSteps) / float(jobResultTotalSteps) * 100)
            with self.sudslock:
                reply = self.vim.UpdateProgress(taskInfo.task, percentComplete)
            
            
        return ret

    def setTaskDescription(self, taskInfo, description) :
        "Updates a task's Description (Details) in the server's locale"
        
        log.info("Updating vCenter task description to '%s'", description)
        try :
            lm = self.factory('LocalizableMessage')
            lm.key = 'com.hp.insight.server.TaskDescription'  #Fake key just to make it happy. Shouldnt be needed when message is used instead of a localized message
            lm.message = description
            with self.sudslock:
                self.vim.SetTaskDescription(taskInfo.task, lm) 
        except :
            log.exception("Error setting vCenter task description")
        
        
    def _getHostConnectSpec(self, host, thumbprint, username, password):
        try:            
            spec = self.client.factory.create("ns0:HostConnectSpec")
            spec.userName = username
            spec.password = password
            spec.hostName = host
            delattr(spec, 'vmFolder')
            
            # vCenter credentials
            spec.vimAccountName = self.decode(self.username)
            spec.vimAccountPassword = self.decode(self.password)
            spec.force = True
            spec.sslThumbprint = thumbprint
    
            return spec
        except :
            log.exception("Error getting host connect spec")
            raise
        
    def _getClusterMob(self, name):
        clusters = self.retreive_cluster_list()
        log.debug("retrieved clusters %s", clusters)
        for cluster in clusters:
            try:
                if cluster.obj._type == 'ClusterComputeResource' and cluster.obj.value == name :
                    log.debug("Found cluster named %s", name)
                    return cluster.obj
                #for prop in cluster.propSet:
                #    if prop.name == 'ClusterComputeResource' and prop.val == name:
                #        log.debug("Found cluster named %s", name)
                #        return cluster.obj
            except:
                log.exception("Error getting cluster mob")
        
        log.error("Error no cluster named %s", name)
        return None

    def _getDatacenterMob(self, name):
        datacenters = self.retrieve_datacenter_list()
        log.debug("retrieved datacenters %s", datacenters)
        for dc in datacenters:
            try:                
                if dc.obj._type == 'Datacenter' and dc.obj.value == name:
                    dc_host_folder = self.retrieve_dc_host_folder(dc.obj.value)
                    log.debug("Found datacenter named %s: %s", name, dc_host_folder)
                    return dc_host_folder
            except:
                log.exception('Error iterating throught the propSet')

        return None

    def getClusterOrDatacenterName(self, target_type, target_name):        
        dc = None
        if target_type == 'Datacenter':
            dc_list = self.retrieve_datacenter_list()            
            dc = [dc for dc in dc_list if dc.obj.value == target_name]
        elif target_type == 'ClusterComputeResource':
            cluster_list = self.retreive_cluster_list()            
            dc = [dc for dc in cluster_list if dc.obj.value == target_name]
        if dc:
            for x in dc[0].propSet:
                if (hasattr(x,'name') and x.name == 'name'):
                    return x.val
            
        return None
        
    def getClusterOrDatacenterMoref(self, target_type, target_name):
        mob = None
        moref = None        
        if target_type == 'Datacenter':
            mob = self._getDatacenterMob(target_name)                
            if mob:
                moref = ManagedObjectRef(mob._type, mob.value)
            else :
                log.error("Error getting Datacenter mob")
                                    
        elif target_type == 'ClusterComputeResource':
            mob = self._getClusterMob(target_name)                
            if mob:
                moref = ManagedObjectRef(mob._type, mob.value)
            else :
                log.error("Error getting cluster mob")
        else :
            log.error("Error unknown moref target type %s", target_type)
        
        return moref

    def _getHostThumbprint(self, host, port=443, md='sha1'):
        try:
            ssl = SSL.Connection(SSL.Context())
            ssl.postConnectionCheck = None
            
            try:
                ssl.socket.settimeout(None)
                ssl.connect((host, port))
            except :
                log.exception('Unable to connect to host %s', host)
                return ""
            
            try:
                fp = ssl.get_peer_cert().get_fingerprint(md)
            except :
                log.exception('Unable to get certificate and fingerprint %s', host)
                ssl.close()
                return ""
            
            # Got certification without exception, now close the connection
            ssl.close()

            # Sometimes the leading zero is dropped which causes an Odd-length string exception.
            # When this happens we'll pad the beginning of the string with a zero.

            if len(fp) > 0 and len(fp) % 2 != 0:
                fp = '0' + fp

            # Return the fingerprint as colon separated hex digits
            return ':'.join(['%02x' % ord(x) for x in fp.decode('hex')])
        except :
            log.exception("Exception processing thumbprint for host %s", host)
            return ""


    def addHostTovCenter(self, host, target_type, target_name, host_username, host_password):
        try:
            log.info("Adding host %s to vCenter at target_type: %s, target_name: %s", host, target_type, target_name)
            log.debug("Getting host thumbprint")
            thumbprint = self._getHostThumbprint(host)
            log.debug("Getting host connect spec")
            spec = self._getHostConnectSpec(host, thumbprint, 
                                            host_username, 
                                            host_password)
            log.debug("host connect spec: %s", spec)
            log.debug("Getting moref for target")
            moref = self.getClusterOrDatacenterMoref(target_type, target_name)
            log.debug("moref: %s", moref)
            if target_type == 'Datacenter':
                ret = None
                with self.sudslock:
                    ret = self.vim.AddStandaloneHost_Task(moref, spec, None, True)
                return ret
            elif target_type == 'ClusterComputeResource':
                ret = None
                with self.sudslock:
                    ret = self.vim.AddHost_Task(moref, spec, True)
                return ret
            else:
                log.error("Unknown target_type '%s'", target_type)
                return None
        except:
            log.exception("Error adding host %s to vCenter at target_type: %s target_name: %s", host, target_type, target_name)
            raise
        
        return None
        
    def putHostInMaintenanceMode(host):
        pass

    def exitHostFromMaintenanceMode(host):
        pass
        
    def get_all_sesssions(self):
        smgr = self.factory('ObjectSpec')
        smgr.obj = self.sc.sessionManager
        smgr.skip = False
        ps = self.factory('PropertySpec')
        ps.all = False
        ps.type = 'SessionManager'
        ps.pathSet = ['sessionList']

        pf = self.factory('PropertyFilterSpec')
        pf.objectSet = [ smgr ]
        pf.propSet = [ ps ]
        try:
            with self.sudslock:
                content = self.vim.RetrieveProperties(self.sc.propertyCollector, [pf])[0]

            log.debug("content = %s", content)
            if 'propSet' in content:
                self.soapsessions[sessionid] = content.propSet[0].val
                self.soapsessions[sessionid].update_time = time.time()
            else:
                if sessionid in self.soapsessions:
                    del self.soapsessions[sessionid]
        except:
            log.exception("Error getting sessions")
    
    def get_current_session_username(self, sessionid):
        username = ''
        smgr = self.factory('ObjectSpec')
        smgr.obj = self.sc.sessionManager
        smgr.skip = False
        ps = self.factory('PropertySpec')
        ps.all = False
        ps.type = 'SessionManager'
        ps.pathSet = ['currentSession']

        pf = self.factory('PropertyFilterSpec')
        pf.objectSet = [ smgr ]
        pf.propSet = [ ps ]
        try:
            with self.sudslock:
                content = self.vim.RetrieveProperties(self.sc.propertyCollector, [pf])[0]
            log.debug("content = %s", content)
            if 'propSet' in content:
                self.soapsessions[sessionid] = content.propSet[0].val
                self.soapsessions[sessionid].update_time = time.time()
                sess = self.soapsessions.get(sessionid)
                if sess:
                    username = sess.userName
            else:
                if sessionid in self.soapsessions:
                    del self.soapsessions[sessionid]
        except:
            log.exception("Error getting sessions")
        return username
    
    def check_valid_session(self, session_key):
        pass   
    
    def test(self):   
        log.info('Test')
        icsp_task = self.createICSPTask('Datacenter', 'datacenter-21') 
        time.sleep(3)
        self.updateICSPTask(icsp_task, 'running', 5, 17)
        time.sleep(3)
        self.updateICSPTask(icsp_task, 'running', 6, 17)
        time.sleep(3)
        self.updateICSPTask(icsp_task, 'success', 0, 1)
        return icsp_task

    def test2(self):
        return self.addHostTovCenter('16.83.122.221', 'Datacenter', 'datacenter-21', 
                                     'root', 'hpinvent')
if __name__ == '__main__':
    def time_function_call(f, *args):
        import time
        t0 = time.time()
        val = f(*args)
        tdiff = time.time() - t0
        return tdiff, val
    
    import time
    import sys
    from util import cmdline_opt 
    import logging    
    log.setLevel(logging.DEBUG)

    ch = logging.StreamHandler()
    ch.setLevel(logging.DEBUG)
    log.addHandler(ch)
    
    
    target, username, password = cmdline_opt.vcenter()
    print target, username, password
    t0 = time.time()
    wsdl = 'file://' + resource('../../../share/vmware/wsdl41/vimService.wsdl')    
    vc = vCenter(target, wsdl)
    tdiff = time.time() - t0
    print "vCenter object initialization time: " + str(tdiff)
    
    et, ret = time_function_call(vc.setup)
    print "Parsing wsdl time: " + str(et)
    

    et, ret = time_function_call(vc.login, base64.encodestring(username), base64.encodestring(password)) 
    print "vCenter login time: " + str(et)

    #print dir(vc)
    
    #moref = vc.getClusterOrDatacenterMoref('ClusterComputeResource', 'domain-c110')
    moref = vc.getClusterOrDatacenterMoref('Datacenter', 'datacenter-21')
    print 'moref=', moref
    moref = vc.getClusterOrDatacenterMoref('ClusterComputeResource', 'domain-c110')
    print 'moref=', moref
    
    sys.exit()
    
    vc.generate_cluster_list()
    task = vc.createICSPTask('datacenter-21') 
    print task
    # sys.exit()
    t0 = time.time()
    content = vc.retrieve_host_list()
    tdiff = time.time() - t0
    print "retrieve host list time: " + str(tdiff)
    print content

    prop_set_minimal = ['name', 'hardware.systemInfo.uuid']
    prop_set_used_in_brek = ['overallStatus', 'hardware', 'name', 'config.ipmi', 'config.network', 'config.product',
                'config.storageDevice.hostBusAdapter', 'config.storageDevice.multipathInfo.lun',
                'vm']

    prop_set_details = ['overallStatus', 'hardware', 'name', 'config', 'vm']
    
    prop_set_vm = ['name' , 'config.uuid', 'config.hardware' ]
    prop_set_dvportgroup = ['name', 'configStatus', 'config', 'host']
    prop_set_cluster = ['name', 'configStatus', 'host']
    prop_set_datastore = ['name', 'info', 'host']
    prop_set_dvswitch = ['name', 'configStatus', 'config']

#    t0 = time.time()
#    content = vc.retrieve_list('HostSystem', prop_set_minimal)
#    tdiff = time.time() - t0
#    print "retrieve host list time: " + str(tdiff)
#    print content

    # vc.generate_host_list()
    et, hosts = time_function_call(vc.generate_host_list)
    print "getting host minimal: " + str(et)
    

#    et, hosts = time_function_call(vc.retrieve_list, 'HostSystem', prop_set_minimal)
#    print "getting host minimal: " + str(et)
#    print hosts
#
#    #et, hosts = time_function_call(vc.retrieve_properties, 'HostSystem', prop_set_used_in_brek, 'HostSystem', 'host-146')
#    #print "getting  prop_set_used_in_brek: " + str(et)
#    
#    et, dvports = time_function_call(vc.retrieve_list, 'DistributedVirtualPortgroup', prop_set_dvportgroup)
#    print "getting  prop_set_dvportgroup: " + str(et)
#
#    et, clusters = time_function_call(vc.retrieve_list, 'ClusterComputeResource', prop_set_cluster)
#    print "getting  prop_set_cluster: " + str(et)
#    et, datastores = time_function_call(vc.retrieve_list, 'Datastore', prop_set_datastore)
#    print "getting  prop_set_datastore: " + str(et)
#    et, dvswitches = time_function_call(vc.retrieve_list, 'VmwareDistributedVirtualSwitch', prop_set_dvswitch)
#    print "getting  prop_set_dvswitch: " + str(et)

    # et, vms_for_host = time_function_call(vc.retrieve_properties, 'VirtualMachine', prop_set_vm, 'VirtualMachine', 'vm-148')
    # print "getting vm list for host: " + str(et)

    # print vms_for_host
#    et, content = time_function_call(vc.retrieve_vm_list_for_host, 'HostSystem', 'host-149')
#    print "getting vm list for host: " + str(et)
#    print content
#    et, content = time_function_call(vc.retrieve_vm_list_for_host, 'HostSystem', 'host-144')
#    print "getting vm list for host: " + str(et)
#    print content
#    et, content = time_function_call(vc.retrieve_vm_list_for_host, 'HostSystem', 'host-141')
#    print "getting vm list for host: " + str(et)
#    print content
#    t0 = time.time()
#    content = vc.retrieve_properties('HostSystem', prop_set_minimal, 'HostSystem', 'host-146')
#    tdiff = time.time() - t0
#    print prop_set_minimal 
#    print "**********************retrieve host prop_set_minimal time: " + str(tdiff)
#    print content
#
#    t0 = time.time()
#    content = vc.retrieve_properties('HostSystem', prop_set_used_in_brek, 'HostSystem', 'host-146')
#    tdiff = time.time() - t0
#    print prop_set_used_in_brek
#    print "**********************retrieve host prop_set_used_in_brek time: " + str(tdiff)
#    print content
#
#    t0 = time.time()
#    content = vc.retrieve_properties('HostSystem', prop_set_details, 'HostSystem', 'host-146')
#    tdiff = time.time() - t0
#    print prop_set_details
#    print "**********************retrieve host prop_set_details time: " + str(tdiff)
#    print content

