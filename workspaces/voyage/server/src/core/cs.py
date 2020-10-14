#####################################################
#
# rest.py
#
# Copyright 2010 Hewlett-Packard Development Company, L.P.
#
# Hewlett-Packard and the Hewlett-Packard logo are trademarks of
# Hewlett-Packard Development Company, L.P. in the U.S. and/or other countries.
#
# Confidential computer software. Valid license from Hewlett-Packard required
# for possession, use or copying. Consistent with FAR 12.211 and 12.212,
# Commercial Computer Software, Computer Software Documentation, and Technical
# Data for Commercial Items are licensed to the U.S. Government under
# vendor's standard commercial license.
#
# Author:
#    Andy Yates
#    Mohammed M. Islam
#
# Description:
#    HP Common Services API
#
#####################################################

from rest import Rest
import json
from util.ddict import ddict
from logging import getLogger

log = getLogger(__name__)

services = {
    'password': '/password/',
    'discovery': '/discovery/',
    'vcservice': '/vcservice/',
    'version': '/version',
    'plugins': '/plugins',
    'config': '/config',
    'event': '/event/',
    'entity': '/entity/',
    }

def convert_keys(data):
    """Convert Unicode dictionary keys to strings.  This is needed because argument
       unpacking does not work with unicode keys in python 2.6"""
    if isinstance(data, unicode):
        return data
    elif isinstance(data, dict):
        sk_dict = {}
        for k,v in data.items() :
            if isinstance(k, unicode) :
                sk_dict[str(k)] = convert_keys(v)
            else :
                sk_dict[k] = convert_keys(v)
        return sk_dict
    elif isinstance(data, (list, tuple, set, frozenset)):
        return type(data)(map(convert_keys, data))
    else:
        return data
    
    
class CommonServicesServerError(Exception):
    def __init__(self, status, response):
        self.status = status
        try:
            data = json.loads(response)            
            self.response = data['error']
        except :        
            self.response = response[:500]  #only return some of the reponse since it can be huge.
        
        #log.error('%s %s' % (self.status, self.response))

    def __str__(self):
        return '%s %s' % (self.status, self.response)    
    
