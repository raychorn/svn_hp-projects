'''
@author: Partho S Bhowmick
'''

import requests
import json
import time
import traceback
import thread
import Queue
import copy
import re
from util.stringcrypt import *
from util import ioTimeDeco
from logging import getLogger
log = getLogger(__name__)

_q = Queue.Queue()
_hostlist_cache = {}

def _cache_server(hponeview_host, servers):
    server_dict = {'baseurl':hponeview_host, 'servers':servers, 'time':time.gmtime()}
    #log.debug("Caching server info = %s", server_dict)
    _q.put(server_dict)

def _deque_server_cache_info():
    while True:
        server_dict = _q.get()
        for host in server_dict['servers']:
            _hostlist_cache[host['uuid'].upper()] = {'baseurl':server_dict['baseurl'], 
                    'time':server_dict['time']}       
            if host['virtualUuid']: 
                _hostlist_cache[host['virtualUuid'].upper()] = _hostlist_cache[host['uuid'].upper()] 
        _q.task_done()

def isServerManagedCache(uuid):
    '''
    Reads from a global cache of server hardware info (maintained from 
    previous calls to getAllServers(), getServersbyUuids(), getServerByUri()
    and isServerManaged() to determine if a host is managed by an instance of 
    HPOV
    @param uuid: The uuid (virtual or physical) of the server being queried
    for.
    '''
    try:
        return _hostlist_cache[uuid.upper()]
    except KeyError:
        return False

thread.start_new_thread(_deque_server_cache_info, ())

def _hponeview_copyconstructor(coredata):
    try:
        obj = hponeview(baseurl = coredata['baseurl'], verify =coredata['verify'])
        obj.headers['auth'] = coredata['auth']
        obj.username = coredata['username']
        obj.epassword = coredata['epassword']
        return obj
    except:
        log.debug("Traceback %s", traceback.format_exc())
        raise
    
class IOTImeAnalysis():
    def __call__(self,*args,**kwargs):
        pass

class hponeview:
    '''
    All things HP One View
    '''
    def __init__(self, baseurl, verify=True):
        '''
        :param - baseurl: the IP address or hostname of the HPOV server
        :param - verify: If True, all GET/POST calls to the HPOV server will 
         require that the server's security certificate is to be valid
        '''
        self.baseurl = baseurl 
        self.verify = verify
        self.headers = {'Content-Type':'application/json',
                'Accept':'application/json'}
        self.server_hardware={}
        self.server_profile={}
        self.enclosure={}
        self.connections={}
        self.interconnect_stats={}
        self.uplink={}
        self.network_set={}

    def _copycore(self):
        return [_hponeview_copyconstructor,
                {'verify':self.verify, 'auth':self.headers['auth'],
                    'epassword':self.epassword, 'username':self.username,
                    'baseurl': self.baseurl
                }
                ]

    def __getwithretry__(self, relurl):
        r = requests.get("https://%s%s" % (self.baseurl, relurl), 
                verify= self.verify,  headers=self.headers)
        if (r.status_code == 401):
            success = self.login(self.username, str_decrypt(self.epassword))
            if (success):
                r = requests.get("https://%s%s" % (self.baseurl, relurl), 
                        verify= self.verify,  headers=self.headers)
        return r

    def __postwithretry__(self, relurl, body):
        r = requests.post("https://%s%s" % (self.baseurl, relurl), 
                verify= self.verify,  headers=self.headers,  data = json.dumps(body))
        if (r.status_code == 401):
            success = self.login(self.username, str_decrypt(self.epassword))
            if (success):
                r = requests.post("https://%s%s" % (self.baseurl, relurl), 
                verify= self.verify,  headers=self.headers,  data = json.dumps(body))
        return r

    def __putwithretry__(self, relurl, body):
        r = requests.put("https://%s%s" % (self.baseurl, relurl), 
                verify= self.verify,  headers=self.headers,  data = json.dumps(body))
        if (r.status_code == 401):
            success = self.login(self.username, str_decrypt(self.epassword))
            if (success):
                r = requests.put("https://%s%s" % (self.baseurl, relurl), 
                verify= self.verify,  headers=self.headers,  data = json.dumps(body))
        return r

    @ioTimeDeco.Analyze('hponeview.login')
    def login(self, username, password):
        '''
        The first call to be made after object creation to log into the
        HPOV service.
        :param - username: The username to use when logging into HPOV
        :param - password: The password to use when logging into HPOV
        :return - True if the object successfully logs into the HPOV service
        :exception - requests.HTTPError thrown on failure.
        '''
        relurl = '/rest/login-sessions'
        self.username = username
        self.epassword = str_encrypt(password)
        fuapi={}
        fuapi['userName']=username
        fuapi['password']=password
        r = requests.post("https://%s%s" % (self.baseurl, relurl), 
                verify=self.verify,  data=json.dumps(fuapi),
                headers= self.headers)
        self.last_response = r.content
        if (r.status_code == 200):
            d = json.loads(r.content)
            if len(d['sessionID']) > 0: 
                self.headers['auth'] = d['sessionID']
                self.networks = {'ethernet':{}, #ethernet networks
                        'fc':{} #fiber channel networks
                        }
                return True
        else:
            r.raise_for_status()
        return False

