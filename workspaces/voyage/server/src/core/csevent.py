'''
Created on Dec 15, 2011

@author: IslamM
'''
import string, time
from datetime import datetime
from util import catalog, config
from core.masterxml import MasterXMLParser

CSEventsWeCareAbout = ['hpcs.event.ConnectError', 'hpcs.event.NoCredentials', 'hpcs.event.AuthError', 
                     'hpcs.event.Failover', 'hpcs.event.InvalidFirmware'] 

class CSEvent():
    def __init__(self, ev):
        self.event = ev
        self.fix_kvpairs()    

    def fix_kvpairs(self):
        for kv in self.event['keyValuePairs']['keyValuePair']:
            if kv['value'] and not isinstance(kv['value'], str): 
                kv['value'] = str(kv['value'])
            if kv['value']:
                kv['value'] = kv['value'] if all(c in string.printable for c in kv['value']) else 'non-printable'
        
    def kvs(self):
        return self.event['keyValuePairs']['keyValuePair'] if self.event['keyValuePairs']['keyValuePair'] else []
    
    def get_args(self):
        kv_pairs = self.kvs()
        ev_desc = {}
        ev_desc['key'] = 'evDescription'
        ev_desc['value'] = self.get_message()
        kv_pairs.append(ev_desc)
        return kv_pairs

    def affects_blades(self):
        for kv in self.event['KeyValuePairs']['KeyValuePair']:
            if kv['key'] == 'blades':
                return kv['value'] != None or kv['value'] != ''

    def get_affected_blade_bays(self):       
        for kv in self.event['keyValuePairs']['keyValuePair']:
            if kv['key'] == 'blades':
                return kv['value'].split(',')
        return []

    def entity_type(self):
        return self.event['eventReferencedDataModel']
    
    def entity_uuid(self):
        return self.event['eventReferencedManagedElementID']
    
    def source(self):
        return self.event['eventSourceIP']

    def detail_source(self):
        esdm = self.event['eventReferencedDataModel'] if self.event['eventReferencedDataModel'] else ''
        esrc = self.event['eventSourceIP'] if self.event['eventSourceIP'] else self.entity_uuid()
        return esdm + ' (' + esrc + ')'
            
    def is_oa_event(self):
        return self.event['eventReferencedDataModel'] == 'oa'

    def is_server_event(self):
        return self.event['eventReferencedDataModel'] == 'server'
    
    def timestamp(self):
        tformat = "%Y-%m-%dT%H:%M:%S.%f"
        tvalue = self.event['eventTimeStamp']
        try:
            dt = datetime.strptime(tvalue, tformat)
        except:
            tformat = '.'.join(tformat.split('.')[0:-1])
            dt = datetime.strptime(tvalue, tformat)
        return time.mktime(dt.timetuple()) 
    
    def severity(self):
        r_val = 'info'
        if self.event['eventSeverity'] in ("minor", 'warning'):
            r_val = "warning"
        elif self.event['eventSeverity'] in ("major", "error"):
            r_val = "error"
        return r_val

    def get_message(self):
        return self.event['eventText']
    
    def name(self):
        return self.event['eventName']
    
    def event_type_id(self):
        return 'com.hp' + self.name().split('hpcs.event')[-1] + '.' + self.severity()
    
    def forwardable(self):
        return False
    
    @staticmethod
    def create(ev, entity):
        if ev['eventReferencedDataModel'] == 'server' and ev['eventCategory'] == 'snmpTrap':
            return SNMPEvent(ev)
        elif ev['eventReferencedDataModel'] == 'server' and ev['eventCategory'] == 'wbemIndication':
            return WBEMEvent(ev)
        elif ev['eventReferencedDataModel'] == 'oa':
            return CSOAEvent(ev, entity)
        elif ev['eventName'] in CSEventsWeCareAbout:
            return CSEvent(ev)
        else:
            return None

class SNMPEvent(CSEvent):
    def __init__(self, ev):
        CSEvent.__init__(self, ev)
    def forwardable(self):
        return True
    def event_type_id(self):
        return 'com.hp.snmp.' + self.severity()
    
    def get_message(self):
        master = catalog.lookup('master-xml-object-for-snmp-traps')
        if not master:
            cfg = config.config()
            master = MasterXMLParser(cfg.masterxml_path)
            catalog.insert(master, 'master-xml-object-for-snmp-traps')
        
        desc = master.get_description(self.event['eventLookupID'])
        if desc:
            return desc[0]
        return 'Trap ID = ' + self.event['eventLookupID'] 

class WBEMEvent(CSEvent):
    def __init__(self, ev):
        CSEvent.__init__(self, ev)
    def forwardable(self):
        return True
    def event_type_id(self):
        return 'com.hp.wbem.' + self.severity()

    def get_message(self):
        for kv in self.kvs(): 
            if kv['key'] == 'Description':
                return kv['value']
        return 'Description missing in the key,value pair'

    
class CSOAEvent(CSEvent):    
    def __init__(self, ev, oa):
        CSEvent.__init__(self, ev)
        self.oa = oa
        self.subsystemlist = ['power subsystem', 'thermal subsystem', 'power supply', 'interconnect', 'oa']

    def seba(self):
        status = ''
        entity = ''
        bay = ''
        action = ''
        for kv in self.kvs():
            if kv['key'] == 'status':
                status = kv['value']
            elif kv['key'] == 'entity':
                entity = kv['value']
            elif kv['key'] == 'bay':
                bay = kv['value']
            elif kv['key'] == 'action':
                action = kv['value']
        return status, entity, bay, action
    
    def get_entity(self):
        for kv in self.kvs():
            if kv['key'] == 'entity':
                return kv['value']
    
    def get_affected_blades(self):
        bb = self.get_affected_blade_bays()
        uuids = []
        entity =  self.get_entity()
        all_blades = False
        if entity in self.subsystemlist:
            all_blades = True
        for uuid in self.oa['contains']['uuid']:
            for bldInfo in self.oa['blade_info']['bladeInfo']:
                if bldInfo['presence'] == 'PRESENT' and (all_blades or (str(bldInfo['bayNumber']) in bb) ):
                    if uuid not in uuids:
                        uuids.append(uuid)
        return uuids
    
    def entity_message(self, entity, bay):
        if bay:
            return entity + " on bay " + bay
        else:
            return entity
        
    def verb_message(self, status, action):
        if status:
            return "changed to " + status
        elif action:
            return action
        
    def enc_name(self):
        return self.oa['enclosure_info']['enclosureName']
    
    def get_message(self):
        status, entity, bay, action = self.seba()
        detail = "Enclosure " + self.enc_name() + ":" + self.entity_message(entity, bay) + " " + self.verb_message(status, action)
        return detail 

    def event_type_id(self):
        return 'com.hp' + self.name().split('hpcs.event')[-1] + '.' + self.severity()

    def forwardable(self):
        return True