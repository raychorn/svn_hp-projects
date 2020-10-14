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
import suds
from suds.client import Client
from suds.sax.parser import Parser
from suds.umx.basic import Basic as bumx
import httplib, httplib2, urllib, urllib2
from util import catalog

from util.resource import resource
from vmware import vmwobj

_PREFIX='ns0'

import logging
log = logging.getLogger(__name__)
from uuid import UUID
import base64
from suds.sax.element import Element, Attribute
from suds.plugin import MessagePlugin
from suds.sudsobject import Object
from threading import Thread, Lock
import time
import sys
import os

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
    
    prop_set_host_minimal=['name', 'hardware.systemInfo.uuid']
    prop_set_host_details=['overallStatus', 'hardware', 'name', 'config.ipmi', 'config.network', 'config.product', 
                'config.storageDevice.hostBusAdapter', 'config.storageDevice.multipathInfo.lun', 'vm']

    prop_set_vm = ['name' , 'config.uuid', 'config.hardware', 'guest']
    prop_set_dvportgroup = ['name', 'configStatus', 'config', 'host']
    prop_set_cluster = ['name', 'configStatus', 'host']
    prop_set_datastore = ['name', 'info', 'host']
    prop_set_dvswitch = ['name', 'configStatus', 'config']
    prop_set_guest = ['guestOperationsManager', 'authManager', 'fileManager', 'processManager']    
    
    def __init__(self, host, wsdl=None, decoder=None, eventing_enable=True):
        self.initializing = True
        self.host = host
        self.url = 'https://%s/sdk' % host
        if not wsdl:
            wsdl = 'file://' + resource('vmware/wsdl41/vimService.wsdl')
        self.wsdl = wsdl
        self.hosts = []
        self.clusters = []
        self.listupdatetime = 0
        self.session = None
        self.sc = None
        self.decoder = decoder
        self.host_discovery_done = False
        self.eventing_enable = eventing_enable
        self.last_event_key = 0
        self.standby_log = {}
        self.reboot_log = {}
        self.vm_renamed_log = {}
        self.ds_renamed_log = {}
        self.destroyed_log = {}
        self.reconfigured_log = {}
        self.running = False
        log.debug("vCenter __init__ host: %s", host)
        
        self.host_update_lock = Lock()
        self.cluster_update_lock = Lock()
        self.retrieve_properties_lock = Lock()
        self.retrieve_list_lock = Lock()
        self.vcenterupdatelock = Lock()
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
            rawdata = http.request('https://%s/sdk/vimServiceVersions.xml' %(self.host), method="GET")
            xml = Parser().parse(string=rawdata[1]).root()
            data = bumx().process(xml)
            ver = []
            for ns in data.namespace:
                if hasattr(ns, 'priorVersions'):
                    ver += ns.priorVersions.version
            self.prior_versions =  ver
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
        evfilter.alarm = None           # Looks like optional params need to be set to None so that they are not included in the SOAP request
        evfilter.scheduledTask = None
        evfilter.eventTypeId += [
            'vim.event.EnteredStandbyModeEvent',
            'vim.event.DrsEnteredStandbyModeEvent',
            'vim.event.ExitedStandbyModeEvent',
            'vim.event.DrsExitedStandbyModeEvent',
            'vim.event.VmGuestRebootEvent',
            'vim.event.VmReconfiguredEvent',
            'vim.event.VmRenamedEvent',
            'vim.event.DatastoreDestroyedEvent',
            'vim.event.DatastoreRenamedEvent'            
        ]        

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
                    filter = self.vim.CreateFilter(self.sc.propertyCollector, ef, False)
                    log.debug("Created new event filter: %s", filter)
                except :
                    log.exception('Error creating event filter')
                    self.comm_error()
                    time.sleep(30)
                    continue
            
            try:                
                log.debug("WaitForUpdates: version=%s", version)
                updates = self.vim.WaitForUpdates(self.sc.propertyCollector, version)
            #except urllib2.URLError:
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
            
            #Look for events related to hosts     could be any of ('datacenter', 'computeResource', 'host', 'vm', 'ds', 'net', 'dvs')
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
            
            #Look for events related to vm     could be any of ('datacenter', 'computeResource', 'host', 'vm', 'ds', 'net', 'dvs')
            print "Looking for EVENTS related to vms..."
            vmevent_details = {}
            ev_details = []
            vmevent = {}
            s = getattr(event, 'vm', None)        
            if s:
                s = getattr(s, 'vm', None)
                if s:  
                    moref = '%s:%s' % (s._type, s.value)
                    log.debug("Received Event %s for vm %s", event_name, moref)
                    log.info("Received Event %s for vm %s", event_name, moref)
                    vm = self.get_vm(moref)
                    if not vm :
                        log.error('Error processing event %s for vm %s: VM not found', event_name, moref)
                        continue
                    if 'VmGuestReboot' in event_name :
                        self.reboot_log.setdefault(moref, {})['vm_guest_reboot'] = event.createdTime
                        log.debug("Event VM %s guest reboot at %s", vm.name, event.createdTime)                    
                        log.info("Event VM %s guest reboot at %s", vm.name, event.createdTime)
                        #print event.fullFormattedMessage
                        vmevent['message'] = event.fullFormattedMessage
                        vmevent['user'] = event.userName
                    elif 'VmRenamed' in event_name :
                        self.vm_renamed_log.setdefault(moref, {})['vm_renamed'] = event.createdTime
                        log.debug("Event VM %s vm renamed at %s", vm.name, event.createdTime)                    
                        log.info("Event VM %s vm renamed at %s", vm.name, event.createdTime)
                        #print event.fullFormattedMessage
                        vmevent['message'] = event.fullFormattedMessage
                        vmevent['user'] = event.userName
                    elif 'VmReconfigured' in event_name :
                        self.reconfigured_log.setdefault(moref, {})['vm_reconfigured'] = event.createdTime
                        log.debug("Event VM %s vm reconfigured at %s", vm.name, event.createdTime)                    
                        log.info("Event VM %s vm reconfigured at %s", vm.name, event.createdTime)
                        #print event.fullFormattedMessage
                        vmevent['message'] = event.fullFormattedMessage
                        vmevent['user'] = event.userName
                        vmevent['configSpec'] = event.configSpec
                        vmevent['id'] = event.chainId
                        #vmevent['tag'] = event.changeTag
                        vmevent['resource'] = event.computeResource
                        vmevent['dc'] = event.datacenter
                        #vmevent['dvs'] = event.dvs
                        vmevent['host'] = event.host
                        vmevent['key'] = event.key
                        #vmevent['net'] = event.net
                        #vmevent['ds'] = event.ds 
                ev_details.append(vmevent)
                    
            vmevent_details['details'] = ev_details
            print (str(vmevent_details))
            
            #return vmevent_details            
            
            #Look for events related to ds     could be any of ('datacenter', 'computeResource', 'host', 'vm', 'ds', 'net', 'dvs')
            print "Looking for EVENTS related to datastores..."
            dsevent_details = {}
            ev_details = []
            dsevent = {}
            t = getattr(event, 'ds', None)
            if t:
                t = getattr(t, 'datastore', None)
                if t:  
                    moref = '%s:%s' % (t._type, t.value)
                    log.debug("Received Event %s for ds %s", event_name, moref)
                    log.info("Received Event %s for ds %s", event_name, moref)
                    ds = self.get_ds(moref)
                    if not ds :
                        log.error('Error processing event %s for ds %s: DS not found', event_name, moref)
                        continue
                    if 'DatastoreRenamed' in event_name :
                        self.ds_renamed_log.setdefault(moref, {})['datastore_renamed'] = event.createdTime
                        log.debug("Event DS %s datastore renamed at %s", ds.name, event.createdTime)                    
                        log.info("Event DS %s datastore renamed at %s", ds.name, event.createdTime)
                        dsevent['message'] = event.fullFormattedMessage
                        dsevent['user'] = event.userName
                    elif 'DatastoreDestroyed' in event_name :
                        self.destroyed_log.setdefault(moref, {})['datastore_destroyed'] = event.createdTime
                        log.debug("Event DS %s datastore destroyed at %s", ds.name, event.createdTime)                    
                        log.info("Event DS %s datastore destroyed at %s", ds.name, event.createdTime)
                        dsevent['message'] = event.fullFormattedMessage
                        dsevent['user'] = event.userName
                        dsevent['id'] = event.chainId
                        #dsevent['tag'] = event.changeTag
                        #dsevent['resource'] = event.computeResource
                        dsevent['dc'] = event.datacenter
                        #dsevent['dvs'] = event.dvs
                        #dsevent['host'] = event.host
                        dsevent['key'] = event.key
                        #dsevent['net'] = event.net
                        #dsevent['vm'] = event.vm                        
                ev_details.append(dsevent)
                    
            dsevent_details['details'] = ev_details
            print (str(dsevent_details))
            
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
            #ext = self.vim.FindExtension(self.sc.extensionManager, 'com.hp.ic4vc')
            #log.debug('Extension found: %s', str(ext['key']))
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
                content = self.vim.RetrieveProperties(self.sc.propertyCollector, [pf])
            except:
                self.comm_error()
                log.exception('Exception in retrieve_list')
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

    def print_host(self, ):
        for host in self.hosts:
            print '--', host._type, host.value, host.name, host.uuid
                
    def retrieve_host_list(self):
        #prop_set_minimal=['name', 'hardware.systemInfo.uuid', 'config.ipmi']
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
        
    def create_virtual_disk(self, moref):
        
        diskCount = 0
        with self.get_vms_for_host_lock:
            host = self.get_host(moref)
            vmlist = []
            vmmoreflist = []
            if hasattr(host, 'vm') and hasattr(host.vm, 'ManagedObjectReference') and isinstance(host.vm.ManagedObjectReference, list):
                for vm_moref in host.vm.ManagedObjectReference:
                    if vmwobj.vm.mo_type and vm_moref.value:
                        content = self.retrieve_properties(vmwobj.vm.mo_type, vmwobj.vm.pset_detail, vmwobj.vm.mo_type, vm_moref.value)
                        if content:
                            vmmoreflist.append(vm_moref)
                            vmlist.append(content)
        
        for device in vmlist[0].config.hardware.device :
            if device.deviceInfo.label == 'SCSI controller 0' :
                controllerKey = device.key
            elif device.__class__.__name__ == 'VirtualDisk' :
                diskCount = diskCount + 1
        
        with self.get_ds_for_host_lock:
            host = self.get_host(moref)
            dslist = []
            dsmoreflist = []
            if hasattr(host, 'datastore') and hasattr(host.datastore, 'ManagedObjectReference') and isinstance(host.datastore.ManagedObjectReference, list):
                for ds_moref in host.datastore.ManagedObjectReference:
                    if vmwobj.ds.mo_type and ds_moref.value:
                        content = self.retrieve_properties(vmwobj.ds.mo_type, vmwobj.ds.pset_detail, vmwobj.ds.mo_type, ds_moref.value)
                        if content:
                            dsmoreflist.append(ds_moref)
                            dslist.append(content)
        
        backing = self.factory('VirtualDiskFlatVer2BackingInfo')
        vdisk = self.factory('VirtualDisk')
        vdevConfig = self.factory('VirtualDeviceConfigSpec')
        vmConfig = self.factory('VirtualMachineConfigSpec')
        
        backing.thinProvisioned = True
        backing.datastore = dsmoreflist[0]
        backing.diskMode = 'persistent'
        backing.fileName = '[' + dslist[0].name + '] ' + vmlist[0].name + '/' + 'test_vdisk' + str(diskCount) + '.vmdk'
        
        vdisk.backing = backing
        vdisk.capacityInKB = 3 * 1048576
        vdisk.controllerKey = controllerKey
        vdisk.key = -1
        vdisk.unitNumber = diskCount
        
        vdevConfig.device = vdisk
        vdevConfig.fileOperation = 'create'
        vdevConfig.operation = 'add'
        
        specs = []
        specs.append(vdevConfig)
        
        vmConfig.deviceChange = specs
        
        task = self.vim.ReconfigVM_Task(vmmoreflist[0], vmConfig)
    
    def file_transfer_to_guest(self, moref, user_vmmoref, username, password, isession, src_filepath, dst_filepath, overwrite) :
        print "ENTERING FILE TRANSFER TO GUEST"
        
        #retrieve managed objects required for calling the vim statement
        with self.get_vms_for_host_lock:
            hosts = self.get_hosts()
            #print(str(hosts))
            sc = self.sc
            vmlist = []
            vmmoreflist = []
            gom = []
            gom_moref = []
            for host in hosts:
                if hasattr(host, 'vm') and hasattr(host.vm, 'ManagedObjectReference') and isinstance(host.vm.ManagedObjectReference, list):
                    for vm_moref in host.vm.ManagedObjectReference:
                        if vmwobj.vm.mo_type and vm_moref.value:
                            content = self.retrieve_properties(vmwobj.vm.mo_type, vmwobj.vm.pset_detail, vmwobj.vm.mo_type, vm_moref.value)
                            if content:
                                vmlist.append(content)
                                vmmoreflist.append(vm_moref)
                                #print(str(vmmoreflist))                         
                                #print(str(vmlist))                    
        
        if user_vmmoref:
            vmmoref = user_vmmoref
        else:
            print "This operation requires a VM Managed Object Reference."
            vmmoref = vmmoreflist[6]
            
        guest_auth = self.factory('NamePasswordAuthentication')
        
        if username and password:
            print "Acquiring user credentials..."
            guest_auth.username = username
            guest_auth.password = password
                            
        else:
            print "'username' and 'password' values are required."            
            guest_auth.username = "root"
            guest_auth.password = "rootpwd"
            
        if isession:
            print "Acquiring isession value..."
            guest_auth.interactiveSession = isession
        else:
            print "This will NOT be an interactive session"
            guest_auth.interactiveSession = False
            
        if src_filepath and dst_filepath:
            filepath = src_filepath
            guest_filepath = dst_filepath                  
        else:
            print "'source filepath', and 'destination filepath' values are required."
            filepath = "C:\Test.zip"
            guest_filepath = "/tmp/Test.zip"
        
        vm = vmlist[6]
        
        if vm.guest.guestFamily == 'linuxGuest' :
            file_attribs = self.factory('GuestPosixFileAttributes')        
        else :
            file_attribs = self.factory('GuestWindowsFileAttributes')
                
        if hasattr(sc, 'guestOperationsManager'):
            if vmwobj.guest_operations_manager.mo_type and sc.guestOperationsManager.value:
                content = self.retrieve_properties(vmwobj.guest_operations_manager.mo_type, vmwobj.guest_operations_manager.pset_detail,
                                                    vmwobj.guest_operations_manager.mo_type, sc.guestOperationsManager.value)
                if content:
                    gom = content
                    gom_moref = sc.guestOperationsManager
        
        fileManager = gom.fileManager
        filesize = os.path.getsize(filepath)
        
        try:
            with open(filepath, 'rb') as f: pass
            data = open(filepath, 'rb')
        except IOError as e:
            print "Cannot locate the file " + filepath       
        
        if overwrite:
            writeover = overwrite
        else:
            writeover = True
            
        try:
            print "Initiating file transfer to guest..."
            put_url = self.vim.InitiateFileTransferToGuest(fileManager, vmmoref, guest_auth, guest_filepath, file_attribs, filesize, writeover)
            if put_url:
                print "Done initiating file transfer to guest.  URL is..."
                print(str(put_url))       
        except:
            log.exception('Error initiating file transfer to guest.')
        
        try:
            print "Uploading " + str(filepath) + " to guest..."
            opener = urllib2.build_opener(urllib2.HTTPSHandler)
            request = urllib2.Request(put_url)
            request.add_data(data)
            request.add_header('content-length', filesize)
            request.get_method = lambda: 'PUT'
            url = opener.open(request)
            print "Successfully uploaded " + str(filepath) + " to guest!"
        except:
            log.exception('Error uploading file to guest.')        
    
    def get_hds_for_hcm(self, moref):
        print ("entering hds")
        host = self.get_host(moref)
        if hasattr(host, 'configManager') and hasattr(host.configManager, 'datastoreSystem'):
            content = self.retrieve_properties(vmwobj.hds.mo_type, vmwobj.hds.pset_detail, vmwobj.hds.mo_type, host.configManager.datastoreSystem.value)
            print (str(host.configManager.datastoreSystem))
            
            host_scsi_disks = []
            host_scsi_disks.append(self.vim.QueryAvailableDisksForVmfs(host.configManager.datastoreSystem))
            print(str(host_scsi_disks))
            
            for host_scsi_disk in host_scsi_disks:
                print(str(host_scsi_disk[0].devicePath))
                vmfs_datastore_options = self.vim.QueryVmfsDatastoreCreateOptions(host.configManager.datastoreSystem, host_scsi_disk[0].devicePath)
                print(str(vmfs_datastore_options))
            
            vmfs_spec = self.factory('VmfsDatastoreCreateSpec')
            vmfs_spec.vmfs.majorVersion = 5
            vmfs_spec.vmfs.volumeName = "test"
            for lun in host.config.storageDevice.scsiLun :
                vmfs_spec.vmfs.extent.partition = 1
                vmfs_spec.vmfs.extent.diskName = lun.canonicalName
            #print(str(vmfs_spec))
            #service = self.vim.CreateVmfsDatastore(host.configManager.datastoreSystem, vmfs_datastore_options[0].spec)
            #print (str(service))
            if content:
                return content
    
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
    
    def get_vm(self, moref):
        vm = None
        content = self.retrieve_list(vmwobj.vm.mo_type, vmwobj.vm.pset_detail)
        for vm in content :
            obj = vmwobj.factory(vm.obj)        
            obj.setprop(vm.propSet)
            vm = obj            
        return vm
    
    def get_ds(self, moref):
        ds = None
        content = self.retrieve_list(vmwobj.ds.mo_type, vmwobj.ds.pset_detail)
        for ds in content :
            obj = vmwobj.factory(ds.obj)        
            obj.setprop(ds.propSet)
            ds = obj            
        return ds
    
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
        hosts = self.retrieve_host_list()
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
            
    def update(self):
        log.debug('Updating...')
        if not self.connected():
            log.debug('Re-connecting session...')
            self.comm_error()
            self.connect()

        if self.connected():
            log.debug('Connected, getting vcenterupdatelock...')
            with self.vcenterupdatelock:
                listupdate_et = time.time() - self.listupdatetime 
                if (listupdate_et) > 60:
                    self.update_host_list()
                    self.update_cluster_list()
            log.debug('updated, vcenterupdatelock released...')
        else:
            log.error('Unable to connect to vCenter: %s', self.host)

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
        #ev.message = message
        ev.userName = "HP Insight Management"
        ev.eventTypeId = event.event_type_id()
        ev.createdTime = self.vim.CurrentTime(self.service_reference)
        ev.arguments = self.arglist(event.get_args())
        self.client.set_options(plugins=[AddAttr('value', 'xsi:type', 'xsd:string')])
        try:
            self.vim.PostEvent(self.sc.eventManager, ev)
        except:
            self.comm_error()
            log.exception('Error in log event')
        self.client.set_options(plugins=[])

