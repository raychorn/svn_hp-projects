'''
Created on Oct 25, 2011

@author: IslamM
'''

from engines import vc_engine 
from engines.csc_engine import csc_engine
from util import catalog
from copy import deepcopy
import string 
import web, time

from logging import getLogger
log = getLogger(__name__)

def mac_format(mac) :
    mac = mac.lower()
    mac = mac.replace('-',':')
    return mac

def stowwn(s) : 
    if s[:2] == '0x' :
        s = s[2:]
    if s[-1] == 'L' :
        s = s[:-1]    
    s = s[:16]
    s = ('0' * (16-len(s))) + s
    s = ':'.join(s[i:i+2] for i in range(0, len(s), 2)).lower()
    return s
    
def itowwn(i) :
    "Convert an integer to wwn string"
    s = hex(i)
    return stowwn(s)

def get_webinput():
    moref, serverGuid, sessionId = None, None, None
    try:
        q = web.input()
        moref = q.moref
        serverGuid = q.serverGuid
        sessionId = q.sessionId
    except:
        pass
    return moref, serverGuid, sessionId

class collector:
    def __init__(self, moref, serverGuid, sessionID):
        self.moref, self.serverGuid, self.sessionId = moref, serverGuid, sessionID
        self.vcntr = vc_engine.get_vcenter(self.moref)
        self.host = self.vcntr.get_host(self.moref)
        self.csce = catalog.get_all(csc_engine)[0]

        self.host_detail, self.ds, self.dvpg, self.vdvsw, self.vms = None, None, None, None, None
        self.server, self.ilo, self.oa, self.vcm = None, None, None, None

        self.collect_vcenter_data()
        self.collect_cs_data()
        self.time_stamp = time.time()

    def collect_host_detail(self):        
        self.host_detail = self.vcntr.get_host_details(self.moref)
    
    def collect_ds(self):        
        self.ds = self.vcntr.get_ds_for_host(self.moref)

    def collect_dvpg(self):            
        self.dvpg = self.vcntr.get_dvpg()

    def collect_vdvsw(self):             
        self.vdvsw = self.vcntr.get_vdvsw()

    def collect_vms(self):        
        self.vms = self.vcntr.get_vms_for_host(self.moref)
        
    def collect_vcenter_data(self, force=False):        
        if not self.host_detail or force:
            self.collect_host_detail()
        if not self.ds or force:
            self.collect_ds()
        if not self.dvpg or force:
            self.collect_dvpg()
        if not self.vdvsw or force:
            self.collect_vdvsw()
        if not self.vms or force:
            self.collect_vms()        
        
    def collect_server(self):        
        try:
            self.server = self.csce.get_hostinfo('server', self.host.hardware.systemInfo.uuid)
        except:
            log.exception('error getting server_entity')

    def collect_ilo(self):        
        try:
            self.ilo = self.csce.get_hostinfo('ilo', self.host.hardware.systemInfo.uuid)
        except:
            log.exception('error getting ilo_entity')

    def collect_oa(self):        
        try:        
            if self.ilo['containedby']['uuid']:
                self.oa = self.csce.get_hostinfo('oa', self.ilo['containedby']['uuid'][0])
        except:
            log.exception('error getting oa_entity')
            
    def collect_vcm(self):             
        try:            
            if self.oa and self.oa.has_key('vcm') and len(self.oa['vcm']['vcmUrl']):                
                self.vcm = self.csce.get_hostinfo('vc', self.host.hardware.systemInfo.uuid)
        except:
            log.exception('error getting vc_entity')        

    def collect_cs_data(self, force=False):        
        if not self.server or force: 
            self.collect_server()
        if not self.ilo or force: 
            self.collect_ilo()
        if not self.oa or force: 
            self.collect_oa()
        if not self.vcm or force: 
            self.collect_vcm()        
        
    def update(self):
        force = False
        et = time.time() - self.time_stamp
        if et > 300:
            force = True
        self.collect_vcenter_data(force)
        self.collect_cs_data(force)
        if force:
            self.time_stamp = time.time()
    
    @staticmethod
    def get_collector():
        moref, serverGuid, sessionId = get_webinput()
        datacollector = catalog.lookup('%s:%s' %(sessionId, moref) )
        if not datacollector:
            datacollector = collector(moref, serverGuid, sessionId)
            catalog.insert(datacollector, '%s:%s' %(sessionId, moref) )
        datacollector.update()
        return datacollector

