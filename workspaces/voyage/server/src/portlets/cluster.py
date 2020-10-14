'''
Created on Oct 25, 2011

@author: IslamM
'''

from portlets.collector import ClusterCollector 
from copy import deepcopy
from host import Host
import json
import web
from util import config
from util import auth
from util import credential
from util.credential import credkey
from util.authclient import get_authclient
from engines import hponeview
import traceback
from util.stringcrypt import str_decrypt

from logging import getLogger
log = getLogger(__name__)

def clusterstatus():
    cluster_status_list = []
    dc = ClusterCollector.get_collector()
    for server in dc.servers :
        Host.add_hoststatus(cluster_status_list, dc.servers[server], dc.ilos.get(server, None))
    
    for oa in dc.oas:            
        Host.add_oastatus(cluster_status_list, oa)

    host_uuid_list = []
    for host in dc.hosts:
        host_uuid_list.append(host.hardware.systemInfo.uuid)
    
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
        
        hpovserverProfileData = client.getServerProfilesbyUuids(host_uuid_list)
        hpovServerHardwareData = client.getServersbyUuids(host_uuid_list)
        
        for k,v in hpovserverProfileData.iteritems():
            if v:
                Host.addOVProfileHealthStatus(cluster_status_list,client.baseurl,v)
        
        for k,v in hpovServerHardwareData.iteritems():
            if v:
                Host.addOVHardwareHealthStatus(cluster_status_list,client.baseurl,v)
        

    return {'result' : cluster_status_list} 

def add_status(data, server):
    pass

def summary():
    dc = ClusterCollector.get_collector()
    data = {}
    data['cluster_name'] = dc.cluster.name
    data['hosts'] = len(dc.hosts)
    no_of_vms = 0
    no_of_blades = 0
    for h in dc.hosts:
        if h.vm:
            no_of_vms += len(h.vm.ManagedObjectReference)
        uuid = h.hardware.systemInfo.uuid
        hd = dc.host_details.get(uuid, None)
        if hd :
            product_name = hd.hardware.systemInfo.model
            if 'bl' in product_name.lower() :
                no_of_blades += 1
    data['vms'] = no_of_vms
    data['blade'] = False
    if no_of_blades :
        data['blade'] = True
    data['blades'] = no_of_blades
    log.debug('Cluster %s has %d blades', data['cluster_name'], data['blades'])
    return data

def get_presense_count(obj_list):
    count = 0
    for b in obj_list:
        if b['presence'] == 'PRESENT':
            count += 1            
    return count

def infrasummary():
    dc = ClusterCollector.get_collector()
    oa_list = []
    
    for oa in dc.oas:
        oa_obj = {}
        enc = {}
        enc['name'] = oa['enclosure_info']['name']
        enc['serialNumber'] = oa['enclosure_info']['serialNumber']
        enc['enclosureName'] = oa['enclosure_info']['enclosureName']
        enc['rackName'] = oa['enclosure_info']['rackName']
        enc['oaBays'] = oa['enclosure_info']['oaBays']
        enc['bladeBays'] = oa['enclosure_info']['bladeBays']
        enc['powerSupplyBays'] = oa['enclosure_info']['powerSupplyBays'] 
        enc['fanBays'] = oa['enclosure_info']['fanBays']
        enc['bladesPresent'] = get_presense_count(oa['blade_info']['bladeInfo'])
        enc['powerSuppliesPresent'] = get_presense_count(oa['ps_info']['powerSupplyInfo'])
        enc['fansPresent'] = get_presense_count(oa['fan_info']['fanInfo'])
        pwr = {}
        pwr['powerConsumed'] = oa['power']['powerConsumed']
        pwr['redundancy'] = oa['power']['redundancy']
        
        therm = {}
        therm['redundancy'] = oa['thermal']['redundancy']
        
        oa_obj['enclosure_info'] = enc
        oa_obj['power'] = pwr
        oa_obj['thermal'] = therm
        
        oa_list.append({'oa': oa_obj})
        
    return {'oas': oa_list}

