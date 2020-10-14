#####################################################
#
# rest.py
#
# Copyright 2011 Hewlett-Packard Development Company, L.P.
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
# 
# Description:
#    REST client
#
#####################################################

import httplib2

class Rest:
    def __init__(self, username=None, password=None) :    
        self.http = httplib2.Http()
        if username or password :
            self.http.add_credentials(username, password)    

    def request(self, url, method, data=None, headers=None) :            
        return self.http.request(uri=url, method=method, body=data, headers=headers)        

    def get(self, url) :
        return self.request(url, 'GET')

    def post(self, url, data=None, headers=None) :
        return self.request(url=url, method='POST', data=data, headers=headers)

    def put(self, url, data=None, headers=None) :
        return self.request(url=url, method='PUT', data=data, headers=headers)

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

        