class host:
    @staticmethod
    def add_status(data, server):
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
    def summary(dcollector=None):
        host_summary = {}
        try:
            if dcollector:
                datacollector = dcollector
            else:
                datacollector = collector.get_collector()
            host_summary = {}
            host.add_host_info(host_summary, datacollector.host_detail)
            host.add_ilo_info(host_summary, datacollector.ilo)
            host.add_server_entity_data(host_summary, datacollector.server)
            host.add_status(host_summary, datacollector.server)
        except Exception as e:
            log.exception("Error in data.hostsummary(): %s", str(e))        
        return host_summary

    @staticmethod
    def add_host_info(data, host):
        data["host_name"] = host.config.network.dnsConfig.hostName        
        data['product_name'] = host.hardware.systemInfo.model
        data['vmware_name'] = host.name
        data['uuid'] = host.hardware.systemInfo.uuid

    @staticmethod
    def add_ilo_info(data, ilo):
        data['ilo_license_type'], data['ilo_firmware_version'], data['enclosure'], data['bay'] =  '', '', '', ''
        if ilo:
            data['ilo_license_type'] = ilo['data']['get_fw_version']['license_type']
            data['ilo_firmware_version'] = ilo['data']['get_fw_version']['firmware_version']            
            # In HPCS 7.2 the value was in 'server_name'. In HPCS 7.3 'server_name' is a dict with the value in 'value'
            data['ilo_server_name'] = ilo['data']['server_name'].get('value', None) or ilo['data']['server_name']
            if ilo['data'].has_key('get_oa_info'):
                oa_info = ilo['data']['get_oa_info']
                data['enclosure'] =  oa_info['encl']
                data['bay'] = oa_info['location']
            if ilo['data'].has_key('get_uid_status'):
                data['uid_status'] = ilo['data']['get_uid_status']['uid']
            if ilo['data'].has_key('get_pwreg'):
                data['power_status'] = ilo['data']['get_pwreg']['get_host_power']['host_power']
            data['total_cpu'] =  host.get_cpu_info_string(ilo)
            host.add_system_rom(data, ilo)

    @staticmethod
    def build_rom_string(items, sep):
        data = ''
        for item in items:
            if item['name'] in ['Family', 'Date', 'Redundant ROM Date']:
                data += item['value'] + sep
        return data.strip()
    
    @staticmethod
    def add_system_rom(data, ilo):
        rom = host.get_records(ilo, 0)
        if len(rom):
            rom = rom[0]
            data['rom'] = host.build_rom_string(rom, ' ')
        bu_rom = host.get_records(ilo, 193)
        if len(bu_rom):
            bu_rom = bu_rom[0]
            data['backup_rom'] = host.build_rom_string(bu_rom, ' ')
        else:
            data['backup_rom'] = 'Not Available'

    @staticmethod
    def add_total_memory(data, server):            
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
        storlist = server['data']['inventory']['SmartArray']
        lvlist = []
        storage = 0
        for stor in storlist:
            if stor.has_key('LogicalDrive'):
                if isinstance(stor['LogicalDrive'], list):
                    for ld in stor['LogicalDrive']:
                        if ld.has_key('Capacity'):
                            cap = host.get_capacity_in_GB(ld['Capacity'])
                            lvlist.append( cap )
            elif stor.has_key('Size'):
                cap = host.get_capacity_in_GB(stor['Size'])
                lvlist.append( cap )
        for lv in lvlist:
            storage += lv
        data['total_storage'] = str(storage) + ' GB'
        
    @staticmethod 
    def add_server_entity_data(data, server):
        host.add_total_memory(data, server)
        host.add_total_nic(data, server)
        host.add_total_storage(data, server)
                    
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
            if host.valid_speed(cpu):
                num = num + 1
        return num
    
    @staticmethod
    def get_cpu_info_string(ilo):
        cpus = host.get_records(ilo, 4)
        total_cpu_desc = str(host.total_cpu(cpus)) + ' of ' + str(len(cpus))
        return total_cpu_desc
        
    @staticmethod
    def get_records(ilo, rec_type):
        smbios = ilo['data']['get_host_data']['smbios_record']
        data = []
        for rec in smbios:
            if rec.has_key('type') and rec['type'] == rec_type:
                if rec.has_key('field'):
                    data.append(rec['field'])
        return data
    
    @staticmethod
    def add_total_nic(data, server):
        nics = server['data']['inventory']['NIC']
        data['total_nics'] = len(nics)
        
    @staticmethod
    def get_rec_field(cpu, field):
        for fld in cpu:
            if fld['name'] == field:
                return fld['value']
        return ''
    
    @staticmethod
    def add_cpu_detail(data, ilo, host_detail):
        cpus = host.get_records(ilo, 4)
        cpuPkg = host_detail.hardware.cpuPkg[0]
        cpu_detail_list = []
        for cpu in cpus:
            cpu_detail = {}
            cpu_detail['name'] = host.get_rec_field(cpu, 'Label')
            cpu_detail['description'] = cpuPkg.description
            cpu_detail['vendor'] = cpuPkg.vendor
            et = host.get_rec_field(cpu, 'Execution Technology')
            cpu_detail['cores'] = et.split(';')[0].strip()
            cpu_detail['threads'] = et.split(';')[-1].strip()
            cpu_detail['speed'] = host.get_rec_field(cpu, 'Speed')
            cpu_detail_list.append(cpu_detail)
            
        data['cpus'] = cpu_detail_list

    @staticmethod
    def add_memory_detail(data, ilo):
        mems = host.get_records(ilo, 17)
        mem_detail_list = []
        data['memory_slots'] = str(len(mems))
        for mem in mems:
            mem_detail = {}
            mem_detail['name'] = host.get_rec_field(mem, 'Label')
            mem_detail['size'] = host.get_rec_field(mem, 'Size')
            mem_detail['speed'] = host.get_rec_field(mem, 'Speed')
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
    def add_sw_component_detail(data, server, component, obj_name, include=[]):
        sw_list = []
        for item in server['data']['inventory'][component]:
            sw = {}
            sw['name'] = item.get('Name', '')
            sw['description'] = item.get('Description', '')
            sw['version'] = item.get('Version', '')
            
            # FC HBAs have the WWN of the adaptor in the name
            if 'FC HBA' in sw['description'] and len(sw['name']) == 16 :
                sw['name'] = stowwn(sw['name'])
            
            if not include or [x for x in include if x in sw['name'] ] :                
                sw_list.append(sw)            
                
        data[obj_name] = sw_list

    @staticmethod
    def add_sw_and_fw_detail(data, server):
        host.add_sw_component_detail(data, server, 'Software', 'software')
        host.add_sw_component_detail(data, server, 'Firmware', 'firmware')

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
    def detail():
        datacollector = collector.get_collector()
        details = {}
        summary = host.summary(datacollector)
        try:
            host.add_cpu_detail(details, datacollector.ilo, datacollector.host_detail)
            host.add_memory_detail(details, datacollector.ilo)
            host.add_storage_detail(details, datacollector.server)
            host.add_mp_detail(details, datacollector.server)
            host.add_sw_and_fw_detail(details, datacollector.server)
        except Exception as e:
            log.error("Error in data.hostdetail(): %s", str(e))
        details.update(summary)        
        return details
        
    @staticmethod        
    def swfwsummary():
        datacollector = collector.get_collector()
        data = {}        
        try:                        
            host.add_sw_component_detail(data, datacollector.server, 'Software', 'software', ['hp-smx',] )
            host.add_sw_component_detail(data, datacollector.server, 'Firmware', 'firmware', ['Array', 'Power', 'iLO', 'System ROM'])
        except Exception as e:
            log.exception("Error in data.swfwsummary(): %s", str(e))                
        return data

    @staticmethod        
    def swfwdetail():
        datacollector = collector.get_collector()
        data = {}        
        try:                        
            host.add_sw_and_fw_detail(data, datacollector.server)
        except Exception as e:
            log.exception("Error in data.swfwdetail(): %s", str(e))                
        return data    
        
    @staticmethod
    def infrasummary() :
        dc = collector.get_collector()
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
        dc = collector.get_collector()
        data = {'oa': None}
        if dc.oa :
            data['oa'] = dc.oa
                
        return data
        
