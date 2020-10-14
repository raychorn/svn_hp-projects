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
# Description:
#    REST client used for HP Common Services
#
#####################################################

import httplib2
import base64
#import urllib

class Rest:
    def __init__(self, username=None, password=None) :    
        self.http = httplib2.Http(disable_ssl_certificate_validation=True)
        self.auth = None 
        if username or password :
            self.auth = base64.encodestring(username + ':' + password)
            self.http.add_credentials(username, password)    

    def request(self, url, method, data=None) :            
        return self.http.request(url, method=method, body=data, headers = {'Authorization' : 'Basic ' + (self.auth if self.auth else '')})        

    def get(self, url) :
        return self.request(url, 'GET')

    def post(self, url, data=None) :
        return self.request(url, 'POST', data)

    def put(self, url, data=None) :
        return self.request(url, 'PUT', data)

    def delete(self, url, data=None) :
        return self.request(url, 'DELETE', data)

if __name__ == '__main__' :
    import json
    rest = Rest('test', 'test')

    response, data = rest.get('http://localhost:50026/password?lang=json')
    print 'get passwords:', response['status']
    for cred in json.loads(data)['password'] :
        print cred['id'], cred['username'], cred['type']
        
    credentials = {
         "username": "test123",
         "host": "1.2.3.4",
         "type": "Virtual Connect",
         "password": "test123",
         "id": "new"
        }
    response, data = rest.post('http://localhost:50026/password?lang=json', json.dumps(credentials))
    print 'create password:', response['status']

    credentials = json.loads(data)    
    credentials['password'] = 'test321'
    #credentials['username'] = 'test321'	// 500 server error
    #credentials['type'] = 'iLO'			// 500 server error

    response, data = rest.put('http://localhost:50026/password?lang=json', json.dumps(credentials))
    print 'update password:', response['status'], data
        
    response, data = rest.delete('http://localhost:50026/password?lang=json', json.dumps(credentials))
    print 'delete password:', response['status'], data    

        