#Begin: Connection templates
    @ioTimeDeco.Analyze('hponeview.get_conn_templates')
    def get_conn_templates(self, names=[], refresh = True):
        '''
        If refresh is False, names is ignored and the cached connection templates 
        info is returned.
        If refresh is True but no names are provided, all the connection templates 
        info is refreshed from Fusion and returned
        If refresh is True and names are provided, only the connection templates 
        in names are refreshed from Fusion.
        Note: ALL known connection templates are returned, not just the one in names
        '''
        if hasattr(self, 'connection_templates') and not refresh:
            return self.connection_templates

        if (not names):
            self.connection_templates = {}
            relurl = '/rest/connection-templates'
            r = self.__getwithretry__(relurl)
            if (r.status_code == 200):
                members = r.json()['members']
                for ct in members:
                    name = ct['name']
                    self.connection_templates[name] = ct
                    del ct['name']
                return self.connection_templates
            else:
                r.raise_for_status()
        else:
            for name in names:
                relurl = self.connection_templates[name]['uri']
                r = self.__getwithretry__(relurl)
                if (r.status_code == 200):
                    self.connection_templates[name] = r.json()
                    del self.connection_templates[name]['name']
                else:
                    r.raise_for_status()
            return self.networks['ethernet']
        pass

    @ioTimeDeco.Analyze('hponeview.add_conn_template')
    def add_conn_template(self, name, max_bw, typical_bw, **kwargs):
        '''
        #Unused
        '''
        body = {
               "type":"connection-template", 'name':name, 
               'bandwidth':{'maximumBandwidth':max_bw,
                   'typicalBandwidth':typical_bw}
               }
        for key, value in  kwargs.iteritems():
            body[key] =  value
        relurl = '/rest/connection-templates'

        r = self.__postwithretry__(relurl, body)
        self.last_response = r.content
        if (r.status_code == 201):
            d = json.loads(r.content)
            self.connection_templates[d['name']] = d
            del d['name']
            return d
        else:
            r.raise_for_status()

    @ioTimeDeco.Analyze('hponeview.del_conn_template')
    def del_conn_template(self, name):
        relurl = self.connection_templates[name]['uri']
        r = requests.delete("https://%s%s" % (self.baseurl, relurl), 
                verify= self.verify,  headers=self.headers)
        self.last_response = r.content
        if (r.status_code == 202):
            return r.json()
        else:
            r.raise_for_status()