class network:

    @staticmethod
    def summary(dcollector = None):
        vmnic = []
        nics = {}        
        try:
            if dcollector: 
                datacollector = dcollector
            else:
                datacollector = collector.get_collector()
            network.add_pnic_info(vmnic, datacollector.host_detail.config.network)            
            network.add_server_nic_info(vmnic, datacollector.server['data']['inventory']['NIC'])
            network.add_pci_info(vmnic, datacollector.host_detail.hardware.pciDevice)
            if datacollector.oa and datacollector.ilo.has_key('data') and datacollector.ilo['data'].has_key('get_oa_info'):
                network.add_blade_physical_nics(datacollector.ilo['data']['get_oa_info']['location'], vmnic, datacollector.oa)
            else:
                network.add_physical_nics(vmnic, datacollector.ilo)
                
        except Exception as e:
            log.error("Error in data.networksummary(): %s", str(e))
        nics['nics'] = vmnic
                
        return nics

    @staticmethod
    def add_pnic_info(nic_summary, network):
        for pn in network.pnic:
            nic = {}
            nic['vmnic'] = pn.device
            if hasattr(pn, 'linkSpeed'):
                nic['speedMb'] = pn.linkSpeed.speedMb
                nic['speedGb'] = pn.linkSpeed.speedMb / 1000.0                
            nic['mac'] = pn.mac
            nic['pci'] = pn.pci
            nic['driver'] = pn.driver
            # Find the vswitch this pnic is on
            for vswitch in network.vswitch :                
                if pn.key in vswitch.pnic :
                    nic['vswitch'] = vswitch.name                    
                    break
            nic_summary.append( nic )

    @staticmethod
    def add_server_nic_info(nic_list, nics):
        for n in nics:
            nic = network.get_nic_by_mac(nic_list, 'mac', n['MACAddress'])
            if nic:
                nic['name'] = n['Name']
                nic['firmware_version'] = n['FirmwareVersion']
            
    @staticmethod
    def get_nic_by_mac(nic_list, field, value):
        for n in nic_list:
            if n[field] == value:
                return n
        return None
    
    @staticmethod
    def add_pci_info(nic_list, pci_devs):
        for pci in pci_devs:
            nic = network.get_nic_by_pci(nic_list, 'pci', pci.id)
            if nic:
                nic['slot'] = pci['slot']

    @staticmethod
    def get_nic_by_pci(nic_list, field, value):
        for n in nic_list:
            if n[field] == value:
                return n
        return None

    @staticmethod
    def find_lom_port(ilo, mac):
        nic_recs = host.get_records(ilo, 209)
        for nic in nic_recs:
            for i, nv in enumerate(nic):
                if nv['name'] == 'MAC':
                    if nv['value'].replace('-', ':').lower() == mac.lower():
                        return nic[i-1]['value']
                
    @staticmethod
    def add_physical_nics(pnics, ilo):
        for nic in pnics:
            if nic['slot'] == 0:
                port = network.find_lom_port(ilo, nic['mac'])
                nic['physical_nic'] =  'LOM-' + str(port)
            else:
                nic['physical_nic'] = 'PCI-Slot ' + str(nic['slot'])  

    @staticmethod
    def get_blade_mezz_info(bay, oa):
        for bladeMezzInfo in oa['mezz_info']['bladeMezzInfoEx']:
            if int(bladeMezzInfo['bayNumber']) == int(bay):
                return bladeMezzInfo

    @staticmethod
    def get_blade_port_map(bay, oa):
        for bladePortMap in oa['blade_portmap']['bladePortMap']:                            
            if int(bladePortMap['bladeBayNumber']) == int(bay) :
                return bladePortMap

    @staticmethod
    def check_flex_nic(nic, bay, oa):
        found = False
        bladeMezzInfo = network.get_blade_mezz_info(bay, oa)
        for mezz in bladeMezzInfo['mezzInfo'] :
            if not hasattr(mezz,'port'): continue
            for mezzPort in mezz['port']:
                if hasattr(mezzPort, 'guid'): continue
                for guid in mezzPort['guid']:
                    if nic['mac'].lower() == guid['guid'].lower() : 
                        found = True
                        if int(mezz['mezzNumber']) == 9 :
                            nic['physical_nic'] = 'LOM:' + str(mezzPort['portNumber']) + '-' + str(guid['physicalFunction'])
                        else:
                            nic['physical_nic'] = 'MEZZ' + str(mezz['mezzNumber']) + ':' + str(mezzPort['portNumber']) + '-' + str(guid['physicalFunction'])
        return found

    @staticmethod
    def check_regular_nic(nic, bay, oa):
        bladePortMap = network.get_blade_port_map(bay, oa)
        for mezz in bladePortMap['mezz'] :
            log.verbose('    mezz: %s', int(mezz['mezzNumber']))                        
            if mezz['mezzDevices']['type'] == "MEZZ_DEV_TYPE_MT": continue
            for mezzDevPort in mezz['mezzDevices']['port'] :
                if nic['mac'].lower() == mezzDevPort['wwpn'].lower() :
                    log.verbose('        port: %s, mac: %s', int(mezzDevPort['portNumber']), mezzDevPort['wwpn'].lower() )
                    if int(mezz['mezzNumber']) == 9 :
                        nic['physical_nic'] = 'LOM-' + str(mezzDevPort['portNumber'])                 
                    else :
                        nic['physical_nic'] = 'MEZZ' + str(mezz['mezzNumber']) + '-' + str(mezzDevPort['portNumber'])                

    @staticmethod
    def add_blade_physical_nics(bay, pnics, oa) :
        for nic in pnics :
            log.verbose('pnic: %s, mac: %s', nic['vmnic'], nic['mac'].lower())            
            found = network.check_flex_nic(nic, bay, oa)            
            if not found:
                network.check_regular_nic(nic, bay, oa)

    @staticmethod
    def detail():
        network_detail = {}
        datacollector = collector.get_collector()
        summary = network.summary(datacollector)
        networkdata = {}
        try:
            networkdata = network.get_network_data(datacollector)
        except Exception as e:
            log.error("Error in data.networksummary(): %s", str(e))
        network_detail.update(summary)
        network_detail.update(networkdata)
        
        return network_detail

    @staticmethod
    def get_network_data(datacollector):
        vms = datacollector.vms         
        raw_vcm = datacollector.vcm
        try:
            raw_vcm['enclosureType'] = None
            if datacollector.oa :                    
                enclosureType = datacollector.oa['enclosure_info']['name']
                if 'c7000' in enclosureType :
                    enclosureType = 'c7000'
                elif 'c3000' in enclosureType :
                    enclosureType = 'c3000'
                else :
                    log.warning('Unknown enclosure: %s' % enclosureType)
                raw_vcm['enclosureType'] = enclosureType
        except Exception as e:
            log.error("Error getting host(%s) enclosure type: %s", datacollector.host_detail.hardware.systemInfo.uuid, str(e)  );
        
        dvss = network.get_dvss(datacollector)
        vss = network.get_vss(datacollector)
        vcm = network.organize_vcm(datacollector)           
        datastores = network.get_fc_datastores(datacollector, vcm)        
    
        #network.check_4_bottle_necks(vcm)
        #network.organize_telemetry(vcm, raw_vcm)
    
        return {'vcm': vcm, 'vss':vss, 'dvss': dvss, 'datastores': datastores}

    @staticmethod    
    def get_dvss(datacollector) :
        """Function to collect and organize vmWare Distributed Virutal Switch data"""        
        if not datacollector.vdvsw:
            return []        
        dvss = []     
        moref = datacollector.host_detail.moref()
        # Get only DVPortgroups that contain this host
        if datacollector.dvpg:    
            dvpgs = [ x for x in datacollector.dvpg 
                        if hasattr(x.host, 'ManagedObjectReference') and 
                        moref in [v.value for v in x.host.ManagedObjectReference] ]       

        # Get all DVSwithes
        for d in datacollector.vdvsw: 
            # Only look at DVSwitches that have hosts
            if not getattr(d.config, "host", None):
                continue
            # Loop through hosts on this DVS looking for self    
            for host_index in range(len(d.config.host)) :            
                if d.config.host[host_index].config.host.value == moref:       
                    dvs = {}
                    dvs['name'] = d.config.name
                    dvs['host'] = d.config.host[host_index].config.host.value
                    dvs['uplink_port_groups'] = []  # Should only be one, but vmware returns a list - go figure
                    dvs['downlink_port_groups'] = []                    
                    
                        
                    # Get only DVPortgroups for this DVSwitch
                    host_dvpgs = [p for p in dvpgs if p.config.distributedVirtualSwitch.value == d.value]
                    # Get the Uplink Port Groups (probably will only be one, but vmware structure shows a list)
                    for pg in [ p.config for p in host_dvpgs if p.value in [x.value for x in d.config.uplinkPortgroup ] ] :
                        port_group = {}
                        port_group['name'] = pg.name
                        port_group['uplinks'] = []
                        # Added the uplink ports to the port group                        
                        for port_key in range(len(d.config.uplinkPortPolicy.uplinkPortName)) :
                            upn = d.config.uplinkPortPolicy.uplinkPortName[port_key]
                            uplink = {}
                            uplink['name'] = upn
                            uplink['pnics'] = []
                            # Added the nics to the port
                            for spec in getattr(d.config.host[host_index].config.backing, 'pnicSpec', []) :
                                if spec.uplinkPortKey == d.config.host[host_index].uplinkPortKey[port_key] :
                                    for pnic in datacollector.host_detail.config.network.pnic :
                                        if spec.pnicDevice == pnic.device :
                                            pnic.mac = mac_format(pnic.mac)                                            
                                            pnic['deviceName'] = None
                                            pnic['vendorName'] = None
                                            pnic['physicalPortMapping'] = None                                            
                                            pnic['speedGb'] = 0
                                            try :
                                                pnic['speedGb'] = int(pnic['linkSpeed']['speedMb'])/1000.0;
                                            except : 
                                                pass
                                            # Get PCI info for nic
                                            try :
                                                for pci in datacollector.host_detail.hardware.pciDevice :
                                                    if pci.id == pnic.pci :
                                                        pnic['deviceName'] = pci.deviceName
                                                        pnic['vendorName'] = pci.vendorName
                                            except Exception as e :
                                                pass
                                            
                                            # Get port mapping if available
                                            if datacollector.vcm :                                                
                                                for e in datacollector.vcm.get('info', {}).get('enetModulePorts', {}).get('item', []):                                                    
                                                    if pnic.mac == mac_format(e.get('macAddress', '')) :
                                                        pnic['physicalPortMapping'] = e.get('physicalPortMapping')
                                                        pnic['physicalPortMapping']['portType'] = 'port'
                                                        break
                                                    elif e.get('subPorts') :
                                                        for sp in e['subPorts'].get('item', []) :
                                                            if pnic.mac == mac_format(sp.get('macAddress', '')) :
                                                                pnic['physicalPortMapping'] = sp.get('physicalPortMapping')
                                                                pnic['physicalPortMapping']['portType'] = 'subport'
                                                                break

                                            uplink['pnics'].append(pnic)
                                        
                            port_group['uplinks'].append(uplink)
                        
                        dvs['uplink_port_groups'].append(port_group)
                    
                    #Get the Downlink Port Groups
                    for pg in [ p.config for p in host_dvpgs if p.value not in [x.value for x in d.config.uplinkPortgroup ] ] :
                        pd = {}
                        pd['name'] = pg.name
                        pd['key'] = pg.key
                        pd['vlanId'] = pg.defaultPortConfig.vlan.vlanId
                        pd['vms'] = []
                        # Find all the VMs with nics in this port group
                        for vm in datacollector.vms :
                            vm_data = None
                            for device in getattr(vm['hardware'], 'device', [] ) :                                
                                try :   # must be in try block because backing only exists if VM is connected to a DVS
                                    if pg.key == device.backing.port.portgroupKey :                                        
                                        if not vm_data :
                                            vm_data={}
                                            vm_data['name'] = vm['name'] or 'Unknown'                                                                                   
                                            vm_data['hardware'] = {'numCPU':vm['hardware'].numCPU, 'memoryMB':vm['hardware'].memoryMB}                                        
                                            vm_data['nics'] = []
                                            pd['vms'].append(vm_data)
                                        vm_data['nics'].append({'macAddress': mac_format(device.macAddress)})
                                except :
                                    pass
                                
                        dvs['downlink_port_groups'].append(pd)
                    
                    dvss.append(dvs)
                    break
                
        return dvss

    @staticmethod
    def get_vss(datacollector) :
        """Function to collect and organize vmWare Virutal Switch data"""        
        vss = []
        
        #Get all the vms
