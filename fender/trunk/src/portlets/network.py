'''
Created on Oct 25, 2011

@author: IslamM
'''

from copy import deepcopy
import string 

from logging import getLogger
log = getLogger(__name__)

from portlets.host import Host
from portlets.collector import Collector
from portlets import mac_format, itowwn
from util import config

import json
 
class Network:

    @staticmethod
    def summary(dcollector = None):        
        vmnic = []
        nics = {}        
        try:
            if dcollector: 
                datacollector = dcollector
            else:
                datacollector = Collector.get_collector()
            if hasattr(datacollector.host_detail.config, 'network') :
                Network.add_pnic_info(vmnic, datacollector.host_detail.config.network)                   
            Network.add_server_nic_info(vmnic, datacollector.server)
            Network.add_pci_info(vmnic, datacollector.host_detail.hardware.pciDevice)
            if datacollector.oa and datacollector.ilo.has_key('data') and datacollector.ilo['data'].has_key('get_oa_info'):
                Network.add_blade_physical_nics(datacollector.ilo['data']['get_oa_info']['location'], vmnic, datacollector.oa)
            else:
                Network.add_physical_nics(vmnic, datacollector.ilo)
                
        except Exception as e:
            log.exception("Error in data.networksummary(): %s", str(e))
        nics['nics'] = vmnic
                
        return nics

    @staticmethod
    def add_pnic_info(nic_summary, network):
        for pn in getattr(network, 'pnic', []):
            nic = {}
            nic['vmnic'] = pn.device
            if hasattr(pn, 'linkSpeed'):
                nic['speedMb'] = pn.linkSpeed.speedMb
                nic['speedGb'] = pn.linkSpeed.speedMb / 1000.0                
            nic['mac'] = pn.mac
            nic['pci'] = pn.pci
            nic['driver'] = pn.driver
            # Find the vswitch this pnic is on
            for vswitch in getattr(network, 'vswitch', []) + getattr(network, 'proxySwitch', []):   
                if hasattr(vswitch, 'pnic') :
                    if pn.key in getattr(vswitch, 'pnic', []) :
                        nic['vswitch'] = getattr(vswitch, 'name', getattr(vswitch, 'dvsName', 'Unknown'))                    
                        break
            nic_summary.append( nic )

    @staticmethod
    def add_server_nic_info(nic_list, server):
        if not server :
            return
        nics = server['data']['inventory']['NIC']
        for n in nics:
            nic = Network.get_nic_by_mac(nic_list, 'mac', n['MACAddress'])
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
            nic = Network.get_nic_by_pci(nic_list, 'pci', pci.id)
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
        nic_recs = Host.get_records(ilo, 209)
        for nic in nic_recs:
            for i, nv in enumerate(nic):
                if nv['name'] == 'MAC':
                    if nv['value'].replace('-', ':').lower() == mac.lower():
                        return nic[i-1]['value']
        return None
        
    @staticmethod
    def add_physical_nics(pnics, ilo):
        log.debug("add_physical_nics")
        for nic in pnics:
            if nic['slot'] == 0:
                port = Network.find_lom_port(ilo, nic['mac'])
                if port :                    
                    nic['physical_nic'] =  'LOM:' + str(port)                    
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
    def is_LOM(bay, mezzNumber, oa) :
        bladePortMap = Network.get_blade_port_map(bay, oa)
        for mezz in bladePortMap.get('mezz', []) :
            if mezzNumber == int(mezz['mezzNumber']) :
                if mezz.get('mezzSlots', {}).get('type', '') ==  "MEZZ_SLOT_TYPE_FIXED" :
                    return True
                if mezz.get('mezzDevices', {}).get('type', '') ==  "MEZZ_SLOT_TYPE_FIXED" :
                    return True
                return False
        return False
    
    @staticmethod
    def get_LOMMEZZNum(bay, oa) :
        """Returns the MEZZ number for the LOM. Should be 9 or 15 or even 5 on really old OAs"""
        bladePortMap = Network.get_blade_port_map(bay, oa)
        for mezz in bladePortMap.get('mezz', []) :
            if mezz.get('mezzSlots', {}).get('type', '') ==  "MEZZ_SLOT_TYPE_FIXED" :
                return int(mezz.get('mezzNumber', '0'))
        return 0
    
    @staticmethod
    def get_BLOMNum(bay, mezzNumber, oa) :
        bladePortMap = Network.get_blade_port_map(bay, oa)
        for mezz in bladePortMap.get('mezz', []) :
            if mezzNumber == int(mezz['mezzNumber']) :
                for ed in mezz.get('extraData', []) :
                    if ed.get('_name', '') == 'aMezzNum' :     
                        return ed.get('value','0')
        
        return '0'
        
    @staticmethod
    def get_BLOMMEZZNum(bay, blomNumber, oa) :
        bladePortMap = Network.get_blade_port_map(bay, oa)
        for mezz in bladePortMap.get('mezz', []) :            
            for ed in mezz.get('extraData', []) :
                if ed.get('_name', '') == 'aMezzNum' :     
                        if int(ed.get('value','0')) == int(blomNumber) :
                            return int(mezz['mezzNumber'])
        return 0
    
    @staticmethod
    def is_BLOM(bay, mezzNumber, oa) :
        """BLOMs aka Flexible LOMs have their aMezzNum set to '1' or '2'. LOMs and Mezz cards are set to '0'"""
        try:
            if Network.get_BLOMNum(bay, mezzNumber, oa) != '0' :
                return True
        except :
            log.exception("Error checcking for BLOM bay: %s, MEZZ: %s", bay, mezzNumber)
        return False
        
    @staticmethod
    def check_flex_nic(nic, bay, oa):
        #log.debug("check_flex_nic %s", nic['mac'].lower())        
        bladeMezzInfo = Network.get_blade_mezz_info(bay, oa)
        
        for mezz in bladeMezzInfo['mezzInfo'] :
            #log.debug('    mezz: %s', mezz['mezzNumber'])                        
            if not 'port' in mezz or not mezz['port'] : 
                #log.debug('        No port in mezz')
                continue            
            for mezzPort in mezz['port'] or not mezzPort['guid'] :
                if not 'guid' in mezzPort : 
                    #log.debug('            No guid in mezzPort')
                    continue
                for guid in mezzPort['guid']:                    
                    if nic['mac'].lower() == guid['guid'].lower() :                         
                        if Network.is_LOM(bay, int(mezz['mezzNumber']), oa) :   #if int(mezz['mezzNumber']) in (9,13) :
                            nic['physical_nic'] = 'LOM:' + str(mezzPort['portNumber']) + '-' + str(guid['physicalFunction'])
                        elif Network.is_BLOM(bay, int(mezz['mezzNumber']), oa) : 
                            nic['physical_nic'] = 'LOM' + Network.get_BLOMNum(bay, int(mezz['mezzNumber']), oa) + ':' + str(mezzPort['portNumber']) + '-' + str(guid['physicalFunction'])
                        else:
                            nic['physical_nic'] = 'MEZZ' + str(mezz['mezzNumber']) + ':' + str(mezzPort['portNumber']) + '-' + str(guid['physicalFunction'])
                        log.debug('                Mapped to %s', nic['physical_nic'])
                        return True
                        
        return False

    @staticmethod
    def check_regular_nic(nic, bay, oa):
        #log.debug("check_regular_nic %s", nic['mac'].lower())
        bladePortMap = Network.get_blade_port_map(bay, oa)
        for mezz in bladePortMap['mezz'] :
            #log.debug('    mezz: %s', int(mezz['mezzNumber']))                        
            if mezz['mezzDevices']['type'] == "MEZZ_DEV_TYPE_MT": continue
            for mezzDevPort in mezz['mezzDevices']['port'] :
                if nic['mac'].lower() == mezzDevPort['wwpn'].lower() :
                    #log.debug('        port: %s, mac: %s', int(mezzDevPort['portNumber']), mezzDevPort['wwpn'].lower() )
                    if Network.is_LOM(bay, int(mezz['mezzNumber']), oa) : 
                        nic['physical_nic'] = 'LOM:' + str(mezzDevPort['portNumber'])                                    
                    elif Network.is_BLOM(bay, int(mezz['mezzNumber']), oa) : 
                        nic['physical_nic'] = 'LOM' + Network.get_BLOMNum(bay, int(mezz['mezzNumber']), oa) + ':' + str(mezzDevPort['portNumber'])
                    else :
                        nic['physical_nic'] = 'MEZZ' + str(mezz['mezzNumber']) + ':' + str(mezzDevPort['portNumber'])                
                    log.debug('            Mapped to %s', nic['physical_nic'])
                    return

    @staticmethod
    def add_blade_physical_nics(bay, pnics, oa) :
        log.debug("add_blade_physical_nics")
        for nic in pnics :
            log.debug('pnic: %s, mac: %s', nic['vmnic'], nic['mac'].lower())            
            found = Network.check_flex_nic(nic, bay, oa)            
            if not found:
                Network.check_regular_nic(nic, bay, oa)

    @staticmethod
    def detail():
        network_detail = {}
        datacollector = Collector.get_collector()
        summary = Network.summary(datacollector)
        networkdata = {}
        try:
            networkdata = Network.get_network_data(datacollector)
        except Exception as e:
            log.exception("Error in data.networksummary(): %s", str(e))
        network_detail.update(summary)
        network_detail.update(networkdata)
        
        return network_detail

    @staticmethod
    def get_network_data(datacollector):
        
        data = {}
        
        dvss = Network.get_dvss(datacollector)
        vss = Network.get_vss(datacollector)
        
        data['vss'] = vss
        data['dvss'] = dvss
        
        raw_vcm = datacollector.vcm        
        if not raw_vcm :
            log.error("No Virtual Connect Information for this host(%s)", datacollector.host_detail.hardware.systemInfo.uuid)
            return data
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
            log.exception("Error getting host(%s) enclosure type: %s", datacollector.host_detail.hardware.systemInfo.uuid, str(e)  );
        
        
        vcm = Network.organize_vcm(datacollector)           
        datastores = Network.get_fc_datastores(datacollector, vcm)        
    
        Network.check_4_bottle_necks(vcm)
        Network.organize_telemetry(vcm, raw_vcm)
    
        data['vcm'] = vcm
        data['datastores'] = datastores
    
        return data

    @staticmethod    
    def check_4_bottle_necks(vcm) :
        """This will not work unless the whole VC structure is analyzed because multiple blades can be uplinked through the same port"""
        
        if not vcm : return
        
        try :
            networks = {}        
            # Sum up active uplinks & downlinks
            for e in vcm.get('enclosures', []) :
                for m in e.get('allVcModuleG1s', []) :
                    for n in m.get('networks', []) :
                        networks.setdefault(n['id'], {'inGb': 0, 'outGb': 0, 'uplinks': set() } )                    
                        for d in n.get('downlinks',[]) :
                            log.debug('Network In %s:%s, in: %s', n['displayName'], n['id'], d.get('speedGb',0))                    
                            try :
                                networks[n['id']]['inGb'] += float(d.get('speedGb',0))
                            except Exception as e :
                                log.exception("error calculating downlink configured bandwidth") 
                        for pl in n.get('portlinks', []) :
                            for ul in m.get('uplinks', []) :
                                if ul['id'] == pl and ul['uplinkType'] == "network" and ul['linkStatus'] == "LINKED-ACTIVE" :                                
                                    log.debug('Network Out %s:%s, in: %s', n['displayName'], n['id'], ul.get('speedGb',0))      
                                    try:
                                        networks[n['id']]['outGb'] += float(ul.get('speedGb',0))                                
                                    except Exception as e :
                                        log.exception("error calculating uplink configured bandwidth") 
                                    
                                    
            # Check for Uplink bottle necks      
            oversubscriptionFactor = config.config().get_oversubscription_factor()        
            if oversubscriptionFactor > 0.0 :
                for e in vcm.get('enclosures', []) :
                    for m in e.get('allVcModuleG1s', []) :
                        for n in m.get('networks', []) :                        
                            try :
                                if networks[n['id']]['inGb'] > networks[n['id']]['outGb'] * oversubscriptionFactor :
                                    n['bottleNeck'] = True;
                                    log.debug('Bottle Neck in %s, in: %s, out: %s * %s', n['displayName'], networks[n['id']]['inGb'], networks[n['id']]['outGb'], oversubscriptionFactor)                    
                            except Exception as e :
                                log.exception("error calculating uplink oversubscription") 
        
        except:
            log.exception("Error Detecting bottle necks")
            
    @staticmethod    
    def organize_telemetry(vcm, raw_vcm) :
        """Get the Telemetry data for uplinks and downlinks(FlexNics not supported)"""
        try :
            if not vcm  or not raw_vcm:
                return
            
            # Collect the module stats properties for each module
            # This is needed for the port_telemetry_count and port_telemetry_period
            module_stats = {}
            for e in vcm.get('enclosures', []) :
                for m in e.get('allVcModuleG1s', []) :
                    for item in raw_vcm.get('telemetry', {}).get('item', []) :  
                        if item.get('id',None) == m['id'] + ':module_stats' :
                            module_stats[m['id']] = item.get('data', {}).get('properties', None)                            
                        
            for e in vcm.get('enclosures', []) :
                for m in e.get('allVcModuleG1s', []) :
                    # Uplink Telemetry
                    for u in m.get('uplinks', []) :
                        for item in raw_vcm.get('telemetry', {}).get('item', []) :  
                            if item.get('id',None) == u['id'] :                                
                                for target1 in item.get('data',{}).get('target', []) :
                                    for target2 in target1.get('instance', {}).get('target',[]) :                                                                            
                                        if target2.get('instance',{}).get('ufit', {}).get('_ufct', None) == 'advancedstats' :     
                                            if (target2.get('instance',{}).get('properties',{}).get('transmit_kilobits_per_sec', None) and 
                                                target2.get('instance',{}).get('properties',{}).get('receive_kilobits_per_sec', None) and
                                                module_stats.get(m['id'], {}).get('port_telemetry_period', None) ) :
                                                telemetry = {}
                                                
                                                telemetry['tx_kbps'] = target2.get('instance',{}).get('properties',{}).get('transmit_kilobits_per_sec','').split(':')[:12]
                                                telemetry['rx_kbps'] = target2.get('instance',{}).get('properties',{}).get('receive_kilobits_per_sec','').split(':')[:12]                                                                                   
                                                log.debug('Telemetry raw tx: "%s"', target2.get('instance',{}).get('properties',{}).get('transmit_kilobits_per_sec',''))
                                                log.debug('Telemetry tx: "%s"', str(telemetry['tx_kbps']))
                                                log.debug('Telemetry raw rx: "%s"', target2.get('instance',{}).get('properties',{}).get('receive_kilobits_per_sec',''))
                                                log.debug('Telemetry rx: "%s"', str(telemetry['rx_kbps']))
                                                
                                                telemetry['properties'] = {}
                                                telemetry['properties']['port_telemetry_period'] = module_stats.get(m['id'], {}).get('port_telemetry_period', 0)
                                                telemetry['properties']['port_telemetry_entry_count'] = len(telemetry['tx_kbps'])
                                                u['telemetry'] = telemetry
                                                log.debug("Telemetry uplink id: %s period: %s count: %s", u['id'], u['telemetry']['properties']['port_telemetry_period'], u['telemetry']['properties']['port_telemetry_entry_count'])                                            
                    
                    # Downlink Telemetry - In VC 3.70+ it should have flex nic specific telemetry                    
                    # for netfab in m.get('networks',[]) + m.get('fabrics',[]) :
                        # for dl in netfab.get('downlinks', []) :
                            # for item in raw_vcm.get('telemetry', {}).get('item', []) :
                                # if item.get('id',None) == dl['id'] :                                
                                    # for target1 in item.get('data',{}).get('target', []) :
                                        # for target2 in target1.get('instance', {}).get('target',[]) :
                                            # if target2.get('instance',{}).get('ufit', {}).get('_ufct', None) == 'advancedstats' :
                                                # dl['telemetry'] = {}
                                                # dl['telemetry']['tx_kbps'] = target2.get('instance',{}).get('properties',{}).get('transmit_kilobits_per_sec','').split(':')
                                                # dl['telemetry']['rx_kbps'] = target2.get('instance',{}).get('properties',{}).get('receive_kilobits_per_sec','').split(':')    
                                                # dl['telemetry']['properties'] = module_stats.get(m['id'], {})                                                
                                                # log.debug("Telemetry downlink id: %s %s period: %s count: %s", dl['id'], dl['telemetry']['properties'].get('port_telemetry', ''), dl['telemetry']['properties'].get('port_telemetry_period', None), dl['telemetry']['properties'].get('port_telemetry_entry_count', None))
        except :
            log.exception("Error Organizing VC Telemetry")
        
    @staticmethod    
    def get_dvss(datacollector) :
        """Function to collect and organize vmWare Distributed Virutal Switch data"""     
        if not datacollector.vdvsw:
            return []        
        dvss = []     
        try : 
            moref = datacollector.host_detail.moref()
            # Get only DVPortgroups that contain this host
            if datacollector.dvpg:   
                dvpgs = [ x for x in datacollector.dvpg 
                            if (hasattr(x.host, 'ManagedObjectReference') and 
                            moref in [v._type+':'+v.value for v in x.host.ManagedObjectReference]) ]       

            # Get all DVSwithes
            for d in datacollector.vdvsw: 
                # Only look at DVSwitches that have hosts
                if not getattr(d.config, "host", None):
                    continue
                # Loop through hosts on this DVS looking for self    
                for host_index in range(len(d.config.host)) :
                    hostmoref = d.config.host[host_index].config.host._type + ':' + d.config.host[host_index].config.host.value
                    if hostmoref == moref:       
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
                            vlanId = getattr(pg.defaultPortConfig.vlan, 'vlanId', getattr(pg.defaultPortConfig.vlan, 'pvlanId', None) )
                            # Trunked VLAN - list of start ends
                            if getattr(vlanId, '__iter__', False) :
                                svlanIds = ''
                                for i in vlanId :
                                    if svlanIds :
                                        svlanIds+=','
                                    if i.start == i.end :
                                        svlanIds += str(i.start)
                                    else :
                                        svlanIds += str(i.start)+'-'+str(i.end)
                                vlanId = svlanIds
                            pd['vlanId'] = vlanId
                            pd['vms'] = []
                            # Find all the VMs with nics in this port group
                            for vm in datacollector.vms :
                                vm_data = None
                                for device in getattr(vm.config.hardware, 'device', [] ) :                                
                                    try :   # must be in try block because backing only exists if VM is connected to a DVS
                                        if pg.key == device.backing.port.portgroupKey :                                        
                                            if not vm_data :
                                                vm_data={}
                                                vm_data['name'] = vm.name or 'Unknown'                                                                                   
                                                vm_data['hardware'] = {'numCPU':vm.config.hardware.numCPU, 'memoryMB':vm.config.hardware.memoryMB}                                        
                                                vm_data['nics'] = []
                                                pd['vms'].append(vm_data)
                                            vm_data['nics'].append({'macAddress': mac_format(device.macAddress)})
                                    except :
                                        pass
                                    
                            dvs['downlink_port_groups'].append(pd)
                        
                        dvss.append(dvs)
                        break
        
        except :
            log.exception("Error getting Distributed vSwitches")
            
        return dvss

    @staticmethod
    def get_vss(datacollector) :
        """Function to collect and organize vmWare Virutal Switch data"""        
        vss = []
        
        #Get all the vms