#End: Connection templates

    #Begin: ethernet networks
    @ioTimeDeco.Analyze('hponeview.add_ethernet_network')
    def add_ethernet_network(self, name, conn_templ_name, vlanId, 
            isPrivateNetwork, isSmartLink, purpose='Unspecified', **kwargs):
        #Unused
        '''
        For kwargs, the following are the acceptable parameters
            state, description, status, uri, category, eTag, 
            created, modified
        Note: This is an async operation, so the added network is not added to 
        Fusion client cache automatically. For that, use the 
        get_ethernet_networks method afterwards.
        '''
        body = {
               "type":"ethernet-network", 'name':name, 'vlanId':vlanId,
                'privateNetwork':isPrivateNetwork, 'smartLink':isSmartLink,
                'purpose':purpose, 
                "connectionTemplate":self.connection_templates[conn_templ_name]['uri']
               }  
        for key, value in  kwargs.iteritems():
            body[key] =  value
        relurl = '/rest/ethernet-networks'
        r = self.__postwithretry__(relurl, body)
        self.last_response = r.content
        if (r.status_code == 202):
            d = r.json()
            return d
        else:
            r.raise_for_status()

    @ioTimeDeco.Analyze('hponeview.get_ethernet_networks')
    def get_ethernet_networks(self, refresh=True):
        '''
        If refresh is False, the cached ethernet network info is returned.
        If refresh is True , all the ethernet networks 
        info is refreshed from Fusion and returned
        '''
        if (not refresh) and hasattr(self, 'networks') and ('ethernet' in self.networks): 
            return self.networks['ethernet']

        if (not hasattr(self, 'networks') ):
            self.networks = {}

        relurl = '/rest/ethernet-networks'
        r = self.__getwithretry__(relurl)
        self.last_response = r.content
        if (r.status_code == 200):
            self.networks['ethernet'] = r.json()['members']
            return self.networks['ethernet']
        else:
            r.raise_for_status()
        
    @ioTimeDeco.Analyze('hponeview.del_ethernet_network')
    def del_ethernet_network(self, name):
        eth = self.networks['ethernet'][name]
        relurl = eth['uri']
        r = requests.delete("https://%s%s" % (self.baseurl, relurl), 
                verify= self.verify,  headers=self.headers)
        self.last_response = r.content
        if (r.status_code == 202):
            return r.json()
        else:
            r.raise_for_status()
    #End  : ethernet networks

    #Begin: fc networks
    @ioTimeDeco.Analyze('hponeview.get_fc_networks')
    def get_fc_networks(self, refresh=True):
        '''
        If refresh is False, the cached ethernet network info is returned.
        If refresh is True , all the ethernet networks 
        info is refreshed from Fusion and returned
        '''
        if (not refresh) and hasattr(self, 'networks') and ('fc' in self.networks): 
            return self.networks['fc']

        if (not hasattr(self, 'networks') ):
            self.networks = {}

        relurl = '/rest/fc-networks'
        r = self.__getwithretry__(relurl)
        self.last_response = r.content
        if (r.status_code == 200):
            self.networks['fc'] = r.json()['members']
            return self.networks['fc']
        else:
            r.raise_for_status()
        
    @ioTimeDeco.Analyze('hponeview.del_fc_network')
    def del_fc_network(self, name):
        eth = self.networks['fc'][name]
        relurl = eth['uri']
        r = requests.delete("https://%s%s" % (self.baseurl, relurl), 
                verify= self.verify,  headers=self.headers)
        self.last_response = r.content
        if (r.status_code == 202):
            return r.json()
        else:
            r.raise_for_status()
    
    #End  : fc networks

    @ioTimeDeco.Analyze('hponeview.getNetworkConfig')
    def getNetworkConfig(self):
        relurl='/rest/appliance/configuration/networkconfig'
        r = self.__getwithretry__(relurl)
        self.last_response = r.content
        if (r.status_code == 200):
            self.networkConfig = r.json()
            return self.networkConfig
        else:
            r.raise_for_status()
        #data={'id':id, 'username':username, 'password':password, 'host':host, 'type':type}

    @ioTimeDeco.Analyze('hponeview.filterAlerts')
    def filterAlerts(self):
        relurl='/rest/alerts?start=0&count=-1 &filter=\"alertState=\'Active\'\"'
        r = self.__getwithretry__(relurl) 
        self.last_response = r.content
        if (r.status_code == 200):
            d = r.json()
            alerts =  d['members']
            self.alerts_by_time={}
            for alert in alerts:
                alerttime = alert['created']
                self.alerts_by_time[alerttime]=alert
            return self.alerts_by_time
        else:
            r.raise_for_status()

    @ioTimeDeco.Analyze('hponeview.getDatacenters')
    def getDatacenters(self):
        relurl='/rest/datacenters'
        r = self.__getwithretry__(relurl) 
        self.last_response = r.content
        if (r.status_code == 200):
            d = r.json()
            self.datacenters = d['members']
            return self.datacenters
        else:
            r.raise_for_status()

    @ioTimeDeco.Analyze('hponeview.updateDatacenter')
    def updateDatacenter(self, ):
        for dc in self.datacenters:
            id_=dc['id']
            uuid=dc['uuid']
            width=dc['width']
            depth=dc['depth']
        #data={'id':'new', 'username':username, 'password':password, 'host':host, 'type':type}
        data={'id':id_,'uuid':uuid,'name':'MyDatacenter888','width':width,'depth':depth}
        relurl+='/'+str(id_)
        r = requests.put("https://%s%s" % (self.baseurl, relurl), 
                verify= self.verify, data=json.dumps(data), 
                headers=self.headers)
        self.last_response = r.content
        if (r.status_code == 200):
            self.updatedDatacenter=r.json()
            return self.updatedDatacenter
        else:
                r.raise_for_status()

    @ioTimeDeco.Analyze('hponeview.getAllServerProfiles')
    def getAllServerProfiles(self, refresh=True):
        '''
        Fetches a list of all server profiles managed by the HPOV instance
        :param - refresh: if True,or the list of server profiles has never been 
        fetched before from the HPOV, refreshes the list from 
        the HPOV instance. Otherwise, returns the cached server profiles list.
        '''
        if hasattr(self, 'server_profiles') and not refresh:
            return self.server_profiles
        self.server_profiles=[]
        relurl='/rest/server-profiles'
        r = self.__getwithretry__(relurl)
        self.last_response = r.content
        
        if (r.status_code == 200):
            d = r.json()
            self.server_profiles = d['members']
            return self.server_profiles
        else:
            r.raise_for_status()

    @ioTimeDeco.Analyze('hponeview.getServerProfilesbyUuids')
    def getServerProfilesbyUuids(self, serverlist_uuid, refresh = True, virtualUuid=True):
        '''
        Given a list of UUIDs, returns a list of server profile infos for the m/cs with the given UUIDs
        (both physical and virtual uuids are checked if virtualUuid == True)
        :param serverlist_uuid = [uuid1, uuid2,..., uuidn]
        :param - refresh: If True, the server profile info is refreshed from HP One View server.
        :param - virtualUuid: (both physical and virtual uuids are checked if virtualUuid == True)
        :return - a dictionary of the form {uuid1:{"server profile info 1"}, uuid2:{"server profile info 2"}}
        '''
        server_profiles_all = self.getAllServerProfiles(refresh = refresh)
        serversby_uuid = self.getServersbyUuids(serverlist_uuid = serverlist_uuid, 
                refresh = refresh , virtualUuid = virtualUuid )
        server_profiles_dict = {}
        for uuid, server in serversby_uuid.items():
            server_profiles_dict[uuid] = None
            if not server:
                continue
            profile_uri = server['serverProfileUri']
            for y in server_profiles_all:
                if profile_uri == y['uri']:
                    server_profiles_dict[uuid] = y 
                    break
        return server_profiles_dict 

    @ioTimeDeco.Analyze('hponeview.updateServerProfile')
    def updateServerProfile(self, profile):
        '''
        Connect to HPOV appliance and update an existing profile.
        @param profile: The server profile to update to.
        '''
        relurl = profile['uri']
        body = profile
        r = self.__putwithretry__(relurl, body)
        self.last_response = r.content
        if (r.status_code == 202 or r.status_code == 200):
            d = r.json()
            return d
        else:
            r.raise_for_status()

    @ioTimeDeco.Analyze('hponeview.getAllServers')
    def getAllServers(self, refresh=True):
        '''
        Fetches a list of all servers managed by the HPOV instance
        :param - refresh: if True,or the list of servers has never been 
        fetched before from the HPOV, refreshes the list from 
        the HPOV instance. Otherwise, returns the cached server list.
        '''
        if hasattr(self, 'servers') and not refresh:
            return self.servers
        self.servers=[]
        relurl='/rest/server-hardware'
        r = self.__getwithretry__(relurl)
        self.last_response = r.content
        
        if (r.status_code == 200):
            d = r.json()
            self.servers = d['members']
            
            thread.start_new_thread(_cache_server, ( self.baseurl, copy.deepcopy(self.servers)))
            return self.servers
        else:
            r.raise_for_status()

    @ioTimeDeco.Analyze('hponeview.getServersbyUuids')
    def getServersbyUuids(self, serverlist_uuid, refresh = True, virtualUuid=True):
        '''
        Given a list of UUIDs, returns a list of server infos for the m/cs with the given UUIDs
        (both physical and virtual uuids are checked if virtualUuid == True)
        :param serverlist_uuid = [uuid1, uuid2,..., uuidn]
        :param - refresh: If True, the server info is refreshed from HP One View server.
        :param - virtualUuid: (both physical and virtual uuids are checked if virtualUuid == True)
        :return - a dictionary of the form {uuid1:{"server info 1"}, uuid2:{"server info 2"}}
        '''
        servers_all = self.getAllServers(refresh = refresh)
        servers_dict = {}
        for x in serverlist_uuid:
            servers_dict[x] = None
            _x_ = str(x).upper()
            for y in servers_all:
                if (_x_ == str(y['uuid']).upper()) or (virtualUuid and _x_ == str(y['virtualUuid']).upper()):
                    servers_dict[x] = y 
                    break
        return servers_dict 

    @ioTimeDeco.Analyze('hponeview.getServerByUri')
    def getServerByUri(self, server_uri, refresh = True):
        '''
        Given the URI of a server, returns the server hardware info for that 
        server.
        @param server_uri: The URI of the server hardware
        @param refresh: If True, refresh the server hardware info from the 
        HPOV appliance. If False, no need to refresh hardware info from 
        appliance, use cached info. If the hardware info is not cached, then 
        the method behaves as if refresh is True. If the behavior is as 
        if refresh == True, updates the global cache
        '''
        assert(re.match('/rest/server-hardware/([\w-]+$)', server_uri))
        if not hasattr(self, 'servers'):
            self.servers=[]
        if not refresh:
            for x in self.servers:
                if x['uri'] == server_uri:
                    return x 

        r = self.__getwithretry__(server_uri)
        if (r.status_code == 200):
            d = r.json()
            for x in range(len(self.servers)):
                if self.servers[x]['uri'] == server_uri:
                    self.servers[x] = d
                    thread.start_new_thread(_cache_server, (self.baseurl, 
                            copy.deepcopy(self.servers)))
                    return d
            self.servers.append(d)
            return d
        else:
            r.raise_for_status()
        
    @ioTimeDeco.Analyze('hponeview.isServerManaged')
    def isServerManaged(self, serverlist_uuid, refresh = True, virtualUuid=True):
        '''
        Given a list of UUIDs, determines which of the UUIDs correspond to 
        those of servers managed by this HPOV instance (both physical and virtual uuids are checked)
        :param serverlist_uuid = [uuid1, uuid2,..., uuidn]
        :param - refresh: If True, the server info is refreshed from HP One View server.
        :param - virtualUuid: (both physical and virtual uuids are checked if virtualUuid == True)
        :return - a dictionary of the form {uuid1:True/False, uuid2:True/False...}
        '''
        servers_all = self.getAllServers(refresh = refresh)
        server_uuid = [x['uuid'].upper() for x in servers_all]
        server_vuuid = []
        for x in servers_all:
            if x['virtualUuid']:
                server_vuuid.append(x['virtualUuid'].upper())
        #log.debug("All server UUIDs = %s", server_uuid)
        ret = {}
        for x in serverlist_uuid:
            ret[x] = (x.upper() in server_uuid) or (virtualUuid and x.upper() in server_vuuid) 
        #log.debug("exists server UUIDs = %s", ret)
        #log.debug("Answering from cache = %s", [{x:isServerManagedCache(x)} for x in ret])
        return ret

    @ioTimeDeco.Analyze('hponeview.setServerPowerState')
    def setServerPowerState(self, uuid, state, control, refresh = True):
        '''
        Change the power state of the host with the given uuid (can be physical or virtual)
        :param uuid - physical or virtual uuid of the host
        :param state - the power state to be sent to the host
        :param control - the control state to be sent to the host
        :param - refresh: If True, the server info is refreshed from HP One View server.
        '''
        assert(state in ['On','Off'])
        assert(control in [ 'MomentaryPress','PressAndHold', 'ColdBoot', 
            'Reset'])
        server = self.getServersbyUuids([uuid], refresh = refresh)[uuid]
        server_uri = server['uri']

        relurl = server_uri+'/powerState'
        r = self.__putwithretry__(relurl = relurl, 
                body = {'powerControl':control, 'powerState':state})
        self.last_response = r.content
        if (r.status_code == 202):
            return  r.json()
        else:
            r.raise_for_status()

    @ioTimeDeco.Analyze('hponeview.getServerPowerState')
    def getServerPowerState(self, uuid, refresh = True):
        '''
        Fetch the current power state of the host with the given uuid 
        (can be physical or virtual)
        :param uuid - physical or virtual uuid of the host
        :param - refresh: If True, the server info is refreshed from HP One View server.
        '''
        server = self.getServersbyUuids([uuid], refresh = refresh)[uuid]
        server_uri = server['uri']

        relurl = server_uri+'/powerState'
        r = self.__getwithretry__(relurl = relurl) 
        self.last_response = r.content
        if (r.status_code == 200):
            return  r.json()
        else:
            r.raise_for_status()

    @ioTimeDeco.Analyze('hponeview.getAllEnclosures')
    def getAllEnclosures(self, refresh=True):
        '''
        Fetches a list of all enclosures managed by the HPOV instance
        :param - refresh: if True,or the list of enclosures has never been 
        fetched before from the HPOV, refreshes the list from 
        the HPOV instance. Otherwise, returns the cached enclosure list.
        '''
        if hasattr(self, 'enclosures') and not refresh:
            return self.enclosures
        self.enclosures=[]
        relurl='/rest/enclosures'
        r = self.__getwithretry__(relurl)
        self.last_response = r.content
        
        if (r.status_code == 200):
            d = r.json()
            self.enclosures = d['members']
            return self.enclosures
        else:
            r.raise_for_status()

    @ioTimeDeco.Analyze('hponeview.getAllLogicalInterconnects')
    def getAllLogicalInterconnects(self, refresh=True):
        '''
        Fetches a list of all logical interconnects managed by the HPOV instance
        :param - refresh: if True,or the list of logical interconnects has never been 
        fetched before from the HPOV, refreshes the list from 
        the HPOV instance. Otherwise, returns the cached logical interconnects list.
        '''
        if hasattr(self, 'logical_interconnects') and not refresh:
            return self.logical_interconnects
        self.logical_interconnects=[]
        relurl='/rest/logical-interconnects'
        r = self.__getwithretry__(relurl)
        self.last_response = r.content
        
        if (r.status_code == 200):
            d = r.json()
            self.logical_interconnects = d['members']
            return self.logical_interconnects
        else:
            r.raise_for_status()

    @ioTimeDeco.Analyze('hponeview.getAllInterconnects')
    def getAllInterconnects(self, refresh=True):
        '''
        Fetches a list of all interconnects managed by the HPOV instance
        :param - refresh: if True,or the list of interconnects has never been 
        fetched before from the HPOV, refreshes the list from 
        the HPOV instance. Otherwise, returns the cached interconnects list.
        '''
        if hasattr(self, 'interconnects') and not refresh:
            return self.interconnects
        self.interconnects=[]
        relurl='/rest/interconnects'
        r = self.__getwithretry__(relurl)
        self.last_response = r.content
        
        if (r.status_code == 200):
            d = r.json()
            self.interconnects = d['members']
            return self.interconnects
        else:
            r.raise_for_status()

    @ioTimeDeco.Analyze('hponeview.getAllNetworkSets')
    def getAllNetworkSets(self, refresh=True):
        '''
        Fetches a list of all network sets managed by the HPOV instance
        :param - refresh: if True,or the list of network sets has never been 
        fetched before from the HPOV, refreshes the list from 
        the HPOV instance. Otherwise, returns the cached network sets list.
        '''
        if hasattr(self, 'network_sets') and not refresh:
            return self.network_sets
        self.network_sets=[]
        relurl='/rest/network-sets'
        r = self.__getwithretry__(relurl)
        self.last_response = r.content
        
        if (r.status_code == 200):
            d = r.json()
            self.network_sets = d['members']
            return self.network_sets
        else:
            r.raise_for_status()

    @ioTimeDeco.Analyze('hponeview.getAllLogicalInterconnectGroups')
    def getAllLogicalInterconnectGroups(self, refresh=True):
        '''
        Fetches a list of all LogicalInterconnect sets managed by the HPOV 
        instance
        :param - refresh: if True,or the list of logical interconnect sets 
        has never been fetched before from the HPOV, refreshes the list from 
        the HPOV instance. Otherwise, returns the cached logical interconnect 
        list.
        '''
        if hasattr(self, 'logical_interconnect_groups') and not refresh:
            return self.logical_interconnect_groups
        self.logical_interconnect_groups=[]
        relurl='/rest/logical-interconnect-groups'
        r = self.__getwithretry__(relurl)
        self.last_response = r.content
        
        if (r.status_code == 200):
            d = r.json()
            self.logical_interconnect_groups = d['members']
            return self.logical_interconnect_groups
        else:
            r.raise_for_status()

    @ioTimeDeco.Analyze('hponeview.getAllLogicalDownlinks')
    def getAllLogicalDownlinks(self, refresh=True):
        '''
        Fetches a list of all Logical downlinks managed by the HPOV instance
        :param - refresh: if True,or the list of  downlinks has never been 
        fetched before from the HPOV, refreshes the list from 
        the HPOV instance. Otherwise, returns the cached network sets list.
        '''
        if hasattr(self, 'logical_downlinks') and not refresh:
            return self.logical_downlinks
        self.logical_downlinks=[]
        relurl='/rest/logical-downlinks'
        r = self.__getwithretry__(relurl)
        self.last_response = r.content
        
        if (r.status_code == 200):
            d = r.json()
            self.logical_downlinks = d['members']
            return self.logical_downlinks
        else:
            r.raise_for_status()

    @ioTimeDeco.Analyze('hponeview.getAllUplinkSets')
    def getAllUplinkSets(self, refresh=True):
        '''
        Fetches a list of all Logical downlinks managed by the HPOV instance
        :param - refresh: if True,or the list of  downlinks has never been 
        fetched before from the HPOV, refreshes the list from 
        the HPOV instance. Otherwise, returns the cached network sets list.
        '''
        if hasattr(self, 'uplink_sets') and not refresh:
            return self.uplink_sets
        self.uplink_sets=[]
        relurl='/rest/uplink-sets'
        r = self.__getwithretry__(relurl)
        self.last_response = r.content
        
        if (r.status_code == 200):
            d = r.json()
            self.uplink_sets = d['members']
            return self.uplink_sets
        else:
            r.raise_for_status()

    @ioTimeDeco.Analyze('hponeview.getAllActiveServerAlerts')
    def getAllActiveServerAlerts(self):
        '''
        Fetches a list of all server-related ACTIVE alerts for all the servers 
        managed by the HPOV instance.
        Unlike most other methods in this class, information is not cached 
        but must be refetched everytime this method is called
        '''
        relurl='/rest/alerts?filter="physicalResourceType=\'server-hardware\'&alertState=\'active\'"'
        r = self.__getwithretry__(relurl)
        self.last_response = r.content
        
        if (r.status_code == 200):
            d = r.json()
            return d['members']
        else:
            r.raise_for_status()

    @ioTimeDeco.Analyze('hponeview.getAllBaselines')
    def getAllBaselines(self,refresh=True):
        '''
        Returns all available firmware baseline information that is available 
        in the HPOV appliance.
        @param refresh: If True, refreshes the baseline information from the 
        HPOV appliance. Otherwise, returns the cached information if 
        available. If False and there is no cached information, behaves as 
        if parameter value is True.
        '''
        if hasattr(self, 'baseline') and not refresh:
            return self.baseline
        self.baseline=[]
        relurl='/rest/firmware-drivers'
        r = self.__getwithretry__(relurl)
        self.last_response = r.content
        
        if (r.status_code == 200):
            d = r.json()
            self.baseline = d['members']
            return self.baseline
        else:
            r.raise_for_status()
            
    @ioTimeDeco.Analyze('hponeview.getTaskStatusFromUri')
    def getTaskStatusFromUri(self, relurl):
        '''
        return the task status info
        @param relurl: The relative URL of the task, as a string
        @return A dictionary object with info about the task.
        '''
        r = self.__getwithretry__(relurl)
        self.last_response = r.content
        
        if (r.status_code == 200):
            d = r.json()
            return d
        else:
            r.raise_for_status()
 
    @ioTimeDeco.Analyze('hponeview.get_server_profile')
    def get_server_profile(self,uri,refresh=True):
        log.debug('hponeview.get_server_profile()')
        if (not refresh):
            return self.server_profile
        r = self.__getwithretry__(uri)
        if (r.status_code == 200):
            self.server_profile=r.json()
            self.server_hardware_uri=self.server_profile['serverHardwareUri']
            self.enclosure_uri=self.server_profile['enclosureUri']
            return self.server_profile
        else:
            r.raise_for_status()

    @ioTimeDeco.Analyze('hponeview.get_server_hardware')
    def get_server_hardware(self,uri,refresh=True):
        log.debug('HPOneView.get_server_hardware()')
        if (not refresh):
            return self.server_hardware
        r = self.__getwithretry__(uri)
        if (r.status_code == 200):
            self.server_hardware=r.json()
            #print self.server_hardware['name']
            return self.server_hardware
        else:
            uri='/rest/server-profiles/'+uri.split('/')[3].lower()
            p=self.get_server_profile(uri)
            self.server_hardware=self.get_server_hardware(p['serverHardwareUri'])
            return self.server_hardware

    @ioTimeDeco.Analyze('hponeview.get_enclosure')
    def get_enclosure(self,uri,refresh=True):
        log.debug('hponeview.get_enclosure()')
        if (not refresh):
            return self.enclosure
        r = self.__getwithretry__(uri)
        if (r.status_code == 200):
            self.enclosure=r.json()
            return self.enclosure
        else:
            r.raise_for_status()

    @ioTimeDeco.Analyze('hponeview.get_connections')
    def get_connections(self,refresh=True):
        log.debug('hponeview.get_connections()')
        if (not refresh):
            return self.connections
        relurl='/rest/connections'
        r = self.__getwithretry__(relurl)
        if (r.status_code == 200):
            members=r.json()['members']
            for conn in members:
                mac = conn['macaddress']
                self.connections[mac] = conn
                #self.interconnect_uris.add(conn['interconnectUri'])
                #self.network_uris[conn['connectionInstanceId']]=conn['networkResourceUri']
            return self.connections
        else:
            r.raise_for_status()

    @ioTimeDeco.Analyze('hponeview.get_interconnect_stats')
    def get_interconnect_stats(self,id,refresh=True):
        log.debug('hponeview.get_interconnect_stats()')
        
        if (not refresh):
            return self.interconnect_stats
        relurl=id+'/statistics'
        r = self.__getwithretry__(relurl)
        log.debug("STATS BACK??? %s",r.status_code)
        if (r.status_code == 200):
            self.interconnect_stats = r.json()
            return self.interconnect_stats
        else:
            r.raise_for_status()  

    @ioTimeDeco.Analyze('hponeview.get_uplink')
    def get_uplink(self,id,refresh=True):
        log.debug('hponeview.get_uplink()')
        if (not refresh):
            return self.uplink
        
        relurl='/rest/uplink-sets/'+id
        r = self.__getwithretry__(relurl)
        if (r.status_code == 200):
            self.uplink=r.json()
            return self.uplink
        else:
            r.raise_for_status()

    @ioTimeDeco.Analyze('hponeview.get_network_set')
    def get_network_set(self,uri,refresh=True):
        log.debug('hponeview.get_network_set()')
        if (not refresh): 
            return self.network_set
        r = self.__getwithretry__(uri)
        if (r.status_code == 200):
            self.network_set=r.json()
            return self.network_set
        else:
            r.raise_for_status()

    @ioTimeDeco.Analyze('hponeview.getovBaseline')
    def getovBaseline(self, baselineuri):
        relurl=baselineuri
        r = self.__getwithretry__(relurl)
        self.last_response = r.content
        if (r.status_code == 200):
            d = r.json()
            return d
        else:
            r.raise_for_status()