#        if vms == None :
#            vms = self.get_vms()
        
        for this_vswitch in getattr(datacollector.host_detail.config.network, 'vswitch', []) :
            vswitch = {}
            vswitch['name'] = this_vswitch.name
            vswitch['numPorts'] = this_vswitch.numPorts
            vswitch['numPortsAvailable'] = this_vswitch.numPortsAvailable
            vswitch['port_groups'] = []
            # Loop through port groups
            for this_pg in getattr(datacollector.host_detail.config.network, 'portgroup', []) :                
                # Only include port groups on this vswtich
                if this_pg.vswitch == this_vswitch.key :
                    pg = {}
                    pg['name'] = this_pg.spec.name
                    pg['vlanId'] = this_pg.spec.vlanId
                    pg['vms'] = []
                    # Get the VMs in this port group
                    # This was done by Port Group Name in the past
                    for this_vm in datacollector.vms :
                        # Loop through hardware looking for devices that have a backing device = to the portgroup name
                        for this_device in getattr(this_vm.config.hardware, 'device', []) :
                            backing = getattr(this_device, 'backing', None)
                            if this_pg.spec.name == getattr(backing, 'deviceName', None) :
                                vm = {}
                                vm['name'] = this_vm.name or 'Unknown'
                                vm['hardware'] = {'numCPU':this_vm.config.hardware.numCPU, 'memoryMB':this_vm.config.hardware.memoryMB}
                                pg['vms'].append(vm)
                    vswitch['port_groups'].append(pg)
            
            vswitch['pnics'] = []
            for this_pnic in getattr(datacollector.host_detail.config.network, 'pnic', []) :
                if this_pnic.key in getattr(this_vswitch, 'pnic', []) :
                    this_pnic.mac = mac_format(this_pnic.mac)                    
                    this_pnic['deviceName'] = None
                    this_pnic['vendorName'] = None       
                    this_pnic['physicalPortMapping'] = None
                    this_pnic['speedGb'] = 0
                    try :
                        this_pnic['speedGb'] = int(this_pnic['linkSpeed']['speedMb'])/1000.0;                        
                    except : 
                        pass
                    # Get PCI info for this nic
                    try :
                        for pci in datacollector.host_detail.hardware.pciDevice :
                            if pci.id == this_pnic.pci :
                                this_pnic['deviceName'] = pci.deviceName
                                this_pnic['vendorName'] = pci.vendorName
                                break
                    except Exception as e :
                        pass

                    # Get port mapping if available
                    if datacollector.vcm :                                                
                        for e in datacollector.vcm.get('info', {}).get('enetModulePorts', {}).get('item', []):                                                    
                            if this_pnic.mac == mac_format(e.get('macAddress', '')) :
                                this_pnic['physicalPortMapping'] = e.get('physicalPortMapping')
                                this_pnic['physicalPortMapping']['portType'] = 'port'
                                break
                            elif e.get('subPorts') :
                                for sp in e['subPorts'].get('item', []) :
                                    if this_pnic.mac == mac_format(sp.get('macAddress', '')) :
                                        this_pnic['physicalPortMapping'] = sp.get('physicalPortMapping')
                                        this_pnic['physicalPortMapping']['portType'] = 'subport'
                                        break
                    
                    vswitch['pnics'].append(this_pnic)
            
            vswitch['pnics'].sort(lambda a, b : cmp(a['deviceName'], b['deviceName']))
            
            vss.append(vswitch)
        return vss
    
    @staticmethod    
    def get_hbas(datacollector, vcm) :   
        """ Get HBAs from vmware data.  vcm must be the organized vcm data and NOT the raw vcm data. """
        hbas = []
        for this_hba in datacollector.host_detail.config.storageDevice.hostBusAdapter.HostHostBusAdapter :
            if this_hba.__class__.__name__ in ('HostFibreChannelHba', 'HostInternetScsiHba') :
                
                hba = {}
                hba['type'] = this_hba.__class__.__name__
                hba['key'] = this_hba.key
                hba['device'] = this_hba.device
                hba['bus'] = this_hba.bus
                hba['status'] = this_hba.status
                hba['model'] = this_hba.model
                hba['driver'] = this_hba.driver                
                hba['pci'] = this_hba.pci
                if hba['type'] == 'HostFibreChannelHba' :
                    hba['speed'] = this_hba.speed
                    hba['speedGb'] = this_hba.speed
                    hba['portWorldWideName'] = itowwn(this_hba.portWorldWideName)
                    hba['nodeWorldWideName'] = itowwn(this_hba.nodeWorldWideName)      
                elif hba['type'] == 'HostInternetScsiHba' :
                    if getattr(this_hba, 'isSoftwareBased', False) :
                        continue
                    hba['speed'] = 0
                    hba['speedGb'] = 0
                    hba['iScsiName'] = this_hba.iScsiName
                    hba['mac'] = getattr(this_hba.ipProperties, 'mac', None)
                
                # Find the VCM port connected to this HBA
                if vcm :                
                    for enclosure in vcm.get('enclosures',[]) :
                        for module in enclosure.get('allVcModuleG1s',[]) :
                            if hba['type'] == 'HostFibreChannelHba' :
                                for fabric in module.get('fabrics',[]) :
                                    for downlink in fabric.get('downlinks',[]) :
                                        if downlink['portWWN'] == hba['portWorldWideName'] :
                                            hba['physicalPortMapping'] = downlink['physicalPortMapping']
                                            if hba.get('speedGb', 0) == 0 :
                                                hba['speedGb'] = downlink['speedGb']                                                
                                                    
                            elif hba['type'] == 'HostInternetScsiHba' :
                                for network in module.get('networks',[]) :
                                    for downlink in network.get('downlinks',[]) :
                                        if downlink['macAddress'] == hba['mac'] :
                                            hba['physicalPortMapping'] = downlink['physicalPortMapping']   
                                            hba['physicalPortMapping']['port'] = string.replace(str(hba['physicalPortMapping']['port']), 'PORT', '')   
                                            if hba.get('speedGb', 0) == 0 :
                                                hba['speedGb'] = downlink['speedGb']                                                
                
                hbas.append(hba)

        return hbas
    
    @staticmethod
    def get_fc_datastores(datacollector, vcm) :
        """Function to collect and organize vmWare Datastore data"""                
        dss = []
        
        hbas = network.get_hbas(datacollector, vcm)
        # Get only Datastores used by this host
        for this_ds in datacollector.ds :            
            ds = {}
            ds['key'] = this_ds.value
            ds['name'] = this_ds.name
            ds['freeSpace'] = this_ds.info.freeSpace
            ds['maxFileSize'] = this_ds.info.maxFileSize                
            ds['hbas'] = []
            ds['vms'] = []
            
            # Collect the VMs using this datastore
            for this_vm in datacollector.vms :
                vm = {}
                vm['name'] = this_vm.name or 'Unknown'
                vm['hardware'] = {'numCPU':this_vm.config.hardware.numCPU, 'memoryMB':this_vm.config.hardware.memoryMB, 'disks': []}
                
                for device in this_vm.config.hardware.device :
                    if device.__class__.__name__ == 'VirtualDisk' :
                        if ds['key'] == device.backing.datastore.value :
                            vm['hardware']['disks'].append({'label': device.deviceInfo.label, 'summary': device.deviceInfo.summary})

                # Only save VMs with disks on this datastore
                if len(vm['hardware']['disks']) :
                    ds['vms'].append(vm)
                            
  
            # Collect the HBAs backing this datastore
            if 'extent' in dir(this_ds.info.vmfs) :
                for ds_disk in this_ds.info.vmfs.extent :
                    for mpLun in datacollector.host_detail.config.storageDevice.multipathInfo.lun.HostMultipathInfoLogicalUnit :                                
                        for path in mpLun.path :
                            # if two datastores used the same NAS, but different vc fabrics there is no
                            # guaranty that VMWare will pick the configured path, because it seems to 
                            # sees both paths equally since they both go to the same place.                                                                        
                            if path.key.endswith(ds_disk.diskName) :
                                for this_hba in hbas :
                                    if path.adapter == this_hba['key'] :
                                        hba = deepcopy(this_hba)    # The same hba could have different path states on each data store
                                        hba['pathState'] = path.pathState
                                        ds['hbas'].append(hba)          # WOW!!! That was painful!
                        
            
            # Only include datastores with hbas
            if len(ds['hbas']) :                
                ds['hbas'].sort(lambda a, b : cmp(a['device'], b['device']))
                ds['vms'].sort(lambda a, b : cmp(a['name'], b['name']))
                dss.append(ds)
        
        dss.sort(lambda a, b : cmp(a['name'], b['name']))
        return dss

    @staticmethod
    def organize_vcm(datacollector):
        """Function organize Virtual Connect data into a more usable form
            vc
                externalStorage
                externalSwitches
                enclosures
                    modules
                        networks
                            portlinks
                            downlinks
                        fabrics
                            portlinks
                            downlinks
                        uplinks
        """        
            
        try:
            physFuncMap = {'VIRT-NIC1':'a', 'VIRT-NIC2':'b', 'VIRT-NIC3':'c', 'VIRT-NIC4':'d' };
            vcm = datacollector.vcm
            vc = {}                
            # Why do we assume only one vcServerProfile?
            # Common Services only returns the profile for this host
            profile = vcm['info']['serverVcProfiles']['profile'][0]
            
            enclosures = {}
            externalStorage = {}
            
            # Loop through Ethernet modules
            for m in vcm['info']['enetVcModuleG1s']['module'] + vcm['info'].get('fcVcModuleG1s', {}).get('module', []): 
                enclosureId = m['commonIoModuleAttrs']['enclosureId']
                enclosureName = m['commonIoModuleAttrs']['enclosureName']
                bay = m['commonIoModuleAttrs']['bay']
                module_id = m['commonAttrs']['id']
                log.verbose('Module %s in Enclosure %s bay %s',m['__classname__'], enclosureId, bay)
                # Setup and empty module
                m['id'] = module_id            
                m['bay'] = bay
                m['enclosureId'] = enclosureId
                m['moduleType'] = m['__classname__']
                m['uplinks'] = []
                m['networks'] = []
                m['fabrics'] = []
                uplinks = {}            
                
                # Loop through the networks
                for n in vcm['info']['sharedNetworks']['sharedNetwork'] + vcm['info']['networks']['network'] :
                    log.verbose('    Network: %s, %s', n['configuration']['displayName'], n['configuration']['id'])
                    network_id = n['configuration']['id']
                    vc_network = {'id':network_id, 'uplinkVLANId': n['configuration'].get('uplinkVLANId'),
                                'maxPortSpeed': n['configuration'].get('maxPortSpeed'),
                                'preferredPortSpeed': n['configuration'].get('preferredPortSpeed'),
                                'uplinkVLANId': n['configuration'].get('uplinkVLANId'),
                                'displayName': n['configuration'].get('displayName'), 'downlinks': [],
                                'portlinks': []}                
                    # Find downlink ports for this network on this module                 
                    for p in vcm['info']['enetModulePorts']['item'] :                                                 
                        if p['ioModuleBay'] == bay  and p['enclosureId'] == enclosureId :  
                            # Find the ethernet connection for this port
                            for e in profile.get('enetConnections', {}).get('connection', []) + profile.get('iSCSIConnections', {}).get('connection', []) :
                                # Gather up all the Network IDs on this ethernet connection
                                networkIds = []                            
                                # One network on a nic                            
                                networkIds.append(e['configuration']['networkID'])
                                # Multiple networks on a nic
                                networkIds += [vNVP['networkId'] for vNVP in e['configuration'].get(
                                                    'multipleNetworks', {}).get(
                                                        'sharedNetworks',{}).get('vNetVlanPairs',[]) ]
                                # See if this Network is on this ethernet connection                            
                                if n['configuration']['id'] in networkIds :                                  
                                    port={}                                
                                    if e['ioModuleDownlinkPortId'] == p['id'] :     # Regular NICs
                                        log.verbose('        Downlink Port: %s',  p['id'])
                                        port['id'] = p['id']
                                        port['macAddress'] = mac_format(p['macAddress'])                                    
                                        port['physicalPortMapping'] = p['physicalPortMapping']
                                        port['physicalPortMapping']['portType'] = 'port'
                                        port['speedGb'] = p.get('opSpeed', 0)/1000.0;
                                        vc_network['downlinks'].append(port)
                                    elif p.get('subPorts') :                        # FlexNICs    
                                        for sp in p['subPorts']['item'] :                                                             
                                            if e['ioModuleDownlinkPortId'] == sp['id'] :
                                                log.verbose('        Downlink SubPort: %s', sp['id'])
                                                port['id'] = sp['id']
                                                port['macAddress'] = mac_format(sp['macAddress'])                                            
                                                port['physicalPortMapping'] = sp['physicalPortMapping']
                                                port['physicalPortMapping']['portType'] = 'subport'
                                                port['speedGb'] = sp.get('opSpeed',0)/1000.0;
                                                vc_network['downlinks'].append(port)
                                                
                    # Find the portlinks (connection from network to module port)                                                
                    portids = [ port['id'] for port in n['configuration'].get('ports',{}).get('port',[]) ]
                                    
                    for uls in vcm['info'].get('uplinkSets',{}).get('uplinkSet',[]) :
                        if uls.get('configuration',{}).get('id') == n['configuration'].get('portSetId') :                        
                            for port in uls.get('configuration',{}).get('ports',{}).get('port',[]) :
                                portids.append(port['id'])                    
                    
                    # Organize the uplink ports
                    for portid in portids :                                        
                        for emp in vcm['info'].get('enetModulePorts', {}).get('item',[]) :                                                
                            if emp['id'] == portid and emp['ioModuleBay'] == bay and emp['enclosureId'] == enclosureId :
                                log.verbose('        PortLink: %s',emp['id'])
                                vc_network.setdefault('portlinks',[]).append(emp['id'])  
                                uplink = {'id':emp['id'], 
                                          'uplinkType': 'network',
                                          'remoteChassisId':emp.get('remoteChassisId'), 
                                          'remotePortId':emp.get('remotePortId'),
                                          'connectorType': emp.get('connectorType'),
                                          'physicalLayer': emp.get('physicalLayer'),
                                          'linkStatus': emp.get('linkStatus'),
                                          'duplexStatus': emp.get('duplexStatus'),
                                          'opSpeed': emp.get('opSpeed'),
                                          'speedGb': int(emp.get('opSpeed'))/1000.0,
                                          'supportedSpeeds': emp.get('supportedSpeeds'),
                                          'portLabel': emp.get('portLabel')}
                                uplinks[emp['id']] = uplink          
                                
                    
                    # Don't include networks where there are no downlinks and no portlinks on this module
                    if vc_network['downlinks'] or vc_network['portlinks'] :
                        m['networks'].append(vc_network)
                
                # Loop through the FC Fabrics
                for f in vcm['info'].get('fcFabrics', {}).get('fcFabric', []) :
                    # Fabrics do not exist on modules.  However, Fabrics have logical port and logical ports have port which are on a module                
                    #*** Need to loop through fcModulePorts looking for ports with this fabric id
                    #    This is because the fcModulePorts have the ioBay AND the enclosure id.
                    #    The fabric only has the ioBay.                
                    log.verbose('    Fabric: %s', f['configuration']['displayName'])
                    fabric_id = f['configuration']['id']
                    fabric = {'id':fabric_id, 'displayName': f['configuration'].get('displayName'), 
                                'downlinks': [], 'portlinks': [] }    
                    # Loop through logical Ports looking for port on this module
                    for flpid in f['logicalPortIds']['id'] :
                        for fclp in vcm['info']['fcLogicalPorts']['logicalPort'] :
                            if fclp['commonAttrs']['id'] == flpid :                                                            
                                for mpid in fclp['modulePortIds']['id'] :                                
                                    # Include fcModulePorts and FCoE ports
                                    for fcmp in vcm['info']['fcModulePorts']['port'] + [ emp['fcoePort'] for emp in vcm['info']['enetModulePorts']['item'] if emp.get('fcoePort',None) ]  :
                                        if mpid == fcmp['id'] and fcmp['ioModuleBay'] == m['bay'] and fcmp['enclosureId'] == m['enclosureId'] :  
                                            log.verbose('        PortLink: %s',fcmp['id'])
                                            fabric['portlinks'].append(fcmp['id'])
                                            uplink = {'id':fcmp['id'], 'uplinkType': 'fc', 'portLabel': fcmp['portLabel'], 'currentSpeed': fcmp['currentSpeed'],
                                                      'portWWN': fcmp['portWWN'], 'connectedToWWN': fcmp['connectedToWWN'], 'speedGb': fcmp['currentSpeed'],
                                                      'portConnectStatus': fcmp['portConnectStatus'] }  
                                            externalStorage.setdefault(fcmp['connectedToWWN'].lower(), {'WWN':fcmp['connectedToWWN'], 'portWWN':[]})['portWWN'].append(fcmp['portWWN'])
                                            uplinks[fcmp['id']] = uplink
                                            log.verbose('        Uplink: %s', fcmp['portLabel'])
                                        
                    # Find the downlinks for this fabric
                    for fcc in profile['fcConnections'].get('connection', []) + profile['fcoeConnections'].get('connection', []):
                        if fcc['configuration']['fabricID'] == fabric_id :
                            # Gather up fcModulePorts, enetModulePorts (just incase an FCoE port is not a subport), & enetModulePort subports (FCoE)
                            fcmps = vcm['info']['fcModulePorts']['port'] + vcm['info']['enetModulePorts']['item']
                            for emp in vcm['info']['enetModulePorts']['item'] :
                                if emp.get('subPorts', None) :
                                    fcmps += emp['subPorts']['item']
                            
                            for fcmp in fcmps :                            
                                if fcc['ioModuleDownlinkPortId'] == fcmp['id'] and fcmp['ioModuleBay'] == m['bay'] and fcmp['enclosureId'] == m['enclosureId'] :                                
                                    # Gather up all the server FC ports and FCoE port/subports
                                    sfcps = []
                                    if vcm['info']['servers']['server'][0].get('fcPorts', None) :
                                        sfcps += vcm['info']['servers']['server'][0]['fcPorts']['port']
                                    if vcm['info']['servers']['server'][0].get('enetPorts', None) :
                                        sfcps += vcm['info']['servers']['server'][0]['enetPorts']['port']                                    
                                        for emp in vcm['info']['servers']['server'][0]['enetPorts']['port'] :
                                            if emp.get('subPorts',None) :                                            
                                                sfcps += emp['subPorts'].get('port',[])
                                                                                
                                    for sfcp in sfcps :
                                        if sfcp['ioModulePortId'] == fcc['ioModuleDownlinkPortId'] :
                                            log.verbose('        Downlink: %s', fcc['ioModuleDownlinkPortId'])
                                            ioBay = fcc['physicalPortMapping']['ioBay']                                        
                                            # Fix differences in the way port is stored. FC is '1' & FCoE is 'PORT1'
                                            port = string.replace(str(fcc['physicalPortMapping']['port']), 'PORT', '')
                                            fcc['physicalPortMapping']['port'] = string.replace(str(fcc['physicalPortMapping']['port']), 'PORT', '')
                                            mezzNumber = fcc['physicalPortMapping']['mezz']
                                            if mezzNumber[:4] == 'MEZZ' :
                                                mezzNumber = mezzNumber[4:]
                                            elif mezzNumber == "LOM" :
                                                mezzNumber = '9'  #LOMs are always MEZZ9 in OA (can be 5 in old firmware
                                            else :
                                                log.error('Unknown mezz number: %s', mezzNumber)
                                            physFunc = physFuncMap.get(fcc['physicalPortMapping'].get('physFunc', None), None)                                        
                                            
                                            # Find the blade bay number    
                                            bladeBayNumber = None
                                            if fcmp.get('physicalServerBay', None) :
                                                bladeBayNumber = fcmp['physicalServerBay']
                                            elif fcmp.get('fcoePort', None) :
                                                bladeBayNumber = fcmp['fcoePort']['physicalServerBay']
                                            else :
                                                log.error('Unable to find Blade Bay Number for downlink: %s', fcc['ioModuleDownlinkPortId'])
                                            
                                            # Find the current port speed    
                                            currentSpeed = 0
                                            try:
                                                if fcmp.get('currentSpeed', None) :
                                                    currentSpeed = float(fcmp['currentSpeed'])
                                                elif fcmp.get('fcoePort', None) :
                                                    currentSpeed = float(fcmp['opSpeed'])
                                                else :
                                                    log.error('Unable to find Current Speed for downlink: %s', fcc['ioModuleDownlinkPortId'])
                                            except:
                                                pass
                                            
                                            downlink = {'id': fcc['ioModuleDownlinkPortId'], 
                                                        'physicalServerBay': bladeBayNumber,
                                                        'speedGb': currentSpeed,
                                                        'portWWN': None,
                                                        'physicalPortMapping': fcc['physicalPortMapping'], }
                                            if downlink['speedGb'] > 10 :
                                                downlink['speedGb'] = downlink['speedGb'] / 1000.0;
                                            
                                            # Use OA port map to match up vmWare WWNs to VC downlink ports
                                            foundOAPortMap = False
                                            # First look for flex nics
                                            # If not there then look at regular nics
                                            #for oamc in datacollector.oa['mezz_cards'] :
                                            #    if bladeBayNumber!= None and int(oamc['bayNumber']) == int(bladeBayNumber) :
                                            mezzes = network.get_blade_mezz_info(int(bladeBayNumber), datacollector.oa)
                                            for mezz in mezzes['mezzInfo'] :                                                
                                                if int(mezzNumber) == int(mezz['mezzNumber']) and hasattr(mezz,'port') :                                                    
                                                    for mezzPort in mezz.port :
                                                        if int(port) == int(mezzPort['portNumber']) and hasattr(mezzPort, 'guid') :                                                            
                                                            for guid in mezzPort['guid'] :
                                                                if guid['physicalFunction'] == physFunc :
                                                                    log.verbose('            oa portWWN found in mezz_cards: %s', guid['guid'])
                                                                    #OA or VC is screwing up the WWNs on CNAs
                                                                    if guid['guid'].endswith(':00:00') and not guid['guid'].startswith('10:00:') :
                                                                        downlink['portWWN'] = ('10:00:' + guid['guid'][0:-6]).lower()
                                                                        log.verbose("Fixing WWN: %s -> %s", guid['guid'], downlink['portWWN'])
                                                                    else :
                                                                        downlink['portWWN'] = guid['guid'].lower()
                                                                    foundOAPortMap = True
                                            
                                            
                                            if not foundOAPortMap :                                                
                                                oapms = network.get_blade_port_map(int(bladeBayNumber), datacollector.oa)
                                                for mezz in oapms['mezz'] :
                                                    log.verbose('                mezz: %s, %s', mezz['mezzNumber'], mezzNumber)
                                                    if int(mezz['mezzNumber']) == int(mezzNumber) : 
                                                        if mezz['mezzDevices']['type'] != "MEZZ_DEV_TYPE_MT" :
                                                            for mezzDevPort in mezz['mezzDevices']['port'] :
                                                                log.verbose('                    port: %s, %s', mezzDevPort['portNumber'], port)
                                                                if int(port) == int(mezzDevPort['portNumber']) :
                                                                    log.verbose('            oa portWWN found in port_maps: %s', mezzDevPort['wwpn'])
                                                                    downlink['portWWN'] = mezzDevPort['wwpn'].lower()
                                                                    foundOAPortMap = True
                                                                            
                                                if not foundOAPortMap :
                                                    log.error('Missing OA Port Mapping: %s - blade:%s; mezz:%s; port:%s', fcc['ioModuleDownlinkPortId'], bladeBayNumber, mezzNumber, port)
                                            
                                            fabric['downlinks'].append(downlink)
                                            
                    if(fabric['portlinks'] or fabric['downlinks'] ) :                        
                            m['fabrics'].append(fabric)
                
                m['uplinks'] = uplinks.values()
                m['uplinks'].sort(lambda a, b : cmp(a['id'], b['id']))
                m['networks'].sort(lambda a, b : cmp(a['displayName'], b['displayName']))
                m['fabrics'].sort(lambda a, b : cmp(a['displayName'], b['displayName']))
                
                # Remove unused data
                if 'downlinkPortIds' in m.keys() :
                    del m['downlinkPortIds']
                if 'uplinkPortIds' in m.keys() : 
                    del m['uplinkPortIds']
                           
                enclosure = {'enclosureType':vcm['enclosureType'], 'id': enclosureId, 'enclosureName': enclosureName,
                             'allVcModuleG1s': []}
                if len(m['uplinks']) or len(m['networks']) or len(m['fabrics']) :             
                    enclosures.setdefault(enclosureId, enclosure )['allVcModuleG1s'].append(m)            
                enclosures[enclosureId]['allVcModuleG1s'].sort(lambda a, b : cmp(a['id'], b['id']))
            
            vc['enclosures'] = enclosures.values()
            vc['enclosures'].sort(lambda a, b : cmp(a['id'], b['id']))    
                
                
            # Organize External Switch data
            externalSwitches = {}        
            for s in vcm.get('stats', []) :
                if not s['stats']['remote-chassis-id'] :
                    continue
                switch_id = s['stats']['remote-chassis-id'].lower()
                port_id = s['id']
                sw = {}         
                sw['id'] = switch_id
                sw['remote-chassis-id'] = switch_id          
                sw['remote-system-desc'] = s['stats']['remote-system-desc']            
                sw['remote-system-name'] = s['stats']['remote-system-name']
                sw['remote-system-capabilities'] = s['stats']['remote-system-capabilities']            
                sw['ports'] = []
                
                port = {}
                port['id'] = port_id
                port['remote-port-id'] = s['stats']['remote-port-id']
                port['remote-port-desc'] = s['stats']['remote-port-desc']
                            
                externalSwitches.setdefault(switch_id, sw)['ports'].append(port)

            vc['externalSwitches'] = externalSwitches.values()
            for es in vc['externalSwitches'] :
                es['ports'].sort(lambda a, b : cmp(a['id'], b['id']))
            
            vc['externalStorage'] = externalStorage.values()
            
            return vc    

        except Exception as e :
            log.exception("Failed to organize VCM data")
            return None