#        if vms == None :
#            vms = self.get_vms()
        try :        
            for this_vswitch in getattr(datacollector.host_detail.config.network, 'vswitch', []) :
                try :
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
                                if hasattr(this_vm, 'config') :
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
                except :
                    log.exception("Error getting vSwitch")
        except :
            log.exception("Error getting vSwitches")
        return vss
    
    @staticmethod    
    def get_hbas(datacollector, vcm) :   
        """ Get HBAs from vmware data.  vcm must be the organized vcm data and NOT the raw vcm data. """
        hbas = []
        try :
            for this_hba in datacollector.host_detail.config.storageDevice.hostBusAdapter.HostHostBusAdapter :
                try : 
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
                except :
                    log.exception("Error getting HBA")
        except :
            log.exception("Error getting HBAs")
            
        return hbas
    
    @staticmethod
    def get_fc_datastores(datacollector, vcm) :
        """Function to collect and organize vmWare Datastore data"""                
        dss = []
        
        if not datacollector.ds :
            return dss
            
        hbas = Network.get_hbas(datacollector, vcm)
        # Get only Datastores used by this host
        for this_ds in datacollector.ds :            
            try: 
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
                if 'vmfs' in dir(this_ds.info) and 'extent' in dir(this_ds.info.vmfs) :
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
            except :
                log.exception("Error analyzing datastore %s", this_ds.name)
        
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
                log.debug('Module %s in Enclosure %s bay %s',m['__classname__'], enclosureId, bay)
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
                    log.debug('    Network: %s, %s', n['configuration']['displayName'], n['configuration']['id'])
                    network_id = n['configuration']['id']
                    vc_network = {'id':network_id, 'uplinkVLANId': n['configuration'].get('uplinkVLANId'),
                                'maxPortSpeed': n['configuration'].get('maxPortSpeed'),
                                'preferredPortSpeed': n['configuration'].get('preferredPortSpeed'),
                                'uplinkVLANId': n['configuration'].get('uplinkVLANId'),
                                'displayName': n['configuration'].get('displayName'), 'downlinks': [],
                                'portlinks': []}                
                    # Find downlink ports for this network on this module                 
                    for p in vcm['info']['enetModulePorts']['item'] :   
                        log.debug('      Port: %s', p['id'])
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
                                    #log.debug('        Checking Downlink: %s', e['ioModuleDownlinkPortId'])
                                    if e['ioModuleDownlinkPortId'] == p['id'] :     # Regular NICs
                                        log.debug('        Downlink Port: %s',  p['id'])
                                        port['id'] = p['id']
                                        port['macAddress'] = mac_format(p['macAddress'])                                    
                                        port['physicalPortMapping'] = p['physicalPortMapping']
                                        port['physicalPortMapping']['portType'] = 'port'
                                        port['speedGb'] = p.get('opSpeed', 0)/1000.0;
                                        vc_network['downlinks'].append(port)
                                    elif p.get('subPorts') :                        # FlexNICs    
                                        for sp in p['subPorts']['item'] :                                  
                                            #log.debug('        Checking Downlink SubPort: %s', sp['id'])
                                            if e['ioModuleDownlinkPortId'] == sp['id'] :
                                                log.debug('        Downlink SubPort: %s', sp['id'])
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
                                log.debug('        PortLink: %s',emp['id'])
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
                    log.debug('    Fabric: %s', f['configuration']['displayName'])
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
                                            log.debug('        PortLink: %s',fcmp['id'])
                                            fabric['portlinks'].append(fcmp['id'])
                                            uplink = {'id':fcmp['id'], 'uplinkType': 'fc', 'portLabel': fcmp['portLabel'], 'currentSpeed': fcmp['currentSpeed'],
                                                      'portWWN': fcmp['portWWN'], 'connectedToWWN': fcmp['connectedToWWN'], 'speedGb': fcmp['currentSpeed'],
                                                      'portConnectStatus': fcmp['portConnectStatus'] }  
                                            # the duplicate 'wwn' field is to make the java Jackson parser happy
                                            externalStorage.setdefault(fcmp['connectedToWWN'].lower(), {'wwn':fcmp['connectedToWWN'],'WWN':fcmp['connectedToWWN'], 'portWWN':[]})['portWWN'].append(fcmp['portWWN'])
                                            uplinks[fcmp['id']] = uplink
                                            log.debug('        Uplink: %s', fcmp['portLabel'])
                                        
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
                                            log.debug('        Downlink: %s', fcc['ioModuleDownlinkPortId'])
                                            ioBay = fcc['physicalPortMapping']['ioBay']                                        
                                            
                                            # Find the blade bay number    
                                            bladeBayNumber = None
                                            if fcmp.get('physicalServerBay', None) :
                                                bladeBayNumber = fcmp['physicalServerBay']
                                            elif fcmp.get('fcoePort', None) :
                                                bladeBayNumber = fcmp['fcoePort']['physicalServerBay']
                                            else :
                                                log.error('Unable to find Blade Bay Number for downlink: %s', fcc['ioModuleDownlinkPortId'])
                                            
                                            # Fix differences in the way port is stored. FC is '1' & FCoE is 'PORT1'
                                            port = string.replace(str(fcc['physicalPortMapping']['port']), 'PORT', '')
                                            fcc['physicalPortMapping']['port'] = string.replace(str(fcc['physicalPortMapping']['port']), 'PORT', '')
                                            mezzNumber = fcc['physicalPortMapping']['mezz']
                                            if mezzNumber[:4] == 'MEZZ' :
                                                mezzNumber = mezzNumber[4:]
                                            elif mezzNumber[:4] == 'BLOM' :
                                                blomNumber = mezzNumber[4:]
                                                mezzNumber = Network.get_BLOMMEZZNum(bladeBayNumber, blomNumber, datacollector.oa)
                                            elif mezzNumber == "LOM" :
                                                mezzNumber = Network.get_LOMMEZZNum(bladeBayNumber, datacollector.oa)
                                            else :
                                                log.error('Unknown mezz number: %s', mezzNumber)
                                                mezzNumber = '0'
                                            physFunc = physFuncMap.get(fcc['physicalPortMapping'].get('physFunc', None), None)                                        
                                            
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
                                            log.debug('Looking for portWWN in oa mezz info - mezzNumber: %d, mezzPort: %d, physFunc: %s',int(mezzNumber), int(port), physFunc)
                                            mezzes = Network.get_blade_mezz_info(int(bladeBayNumber), datacollector.oa)
                                            for mezz in mezzes['mezzInfo'] :                                                
                                                #log.debug('    mezzNumber: %d', int(mezz['mezzNumber']))
                                                if int(mezzNumber) == int(mezz['mezzNumber']) and 'port' in mezz :                                                    
                                                    for mezzPort in mezz['port'] :
                                                        log.debug('        mezzPort: %d', int(mezzPort['portNumber']))
                                                        if int(port) == int(mezzPort['portNumber']) and 'guid' in mezzPort :                                                            
                                                            for guid in mezzPort['guid'] :
                                                                log.debug('            physFunc: %s', guid['physicalFunction'])
                                                                if guid['physicalFunction'] == physFunc :
                                                                    log.debug('            oa portWWN FOUND in mezz_cards: %s', guid['guid'])
                                                                    #OA or VC is screwing up the WWNs on CNAs
                                                                    if guid['guid'].endswith(':00:00') and not guid['guid'].startswith('10:00:') :
                                                                        downlink['portWWN'] = ('10:00:' + guid['guid'][0:-6]).lower()
                                                                        log.debug("Fixing WWN: %s -> %s", guid['guid'], downlink['portWWN'])
                                                                    else :
                                                                        downlink['portWWN'] = guid['guid'].lower()
                                                                    foundOAPortMap = True
                                            
                                            
                                            if not foundOAPortMap :      
                                                log.debug('Looking for mapping in oa port map - mezzNumber: %d, mezzPort: %d',int(mezzNumber), int(port))
                                                oapms = Network.get_blade_port_map(int(bladeBayNumber), datacollector.oa)
                                                for mezz in oapms['mezz'] :
                                                    #log.debug('                mezz: %s, %s', mezz['mezzNumber'], mezzNumber)
                                                    if int(mezz['mezzNumber']) == int(mezzNumber) : 
                                                        if mezz['mezzDevices']['type'] != "MEZZ_DEV_TYPE_MT" :
                                                            for mezzDevPort in mezz['mezzDevices']['port'] :
                                                                #log.debug('                    port: %s, %s', mezzDevPort['portNumber'], port)
                                                                if int(port) == int(mezzDevPort['portNumber']) :
                                                                    log.debug('            oa portWWN FOUND in port_maps: %s', mezzDevPort['wwpn'])
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
                if not s.get('stats', {}).get('remote-chassis-id', None) :
                    continue
                switch_id = s['stats']['remote-chassis-id'].lower()
                port_id = s['id']
                sw = {}         
                sw['id'] = switch_id
                sw['remote-chassis-id'] = switch_id          
                sw['remote-system-desc'] = s['stats']['remote-system-desc']            
                sw['remote-system-name'] = s['stats']['remote-system-name']
                sw['remote-system-capabilities'] = s['stats']['remote-system-capabilities']            
                
                # NGC specific as java can't handle hyphenated properties
                sw['remote_chassis_id'] = switch_id          
                sw['remote_system_desc'] = s['stats']['remote-system-desc']            
                sw['remote_system_name'] = s['stats']['remote-system-name']
                sw['remote_system_capabilities'] = s['stats']['remote-system-capabilities']            

                sw['ports'] = []
                
                port = {}
                port['id'] = port_id
                port['remote-port-id'] = s['stats']['remote-port-id']
                port['remote-port-desc'] = s['stats']['remote-port-desc']
                # NGC specific as java can't handle hyphenated properties
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
