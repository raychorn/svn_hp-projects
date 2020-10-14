import re
import json
from logging import getLogger
from util import exceptions
from util.Dict import Dict
log = getLogger(__name__)
import logging

class VCM:
    @staticmethod
    def hponeview_vcm(oneview,uuid):
        normalize = lambda aList:[Dict(__d__) for __d__ in aList]
        try:
            vc={}
            server_hardware_uri='/rest/server-hardware/'+str(uuid).upper()
            server_hardware=oneview.get_server_hardware(server_hardware_uri)
            profile=oneview.get_server_profile(server_hardware['serverProfileUri'])
            
            physical_nics=[]
            for device in server_hardware['portMap']['deviceSlots']:
                for p in device['physicalPorts']:
                    for vp in p['virtualPorts']:
                        p_nic={}
                        p_nic['physicalPortMapping']={}
                        p_nic['physicalPortMapping']['mezz']=device['location'].upper()
                        if p_nic['physicalPortMapping']['mezz']=='FLB 1':
                            p_nic['physicalPortMapping']['mezz']='LOM 1'
                        p_nic['physicalPortMapping']['port']='PORT'+str(p['portNumber'])+'-'+vp['portFunction']
                        p_nic['physicalPortMapping']['portType'] = 'subport'
                        p_nic['physicalPortMapping']['ioBay']=0
                        p_nic['mac']=vp['mac'].lower()
                        physical_nics.append(p_nic)
            vc['physical_nics']=physical_nics
            enclosures={}
            externalStorage={}
            externalSwitches={}

            connections=Dict(oneview.get_connections())
            hostConnections={}
            network_uris=set()

            for conn in normalize(profile['connections']):
                hostConnections[connections[conn['mac']]['connectionInstanceId']]=connections[conn['mac']]
                hostConnections[connections[conn['mac']]['connectionInstanceId']]['port']=conn['portId']
                hostConnections[connections[conn['mac']]['connectionInstanceId']]['speed']=conn['allocatedMbps']
                if connections[conn['mac']]['connectionInstanceType']=='NetworkSet':
                    n_set=oneview.get_network_set(conn['networkUri'])
                    network_uris=network_uris | set(n_set['networkUris'])
                else:network_uris.add(connections[conn['mac']]['networkResourceUri'])
            interconnects=oneview.getAllInterconnects()
            for interconnect in normalize(interconnects):
                enc=Dict(oneview.get_enclosure(interconnect['enclosureUri']))
                enclosureName=enc['name']
                enclosureId=enc['uuid']
                enclosureType=enc['enclosureType']
                rack=enc['rackName']

                __re__ = re.compile(r"\s(?P<enclosuretype>[a-zA-Z][0-9]*)\s", re.MULTILINE)
                __match__ = __re__.search(enclosureType)
                if __match__:
                    enclosureType = str(__match__.group()).strip()
                bay=interconnect['ports'][0]['bayNumber']
                log.debug('Module %s in Enclosure %s bay %s',interconnect['uri'],enclosureId,bay)
                m={}  
                m['id']=interconnect['uri']
                m['bay']=bay
                m['enclosureId']=enclosureId
         
                c={}
                c['bay']=bay
                c['powerState']=interconnect['powerStatus']
                c['ipaddress']=interconnect['interconnectIP'] if (interconnect['interconnectIP']) else 'unknown'
                c['serialNumber']=interconnect['serialNumber'] if (interconnect['serialNumber']) else 'unknown'
                c['productName']=interconnect['productName']
                c['fwRev']=interconnect['firmwareVersion']
                c['rackName']=rack
                c['partNumber']=interconnect['partNumber']
                c['enclosureName']=enclosureName
                c['vcManagerRole']='HP One View Managed'

                c['powerState']=interconnect['powerStatus']
                c['commonAttrs']={}
                c['commonAttrs']['overallStatus']=interconnect['status']

                m['commonIoModuleAttrs']=c

                m['commStatus'] = interconnect['status']
                m['dipSwitchSettings'] = 0
                m['downlinkPorts'] = None
                m['fcAttrs'] = {}
                m['oaReportedStatus'] = interconnect['status']
                m['powerState'] = interconnect['powerStatus']
                m['uid'] = '???'
                m['uplinkPorts'] = None

                commonAttrs = {}
                commonAttrs['id'] = m['id']
                commonAttrs['managedStatus'] = 'NORMAL'
                commonAttrs['overallStatus'] = m['commStatus']   
                commonAttrs['vcOperationalStatus'] = m['commStatus']
                m['commonAttrs'] = commonAttrs
                
                fcAttrs={}
                fcAttrs['moduleWwn']='10:00:00:11:0a:02:09:35' #need to figure out what to put here
                fcAttrs['moduleType']= "HP One View"    #need to figure out what to put here
                m['fcAttrs']=fcAttrs


                m['macAddress'] = None
                m['uplinks']=[]
                m['networks']=[]
                m['fabrics']=[]
                uplinks={}

                ethernet_networks=oneview.get_ethernet_networks()
                for n in normalize(ethernet_networks):
                    if n['uri'] in network_uris:
                        log.debug('    Network: %s, %s', n['name'] , n['uri'])
                        vc_network = {'id':n['uri'], 'uplinkVLANId': n['vlanId'],
                                      'maxPortSpeed': 10000,
                                      'preferredPortSpeed': 2500,
                                      'displayName': n['name'], 'downlinks': [],
                                      'portlinks': []}
                       
                        for conn in normalize(hostConnections.itervalues()):
                            if conn['interconnectUri']==interconnect['uri'] and conn['connectionInstanceType']=='Ethernet' and n['uri']==conn['networkResourceUri']:
                                port={}
                                log.debug('     Downlink SubPort:%s',conn['interconnectSubPort'])
                                port['id']=interconnect['uri']+':'+str(conn['interconnectPort']) +':'+str(conn['interconnectSubPort'])
                                port['macAddress']=conn['macaddress'].lower()
                                port['speedGb']=conn['speed']/1000.0
                             
                                port['physicalPortMapping']={}
                                port['physicalPortMapping']['mezz']=conn['port'].split(':')[0].upper()
                                log.debug("NIC NAME %s",port['physicalPortMapping']['mezz'])
                                log.debug("ENTER %s",port['physicalPortMapping']['mezz']=='FLB')
                                if port['physicalPortMapping']['mezz']=='FLB 1':
                                    port['physicalPortMapping']['mezz']='LOM 1'
                                port['physicalPortMapping']['port']='PORT'+conn['port'].split(':')[1]
                                #log.debug("MEZZ!!! %s,PORT!!! %s",port['physicalPortMapping']['mezz'],port['physicalPortMapping']['port'])
                                port['physicalPortMapping']['portType'] = 'subport'
                                port['physicalPortMapping']['ioBay']=bay
                                vc_network['downlinks'].append(port)
                                
                                    
                                
                            elif conn['interconnectUri']==interconnect['uri'] and conn['connectionInstanceType']=='NetworkSet':
                                n_set=oneview.get_network_set(conn['networkResourceUri'])
                                if n['uri'] in n_set['networkUris']:
                                    port={}
                                    log.debug('     Downlink SubPort:%s',conn['interconnectSubPort'])
                                    port['id']=interconnect['uri']+':'+str(conn['interconnectPort']) +':'+str(conn['interconnectSubPort'])
                                    port['macAddress']=conn['macaddress'].lower()

                                   
                                    port['speedGb']=conn['speed']/1000.0
                                    port['physicalPortMapping']={}

                                    port['physicalPortMapping']['mezz']=conn['port'].split(':')[0].upper()
                                    if port['physicalPortMapping']['mezz']=='FLB 1':
                                        port['physicalPortMapping']['mezz']='LOM 1'
                                    port['physicalPortMapping']['port']='PORT'+conn['port'].split(':')[1]
                                    port['physicalPortMapping']['portType'] = 'subport'
                                    port['physicalPortMapping']['ioBay']=bay
                                    vc_network['downlinks'].append(port)
                              

                        for port in normalize(interconnect['ports']):
                            if port['portType']=='Uplink' and port['associatedUplinkSetObjectId']:
                                uplink=oneview.get_uplink(port['associatedUplinkSetObjectId'])
                                if n['uri'] in uplink['networkUris']:
                                    log.debug('     Uplink: %s', port['portId'])
                                    vc_network.setdefault('portlinks',[]).append(port['portId'])
                                    log.debug("VC_NETWORK SET DEFAULT %s",vc_network['displayName'])
                                    speedGb=port['operationalSpeed']
                                    speedGb=int(re.sub("[^0-9]", "",speedGb ))

                                    uplink={'id':port['portId'], 
                                            'uplinkType': 'network',
                                            'duplexStatus':'FULL',
                                            'supportedSpeeds':'1000/full',
                                            'physicalLayer':'10G-SFP',
                                            'remoteChassisId':port['neighbor']['remoteChassisId'], 
                                            'remotePortId':port['neighbor']['remotePortId'],
                                            'connectorType': port['connectorType'],
                                            'linkStatus':port['portStatus'].upper()+'-'+port['portStatusReason'].upper(),
                                            'opSpeed':speedGb,
                                            'speedGb':speedGb,
                                            'portLabel': port['portName']}
                                    uplinks[port['portId']] = uplink
                       
                          
                            
                        if vc_network['downlinks'] or vc_network['portlinks'] :
                             m['networks'].append(vc_network)
              
                fc_networks=oneview.get_fc_networks()
                for f in normalize(fc_networks):
                    if f['uri'] in network_uris:
                        log.debug('    Fabric: %s, %s', f['name'] , f['uri'])
                        fabric_id = f['uri']
                        fabric = {'id':fabric_id, 'displayName': f['name'], 
                              'downlinks': [], 'portlinks': [] }  
                        for conn in normalize(hostConnections.itervalues()):
                            if conn['interconnectUri']==interconnect['uri'] and conn['connectionInstanceType']=='Fcoe' and f['uri']==conn['networkResourceUri']:
                                log.debug('  FC   Downlink SubPort:%s',conn['interconnectSubPort'])
                                port_id=interconnect['uri']+':'+str(conn['interconnectPort']) +':'+str(conn['interconnectSubPort'])
                                
                                speedGb=conn['speed']/1000.0
                             
                                physicalPortMapping={}
                                
                                physicalPortMapping['mezz']=conn['port'].split(':')[0].upper()
                                if physicalPortMapping['mezz']=='FLB 1':
                                    physicalPortMapping['mezz']='LOM 1'
                                physicalPortMapping['port']=conn['port'].split(':')[1]
                                physicalPortMapping['portType'] = 'subport'
                                physicalPortMapping['ioBay']=bay
                                
                                downlink = {'id': port_id, 
                                                        'physicalServerBay': server_hardware['position'],
                                                        'speedGb': speedGb,
                                                        'portWWN': None,
                                                        'physicalPortMapping':physicalPortMapping}
                                downlink['portWWN']=conn['wwpn']
                                fabric['downlinks'].append(downlink)
                        for port in normalize(interconnect['ports']):
                            if port['portType']=='Uplink' and port['associatedUplinkSetObjectId'] and 'FibreChannel' in port['configPortTypes']:
                                log.debug("FIBER CHANNEL CONFIG")
                                uplink=oneview.get_uplink(port['associatedUplinkSetObjectId'])
                                if f['uri'] in uplink['fcNetworkUris']:
                                    log.debug('     Uplink: %s', port['portId'])
                                    fabric.setdefault('portlinks',[]).append(port['portId'])
                                    log.debug("FC_NETWORK SET DEFAULT %s",fabric['displayName'])
                                    speedGb=port['operationalSpeed']
                                    speedGb=int(re.sub("[^0-9]", "",speedGb ))
                                    
                                    fabric['portlinks'].append(port['portId'])
                                    uplink = {'id':port['portId'], 'uplinkType': 'fc', 'portLabel':port['portName'], 'currentSpeed':speedGb,
                                    'portWWN': port['fcPortProperties']['wwpn'], 'connectedToWWN':port['fcPortProperties']['logins'], 'speedGb': speedGb,
                                    'portConnectStatus': port['portStatusReason'] }  
                                     # the duplicate 'wwn' field is to make the java Jackson parser happy
                                    #externalStorage.setdefault(port['nodeWwn'].lower(), {'wwn':port['nodeWwn'],'WWN':port['nodeWwn'], 'portWWN':[]})['portWWN'].append(port['portWwn'])
                                    uplinks[port['portId']] = uplink
                                    
                                    log.debug('  FC      Uplink: %s', port['portName'])
                                   
                                    
                        if(fabric['portlinks'] or fabric['downlinks'] ) :                        
                                            m['fabrics'].append(fabric)
                
                
                for port in normalize(interconnect['ports']):
                    if m['powerState']=='On' and port['portType']=='Uplink' and port['associatedUplinkSetObjectId'] and port['neighbor']['remoteChassisId']!='' and port['portTypeExtended']=='External':
                        uplink=oneview.get_uplink(port['associatedUplinkSetObjectId'])
                        nets=set(uplink['networkUris'])
                        if nets & network_uris:
                            switch_id=port['neighbor']['remoteChassisId']
                            port_id=port['portId']
                            sw={}
                            sw['id']=switch_id
                            sw['remote-chassis-id'] = switch_id          
                            sw['remote-system-desc'] = port['neighbor']['remoteSystemDescription']            
                            sw['remote-system-name'] = port['neighbor']['remoteSystemName']
                            sw['remote-system-capabilities'] = port['neighbor']['remoteSystemCapabilities']            

                            # NGC specific as java can't handle hyphenated properties
                            sw['remote_chassis_id'] = switch_id          
                            sw['remote_system_desc'] = port['neighbor']['remoteSystemDescription']
                            sw['remote_system_name'] = port['neighbor']['remoteSystemName']
                            sw['remote_system_capabilities'] =port['neighbor']['remoteSystemCapabilities']

                            sw['ports'] = []

                            p = {}
                            p['id'] = port_id
                            p['remote-port-id'] = port['neighbor']['remotePortId']
                            p['remote-port-desc'] = port['neighbor']['remotePortDescription']
                            # NGC specific as java can't handle hyphenated properties
                            p['remote_port_id'] = port['neighbor']['remotePortId']
                            p['remote_port_desc'] = port['neighbor']['remotePortDescription']

                            externalSwitches.setdefault(switch_id, sw)['ports'].append(p)  
                for port in normalize(interconnect['ports']):
                    if m['powerState']=='On' and port['portType']=='Uplink' and port['associatedUplinkSetObjectId'] and 'FibreChannel' in port['configPortTypes']:
                        uplink=oneview.get_uplink(port['associatedUplinkSetObjectId'])
                        nets=set(uplink['fcNetworkUris'])
                        if nets & network_uris:
                            externalStorage.setdefault(port['fcPortProperties']['logins'], {'wwn':port['fcPortProperties']['logins'],'WWN':port['fcPortProperties']['logins'], 'portWWN':[]})['portWWN'].append(port['fcPortProperties']['wwpn'])
                            

                m['uplinks']=uplinks.values()
                m['uplinks'].sort(lambda a, b : cmp(a['id'], b['id']))
                m['networks'].sort(lambda a, b : cmp(a['displayName'], b['displayName']))
                m['fabrics'].sort(lambda a, b : cmp(a['displayName'], b['displayName']))

                enclosure = {'enclosureType':enclosureType, 'id': enclosureId, 'enclosureName': enclosureName,
                             'allVcModuleG1s': []}
                if len(m['uplinks']) or len(m['networks']) or len(m['fabrics']) :
                    enclosures.setdefault(enclosureId, enclosure)['allVcModuleG1s'].append(m)            
                    enclosures[enclosureId]['allVcModuleG1s'].sort(lambda a, b : cmp(a['bay'], b['bay']))                    
            vc['enclosures'] = enclosures.values()
            vc['enclosures'].sort(lambda a, b : cmp(a['id'], b['id']))




            vc['externalSwitches'] = externalSwitches.values()
            for es in vc['externalSwitches'] :
                es['ports'].sort(lambda a, b : cmp(a['id'], b['id']))

            vc['externalStorage'] = externalStorage.values()
            return vc
        except Exception, details :
            f = exceptions.formattedException(details=details)
            log.exception("Failed to get VCM data from HP Oneview as follows: %s"%(f))
        return None

    @staticmethod
    def hponeview_telemetry(vcm,oneview):
        try:
            if not vcm:return
            module_stats={}
            for e in vcm.get('enclosures',[]):
                for m in e.get('allVcModuleG1s',[]):
                    if m['powerState']=='On':
                        module_stats[m['id']]=oneview.get_interconnect_stats(m['id'])
            for e in vcm.get('enclosures', []) :
                for m in e.get('allVcModuleG1s', []) :
                    # Uplink Telemetry
                    if m['powerState']=='On':
                        for u in m.get('uplinks', []) :
                            for port in module_stats[m['id']]['portStatistics']:
                                if port['portName']== u['portLabel']:
                                    telemetry = {}
                                    telemetry['tx_kbps'] = port['advancedStatistics']['transmitKilobitsPerSec'].split(':')[:12]
                                    telemetry['rx_kbps'] = port['advancedStatistics']['receiveKilobytesPerSec'].split(':')[:12]                                                                                   
                                    log.debug('Telemetry raw tx: "%s"', port['advancedStatistics']['transmitKilobitsPerSec'])
                                    log.debug('Telemetry tx: "%s"', str(telemetry['tx_kbps']))
                                    log.debug('Telemetry raw rx: "%s"', port['advancedStatistics']['receiveKilobytesPerSec'])
                                    log.debug('Telemetry rx: "%s"', str(telemetry['rx_kbps']))

                                    telemetry['properties'] = {}
                                    telemetry['properties']['port_telemetry_period'] = module_stats[m['id']]['moduleStatistics']['portTelemetryPeriod']
                                    telemetry['properties']['port_telemetry_entry_count']=len(telemetry['tx_kbps'])
                                    u['telemetry'] = telemetry
                                    log.debug("Telemetry uplink id: %s period: %s count: %s", u['id'], u['telemetry']['properties']['port_telemetry_period'], u['telemetry']['properties']['port_telemetry_entry_count'])
        except Exception, details :
            f = exceptions.formattedException(details=details)
            log.exception("Failed to get VCM data from HP Oneview as follows: %s"%(f))
        return None
