'''
Created on Sep 3, 2013

@author: Partho Bhowmick
'''
import json
from logging import getLogger
log = getLogger(__name__)
from vmware.vcenter import vCenter
from util import catalog

'''
Home of all enclosure related methods
'''

def getEnclosureList(vcguid, csce):
    '''
    Returns an object listing the enclosures in the vCenter's hosts reside
    @param vcguid: The UUID of the vCenter
    @param csce: The common services engine as extracted from the systemwide 
                catalog
    @return: A dict with the schema 
            {"enclosure1_uuid":"enclosure 1 name",
             "enclosure2_uuid":"enclosure 2 name",
                .
                .
                .
            }
    '''
    try:
        vc = [vc for vc in catalog.get_all(vCenter) if vc.sc.about.instanceUuid.lower() == vcguid.lower()][0]
    except:
        log.exception('error finding vCenter object for %s', vcguid)
        raise
    
    hosts_uuid_list = [ host.hardware.systemInfo.uuid.lower() for host in vc.get_hosts() ]
    
    oa_list = csce.get_hostinfo('OA', None)['uuid']
    #log.debug("oa_list = %s", oa_list)
    oa_info = {}
    for oa_uuid in oa_list:
        oa = csce.get_hostinfo('OA', oa_uuid)
        if (anyHostInOA(hosts_uuid_list, oa)):
            oa_info[oa_uuid] = getEnclosureNameFromOA(oa)
    log.debug("Enclosures: %s", oa_info)
    return oa_info

def getEnclosureHosts(vcguid, enclosureguid, csce):
    '''
    Returns an listing the hosts in an enclosure
    @param vcguid: The UUID of the vCenter
    @param enclosureguid: The UUID of enclosure    
    @param csce: The common services engine as extracted from the systemwide 
                catalog
    @return: a list of Host Managed Object Ids in the enclosure
    '''
    try:
        vc = [vc for vc in catalog.get_all(vCenter) if vc.sc.about.instanceUuid.lower() == vcguid.lower()][0]
    except:
        log.exception('error finding vCenter object for %s', vcguid)
        raise
          
    hosts_uuid_list = []
    hosts_by_uuid = {}
    
    for host in vc.get_hosts() :        
        hosts_uuid_list.append(host.hardware.systemInfo.uuid)
        hosts_by_uuid[host.hardware.systemInfo.uuid] = host
    
    response = []
    
    oa = csce.get_hostinfo('OA', enclosureguid)
    oa_hosts = getHostsInOA(hosts_uuid_list, oa)    
    
    for host in oa_hosts:        
        response.append(hosts_by_uuid[host].value)
    
    log.debug("Enclosure %s hosts: %s",enclosureguid, response)
    
    return response