class CommonServices :
    def __init__(self, username=None, password=None, host='localhost', protocol='http',port=50026, lang='json') :
        log.verbose( 'CommonServices(%s, ****, %s)', username, host )
        self.username = username
        self.password = password
        self.protocol = protocol
        self.host = host
        self.port = port
        self.lang = lang
        
        self.rest = Rest(self.username, self.password)

    def _make_uri(self, service, me_name=None, me_id=None) :
        uri = self.protocol + '://' + self.host
        if self.port :
            uri += ':' + str(self.port)
        uri += services[service]
        if me_name :
            uri += me_name
            if me_id :
                uri += '/' + me_id
        if self.lang :
            uri += '?lang=' + self.lang
        
        return uri

    def _check_server_response(self, response, data) :
        if response['status'] not in ['200', '201', '203', '204']:
            raise CommonServicesServerError(response['status'], data)    
            
    def get_version(self) :
        log.debug('CommonServices.get_version()')
        response, data = self.rest.get(self._make_uri('version'))        
        self._check_server_response(response, data)
        
        data = json.loads(data)        
        return data
        
    def restart(self) :
        log.debug('CommonServices.restart()')
        response, data = self.rest.get(self._make_uri('restart') + '?word=please')
        self._check_server_response(response, data)
        
        data = json.loads(data)        
        return data

    def get_passwords(self) :
        log.debug('CommonServices.get_passwords()')
        uri = self._make_uri('password')
        response, data = self.rest.get(uri)
        self._check_server_response(response, data)
        
        data = json.loads(data)
        data = convert_keys(data)
        return data['password']
        
    def get_password(self, username, type, host, **extra) :
        log.debug('CommonServices.get_password(%s, %s, %s)', username,type,host )
        passwords = self.get_passwords()
        for p in passwords :
            if p['username'] == username and p['type'] == type and p['host'] == host :
                return p
        return None
        
    def add_password(self, username, password, type, host='*', **extra) :
        log.debug('CommonServices.add_password(%s, ******, %s, %s)', username, type, host )
        data={'id':'new', 'username':username, 'password':password, 'host':host, 'type':type}
        response, data = self.rest.post(self._make_uri('password'), json.dumps(data))
        self._check_server_response(response, data)
        
        data = json.loads(data)
        data = convert_keys(data)
        return data

    def update_password(self, id, username, password, type, host='*', **extra):
        log.debug('CommonServices.update_password(%s, ******, %s, %s)' % (username, type, host) )
        if password:
            data={'id':id, 'username':username, 'password':password, 'host':host, 'type':type}
        else:
            data={'id':id, 'username':username, 'host':host, 'type':type}
        response, data = self.rest.put(self._make_uri('password'), json.dumps(data))
        self._check_server_response(response, data)
        
    def delete_password(self, id, username, type, host, **extra):
        log.debug('CommonServices.delete_password(%s, %s, %s)' % (username, type, host) )
        data={'id':id, 'username':username, 'host':host, 'type':type}
        response, data = self.rest.delete(self._make_uri('password'), json.dumps(data))
        self._check_server_response(response, data)

    def get_managed_element_names(self):
        log.debug('CommonServices.get_managed_element_names()' )
        response, data = self.rest.get(self._make_uri('discovery'))
        self._check_server_response(response, data)
        
        data = json.loads(data)
        return data['type']
        
    def discover(self, ip, param=None):
        log.debug('CommonServices.discover(%s), parameters (%s)', ip, param )
        response, data = self.rest.post(self._make_uri('discovery', ip), json.dumps(param) if param else None)
        self._check_server_response(response, data)
        data = json.loads(data)
        return data

    def discovery_status(self, ip):
        log.debug('CommonServices.discovery_status(%s)', ip )
        response, data = self.rest.get(self._make_uri('discovery', 'engine', ip))
        self._check_server_response(response, data)
        return data
    
    def get_entity(self, host):
        log.debug('CommonServices.get_entity(%s)', host )
        response, data = self.rest.get(self._make_uri('entity', host))
        self._check_server_response(response, data)
        return data
        
    def get_managed_elements(self, me_name) :
        log.debug('CommonServices.get_managed_elements(%s)', me_name)
        response, data = self.rest.get(self._make_uri('discovery', me_name))
        self._check_server_response(response, data)
        
        data = json.loads(data)
        return data['uuid']
        
    def get_managed_element(self, me_name, me_id) :
        log.debug('CommonServices.get_managed_element(%s, %s)', me_name, me_id )
        response, data = self.rest.get(self._make_uri('discovery', me_name, me_id))
        self._check_server_response(response, data)
        
        data = json.loads(data)
        return data

    def get_me(self, me_name, me_id) :        
        response, data = '',''
        log.debug('CommonServices.get_managed_element(%s, %s)', me_name, me_id )
        try:
            response, data = self.rest.get(self._make_uri('entity', me_name, me_id))
            #log.debug("in get_me function of cs.py \nvalue of response : %s\n value of data : %s\n",response,data)
            self._check_server_response(response, data)
        except:
            log.exception('response = %s, data = %s', response, data)
            raise
        data = json.loads(data)
        '''
        if (me_name == 'oa'):
            log.debug("OA data is %s", json.dumps(data, sort_keys = True, 
                indent = 4, separators = (',', ': ')))
        '''
        return data
    
    def delete_me(self, me_name, me_id) :        
        log.debug('CommonServices.delete_managed_element(%s, %s)', me_name, me_id )
        response, data = self.rest.delete(self._make_uri('entity', me_name, me_id))
        self._check_server_response(response, data)        
    
    def get_host_vc(self, uuid) :        
        log.debug('CommonServices.get_host_vc(%s)', uuid)
        response, data = self.rest.get(self._make_uri('vcservice', uuid))
        #log.debug("in CS.py the values of response and data are \nresponse : %s \n data :%s",response,data)
        self._check_server_response(response, data)
        
        data = json.loads(data, object_hook=ddict)   
        #json.dump(data, open('vcm.json', 'w+'), indent=4)    #debug     
        log.debug("data returned from get_host_vc is : %s",data)
        return data
            
    def get_plugins(self) :
        log.debug('CommonServices.get_plugins()')
        response, data = self.rest.get(self._make_uri('plugins'))
        self._check_server_response(response, data)        
        data = json.loads(data)
                
        return data['plugin']
        
    def update_plugins(self, plugins) :            
        log.debug('CommonServices.update_plugins(%s)', plugins )
        data={'plugins': {'plugin':plugins }}         
        self.set_config(data)

    def set_config(self, data) :
        log.debug('CommonServices.set_config(%s)', data )        
        response, data = self.rest.put(self._make_uri('config'), json.dumps(data))
        self._check_server_response(response, data)
        
    def get_config(self) :
        log.debug('CommonServices.get_config()')
        response, data = self.rest.get(self._make_uri('config'))
        self._check_server_response(response, data)
        
        data = json.loads(data)
        return data

    def get_discovery_engine(self):
        log.debug('CommonServices.get_discovery_engine()')
        response, data = self.rest.get(self._make_uri('discovery', 'engine'))
        self._check_server_response(response, data)
        
        data = json.loads(data)
        return data
        
    def get_event(self, me_id) :
        log.debug('CommonServices.get_event(%s)', me_id)
        response, data = self.rest.get(self._make_uri('event', me_id))        
        self._check_server_response(response, data)                     
        data = json.loads(data)
        return data
    
    def entity_control(self, me_name, me_id, device, cmd):
        uri = self._make_uri('entity', me_name, me_id + '/' + device + '/' + cmd) 
        log.debug('CommonServices.entity_control(%s)', uri)
        response, data = self.rest.put(uri)        
        self._check_server_response(response, data)
        return

    def updatenow(self, me_name, me_id):
        uri = self._make_uri('entity', me_name, me_id + '/__updatenow') 
        log.debug('CommonServices.updatenow(%s)', uri)
        response, data = self.rest.put(uri)        
        self._check_server_response(response, data)
        return

