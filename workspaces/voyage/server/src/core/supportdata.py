'''
Created on Oct 14, 2011

@author: IslamM
'''
from suds.mx.basic import Basic as Bmx
from suds.sudsobject import Object
from util import catalog
from vmware import vmwobj
from engines.csc_engine import csc_engine
import json

class serverData(Object): pass

class SupportData:
    def get_support_data(self):
        servers, ilos, oas, vcms = 0,0,0,0
        
        obj = serverData()
        obj._app = "HPICSM"
        obj._supportDataVersion = "3"
        obj.Metric = [Object(), Object(),Object(), Object()]
        obj.Metric[0]._type = 'servers'
        obj.Metric[0]._value = 'Not discoverd yet'
        obj.Metric[1]._type = 'ilos'
        obj.Metric[1]._value = 'Not discoverd yet'
        obj.Metric[2]._type = 'oas'
        obj.Metric[2]._value = 'Not discoverd yet'
        obj.Metric[3]._type = 'vcms'
        obj.Metric[3]._value = 'Not discoverd yet'

        csce = catalog.get_all(csc_engine)[0]
        server_list = json.loads(csce.get_cs_entity_list('server'))
        ilo_list = json.loads(csce.get_cs_entity_list('ilo'))
        oa_list = json.loads(csce.get_cs_entity_list('oa'))
        vc_list = json.loads(csce.get_cs_entity_list('vcdomain'))

        servers = len(server_list['uuid'])
        ilos = len(ilo_list['uuid'])
        oas = len(oa_list['uuid'])
        vcms = len(vc_list['uuid'])
        
        obj.Metric[0]._value = servers
        obj.Metric[1]._value = ilos
        obj.Metric[2]._value = oas
        obj.Metric[3]._value = vcms
        
        mx = Bmx()
        xml = str(mx.process(obj))
        return xml