def getOaSummary(vcguid, oa_uuid, csce):
    try:
        vc = [vc for vc in catalog.get_all(vCenter) if vc.sc.about.instanceUuid.lower() == vcguid.lower()][0]
    except:
        log.exception('error finding vc object for %s')
        raise
        
    hosts_uuid_list = [ host.hardware.systemInfo.uuid.lower() for host in vc.get_hosts() ]
    
    oa = csce.get_hostinfo('OA', oa_uuid)
    name = getEnclosureNameFromOA(oa)
    model = getEnclosureModelFromOA(oa)
    uuid = getEnclosureUuidFromOA(oa)
    oa_firmware = getOAFirmwareInfoFromOA(oa)
    if oa_firmware:
        oa_firmware = oa_firmware[0]
    else:
        oa_firmware = None
    power = getEnclosureTotalPowerConsumedFromOA(oa)
    total_hosts = getPresentHostsFromOA(oa)
    vmware_hosts = getPresentHostsFromOA(oa, hosts_uuid_list)
    vmware_hosts_name = getVmwareHostsNames(vc = vc, 
                                    vmhosts_uuid_list = vmware_hosts.keys())
    bays = getVmwareBays(vmware_hosts, vmware_hosts_name )
    empty_hosts = getAbsentHostsFromOA(oa)
    bays.extend([{'bay_no':h['bayNumber'], 
        'name':'Empty'} for h in empty_hosts])
    subsumed_bays = getSubsumedBaysFromOA(oa)
    bays.extend([{'bay_no':h['bayNumber'], 
        'name':'Subsumed' } for h in subsumed_bays])
    bays.extend([{'bay_no':h['bayNumber'], 'name':'Unmanaged'} \
        for h in total_hosts if h not in vmware_hosts.values()])
    temperature = getTemperatureFromOA(oa)
    status = getHealthStatusFromOA(oa)
    ip = getOANetworkInfo(oa)
    fans = getAllFansInfo(oa)
    power_supplies = getAllPowerSuppliesInfo(oa)
    interconnects = getAllInterconnectsInfo(oa)
    return {'ip':ip,
            'name':name,
            'model':model,
            'uuid':uuid,
            'firmware':oa_firmware,
            'vmware_hosts':len(vmware_hosts),
            'total_hosts':len(total_hosts),
            'empty_hosts':len(empty_hosts),
            'operationalStatus':status,
            'power':power,
            'temperature':temperature,
            'serial_number':getEnclosureSerialNoFromOA(oa),
            'rack_name':getEnclosureRackNameFromOA(oa),
            'servers':bays,
            'fans':fans,
            'powerSupplies':power_supplies,
            'interConnects':interconnects 
            }

def getHostsInOA(hosts_uuid_list, oa):
    __hosts__ = []
    if hosts_uuid_list:
        hosts_uuid_list = map(lambda x:x.lower(), hosts_uuid_list)
    blade_array = oa['blade_info']['bladeInfo']
    for blade in blade_array:
        for xtra_data in blade['extraData']:
            if xtra_data['_name'] in ['uuid', 'virtualUuid1', 'cUUID']:
                if xtra_data['value'].lower() in hosts_uuid_list:
                    __hosts__.append(xtra_data['value'].lower())
    return __hosts__
        
def anyHostInOA(hosts_uuid_list, oa):
    if hosts_uuid_list:
        hosts_uuid_list = map(lambda x:x.lower(), hosts_uuid_list)
    blade_array = oa['blade_info']['bladeInfo']
    for blade in blade_array:
        for xtra_data in blade['extraData']:
            if xtra_data['_name'] in ['uuid', 'virtualUuid1', 'cUUID']:
                if xtra_data['value'].lower() in hosts_uuid_list:
                    return True
    return False
        
def getEnclosureModelFromOA(oa):
    return oa['enclosure_info']['name']

def getEnclosureNameFromOA(oa):
    return oa['enclosure_info']['enclosureName']

def getEnclosureUuidFromOA(oa):
    return oa['enclosure_info']['uuid']

def getEnclosureSerialNoFromOA(oa):
    return oa['enclosure_info']['serialNumber']

def getEnclosureRackNameFromOA(oa):
    return oa['enclosure_info']['rackName']

def getOAFirmwareInfoFromOA(oa):
    return [x['fwVersion'] for x in oa['oa_info']['oaInfo'] if x['fwVersion'] ]

def getEnclosureTotalPowerConsumedFromOA(oa):
    pwrinfo = oa['power']
    return {"consumed":round(pwrinfo['inputPower']) ,
            "capacity":round(pwrinfo['inputPowerCapacity']) }

def getPresentHostsFromOA(oa, hosts_uuid_list=None):
    if hosts_uuid_list:
        hosts_uuid_list = map(lambda x:x.lower(), hosts_uuid_list)
    blade_array = oa['blade_info']['bladeInfo']
    if not hosts_uuid_list:
        return [blade for blade in blade_array if blade['presence'] == 'PRESENT']
    present = {}
    for blade in blade_array:
        if blade['presence'] != 'PRESENT':
            continue
        for xtra_data in blade['extraData']:
            if xtra_data['_name'] in ['uuid', 'virtualUuid1', 'cUUID']:
                uuid = xtra_data['value'].lower() 
                if uuid in hosts_uuid_list:
                    present[uuid] = blade
                    continue
    return present

