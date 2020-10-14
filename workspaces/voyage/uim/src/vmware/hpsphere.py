#####################################################
#
# hpsphere.py
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
#    Andy Yates
# 
# Description:
#   Extension of the pSphere module for registering plugin, alarms, & tasks.
#
#####################################################
import logging
import suds
import psphere
from psphere.client import Client
from psphere.managedobjects import ClusterComputeResource

log = logging.getLogger(__name__)
log.setLevel(logging.INFO)  
  
  
def event_config_to_eventTypeSchema(event) :
    """Generate eventTypeSchema for ExtensionEventTypeInfo from event configuration"""
    
    xml = "<EventType>"
    xml += "<eventTypeID>%s</eventTypeID>" % event['event_id']
    xml += "<description>%s</description>" % event['description']
    args = event.get('arguments', [])
    arg_xml = ''
    for arg in args :
        arg_xml += "<argument><name>%s</name><type>%s</type></argument>" % (arg['name'], arg['type'])
    
    if arg_xml :
        xml += "<arguments>%s</arguments>" % arg_xml
   
    xml += "</EventType>"
    
    return xml
  
class VCClient(Client):    

    def __init__(self, server=None, username=None, password=None,
                 wsdl_location="local", timeout=30) :
        
        Client.__init__(self, server = server, username = username, password = password,
                                wsdl_location = wsdl_location, timeout = timeout)
    
    def register_ngc_plugin(self, key, package_url, version, admin_email, company, name, description, server_thumbprint) :
        """Register a Next Gen Client Package"""
        
        desc = self.create('Description', label = name, summary = description)
        
        esi = self.create('ExtensionServerInfo', type='com.vmware.vim.viClientScripts', url = '',
                            company = company, description = desc, adminEmail = [ admin_email ], serverThumbprint = server_thumbprint)
        
        eci = self.create('ExtensionClientInfo', version = version, company = company, description = desc, 
                            type = 'vsphere-client-serenity', url = package_url)      
    
        ext = self.create('Extension', company = company, key = key, version = version, subjectName = 'VC Extensibility',
                            server = [ esi ], client = [ eci ], lastHeartbeatTime = self.si.CurrentTime() )
    
        # ext.description = description does not seem to work, ie the plug-in name and description does not show up in the vSphere plugin manager
        # These values show up if they are set through an ExtensionResourceInfo object.  No docs on this...   
        for locale in ['en', 'ja'] :
            resinfo = self.create('ExtensionResourceInfo', locale = locale, module = 'extension')        

            data = self.create('KeyValue', key = '%s.label' % key, value = name)        
            resinfo.data.append(data)
            data = self.create('KeyValue', key = '%s.summary' % key, value = description)        
            resinfo.data.append(data)
            
            # This is needed for the name and description to show up in the plug-in manager for non-English locals
            # Go figure.
            data = self.create('KeyValue', key = '%s.%s.label' % (ext.key, esi.type), value = name)        
            resinfo.data.append(data)        
            data = self.create('KeyValue', key = '%s.%s.summary' % (ext.key, esi.type), value = description)        
            resinfo.data.append(data)
            
            ext.resourceList.append(resinfo)
    
        # Make sure any previous installs are unregistered
        for pkey in ['com.hp.ic4vc.ngc.all', 'com.hp.ic4vc.ngc.server', 'com.hp.ic4vc.ngc.storage'] :
            if pkey == key :    # Will get checked before registration
                continue
            if self.query_plugin(pkey) :            
                self.unregister_plugin(pkey)
    
    
        # if plugin is already registered, unregister the old one first.
        # vCenter will return 'A specified parameter was not correct.' if you
        # try to register an already registered key
        if self.query_plugin(key) :            
            self.unregister_plugin(key)
    
        log.info("Registering plugin '%s' on vCenter '%s'", key, self.server)        
        self.sc.extensionManager.RegisterExtension(extension = ext)
    
    def register_plugin(self, key, config_url, version='', name='', description='', 
                        admin_email='', company='', resources = []) :
               
        esi = self.create('ExtensionServerInfo', type='com.vmware.vim.viClientScripts', url = config_url,
                            company = company, adminEmail = [ admin_email ])                        
                
        eci = self.create('ExtensionClientInfo', version = version, company = company, 
                            type = 'com.vmware.vim.viClientScripts', url = config_url)        
                
        ext = self.create('Extension', company = company, key = key, version = version, subjectName = 'VC Extensibility',
                            server = [ esi ], client = [ eci ], lastHeartbeatTime = self.si.CurrentTime() )
        
        
        # ext.description = description does not seem to work, ie the plug-in name and description does not show up in the vSphere plugin manager
        # These values show up if they are set through an ExtensionResourceInfo object.  No docs on this...        
        resinfo = self.create('ExtensionResourceInfo', locale = 'en', module = 'extension')        

        data = self.create('KeyValue', key = '%s.label' % key, value = name)        
        resinfo.data.append(data)
        data = self.create('KeyValue', key = '%s.summary' % key, value = description)        
        resinfo.data.append(data)
        
        # This is needed for the name and description to show up in the plug-in manager for non-English locals
        # Go figure.
        data = self.create('KeyValue', key = '%s.%s.label' % (ext.key, esi.type), value = name)        
        resinfo.data.append(data)        
        data = self.create('KeyValue', key = '%s.%s.summary' % (ext.key, esi.type), value = description)        
        resinfo.data.append(data)
        
        ext.resourceList.append(resinfo)
                
        # Register Resources (tasks, events, faults, auths)        
        if not resources :
            log.error("register_plugin Odd no resources")
        for resource in resources :            
            resinfo = self.create('ExtensionResourceInfo')
            resinfo.locale = resource['locale']
            resinfo.module = resource['module']
            
            if resource['module'] == 'event' :
                for event in resource['events'] :                    
                    # An event can be in the config multiple times; once for each locale.
                    # Only add it once; the first time it is encountered.
                    if event['event_id'] not in [x.eventID for x in ext.eventList] :
                        event_info = self.create('ExtensionEventTypeInfo')
                        event_info.eventID = event['event_id']
                        event_info.eventTypeSchema = event_config_to_eventTypeSchema(event)
                        ext.eventList.append(event_info)
                    for k,v in event['key_values'].items() :
                        key_value = self.create('KeyValue')
                        key_value.key = k if event['event_id'] in k else '%s.%s' % (event['event_id'], k)
                        key_value.value = v
                        resinfo.data.append(key_value)
                    
            elif resource['module'] == 'task' :
                for task in resource['tasks'] :
                    # A task can be in the config multiple times; once for each locale.
                    # Only add it once; the first time it is encountered.
                    if task['task_id'] not in [x.taskID for x in ext.taskList] :
                        task_info = self.create('ExtensionTaskTypeInfo')
                        task_info.taskID = task['task_id']
                        ext.taskList.append(task_info)
                    for k,v in task['key_values'].items() :
                        key_value = self.create('KeyValue')
                        key_value.key = k if task['task_id'] in k else '%s.%s' % (task['task_id'], k)
                        key_value.value = v
                        resinfo.data.append(key_value)
                        
            elif resource['module'] == 'fault' :
                for fault in resource['faults'] :
                    # A fault can be in the config multiple times; once for each locale.
                    # Only add it once; the first time it is encountered.
                    if fault['fault_id'] not in [x.faultID for x in ext.faultList] :
                        fault_info = self.create('ExtensionFaultTypeInfo')
                        fault_info.faultID = fault['fault_id']
                        ext.faultList.append(fault_info)
                    for k,v in fault['key_values'].items() :
                        key_value = self.create('KeyValue')
                        key_value.key = k if fault['fault_id'] in k else '%s.%s' % (fault['fault_id'], k)
                        key_value.value = v
                        resinfo.data.append(key_value)
                
            elif resource['module'] == 'auth' :
                for priv in resource['privs'] :
                    # A privilege can be in the config multiple times; once for each locale.
                    # Only add it once; the first time it is encountered.
                    if priv['priv_id'] not in [x.privID for x in ext.privilegeList] :
                        priv_info = self.create('ExtensionPrivilegeInfo')
                        priv_info.privID = priv['priv_id']
                        priv_info.privGroupName = priv['priv_group_name']
                        ext.privilegeList.append(priv_info)
                    for k,v in priv['key_values'].items() :
                        key_value = self.create('KeyValue')
                        key_value.key = k if priv['priv_id'] in k else '%s.%s' % (priv['priv_id'], k)
                        key_value.value = v
                        resinfo.data.append(key_value)
                
                for priv_group in resource['privilege_groups'] :
                    for k,v in priv_group['key_values'].items() :
                        key_value = self.create('KeyValue')
                        key_value.key = k if priv_group['priv_group_id'] in k else '%s.%s' % (priv_group['priv_group_id'], k)
                        key_value.value = v
                        resinfo.data.append(key_value)
                            
            else :
                pass  # Ignore unknown modules
        
            ext.resourceList.append(resinfo)
            
        # if plugin is already registered, unregister the old one first.
        # vCenter will return 'A specified parameter was not correct.' if you
        # try to register an already registered key
        if self.query_plugin(key) :            
            self.unregister_plugin(key)
    
        log.info("Registering plugin '%s' on vCenter '%s'", key, self.server)        
        self.sc.extensionManager.RegisterExtension(extension = ext)
        
    def unregister_plugin(self, key) :
        log.info("Unregistering plugin '%s' from vCenter '%s'", key, self.server)
        self.sc.extensionManager.UnregisterExtension(extensionKey = key)
    
    def query_plugin(self, key) :
        log.info("Querying for plugin '%s' on vCenter '%s'", key, self.server)
        p = self.sc.extensionManager.FindExtension(extensionKey = key)
        if p :
            log.info("Plugin '%s' is registered on vCenter '%s'", key, self.server)
        else :
            log.info("Plugin '%s' not registered on vCenter '%s'", key, self.server)
            
        return p


    def create_alarm(self, spec) :
        log.info('Creating Alarm: %s', spec.name)
        try:
            self.sc.alarmManager.CreateAlarm(entity = self.sc.rootFolder, spec = spec)
        except suds.WebFault :            
            log.exception('Unable to create alarm %s', spec.name)            

            
    def create_alarms(self) :
        "Create HP Alarms in vCenter"
        
        #Create the Alarm Specs
        alarm_specs = [
                self.AlarmSpec('HP BladeSystem Power Alarm', 'HP BladeSystem Power Alarm',
                            self.OrAlarmExpression('com.hp.cseries.status', 'entity', 'startsWith', 'power subsystem')),
                self.AlarmSpec('HP BladeSystem Thermal Alarm', 'HP BladeSystem Thermal Alarm',
                            self.OrAlarmExpression('com.hp.cseries.status', 'entity', 'startsWith', 'thermal subsystem')),
                self.AlarmSpec('HP Server WBEM Indication', 'HP Server WBEM Indication',
                            self.OrAlarmExpression('com.hp.wbem', None, None, None)),
                self.AlarmSpec('HP Server SNMP Trap', 'HP Server SNMP Trap',
                            self.OrAlarmExpression('com.hp.snmp', None, None, None)),
            ]

        # Check to see what alarms are already registered. If our alarms are already there do not reregister
        current_alarms = self.sc.alarmManager.GetAlarm()    
        current_alarms = self.get_views([x._mo_ref for x in current_alarms], 'info')
        
        # Create the alarms based on the specs
        for spec in alarm_specs :
            if spec.name not in [calarm.info.name for calarm in current_alarms] :                
                self.create_alarm(spec)
        
            
    def EventAlarmExpression(self, event_type_id, status, object_type, attributeName, operator, value) :
        ex = self.create('EventAlarmExpression')
        ex.eventType = 'vim.event.EventEx'
        ex.eventTypeId = event_type_id
        ex.objectType = object_type
        ex.status = status
        
        comparison = self.create('EventAlarmExpressionComparison')
        comparison.attributeName = attributeName
        comparison.operator = operator
        comparison.value = value
        ex.comparisons.append(comparison)
        
        return ex
            
    def OrAlarmExpression(self, event_type_id, attributeName, operator, value) :
        """Factory for creating an OrAlarmExpression"""
                
        or_expr = self.create('OrAlarmExpression')
        ex = self.EventAlarmExpression('%s.error' % event_type_id, 'red', 'vim.HostSystem', attributeName, operator, value)
        or_expr.expression.append(ex)

        ex = self.EventAlarmExpression('%s.warning' % event_type_id, 'yellow', 'vim.HostSystem', attributeName, operator, value)
        or_expr.expression.append(ex)

        ex = self.EventAlarmExpression('%s.info' % event_type_id, 'green', 'vim.HostSystem', attributeName, operator, value)
        or_expr.expression.append(ex)
        
        return or_expr
    
    def AlarmSpec(self, name, description, alarm_expression, enabled=True, actionFrequency = 0, toleranceRange = 0, reportingFrequency=0) :       
        """Factory for creating an alarm spec object"""
        alarm = self.create('AlarmSpec')
        alarm.name = name
        alarm.description = description
        alarm.enabled = enabled        
        alarm.actionFrequency = actionFrequency        
        alarm.setting.toleranceRange = toleranceRange
        alarm.setting.reportingFrequency = reportingFrequency
        alarm.expression = alarm_expression
        
        return alarm        

    # Returns a list of moref objects for each host in the cluster.
    def get_cluster_hosts(self, mob_str):
        log.debug("get_cluster_hosts: %s", mob_str)
        hosts = []
        try:    
            try :
                self.si.CurrentTime()
            except :
                log.debug("Error validating connection; logging in again.")
                self.login()
                
            os = self.create("ObjectSpec")
            os.obj._type = mob_str.split(':')[0]
            os.obj.value = mob_str.split(':')[1]
            name = self.get_view(os.obj).name
            cluster = ClusterComputeResource.get(self, name=name)
            for host in cluster.host:
                log.debug("get_cluster_hosts: %s %s %s", mob_str, host.name, host._mo_ref)
                hosts.append({'name':host.name, 'moref':host._mo_ref})
        except AttributeError :
            log.exception("Attribute Error for object type: %s value: %s", os.obj._type, os.obj.value)
        except psphere.errors.ObjectNotFoundError:
            log.exception("Object not found: %s", mob_str)
        return hosts

    def get_cluster(self, mob_str) :    
        os = self.create("ObjectSpec")
        os.obj._type = mob_str.split(':')[0]
        os.obj.value = mob_str.split(':')[1]
        name = self.get_view(os.obj).name
        cluster = ClusterComputeResource.get(self, name=name)    
       
        return cluster
        
    def get_session_info(self, session_key):
        try:
            self.sc.sessionManager.update()
            sessions = self.sc.sessionManager.sessionList
            info = [s for s in sessions if s.key.upper()==session_key.upper()]
            return info[0] or None
        except:
            log.exception("Error getting session info for session_key: %s", session_key)
            return None
