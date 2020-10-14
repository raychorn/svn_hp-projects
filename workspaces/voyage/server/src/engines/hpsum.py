#!/usr/bin/python

'''
$Author: partho.bhowmick@hp.com $
$Date: 2013-05-23 12:38:42 -0500 (Thu, 23 May 2013) $
$Header: https://svn02.atlanta.hp.com/local/ic4vc-dev/server/trunk/src/engines/hpsum.py 5438 2013-05-23 17:38:42Z partho.bhowmick@hp.com $
$Revision: 5438 $
'''

import web
import json
import requests
from threading import Thread
from logging import getLogger

log = getLogger(__name__)

class HapiException(Exception):
    def __init__(self, errNo):
        self.errval = errNo

    def __str__(self):
        return repr(self.errval)

class HpSum:
    def __init__(self, baseurl):
        '''
        Creates an HpSum client object with the given base URL
        :param baseurl: The URL pointing to the HP SUM service this client will be interacting with.
        '''
        self.baseurl = baseurl

    def ppj(self, d):
        return json.dumps(d, sort_keys = True, indent = 4,
                                separators = (',', ': '))

    def login(self, username, password):
        '''
        The first call to make after object creation is login
        :param username - the username to login with
        :param
        '''
        relurl = '/session/create'
        hapi = {}
        hapi['username'] = username
        hapi['password'] = password
        hapi["language"] = "en"
        hapi["mode"] = "gui"
        hapi["settings"] = {}
        hapi["settings"]['output_filter'] = "passed"
        hapi["settings"]['port_number'] = "444"
        relurl = '/session/create'
        r = requests.post("https://%s%s" % (self.baseurl, relurl),
                verify = False,  data = json.dumps({'hapi':hapi}),
                headers = {'Content-Type':'text/json','Accept':'text/json'})
        self.last_response = r
        if (r.status_code == 200):
            d = r.json()
            if d['hapi']['hcode'] == 0:
                self.session = d['hapi']['sessionId']
                return True
            else:
                self.session = None
                return False
        else:
            r.raise_for_status()


    def getallbaselinelocations(self):
        '''
            Get the baselines for all known locations.
            Requires that this HpSum client object is logged into
            the HpSum service (see method login)
        '''
        relurl = '/Session/' + self.session +'/baseline/location/index'
        r = requests.get("https://%s%s" % (self.baseurl, relurl),
                verify = False,
                headers = {'Content-Type':'text/json','Accept':'text/json'})
        self.last_response = r
        if (r.status_code == 200):
            d = r.json()
            self.baseline_location = {}
            if d['hapi']['hcode'] == 0:
                baselines =  d['hapi']['output_data']['baselines']['baseline']
                for baseline in baselines:
                    location_id = baseline['locationid']
                    self.baseline_location[location_id] = baseline
                    del self.baseline_location[location_id]['locationid']
                    self.baseline_location[location_id]['bundles'] = []
                return self.baseline_location
            else:
                raise HapiException(d['hapi']['hcode'])
        else:
            r.raise_for_status()

    def getBaselineInfo(self, baseline_id, refresh = True):
        if not refresh and hasattr(self, 'baseline_location'):
            return self.baseline_location[baseline_id]
        relurl = '/Session/' + self.session +'/baseline/location/'+ baseline_id+'/index'
        r = requests.get("https://%s%s" % (self.baseurl, relurl),
                verify = False,
                headers = {'Content-Type':'text/json','Accept':'text/json'})
        self.last_response = r
        if (r.status_code == 200):
            d = r.json()
            baselines_arr =  d['hapi']['output_data']['baselines']['baseline']
            if not hasattr(self, 'baseline_at_location'):
                self.baseline_at_location = {}
            baseref = self.baseline_at_location[baseline_id] = {}
            for _baseline in baselines_arr:
                baseid = _baseline['baseline_id']
                baseref[baseid] = _baseline
                del baseref[baseid]['baseline_id']
            return self.baseline_at_location[baseline_id]
        else:
            r.raise_for_status()

    def getBaselineBundle(self, baseline_id, bundle_id, refresh = True):
        if (not refresh) and hasattr(self, 'bundles') and hasattr(self.bundles, baseline_id) and hasattr(self.bundles[baseline_id], bundle_id):
            return self.bundles[baseline_id][bundle_id]
        if not hasattr(self, 'bundles'):
            self.bundles={}

        relurl = '/session/' + self.session +'/baseline/location/'+ baseline_id+'/bundle/'+bundle_id+'/inventory'
        r = requests.get("https://%s%s" % (self.baseurl, relurl),
                verify = False,
                headers = {'Content-Type':'text/json','Accept':'text/json'})
        self.last_response = r
        if (r.status_code == 200):
            d = r.json()
            if d['hapi']['hcode'] == 0:
                relurl2 = '/session/' + self.session +'/baseline/location/'+ baseline_id+'/bundle/'+bundle_id+'/index'
                r = requests.get("https://%s%s" % (self.baseurl, relurl2),
                        verify = False,
                        headers = {'Content-Type':'text/json','Accept':'text/json'})
                self.last_response = r
                if (r.status_code == 200):
                    d2 = r.json()
                    ret = d2['hapi']
                    if not hasattr(self.bundles, baseline_id):
                        self.bundles[baseline_id] = {}
                    del ret['location_id']
                    del ret['baseline_id']
                    del ret['hcode']
                    del ret['hmessage']
                    self.bundles[baseline_id][bundle_id] = ret
                    return ret
                else:
                    r.raise_for_status()
            else:
                raise HapiException(d['hapi']['hcode'])
        else:
            r.raise_for_status()

    def getNodeStatus(self, nodename, refresh = True):
        '''
        Possibly deprecated because getNodeState might be getting 
        a superset information of getNodeStatus
        '''
        if (not refresh):
            return self.nodes[nodename]['last_known_status']

        relurl = '/session/' + self.session +'/node/' + self.nodes[nodename]['node_id'] + '/getstatus'
        r = requests.get("https://%s%s" % (self.baseurl, relurl),
                verify = False,
                headers = {'Content-Type':'text/json','Accept':'text/json'})
        self.last_response = r
        if (r.status_code == 200):
            d = r.json()
            #print 'getNodeStatus', self.ppj(d)
            if d['hapi']['hcode'] == 0:
                ret = d['hapi']['node_status']
                del ret['node_id']
                del ret['node_name']
                self.nodes[nodename]['last_known_status'] = ret
                return ret
            else:
                raise HapiException(d['hapi']['hcode'])
        else:
            r.raise_for_status()


    def getAllNodes(self, refresh = True):
        '''
        Get all node information from HP SUM service
        Puts them in a dictionary pointed to by self.nodes
        :param - refresh: If True, all node information is gotten afresh from 
        '''
        if (not refresh) and hasattr(self, 'nodes'):
            return self.nodes
        relurl = '/Session/' + self.session +'/node/index'
        self.nodes = {}
        r = requests.get("https://%s%s" % (self.baseurl, relurl),
                verify = False,
                headers = {'Content-Type':'text/json','Accept':'text/json'})
        self.last_response = r
        if (r.status_code == 200):
            d = r.json()
            if (d['hapi']['hcode'] == 0):
                ret = {}
                nodes = d['hapi']['output_data']['nodes']['node']
                for node in nodes:
                    node_name = node['node_name']
                    ret[node_name] = node
                    del ret[node_name]['node_name']
                    ret[node_name]['last_known_status'] = {}
                    ret[node_name]['state'] = {}
                self.nodes = ret
                return self.nodes
            else:
                raise HapiException(d['hapi']['hcode'])
        else:
            r.raise_for_status()

    def deleteNode(self, nodename):
        '''
        Assumes that the node list the client has is up to date
        User may want to ensure things are in sync by calling getAllNodes()
        Presupposes the existence of all node info in self.nodes dictionary
        Note: deleting a node does not mean the node is automatically
        removed to the set of cached node information.
        You have to call getAllNodes() with the refresh set to True
        to update cache
        '''
        if (not hasattr(self, 'nodes')) or not self.nodes or (not nodename in self.nodes):
            return False
        relurl = '/session/' + self.session +'/node/' + self.nodes[nodename]['node_id'] + '/remove'
        r = requests.post("https://%s%s" % (self.baseurl, relurl),
                verify = False, headers = {'Content-Type':'text/json','Accept':'text/json'})
        self.last_response = r
        if (r.status_code == 200):
            d = r.json()
            hcode = d['hapi']['hcode']
            return hcode == 0
        else:
            r.raise_for_status()

    def addNode(self, ip_dns, name, baseline, baseline_addition, node_type,
            node_skip, username, password):
        '''
        Add a node
        Note: adding a node does not mean the node is automatically
        added to the set of cached node information.
        You have to call getAllNodes() with the refresh set to True
        to update cache
        :para
        '''
        relurl = '/session/' + self.session +'/node/add'
        hapi = {}
        hapi['ip'] = ip_dns
        hapi['name'] = name
        hapi['type'] = node_type
        hapi['baselines'] = {'baseline':[baseline, baseline_addition]}
        hapi['node_skip'] = node_skip
        hapi['username'] = username
        hapi['password'] = password

        r = requests.post("https://%s%s" % (self.baseurl, relurl),
                verify = False,  data = json.dumps({'hapi':hapi}),
                headers = {'Content-Type':'text/json','Accept':'text/json'})
        self.last_response = r
        if (r.status_code == 200):
            d = r.json()
            return d['hapi']['hcode'] == 0
        else:
            r.raise_for_status()

    def inventoryNode(self, nodename, baseline, refresh = True):
        '''
        Returns node inventory state from HP SUM service
        If refresh is True, then the HP SUM service is queried afresh for node
        inventory information, else cached information is returned.
        Puts it in a dictionary pointed to by self.nodes under
        self.nodes[nodename]['inventory']
        '''
        if (not refresh) and hasattr(self.nodes[nodename], 'status'):
            return True
        self.nodes[nodename]['status'] = {}
        relurl = '/session/' + self.session +'/node/' + self.nodes[nodename]['node_id'] + '/inventory'
        hapi = self.nodes[nodename]['baselines']
        r = requests.post("https://%s%s" % (self.baseurl, relurl), 
                data = json.dumps({'hapi':hapi}), verify = False,
                headers = {'Content-Type':'text/json','Accept':'text/json'})
        self.last_response = r
        if (r.status_code == 200):
            d = r.json()
            return d['hapi']['hcode'] == 0
        else:
            r.raise_for_status()

    def getNodeState(self, nodename, refresh=True):
        if (not refresh) and hasattr(self.nodes[nodename], 'state') and self.nodes[nodename]['state']: 
            return self.nodes[nodename]['status'] 
        relurl = '/session/' + self.session +'/node/' + self.nodes[nodename]['node_id'] + '/getdata'
        r = requests.get("https://%s%s" % (self.baseurl, relurl),
                verify = False,
                headers = {'Content-Type':'text/json','Accept':'text/json'})
        self.last_response = r
        if (r.status_code == 200):
            d = r.json()
            #print 'getNodeStatus', self.ppj(d)
            if d['hapi']['hcode'] == 0:
                ret = d['hapi']['node_status']
                del ret['node_id']
                del ret['node_name']
                self.nodes[nodename]['state'] = ret
                return ret
            else:
                raise HapiException(d['hapi']['hcode'])
        else:
            r.raise_for_status()
        