def detail():
    dc = ClusterCollector.get_collector()
    data = {}
    data['cluster_name'] = dc.cluster.name
    data['hosts'] = []
    data['vms'] = []
    log.debug("Cluster: %s, server count: %d", dc.cluster.name, len(dc.servers))
    for host in dc.hosts :   
        host_data = {}
        uuid = host.hardware.systemInfo.uuid
                
        Host.add_power_cost_advantage(host_data, dc.vcntr, dc.ilos.get(uuid, None), host)
        Host.add_host_info(host_data, dc.host_details.get(uuid, None))
        Host.add_ilo_info(host_data, dc.ilos.get(uuid, None))
        host_data['ilo_address'] = dc.ilo_address(uuid)
        Host.add_server_entity_data(host_data, dc.servers.get(uuid, None))
        Host.add_status(host_data, dc.servers.get(uuid, None))
        Host.add_provider_bundle_status(host_data, dc.servers.get(uuid, None))        
        Host.add_memory_detail(host_data, dc.ilos.get(uuid, None))
        Host.add_cpu_detail(host_data, dc.ilos.get(uuid, None), dc.host_details.get(uuid, None))
        Host.add_sw_and_fw_detail("", host_data, dc.servers.get(uuid, None))        
        Host.add_logs(host_data, dc.ilos.get(uuid, None))
        
        data['hosts'].append(host_data)        
                
        
    return data

def infradetail():
    dc = ClusterCollector.get_collector()    
    oas = deepcopy(dc.oas)
    for oa in oas :
        oa['enclosure_info']['bladesPresent'] = get_presense_count(oa['blade_info']['bladeInfo'])
        oa['enclosure_info']['powerSuppliesPresent'] = get_presense_count(oa['ps_info']['powerSupplyInfo'])
        oa['enclosure_info']['fansPresent'] = get_presense_count(oa['fan_info']['fanInfo'])
    return {'oas': oas}
        

def swfwsummary():
    dc = ClusterCollector.get_collector()
    data = {}
    
    for server in dc.servers :
        swfw_data = {}
        try:                        
            Host.add_sw_component_detail('',swfw_data, dc.servers[server], 'Software', 'software', ['hp-smx',] )
            Host.add_sw_component_detail('',swfw_data, dc.servers[server], 'Firmware', 'firmware', ['Array', 'Power', 'iLO', 'System ROM'])
            for object_name in swfw_data :
                for item in swfw_data[object_name] :
                    if item['version'] not in data.setdefault(object_name, {}).setdefault(item['name'], {'name': item['name'], 'description':item['description'], 'version': None, 'versions':[]})['versions'] :
                        data[object_name][item['name']]['versions'].append(item['version'])
                
        except Exception as e:
            log.exception("Error getting cluster swfwsummary for %s", str(server))                
    
    for object_name in data :
        data[object_name] = data[object_name].values()
    data.setdefault('software', [])
    data.setdefault('firmware', [])
    return data    

