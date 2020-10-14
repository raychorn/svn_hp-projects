'''
Created on Oct 25, 2011

@author: IslamM
'''

import web
from portlets.collector import Collector 
from portlets import stowwn
from copy import deepcopy
from util import config
from util import auth
from util import credential
from util.credential import credkey
from util.authclient import get_authclient
from datetime import datetime
from engines import hponeview
import time
import base64
import traceback
from util.stringcrypt import str_decrypt
from logging import getLogger
from requests import HTTPError
import json
from  util.scheduletasks import *
import engines.vc_engine as vc_engine
from vmware.vcenter import vCenter,ManagedObjectRef
from threading import Timer
from operator import itemgetter

from urlparse import urlparse
from core.uim import UIManager


from util import threadpool

log = getLogger(__name__)
component_name_map = {'memory': 'Memory',
                      'CPU': 'CPU',
                      'fan': 'Fan',
                      'tempSensor': 'Temperature Sensor',
                      'powerSupply': 'Power Supply',
                      'storage': 'Storage',
                      'network': 'Network',
                      'iLO': 'iLO',
                      'IML': 'Integrated Management Log',
                      'ASR': 'Automatic Server Recovery',}

def _getvCenters():
    pass

class Host:

    @staticmethod
    def hoststatus(dcollector=None):
        status_list = []
        try:
            if dcollector:
                dc = dcollector
            else:
                dc = Collector.get_collector()
            Host.add_hoststatus(status_list, dc.server, dc.ilo)
            Host.add_oastatus(status_list, dc.oa)
            if dc.oa :
                Host.add_vcmstatus(status_list, dc.vcm, dc.oa['vcm']['vcmUrl'])

            host_uuid=dc.host.hardware.systemInfo.uuid
            
            ovClient = Host.findOVClient(host_uuid)

            if ovClient :
                hpovserverProfileData = ovClient.getServerProfilesbyUuids([host_uuid])[host_uuid]
                Host.addOVProfileHealthStatus(status_list,ovClient.baseurl,hpovserverProfileData)

                hpovServerHardwareData = ovClient.getServersbyUuids([host_uuid])[host_uuid]
                Host.addOVHardwareHealthStatus(status_list,ovClient.baseurl,hpovServerHardwareData)
                
                
        except Exception as e:
            log.exception("Error in data.hostsummary(): %s", str(e))        
        return {'result': status_list}

    @staticmethod   
    def findOVClient(uuid):
        hpov_exists = False 
        hpov_creds = credential.get(credtype='hponeview')
        log.debug("Host: All HPOVs %s", hpov_creds)
        for x in hpov_creds:
            client = get_authclient().get(x)
            if not client:
                pw = credential.find_pw_for_host_and_type(
                            host = x['ip'], pwtype = 'hponeview')
                client = hponeview.hponeview(baseurl = x['ip'], verify=False)
                __passwd = str_decrypt(pw['epassword'])
                success = client.login(username = x['username'], password = __passwd)
                assert(success)
                get_authclient().add(credkey(typ=x['type'], ip = x['ip'],username = x['username']), client)
            
            hpov_exists = client.isServerManaged([uuid])[uuid]

            if hpov_exists:
                log.debug("hponeview managing this current uuid exists") 
                return client
        log.debug("hponeview managing this current uuid does not exists") 
        return None
        
        
    @staticmethod
    def convert_to_uim_status(status):
        if status.lower() in ('error', 'critical', 'major'):
            return "ERROR"
        elif status.lower() in ('minor', 'warning'):
            return "WARNING"
        return "OK"
    
    @staticmethod
    def component_printable_name(component_name):
        return component_name_map[component_name] if component_name_map.has_key(component_name) else component_name

    @staticmethod
    def get_oa_status(status):
        if status.upper() in ('OP_STATUS_ERROR', 'OP_STATUS_PREDICTIVE_FAILURE', 'OP_STATUS_NON_RECOVERABLE_ERROR'):
            return "ERROR"
        elif status.upper() in ('OP_STATUS_DEGRADED'):
            return "WARNING"
        elif status.upper() in ('OP_STATUS_OTHER', 'OP_STATUS_UNKNOWN'):
            return "INFORMATION"
        else:
            return "OK"
        
    @staticmethod
    def add_vcmstatus(data, vcm, vcmaddr):
        if vcm:
            stat = {}
            stat['name'] = vcmaddr
            #stat['detail'] = vcm['product'] + ', last updated: ' + vcm['time']['modified']
            stat['detail'] = 'HP Virtual Connect Manager'
                        
            stat['healthStatus'] = vcm['info']['serverVcProfiles']['profile'][0]['commonAttrs']['overallStatus']
            data.append(stat)
    
    @staticmethod
    def addOVProfileHealthStatus(data,fusionSource,profileHealth):
        stat = {}
        
        healthStatus = profileHealth["status"]
        lastChanged  = profileHealth["modified"]

        stat['detail'] =  'HP OneView' + ' : '  \
                            +  Host.component_printable_name('server-Profile') +  ' Component'  + ', last updated: ' + lastChanged

        stat['name'] = fusionSource
        if healthStatus == 'Unknown':
            healthStatus = 'warning';

        stat['healthStatus'] = Host.convert_to_uim_status(healthStatus)
        data.append(stat)

    @staticmethod
    def addOVHardwareHealthStatus(data,fusionSource,hardwareHealth):
        stat = {}
        
        healthStatus = hardwareHealth["status"]
        lastChanged  = hardwareHealth["modified"]

        stat['detail'] =  'HP OneView' + ' : '  \
                            +  Host.component_printable_name('server-Hardware') +  ' Component'  + ', last updated: ' + lastChanged

        stat['name'] = fusionSource
        if healthStatus == 'Disabled':
            healthStatus = 'warning';
            
        stat['healthStatus'] = Host.convert_to_uim_status(healthStatus)
        data.append(stat)


    @staticmethod
    def add_oastatus(data, oa):        
        
        if oa:
            stat = {}
            stat['name'] = oa['address']['host']
            stat['detail'] = oa['product'] + ' - ' + oa['enclosure_info']['rackName'] + ' : ' + oa['enclosure_info']['enclosureName'] \
                            + ', last updated: ' + oa['time']['modified']
            stat['healthStatus'] = Host.get_oa_status(oa['enclosure_status']['operationalStatus'])
            data.append(stat)

    @staticmethod
    def add_hoststatus(data, server, ilo):
        if server :
            for k,v in server['data']['status'].items():
                if k != '__classname__' and k != 'overall' and k != 'IML':
                    stat = {}
                    stat['name'] = server['address']['host'] 
                    stat['detail'] = server['product'] + ' - ' + server['protocol'] + ' : ' + Host.component_printable_name(k) + ' Component'  \
                                     + ', last updated: ' + server['time']['modified']
                    stat['healthStatus'] = Host.convert_to_uim_status(v)
                    data.append(stat)

        if ilo :
            for k,v in ilo['data']['get_embedded_health_data']['health_at_a_glance'].items():
                if v and k != '__classname__' and k != 'overall':
                    stat = {}
                    stat['name'] = ilo['address']['host'] 
                    stat['detail'] = ilo['product'] + ' - ' + k + ', last updated: ' + ilo['time']['modified']
                    if isinstance(v, list):
                        for vv in v:
                            if vv.has_key('status'):
                                v = vv
                                break
                            
                    stat['healthStatus'] = Host.convert_to_uim_status(v['status'].lower())
                    data.append(stat)

        
    @staticmethod
    def add_status(data, server):
        if not server :
            return
        stat = {}
        stat['overall'] = server['data']['status']['overall']
        stat['memory'] = server['data']['status']['memory']
        stat['cpu'] = server['data']['status']['CPU']
        stat['fan'] = server['data']['status']['fan']
        stat['temp_sensor'] = server['data']['status']['tempSensor']
        stat['ps'] = server['data']['status']['powerSupply']
        stat['storage'] = server['data']['status']['storage']
        stat['network'] = server['data']['status']['network']
        stat['ilo'] = server['data']['status']['iLO']
        stat['iml'] = server['data']['status']['IML']
        stat['asr'] = server['data']['status']['ASR']
        data['status'] = stat

    @staticmethod
    def add_provider_bundle_status(data, server):
        hp_providers_installed = None        
        if server :            
            hp_providers_installed = False
            for sw in server['data']['inventory']['Software']:
                if sw['Name'] == 'hp-smx':
                    hp_providers_installed = True    
                    break
                
        data['hp_bundle_status'] = hp_providers_installed
        

    @staticmethod
    def summary(dcollector=None):
        host_summary = {}
        try:
            if dcollector:
                datacollector = dcollector
            else:
                datacollector = Collector.get_collector()
            host_summary = {}
            Host.add_host_info(host_summary, datacollector.host_detail)

            cnt = 0
            #while not datacollector.ilo and cnt < 50 :
            #    cnt+=1
            #    time.sleep(0.1)
            #    log.debug("waiting for iLO %s", cnt)
                
            Host.add_ilo_info(host_summary, datacollector.ilo)
            Host.add_server_entity_data(host_summary, datacollector.server)
            Host.add_status(host_summary, datacollector.server)
            Host.add_provider_bundle_status(host_summary, datacollector.server)            
            host_summary['ilo_address'] = datacollector.ilo_address()
        except Exception as e:
            log.exception("Error in data.hostsummary(): %s", str(e))        
        return host_summary

    @staticmethod
    def add_host_info(data, vmwhostobj):
        if vmwhostobj :
            try:
                data["host_name"] = vmwhostobj.config.network.dnsConfig.hostName        
                data['product_name'] = vmwhostobj.hardware.systemInfo.model
                data['blade'] = 'bl' in data['product_name'].lower()
                log.debug("PRODUCT NAME!!!%s,",data['product_name'])
                data['vmware_name'] = vmwhostobj.name
                data['uuid'] = vmwhostobj.hardware.systemInfo.uuid
            except Exception as e:
                log.exception("Error in add_host_info")

    @staticmethod
    def add_power_cost_advantage(data, vc, ilo, host) :
        if ilo and host :
            try :        
                moref = '%s:%s' % (host._type, host.value)
                log.debug("Power Cost Advantage for host %s %s", host.name, moref)                
                average_power = ilo['data'].get('get_power_readings',{}).get('average_power_reading', {}).get('value', None)
                unit = ilo['data'].get('get_power_readings',{}).get('average_power_reading', {}).get('unit', None)
                if unit != 'Watts' :
                    log.error("Power Cost Advantage for host %s: Unhandled unit in power cost advantage calculation: %s", host.name, unit)
                    return
                log.debug("Power Cost Advantage for host %s: average power %s %s", host.name, average_power, unit)
                average_power = float(average_power)/1000.0 # convert to KW                                
                if average_power :                                   
                    t0 = vc.standby_log.get(moref, {}).get('entered_standby_mode', None)
                    t1 = vc.standby_log.get(moref, {}).get('exited_standby_mode', datetime.now()) or datetime.now()
                    if t0 :
                        power_cost = config.config().get_power_cost()                        
                        if power_cost :
                            log.debug("Power Cost Advantage for host %s %s", host.name, power_cost)
                            log.debug("Power Cost Advantage for host %s: entered: %s exited: %s", host.name, t0, t1)                        
                            delta = t1 - t0
                            total_seconds = delta.total_seconds()
                            hours = total_seconds/3600.0
                            data['power_cost_advantage'] = "%.2f" % (average_power * hours * power_cost)  # kW*h*$/kWh = $
                            log.debug('Power Cost Advantage for host %s: %s', host.name, data['power_cost_advantage'])
                        else :
                            log.debug('Power Cost Advantage for host %s: no power cost setting', host.name)
                    else :
                        log.debug("Power Cost Advantage for host %s: no entered standby mode time", host.name)
            except:
                log.exception("Error calculating Power Cost Advantage for host %s", host.name)
            
    @staticmethod
    def add_ilo_info(data, ilo):
        data['ilo_license_type'], data['ilo_firmware_version'], data['enclosure'], data['bay'] =  '', '', '', ''
            
        if ilo:
            data['ilo_license_type'] = ilo['data']['get_fw_version'].get('license_type','')
            data['ilo_firmware_version'] = ilo['data']['get_fw_version']['firmware_version']
            data['ilo_firmware_date'] = ilo['data']['get_fw_version']['firmware_date']
            # In HPCS 7.2 the value was in 'server_name'. In HPCS 7.3 'server_name' is a dict with the value in 'value'
            data['ilo_server_name'] = ilo['data']['server_name'].get('value', None) or ilo['data']['server_name']
            data['ilo_power'] = ilo['data'].get('get_power_readings',None)
            
            #blade should be true if blade and false if not
            #As a backup blade is also set based on the product name incase no ilo is available
            data['blade'] = False
            if ilo['data'].has_key('get_oa_info'):
                with open('iloData.txt', 'w') as outfile:
                    json.dump(ilo['data'], outfile)
                if ilo['data']:  
                    data['blade'] = True
                    oa_info = ilo['data']['get_oa_info']
                    data['enclosure'] =  oa_info['encl']
                    data['bay'] = oa_info['location']
            if ilo['data'].has_key('get_uid_status'):
                data['uid_status'] = ilo['data']['get_uid_status']['uid']
            if ilo['data'].has_key('get_pwreg'):
                data['power_status'] = ilo['data']['get_pwreg']['get_host_power']['host_power']
            data['total_cpu'] =  Host.get_cpu_info_string(ilo)
            Host.add_system_rom(data, ilo)

    @staticmethod
    def build_rom_string(items, sep):
        data = ''
        for item in items:
            if item['name'] in ['Family', 'Date', 'Redundant ROM Date']:
                data += item['value'] + sep
        return data.strip()
    
    @staticmethod
    def add_system_rom(data, ilo):
        data['backup_rom'] = 'Not Available'
        rom = Host.get_records(ilo, 0)
        if len(rom):
            rom = rom[0]
            data['rom'] = Host.build_rom_string(rom, ' ')
        bu_rom = Host.get_records(ilo, 193)
        if len(bu_rom):
            bu_rom = bu_rom[0]
            data['backup_rom'] = Host.build_rom_string(bu_rom, ' ')
        else:
            # smbios record 193 is not decoded by iLO. Let's try to decode it here
            try:
                bu_rom_raw = Host.get_records_raw(ilo, 193)
                bu_rom_binary = base64.b64decode(bu_rom_raw)
                if len(bu_rom_binary) > 5:
                    if ord(bu_rom_binary[4]):
                        start_of_string_array = ord(bu_rom_binary[1])
                        bu_rom_str_location =  ord(bu_rom_binary[5]) - 1
                        bu_rom_str_array = bu_rom_binary[start_of_string_array:].split('\0')
                        data['backup_rom'] = bu_rom_str_array[bu_rom_str_location]
            except:
                log.exception('Error decoding backup rom')

    @staticmethod
    def add_total_memory(data, server):            
        if not server :
            return
        mem = server['data']['summary']['physicalMemorySize']
        unit = mem[-2:]
        mem = mem[:-2]
        data['total_memory'] = mem + ' ' + unit  

    @staticmethod
    def get_capacity_in_GB(cap):
        if cap.endswith('MB'):
            capacity = int(cap.strip('MB')) / 1024
        else:
            capacity = int(cap.strip('GB'))
        return capacity
        
    @staticmethod
    def add_total_storage(data, server):
        if not server :
            return
        storlist = server['data']['inventory']['SmartArray']
        lvlist = []
        storage = 0
        for stor in storlist:
            if stor.has_key('LogicalDrive'):
                if isinstance(stor['LogicalDrive'], list):
                    for ld in stor['LogicalDrive']:
                        if ld.has_key('Capacity'):
                            cap = Host.get_capacity_in_GB(ld['Capacity'])
                            lvlist.append( cap )
            elif stor.has_key('Size'):
                cap = Host.get_capacity_in_GB(stor['Size'])
                lvlist.append( cap )
        for lv in lvlist:
            storage += lv
        data['total_storage'] = str(storage) + ' GB'
        
    @staticmethod 
    def add_server_entity_data(data, server):
        Host.add_total_memory(data, server)
        Host.add_total_nic(data, server)
        Host.add_total_storage(data, server)
                    
    @staticmethod
    def get_cpu_ext(cpu):
        for rec in cpu:
            if rec['name'] == "Execution Technology":
                return rec['value']
        return ''
    
    @staticmethod
    def valid_speed(cpu):
        for f in cpu:
            if f['value'].split(' ')[0] in ['0', '']:
                return False
        return True
    
    @staticmethod
    def total_cpu(cpus):
        num = 0
        for cpu in cpus:
            if Host.valid_speed(cpu):
                num = num + 1
        return num
    
    @staticmethod
    def get_cpu_info_string(ilo):
        cpus = Host.get_records(ilo, 4)
        total_cpu_desc = str(Host.total_cpu(cpus)) + ' of ' + str(len(cpus))
        return total_cpu_desc
        
    @staticmethod
    def get_records(ilo, rec_type):
        data = []
        if not ilo :
            return data
            
        smbios = ilo['data']['get_host_data']['smbios_record']
        
        for rec in smbios:
            if rec.has_key('type') and rec['type'] == rec_type:
                if rec.has_key('field'):
                    data.append(rec['field'])
        return data

    @staticmethod
    def get_records_raw(ilo, rec_type):
        if not ilo :
            return None
            
        smbios = ilo['data']['get_host_data']['smbios_record']
        
        for rec in smbios:
            if rec.has_key('type') and rec['type'] == rec_type:
                if rec.has_key('b64_data'):
                    return rec['b64_data']
        return None
    
    @staticmethod
    def add_total_nic(data, server):
        data['total_nics'] = None
        if server :            
            data['total_nics'] = len(server['data']['inventory']['NIC'])  
        
    @staticmethod
    def get_rec_field(cpu, field):
        for fld in cpu:
            if fld['name'] == field:
                return fld['value']
        return ''
    
    @staticmethod
    def add_cpu_detail(data, ilo, host_detail):
        cpus = Host.get_records(ilo, 4)
        cpuPkg = host_detail.hardware.cpuPkg[0]
        cpu_detail_list = []
        for cpu in cpus:
            cpu_detail = {}
            cpu_detail['name'] = Host.get_rec_field(cpu, 'Label')
            cpu_detail['description'] = cpuPkg.description
            cpu_detail['vendor'] = cpuPkg.vendor
            et = Host.get_rec_field(cpu, 'Execution Technology')
            cpu_detail['cores'] = et.split(';')[0].strip()
            cpu_detail['threads'] = et.split(';')[-1].strip()
            cpu_detail['speed'] = Host.get_rec_field(cpu, 'Speed')
            cpu_detail_list.append(cpu_detail)
            
        data['cpus'] = cpu_detail_list

    @staticmethod
    def add_memory_detail(data, ilo):
        mems = Host.get_records(ilo, 17)
        mem_detail_list = []
        data['memory_slots'] = str(len(mems))
        for mem in mems:
            mem_detail = {}
            mem_detail['name'] = Host.get_rec_field(mem, 'Label')
            mem_detail['size'] = Host.get_rec_field(mem, 'Size')
            mem_detail['speed'] = Host.get_rec_field(mem, 'Speed')
            mem_detail_list.append(mem_detail)
            
        data['memory_modules'] = mem_detail_list

    @staticmethod
    def add_storage_detail(data, server):
        local_storage_list = []
        for sa in server['data']['inventory']['SmartArray']:
            if not sa.has_key('Controller'):
                continue
            strg_cntrl = sa['Controller']
            cntrl = {}
            cntrl['name'] = strg_cntrl['CntlrName']
            cntrl['manufacturer'] = strg_cntrl['Manufacturer']
            cntrl['model'] = strg_cntrl['Model']
            cntrl['serial_number'] = strg_cntrl['SerialNumber']
            cntrl['firmware'] = strg_cntrl['FirmwareVersion']
            for logdrv in sa['LogicalDrive']:
                ldrv = {}
                ldrv['name'] = logdrv['LogDrvName']
                ldrv['capacity'] = logdrv['Capacity']
                ldrv['stripe_size'] = logdrv['StripeSize']
                ldrv['fault_tolerance'] = logdrv['FaultTolerance']
                cntrl['logical_drive'] = ldrv
            for pysdrv in sa['PhysicalDrive']:
                pdrv = {}
                pdrv['name'] = pysdrv['PhysDrvName']
                pdrv['serial_number'] = pysdrv['PhysDrvSerialNumber']
                pdrv['size'] = pysdrv['Size']
                pdrv['interface'] = pysdrv['Interface']
                pdrv['negotiated_speed'] = pysdrv['NegotiatedSpeed']
                cntrl['physical_drive'] = pdrv
            local_storage_list.append(cntrl)
            
        data['storage_detail'] = local_storage_list
        
    @staticmethod
    # component: is the name of the component in the server entity
    # obj_name is the name of the object created in the data dictionary
    # include filter based on if the Name contains entries in the lists
    def add_sw_component_detail(hostname, data, server, component, obj_name, include=[], max_list=None):
        sw_list = []
        if not server :
            return sw_list
            
        for item in server['data'].get('inventory', {}).get(component, []) :
            sw = {}
            sw['host'] = hostname
            sw['name'] = item.get('Name', '') or ''
            sw['description'] = item.get('Description', '') or ''
            sw['version'] = item.get('Version', '') or ''
            
            # FC HBAs have the WWN of the adaptor in the name
            if 'FC HBA' in sw['description'] and len(sw['name']) == 16 :
                sw['name'] = stowwn(sw['name'])
            
            if not include or [x for x in include if x in sw['name'] ] : # Make sure the name is in the include list or there is no include list
                if max_list and len(sw_list) < max_list :  # If there is a maxium set and the list is shorter than the max add the item
                    sw_list.append(sw)            
                elif not max_list :     # If there is not a maximum set then just add it to the list
                    sw_list.append(sw)            
                
        data[obj_name] = sw_list

    @staticmethod
    def add_sw_and_fw_detail(hostname, data, server):
        try:
            Host.add_sw_component_detail(hostname, data, server, 'Software', 'software')
            Host.add_sw_component_detail(hostname, data, server, 'Firmware', 'firmware')
        except:
            log.exception("Error getting sw fw detail")      
            if not data.haskey('software'):
                data['software'] = []
            if not data.haskey('firmware'):
                data['firmware'] = []

    @staticmethod
    def add_mp_detail(data, server):
        mp = {}
        m_proc = server['data']['inventory']['ManagementProcessor'][0]
        mp['model'] = m_proc['Model']
        mp['serial_number'] = m_proc['SerialNumber']
        mp['ip'] = m_proc['IPAddress']
        mp['url'] = m_proc['URL']
        data['management_processor'] = mp
        
    @staticmethod
    def add_logs(data, ilo):
        if ilo :
            data['ilolog'] = ilo['data']['ilolog']
            data['iml'] = ilo['data']['iml']
    
    @staticmethod        
    def detail():
        datacollector = Collector.get_collector()
        hostname = datacollector.host_detail.name
        details = {}
        summary = Host.summary(datacollector)
        try:
            Host.add_cpu_detail(details, datacollector.ilo, datacollector.host_detail)
            Host.add_memory_detail(details, datacollector.ilo)
            Host.add_storage_detail(details, datacollector.server)
            Host.add_mp_detail(details, datacollector.server)
            Host.add_sw_and_fw_detail(hostname,details, datacollector.server)
            Host.add_logs(details, datacollector.ilo)    
            Host.add_power_cost_advantage(details, datacollector.vcntr, datacollector.ilo, datacollector.host)
        except Exception as e:
            log.error("Error in data.hostdetail(): %s", str(e))
        details.update(summary)        
        return details
        
    @staticmethod        
    def swfwsummary():        
        data = {'software':[], 'firmware':[]}  
        
        try:          
            datacollector = Collector.get_collector()
            hostname = datacollector.host_detail.name
            Host.add_sw_component_detail(hostname, data, datacollector.server, 'Software', 'software', ['hp-smx',] )            
            if not len(data['software']) :
                Host.add_sw_component_detail(hostname, data, datacollector.server, 'Software', 'software', max_list = 3 )
            Host.add_sw_component_detail(hostname, data, datacollector.server, 'Firmware', 'firmware', ['Array', 'Power', 'iLO', 'System ROM'])
            if not len(data['firmware']) :
                Host.add_sw_component_detail(hostname, data, datacollector.server, 'Firmware', 'firmware', max_list = 3)
                
        except Exception:
            log.exception("Error in data.swfwsummary()")      
                    
        return data

    @staticmethod        
    def swfwdetail():
        datacollector = Collector.get_collector()
        hostname = datacollector.host_detail.name
        data = {}        
        try:                        
            Host.add_sw_and_fw_detail(hostname, data, datacollector.server)
        except Exception as e:
            log.exception("Error in data.swfwdetail(): %s", str(e))                
        return data    
        
    @staticmethod
    def infrasummary() :
        dc = Collector.get_collector()
        data = {'oa': None}        
        if dc.oa :
            data['oa'] = {'enclosure_info':{}}
            data['oa']['enclosure_info']['rackName'] = dc.oa['enclosure_info']['rackName']
            data['oa']['enclosure_info']['enclosureName'] = dc.oa['enclosure_info']['enclosureName']
            data['oa']['enclosure_info']['bladeBays'] = dc.oa['enclosure_info']['bladeBays']
            data['oa']['enclosure_info']['fanBays'] = dc.oa['enclosure_info']['fanBays']
            data['oa']['enclosure_info']['powerSupplyBays'] = dc.oa['enclosure_info']['powerSupplyBays']
            data['oa']['enclosure_info']['name'] = dc.oa['enclosure_info']['name']
            data['oa']['enclosure_info']['serialNumber'] = dc.oa['enclosure_info']['serialNumber']
            
            data['oa']['enclosure_info']['fansPresent']=0
            for i in dc.oa['fan_info']['fanInfo'] :
                if i['presence'] == 'PRESENT' :
                    data['oa']['enclosure_info']['fansPresent'] += 1
            
            data['oa']['enclosure_info']['powerSuppliesPresent']=0
            for i in dc.oa['ps_info']['powerSupplyInfo'] :
                if i['presence'] == 'PRESENT' :
                    data['oa']['enclosure_info']['powerSuppliesPresent'] += 1
                    
            data['oa']['enclosure_info']['bladesPresent']=0
            for i in dc.oa['blade_info']['bladeInfo'] :
                if i['presence'] == 'PRESENT' :
                    data['oa']['enclosure_info']['bladesPresent'] += 1        
                    
            data['oa']['power'] = {}
            data['oa']['power']['powerConsumed'] = dc.oa['power']['powerConsumed']    
            
        return data

    @staticmethod
    def infradetail() :
        dc = Collector.get_collector()
        data = {'oa': None}
        try:
            if dc.oa :
                data['oa'] = deepcopy(dc.oa)                
                    
        except:
            log.exception('Error adding infradetail')
        return data

    @staticmethod
    def simlaunchinfo() :
        q = web.input()
        dc = Collector.get_collector()
        data = {'sim_launch_info' : {} }
        cfg = config.config()
        role = -9999
        try :
            role = auth.get_effective_role(dc.sessionId, dc.moref, dc.serverGuid)
        except :
            log.exception("Error getting user effective role")
            return data
            
        log.debug('Effective Role for %s is %s', q.sessionId, role)
        if role != -1 :     # Read-Only users do not sim launch information.  Sorry...
            return data
        
        simpw = cfg.find_pw('HP SIM', None)
        if simpw :  
            try :            
                #data['sim_launch_info']['simhost'] = dc.host.name
                data['sim_launch_info']['simuser'] = simpw.username
                data['sim_launch_info']['simsso'] = simpw.password
            except :
                log.exception("Exception creating SIM launch tool")

        return data

    @staticmethod
    def addHpovLaunchLink(host_uuid):
        '''
        tests if a host is Fusion-managed. If yes,
        returns a launch link to the Fusion-instance that manages this host
        :param - host_uuid: the UUID of the host as a 'str' object
        '''
        # code to test Hp One View link
        try:        
            hpov_exists = False 
            hpov_creds = credential.get(credtype='hponeview')
            log.debug("Host: All HPOVs %s", hpov_creds)
            for x in hpov_creds:
                try:
                    client = get_authclient().get(x)
                    log.debug("Found HPOV client %s", client)
                    if not client:
                        pw = credential.find_pw_for_host_and_type(
                                host = x['ip'], pwtype = 'hponeview')
                        log.debug("Host.py: HPOV cred = %s and password = %s ", x, pw)
                        client = hponeview.hponeview(baseurl = x['ip'], verify=False)
                        __passwd = str_decrypt(pw['epassword'])
                        log.debug("Password = %s", __passwd)
                        success = client.login(username = x['username'], password = __passwd)
                        assert(success)
                        get_authclient().add(credkey(typ=x['type'], ip = x['ip'], 
                            username = x['username']), client)
                        log.debug("host_uuid = %s", host_uuid )
                        #host_uuid = host_uuid.upper() 
                    hpov_exists = client.isServerManaged([host_uuid])[host_uuid]
                    log.debug("hpov_exists = %s", hpov_exists) 
                    lt = {}
                    if (hpov_exists):
                        lt['id'] = 'hp_one_view_launch_tool'
                        lt['icon_url'] = "/static/img/icons/oneview_icon.png"
                        lt['launch_links'] = []
                        ll = {}
                        ll['type'] = 'LINK'
                        ll['label'] = 'HPOV Login Page'
                        ll['url'] = 'https://'+client.baseurl
                        lt['launch_links'].append(ll)
                        log.debug("Inside of HPOV: lt = %s", lt)
                        return lt 
                except :
                    log.error('Unable to contact HPOV = %s', client.baseurl)
                    continue
        except:
            log.exception("Exception creating HP One View launch tool")
            log.debug("Traceback %s", traceback.format_exc())
        return None
        # End code to test Hp One View link
                
    @staticmethod
    def launchtools() :
        q = web.input()
        dc = Collector.get_collector()
        data = { 'launch_tools' : [] }        
        cfg = config.config()
        role = -9999
        try :
            log.debug("Collector serverguid = %s, moref = %s", dc.serverGuid, dc.moref)
            role = auth.get_effective_role(dc.sessionId, dc.moref, dc.serverGuid)
        except :
            log.exception("Error getting user effective role")
            return data
            
        log.debug('Effective Role for %s is %s', q.sessionId, role)
        if role == -2 :     # Read-Only users do not get launch links.  Sorry...
            return data
        
        if role == -9999 :  # Error reading effective role
            return data
        lt = Host.addHpovLaunchLink(dc.host.hardware.systemInfo.uuid)
        hpov_exists = lt
        if (hpov_exists):
            data['launch_tools'].append(lt)
        ilo_address = dc.ilo_address()
        log.debug("Launch Tools ilo address %s", ilo_address)
        try :            
            if ilo_address :
                lt = {}
                lt['id'] = 'ilo_launch_tool'
                lt['icon_url'] = "/static/img/icons/iLO_icon.png"
                lt['launch_links'] = []
                
                pw = cfg.find_pw('iLO', ilo_address)                    
                
                if pw :                
                    if role == -1 :
                        # Single Sign On
                        ll = {}
                        ll['type'] = 'SSO'
                        ll['label'] = 'Web Administration'
                        ll['url_base'] = 'https://' + ilo_address + '/login.htm'
                        ll['url'] = ''
                        ll['username'] = pw.username
                        ll['password'] = pw.password
                        lt['launch_links'].append(ll)
                
                        ll = {}
                        ll['type'] = 'SSO'
                        ll['label'] = 'Integrated Remote Console'
                        ll['url_base'] = 'https://' + ilo_address + '/login.htm'
                        ll['url'] = 'IRC'
                        ll['username'] = pw.username
                        ll['password'] = pw.password
                        lt['launch_links'].append(ll)
                
                        ll = {}
                        ll['type'] = 'SSO'
                        ll['label'] = 'Remote Console'
                        ll['url_base'] = 'https://' + ilo_address + '/login.htm'
                        ll['url'] = 'REMCONS'
                        ll['username'] = pw.username
                        ll['password'] = pw.password
                        lt['launch_links'].append(ll)
                    else :
                        # Non Admins just get a link to iLO
                        ll = {}
                        ll['type'] = 'LINK'
                        ll['label'] = 'iLO'
                        ll['url'] = 'https://' + ilo_address
                        lt['launch_links'].append(ll)
                else :
                    # If WE don't have iLO creds then just give them a link it iLO
                    log.error('Launch Tools: No iLO credentials for %s', ilo_address)
                    ll = {}
                    ll['type'] = 'LINK'
                    ll['label'] = 'iLO'
                    ll['url'] = 'https://' + ilo_address
                    lt['launch_links'].append(ll)
                    
                data['launch_tools'].append(lt)
                
        except :
            log.exception("Exception creating iLO launch tool")
                
        
        try :
            if dc.oa :
                lt = {}
                lt['id'] = 'oa_launch_tool'
                lt['icon_url'] = "/static/img/icons/oa_icon.png"
                lt['launch_links'] = []
                
                if role == -1 :
                    ll = {}
                    ll['type'] = 'LINK'
                    ll['label'] = 'OA SSO'
                    ll['url'] = cfg.get_uim_root() + '/sso/' + web.ctx.query + '&ssoip=' + dc.oa['address']['ipv4']
                    lt['launch_links'].append(ll)
                else :
                    ll = {}
                    ll['type'] = 'LINK'
                    ll['label'] = 'OA Login Page'
                    ll['url'] = 'https://' + dc.oa['address']['ipv4']
                    lt['launch_links'].append(ll)
                    
                data['launch_tools'].append(lt)                
        except :
            log.exception("Exception creating host OA launch tool")
        
        try :
            if dc.oa and dc.oa['vcm']['isVcmMode'] and (not hpov_exists):
                lt = {}
                lt['id'] = 'vcm_launch_tool'
                lt['icon_url'] = "/static/img/icons/vc_icon.png"
                lt['launch_links'] = []
                
                ll = {}
                ll['type'] = 'LINK'
                ll['label'] = 'VCM'
                ll['url'] = dc.oa['vcm']['vcmUrl']
                lt['launch_links'].append(ll)
                
                data['launch_tools'].append(lt)
                log.debug("Inside of VC: lt = %s", lt)
        except :
            log.exception("Exception creating host VCM launch tool")
        try:
            log.debug("Outside of VC: data['launchtools'] = %s", data['launchtools'])
        except:
            pass
        simpw = cfg.find_pw('HP SIM', None)
        if simpw :  
            try :            
                
                lt = {}
                lt['id'] = 'sim_launch_tool'
                lt['icon_url'] = "/static/img/icons/sim_icon.png"
                lt['launch_links'] = []
                
                if role == -1 :
                    ll = {}
                    ll['type'] = 'LINK'
                    ll['label'] = 'SIM SSO'
                    ll['url'] = cfg.get_uim_root() + '/static/hpsimlogin.html' + web.ctx.query + '&simtool=default&simserver=' + simpw.host + '&simport=' + str(cfg.get_simport()) + '&simhost=' + dc.host.name
                    lt['launch_links'].append(ll)
                else :
                    ll = {}
                    ll['type'] = 'LINK'
                    ll['label'] = 'SIM'
                    ll['url'] = 'https://' + simpw.host + ':' + str(cfg.get_simport())
                    lt['launch_links'].append(ll)
                
                
                
                data['launch_tools'].append(lt)
            except :
                log.exception("Exception creating host SIM launch tool")
                
            try :            
                
                lt = {}
                lt['id'] = 'ipm_launch_tool'
                lt['icon_url'] = "/static/img/icons/ipm_icon.png"
                lt['launch_links'] = []
                
                if role == -1 :
                    ll = {}
                    ll['type'] = 'LINK'
                    ll['label'] = 'IPM SSO'
                    ll['url'] = cfg.get_uim_root() +'/static/hpsimlogin.html' + web.ctx.query + '&simtool=power&simserver=' + simpw.host + '&simport=' + str(cfg.get_simport()) + '&simhost=' + dc.host.name
                    lt['launch_links'].append(ll)
                else :
                    ll = {}
                    ll['type'] = 'LINK'
                    ll['label'] = 'IPM'
                    ll['url'] = 'https://' + simpw.host + ':' + str(cfg.get_simport())
                    lt['launch_links'].append(ll)
                
                
                
                data['launch_tools'].append(lt)
            except :
                log.exception("Exception creating host SIM PM launch tool")

            try :            
                if dc.oa and dc.oa['vcm']['isVcmMode'] and dc.vcm and dc.vcm['externalManager'] :
                    lt = {}
                    lt['id'] = 'vcem_launch_tool'
                    lt['icon_url'] = "/static/img/icons/vcem_icon.png"
                    lt['launch_links'] = []
                                        
                    if role == -1 :
                        ll = {}
                        ll['type'] = 'LINK'
                        ll['label'] = 'VCEM SSO'
                        ll['url'] = cfg.get_uim_root() +'/static/hpsimlogin.html' + web.ctx.query + '&simtool=vcem&simserver=' + simpw.host + '&simport=' + str(cfg.get_simport()) + '&simhost=' + dc.host.name
                        lt['launch_links'].append(ll)
                    else :
                        ll = {}
                        ll['type'] = 'LINK'
                        ll['label'] = 'VCEM'
                        ll['url'] = dc.oa['vcm']['vcmUrl']
                        lt['launch_links'].append(ll)
                        
                    data['launch_tools'].append(lt)
                
            except :
                log.exception("Exception creating VCEM launch tool")
        log.debug("Just before returning: data = %s", data)
        return data
        
        
    @staticmethod
    def commsummary() :
        dc = Collector.get_collector()
        
        ovCacheData = None
        
        host_uuid=dc.host.hardware.systemInfo.uuid

        ovClient = Host.findOVClient(host_uuid)

        if ovClient == None :
            ov_checkCache = hponeview.isServerManagedCache(host_uuid)
            if(ov_checkCache):
                ovCacheData = True
            else:
                ovCacheData = False
            

        if ovClient or ovCacheData:
            data = {'oa': None, 'ilo': None, 'ov': None, 'server': None, 'blade': False , 'fusionmanaged':False}

        else:
            data = {'oa': None, 'ilo': None, 'vcm': None, 'server': None, 'blade': False,'fusionmanaged':False}
            
        try:
            if dc.host_detail:
                if 'bl' in dc.host_detail.hardware.systemInfo.model.lower():
                    data['blade'] = True
        except:
            log.exception('Error adding blade property to communication status')
            
        try:
            if dc.oa :
                data['oa'] = {}
                data['oa']['status'] = dc.oa['status']
                try:
                    last_update = datetime.strptime(dc.oa['time']['modified'], '%Y-%m-%dT%H:%M:%S.%f')                
                    data['oa']['last_update'] = last_update.strftime("%H:%M:%S %m-%d-%Y")
                    dt = datetime.utcnow() - last_update
                    data['oa']['last_update_seconds'] = int(dt.total_seconds())
                except :
                    log.error("Error parsing oa modified time %s", dc.oa['time']['modified'])
        except:
            log.exception('Error adding oa entity comm status')
            
        try:
            if ovClient:
                data['fusionmanaged'] = True
                data['ov'] = {}
                data['ov']['status'] = 'normal'
                try:
                    data['ov']['last_update'] = datetime.now().strftime('%H:%M:%S %m-%d-%Y')
                except :
                    log.exception("Error setting last_update time for HP OneView comm status")
            elif(ovCacheData):
                data['fusionmanaged'] = True
                data['ov'] = {}
                data['ov']['status'] = 'unavailable'
                try:
                    data['ov']['last_update'] = datetime.now().strftime('%H:%M:%S %m-%d-%Y')
                except :
                    log.exception("Error setting last_update time for HP OneView comm status")
        except:
            log.exception('Error adding ov entity comm status')
        try:
            if dc.ilo :
                data['ilo'] = {}
                data['ilo']['status'] = dc.ilo['status']
                try:
                    last_update = datetime.strptime(dc.ilo['time']['modified'], '%Y-%m-%dT%H:%M:%S.%f')
                    data['ilo']['last_update'] = last_update.strftime("%H:%M:%S %m-%d-%Y")
                    dt = datetime.utcnow() - last_update
                    data['ilo']['last_update_seconds'] = int(dt.total_seconds())
                except :
                    log.error("Error parsing ilo modified time %s", dc.ilo['time']['modified'])
        except:
            log.exception('Error adding ilo entity comm status')    
        
        try:
            if dc.vcm :  
                data['vcm'] = {}
                data['vcm']['status'] = dc.vcm.get('status', 'unknown')                
                try:
                    last_update = datetime.strptime(dc.vcm['time']['modified'], '%Y-%m-%dT%H:%M:%S.%f')
                    data['vcm']['last_update'] = last_update.strftime("%H:%M:%S %m-%d-%Y")
                    dt = datetime.utcnow() - last_update
                    data['vcm']['last_update_seconds'] = int(dt.total_seconds())
                except :
                    log.error("Error parsing vcm modified time %s", dc.vcm['time']['modified'])
        except:
            log.exception('Error adding vcm entity comm status')
        
        try:
            if dc.server :
                data['server'] = {}
                data['server']['status'] = dc.server['status']
                try:
                    last_update = datetime.strptime(dc.server['time']['modified'], '%Y-%m-%dT%H:%M:%S.%f')
                    data['server']['last_update'] = last_update.strftime("%H:%M:%S %m-%d-%Y")
                    dt = datetime.utcnow() - last_update
                    data['server']['last_update_seconds'] = int(dt.total_seconds())
                except :
                    log.error("Error parsing server modified time %s", dc.vcm['time']['modified'])
        except:
            log.exception('Error adding server entity comm status')
        
        log.debug("the data is : %s",data)
        return data
        
from util import threadpool
__Q__ = threadpool.ThreadQueue(10)
@threadpool.threadify(__Q__)
def __applyBaseline__(identifier,vcguid, host_uuid, baseline_uri, restart, enter_maintenance_mode, exit_maintenance_mode):
    Host.applyBaseline(identifier,vcguid, host_uuid, baseline_uri, restart, enter_maintenance_mode, exit_maintenance_mode)
#__applyBaseline__(None, None, None, None, None, None, None)