#***********************************************************************
# Test code
#***********************************************************************
if __name__ == '__main__' :
    import pprint
    pp = pprint.PrettyPrinter(indent=2, depth=3)
    
    cs = CommonServices(username='testcs', password='testcs')

    
    print 'Getting Version'
    print cs.get_version()
    
    print 'Getting Config'
    pp.pprint(cs.get_config())
    
    print 'Getting Plugins'
    plugins = cs.get_plugins()
    pp.pprint(plugins)    
    
    print 'Update Plugins'
    plugins = [{'_name':'hp.plugins.discovery'},
    {'_name':'hp.plugins.vcm'},
    {'_name':'hp.plugins.oa'},
    {'_name':'hp.plugins.ilo'}]
    cs.update_plugins(plugins)    
    
    print
    print 'Getting Passwords'
    creds = cs.get_passwords()
    for cred in creds :
        print cred['id'], cred['type'], cred['host'], cred['username']
    
    print
    print 'Adding Password'
    cred = {
         "username": "test123",
         "host": "1.2.3.4",
         "type": "Virtual Connect",
         "password": "test123",         
        }
        
    new_cred = cs.add_password(**cred)
    
    print
    print 'Getting Password'
    new_cred2 = cs.get_password(**cred)
    
    # Try to re-add, catch error, delete and add
    print
    print 'Testing Duplicate Add'
    try :
        new_cred = cs.add_password(**cred)
    except CommonServicesServerError as e:
        print e.status, e.response
        if '409' == e.status :
            print 'Deleting and readding', e.response
            cs.delete_password(id = e.response, **cred)
            new_cred = cs.add_password(**cred)
        else :
            raise
        
    print
    print 'Updating Password'
    new_cred['password'] = 'test321'
    #new_cred['username'] = 'test321'	# 500 server error
    #new_cred['type'] = 'iLO'			# 500 server error
    cs.update_password(**new_cred)
    
    print
    print 'Deleting Password'
    cs.delete_password(**new_cred)
    
    print
    print 'Getting ME names'
    try:
        me_names = cs.get_managed_element_names()
    except CommonServicesServerError as e:
        if '404' == e.status :
            print 'CS has not discovered anything'
        else :
            raise
          

    print
    for ip in ['16.83.121.37','16.83.121.7', '16.83.121.3'] :
        print 'Discovering', ip
        try :
            cs.discover(ip)
        except CommonServicesServerError as e :
            if '404' == e.status :
                print 'Unable to discover', ip
            else :
                raise     
    
    print
    print 'Get all discovery data'
    for me_name in me_names :
        print me_name
        me_ids = cs.get_managed_elements(me_name)
        for me_id in me_ids :
            print '  ', me_id
            data = cs.get_managed_element(me_name, me_id)
            print '      ', data['product']
            if me_name == 'ilo' :
                host_vc = cs.get_host_vc(data['uuid'])
                #print pp.pprint(host_vc)
            #if me_name == 'vcdomain' :
            #    print pp.pprint(data)
    