if __name__ == '__main__':
    def time_function_call(f, *args):
        import time
        t0 = time.time()
        val = f(*args)
        tdiff = time.time() - t0
        return tdiff, val
    
    import time
    from util import cmdline_opt 
    
    target, username, password = cmdline_opt.vcenter()
    t0 = time.time()
    vc = vCenter(target)
    tdiff = time.time() - t0
    print "vCenter object initialization time: " + str(tdiff)
    
    et, ret = time_function_call(vc.setup)
    print "Parsing wsdl time: " + str( et )

    et, ret = time_function_call(vc.login, username, password )    
    print "vCenter login time: " + str(et)

#    t0 = time.time()
#    content = vc.retrieve_host_list()
#    tdiff = time.time() - t0
#    print "retrieve host list time: " + str(tdiff)
#    print content

    prop_set_minimal=['name', 'hardware.systemInfo.uuid']
    prop_set_used_in_brek=['overallStatus', 'hardware', 'name', 'config.ipmi', 'config.network', 'config.product', 
                'config.storageDevice.hostBusAdapter', 'config.storageDevice.multipathInfo.lun',
                'vm']

    prop_set_details=['overallStatus', 'hardware', 'name', 'config','vm']
    
    prop_set_datacenter = ['name', 'configuration']
    prop_set_vm = ['name' , 'config.uuid', 'config.hardware', 'guest' ]
    prop_set_dvportgroup = ['name', 'configStatus', 'config', 'host']
    prop_set_cluster = ['name', 'configStatus', 'host']
    prop_set_datastore = ['name', 'info', 'host']
    prop_set_dvswitch = ['name', 'configStatus', 'config']
    

#    t0 = time.time()
#    content = vc.retrieve_list('HostSystem', prop_set_minimal)
#    tdiff = time.time() - t0
#    print "retrieve host list time: " + str(tdiff)
#    print content

    #vc.generate_host_list()
    et, hosts = time_function_call(vc.generate_host_list)
    print "getting host minimal: " + str(et)

    vc.print_host()
    #print hosts

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

    #et, vms_for_host = time_function_call(vc.retrieve_properties, 'VirtualMachine', prop_set_vm, 'VirtualMachine', 'vm-148')
    #print "getting vm list for host: " + str(et)

    #print vms_for_host
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