def getAbsentHostsFromOA(oa):
    blade_array = oa['blade_info']['bladeInfo']
    return [blade for blade in blade_array if blade['presence'] == 'ABSENT' and blade['bayNumber'] <= oa['enclosure_info']['bladeBays']]

def getSubsumedBaysFromOA(oa):
    blade_array = oa['blade_info']['bladeInfo']
    return [blade for blade in blade_array if blade['presence'] == 'SUBSUMED']

def getTemperatureFromOA(oa):
    temperature = oa['ambient']
    return {'current': temperature['temperatureC'] ,
            'caution':temperature['cautionThreshold'] ,
            'critical':temperature['criticalThreshold'] }

def getHealthStatusFromOA(oa):
    return oa['enclosure_status']['operationalStatus']

def getOANetworkInfo(oa):
    network = oa['address']
    if network['ipv4']:
        return network['ipv4'] 
    elif network['host']:
        return network['host'] 
    else:
        return network['ipv6'] 

def getVmwareHostsNames(vc, vmhosts_uuid_list):
    '''
    Returns the names of VMWare hosts
    @param vc: A vCenter object
    @param vmhosts_uuid_list: a list of vmware hosts' uuids in teh format
    ['<host 1 uuid>', '<host 2 uuid>', ...]
    @return: A dictionary with the schema {'<host 1 uuid>':'<host 1name>',
                                    '<host 1 uuid>':'<host 1name>',...}

    '''
    ret = {}
    
    for host in vc.get_hosts() :
        ret[host.hardware.systemInfo.uuid.lower()] = host.name
        
    return ret

def getVmwareBays(vmware_hosts, vmware_hosts_name):
    return [{"bay_no":host['bayNumber'] , "name":vmware_hosts_name[uuid]} for uuid, host in vmware_hosts.items()]

def getAllPowerSuppliesInfo(oa):
    power_supplies = oa['ps_info']['powerSupplyInfo']
    ret = []

    ps_status = {}
    for stat in oa['ps_status']['powerSupplyStatus']:
        ps_status[stat['bayNumber']] = stat['operationalStatus']

    for ps in power_supplies:
        if ps['presence'] != 'PRESENT':
            continue
        try:
            model_name = [x['value'] for x in ps['extraData'] if x['_name'] == 'productName'][0]
        except:
            model_name = None
        ret.append({'bay_no':ps['bayNumber'],
            'presentPower':ps['actualOutput'],
            'model':model_name,
            'status':ps_status[ps['bayNumber']]
            })
    return ret


def getAllFansInfo(oa):
    all_fans = oa['fan_info']['fanInfo']
    ret = []
    for fan in all_fans:
        try:
            ret.append({'bay_no':fan['bayNumber'],
                        'speed':str(round(fan['fanSpeed']*100/float(fan['maxFanSpeed']))) + '%',
                        'model':fan['name'], 
                        'status':fan['presence']})
        except ZeroDivisionError:
            ret.append({'bay_no':fan['bayNumber'],
                        'speed':'-',
                        'model':fan['name'], 
                        'status':fan['presence']})
    return ret


def getAllInterconnectsInfo(oa):
    interconnects = oa['tray_info']['interconnectTrayInfo']
    interconnectTrayPortMap =oa['tray_portmap']['interconnectTrayPortMap']
    status = {s['interconnectTrayBayNumber']:s['status'] for s in interconnectTrayPortMap} 
    ret = []
    for ic in interconnects:
        if ic['interconnectTrayType'] != 'INTERCONNECT_TRAY_TYPE_NO_CONNECTION':
            firmware = None
            for xd in ic['extraData']:
                if xd['_name'] == 'swmFWVersion':
                    firmware = xd['value']
                    break
            ret.append({'bay_no':ic['bayNumber'],
                'productName':ic['name'],
                'firmware':firmware,
                'status':status[ic['bayNumber']]})
    return ret

