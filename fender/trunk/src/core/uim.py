'''
Created on Oct 26, 2011

@author: IslamM
'''

from rest import Rest
import json
from datetime import datetime
import time
from logging import getLogger

log = getLogger(__name__)

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

services = {
    'newsfeed': '/newsfeed/',
    }

class UIMServerError(Exception):
    def __init__(self, status, response):
        self.status = status
        try:
            data = json.loads(response)            
            self.response = data['error']
        except :        
            self.response = response
        
        log.error('%s %s' % (self.status, self.response))

    def __str__(self):
        return '%s %s' % (self.status, self.response)    

class UIManager:
    def __init__(self, username=None, password=None, host='localhost', protocol='http',port=50026, lang='json'):        
        self.username = username
        self.password = password
        self.protocol = protocol
        self.host = host
        self.port = port
        self.lang = lang
        
        self.rest = Rest(self.username, self.password)
    
    def _make_uri(self, service, me_name=None, me_id=None):
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
        if response['status'] not in ['200', '201']:
            raise UIMServerError(response['status'], data)    
    
    def post_event(self, obj_path, message, source, severity, ev_ts):
        status = "INFORMATION"
        if severity == "error":
            status = "FAILED"
        elif severity == "warning":
            status = "WARNING"
        elif severity == "info":
            status = "OK"
        data = {}
        data['message'] = message
        data['pluginSource'] = 'server'
        data['eventSource'] = source 
        data['status'] = status
        data['eventDate'] = ev_ts
        uri = self._make_uri('newsfeed') + "&" + obj_path
        response, rsp_data = self.rest.post(uri, json.dumps({'events' : [data]}))
        self._check_server_response(response, rsp_data)
        rsp_data = json.loads(rsp_data)
        return rsp_data