def launchtools() :
    q = web.input()
    dc = ClusterCollector.get_collector()
    cfg = config.config()
    data = { 'launch_tools' : [] }        
    role = -9999
    try:
        role = auth.get_effective_role(dc.sessionId, dc.moref, dc.serverGuid)
    except:
        log.exception("Error getting user effective role")
        return data
        
    log.debug('Effective Role for %s is %s', q.sessionId, role)
    log.debug("ClusterCollector attribs = %s", dc)
    if role == -2 :     # Read-Only users do not get launch links.  Sorry...
        return data
    
    if role == -9999 :  # Error reading effective role
        return data
    
    lt = {}
    lt['id'] = 'cluster_oa_launch_tool'
    lt['icon_url'] = "/static/img/icons/oa_icon.png"
    lt['launch_links'] = []
    
    for oa in dc.oas :                
        try : 
            if role == -1 :
                ll = {}
                ll['type'] = 'LINK'
                ll['label'] = 'OA at ' + oa['address']['ipv4']
                ll['url'] = cfg.get_uim_root() + '/sso/' + web.ctx.query + '&ssoip=' + oa['address']['ipv4']
                lt['launch_links'].append(ll)
            else :
                ll = {}
                ll['type'] = 'LINK'
                ll['label'] = 'OA at ' + oa['address']['ipv4']
                ll['url'] = 'https://' + oa['address']['ipv4']
                lt['launch_links'].append(ll)
                
        except : 
            log.exception("Exception creating OA launch tool")
            
    if lt['launch_links'] :
        data['launch_tools'].append(lt)
    
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
                ll['url'] = cfg.get_uim_root() + '/static/hpsimlogin.html' + web.ctx.query + '&simtool=none&simserver=' + simpw.host + '&simport=' + str(cfg.get_simport()) #+ '&simhost=' + simpw.host                 
                lt['launch_links'].append(ll)
            else :
                ll = {}
                ll['type'] = 'LINK'
                ll['label'] = 'SIM'
                ll['url'] = 'https://' + simpw.host + ':' + str(cfg.get_simport())
                lt['launch_links'].append(ll)
            
            data['launch_tools'].append(lt)
        except :
            log.exception("Exception creating SIM launch tool")

    #Begin HPOV launch links
    try:
        hpov_creds = credential.get(credtype='hponeview')
        log.debug("Host: All HPOVs %s", hpov_creds)
        lt = {}
        lt['id'] = 'hp_one_view_launch_tool'
        lt['icon_url'] = "/static/img/icons/oneview_icon.png"
        lt['launch_links'] = []
        for x in hpov_creds:
            hpov_exists = False
            client = get_authclient().get(x)
            if not client:
                pw = credential.find_pw_for_host_and_type(
                        host = x['ip'], pwtype = 'hponeview')
                #log.debug("Host.py: HPOV cred = %s and password = %s ", x, pw)
                client = hponeview.hponeview(baseurl = x['ip'], verify=False)
                __passwd = str_decrypt(pw['epassword'])
                log.debug("Password = %s", __passwd)
                success = client.login(username = x['username'], password = __passwd)
                assert(success)
                get_authclient().add(credkey(typ=x['type'], ip = x['ip'], 
                    username = x['username']), client)
            for host in dc.hosts:
                host_uuid = host.hardware.systemInfo.uuid
                log.debug("host_uuid = %s", host_uuid )
                hpov_exists = client.isServerManaged([host_uuid])[host_uuid]
                if hpov_exists:
                    break
            if hpov_exists:
                ll = {}
                ll['type'] = 'LINK'
                ll['label'] = 'HPOV Login Page:' + client.baseurl
                ll['url'] = 'https://'+client.baseurl
                lt['launch_links'].append(ll)
                log.debug("Inside of HPOV: lt = %s", lt)
                continue
        if (lt['launch_links']):
            #Dummy launch link code to check hover-over-icon behavior: Begin
            #Uncomment the code lines below to reintroduce the dummy
            '''
            ll = {}
            ll['type'] = 'LINK'
            ll['url'] = 'https://'+'16.83.121.47'
            ll['label'] = 'HPOV Login Page: ' + '16.83.121.47'
            lt['launch_links'].append(ll)
            '''
            #Dummy launch link code to check hover-over-icon behavior: End
            data['launch_tools'].append(lt)
    except:
        log.exception("Exception creating HP One View launch tool")
        log.debug("Traceback %s", traceback.format_exc())

    #End HPOV launch links
    return data    
        
class ClusterSwFwDetail :
    
    def GET(self) :
        log.debug('ClusterSwFwDetail GET query: %s', web.ctx.query)
        data = self.get_swfwdetail()
        return json.dumps(data)
    
    def POST(self) :
        log.debug('ClusterSwFwDetail POST query: %s', web.ctx.query)
        return json.dumps(None)
    
    def get_swfwdetail(self) :
        dc = ClusterCollector.get_collector()
        data = {                
                'rows': [], 
                }        
        for server in dc.servers :
            swfw_data = {}        
            try:          
                host_name = dc.get_host(server).name
                Host.add_sw_component_detail(host_name, swfw_data, dc.servers[server], 'Software', 'software')
                Host.add_sw_component_detail(host_name, swfw_data, dc.servers[server], 'Firmware', 'firmware')
                for object_name in swfw_data :
                    for item in swfw_data[object_name] :                
                        data['rows'].append(
                            {
                                'host': host_name,
                                'type': object_name,
                                'name': item['name'],
                                'description': item['description'],
                                'version' : item['version'],                            
                            }
                        )                        
                    
            except Exception as e:
                log.exception("Error getting cluster swfwdetail for %s", str(server))                
        
        return data    
        
