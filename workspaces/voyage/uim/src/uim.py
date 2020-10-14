# $Author: partho.bhowmick@hp.com $
# $Date: 2013-09-10 08:23:29 -0500 (Tue, 10 Sep 2013) $
# $HeadURL: https://svn02.atlanta.hp.com/rg0201/hpic4vc-uim/branches/IC4VC.SERVER.7.3.0/uim/src/uim.py $
# $Id: uim.py 6702 2013-09-10 13:23:29Z partho.bhowmick@hp.com $
# $Rev: 6702 $
# the root directory needs to be set when running as a service.
# Next three lines will take care of that.
from utils.environment import Environment
env = Environment()
env.set_root_dir()

# This Python file uses the following encoding: utf-8
import logging
import logging.config
logging.config.fileConfig('share\\logging.conf')   # Must be before imports that log

import web
import json
import sys
import os
import socket
from urlparse import urlparse
from utils.rest import Rest
import vmware
from vmware import plugin
from vmware.hpsphere import VCClient
from psphere.managedobjects import Alarm, VirtualMachine
from utils.mergedictlist import merge_dict_list
from utils.dbutils import get_db_password, get_db_username, get_db_host, get_db_port, create_db_tables
from icdb.newsfeed import Newsfeed
from icdb.tasks import Tasks
from utils import strings, auth
from utils import sso
from threading import Thread
import time
from utils.stringcrypt import str_decrypt, str_encrypt
from utils import cert
from uimwebapp import UIMWEBApp
import requests
from urllib import unquote
import config
from M2Crypto import X509
import xml.dom.minidom
from utils import soap

log = logging.getLogger(__name__)

version = '7.0'
ngc_plugin_name = ''

file = xml.dom.minidom.parse("vsphere-web-client/plugin-package.xml")
root = file.getElementsByTagName('pluginPackage')
ngc_plugin_name = root[0].getAttribute('id')
version = root[0].getAttribute('version')

socket.setdefaulttimeout(30)

urls = [
            "/", "hello",
            "/newsfeed/", "NewsFeedAPI",
            "/tasks/", "TasksAPI",
            "/services/([^/]+)/([^/]+)(.*)", "Service",
            "/orservices/(.+)/(.+)", "OrService",
            "/agservices/(.+)", "AggregateService",
            "/config/(.+)", "Config",
            "/hostswfwexport", "HostSoftwareFirmwareExport",
            "/clusterswfwexport", "ClusterSoftwareFirmwareExport",
            "/strings/", "Strings",
            "/cstatus/(.+)", "ConsolidatedStatus",
            "/userinfo/", "UserInfo",
            "/sso/", "SingleSignOn",
            "/credentials", "Credentials",
            "/settings/(.+)", "Settings",
            "/certificate/(.+)", "Certificate",
            "/postinstallconfig", "PostInstallConfig",
        ]

class hello:
    def GET(self):
        return 'Insight Control for VCenter UI Manager'
        
class Service :
    def GET(self, provider, service, extendedPath) :
        "Query the service of a given provider and return the data"
        log.debug('Service %s %s %s (GET)', provider, service, extendedPath)        
        
        if provider not in config.config['public_config']['providers'] :
            return web.notfound("Provider not found.")
        if service not in config.config['public_config']['providers'][provider] :
            return web.notfound("Service not found.")
        
        rest = Rest()        
        url = config.config['public_config']['providers'][provider][service]['url'] + extendedPath + web.ctx.get('query')    
        try:
            log.debug('Service URL = %s', url)
            response, data = rest.get(url)                        
        except:
            log.exception('Error Calling provider=%s, service=%s', provider, service)
            raise
        
        return data

    def upload_file(self):
        winput = web.input(_unicode=False)
        log.debug("smart_components POST %s", winput['filename'])
        # This is key - make sure we don't treat binary files as unicode. Also make sure to
        # use the binary file mode (wb) when writing the file.
        filename = ''
        try:
            filename = winput['filename']
            idx = filename.rfind('\\')+1
            filename = filename[idx:]

            if not os.path.exists('static/sc_share'):
                os.makedirs('static/sc_share')
            f = open('static/sc_share/'+filename, 'wb')
            f.write(winput['sc_file'])
            f.close()
        except:
            log.debug('error uploading file')
        return filename
        
    def POST(self, provider, service, extendedPath):
        log.debug('Service %s %s %s (POST)', provider, service, extendedPath)
        
        if provider not in config.config['public_config']['providers'] :
            return web.notfound("Provider not found.")
        if service not in config.config['public_config']['providers'][provider] :
            return web.notfound("Service not found.")
        postdata = ''
        headers = None
        if provider == "host" and service == "smart_components":
            postdata = json.dumps({'filename': self.upload_file()})
        else:
            postdata = web.data()
            # If there is a content-type header then pass that through
            content_type = web.ctx.env.get('CONTENT_TYPE', None)
            if content_type :
                headers = {'content-type':content_type}
                
        rest = Rest()
        url = config.config['public_config']['providers'][provider][service]['url'] + extendedPath + web.ctx.get('query')        
                
        response, data = rest.post(url=url, data=postdata, headers=headers )

        return data

    def PUT(self, provider, service, extendedPath):
        log.debug('Service %s %s (PUT)', provider, service)
        
        headers = None
        if provider not in config.config['public_config']['providers'] :
            return web.notfound("Provider not found.")
        if service not in config.config['public_config']['providers'][provider] :
            return web.notfound("Service not found.")

        content_type = web.ctx.env.get('CONTENT_TYPE', None)
        if content_type :
            headers = {'content-type':content_type}
        
        rest = Rest()
        url = config.config['public_config']['providers'][provider][service]['url'] + extendedPath + web.ctx.get('query')
        response, data = rest.put(url=url, data=web.data(), headers=headers)

        return data

    def DELETE(self, provider, service, extendedPath):
        log.debug('Service %s %s %s (DELETE)', provider, service, extendedPath)
        rest = Rest()
        url = config.config['public_config']['providers'][provider][service]['url'] + extendedPath + web.ctx.get('query')
        #log.info(url)
        response, data = rest.delete(url, web.data())
        if response['status'] == '404' :
            raise web.notfound()
        return data

class AggregateService :
    def GET(self, service) :
        "Query the service of each provider and merge the data"
        aggregate_data = {}
        rest = Rest()
        log.debug('AggregateService %s', service)
        for provider in config.config['public_config']['providers'] :
            try :
                if service in config.config['public_config']['providers'][provider] :
                    url = config.config['public_config']['providers'][provider][service]['url']+web.ctx.get('query')            
                    #print "***********************" + url                    
                    response, data = rest.get(url)
                    try:
                        json_data = json.loads(data)
                    except:
                        log.debug('AggregateService %s %s: Unable to parse "%s" from %s', provider, service, data, url)
                        raise
                    #print "-----------------------" + data
                    aggregate_data = merge_dict_list(aggregate_data, json_data)
            except :
                log.exception('AggregateService failed for %s %s', provider, service)
        
        return json.dumps(aggregate_data)
        
class HostSoftwareFirmwareExport :
    def GET(self) :
        
        q = web.input()
        ag_data = {}
        rest = Rest()
        log.debug('HostSoftwareFirmwareExport')
        for provider in config.config['public_config']['providers'] :
            try :
                if 'hostswfwdetail' in config.config['public_config']['providers'][provider] :
                    url = config.config['public_config']['providers'][provider]['hostswfwdetail']['url']+web.ctx.get('query')            
                    response, data = rest.get(url)
                    ag_data = merge_dict_list(ag_data, json.loads(data))
            except :
                log.exception('HostSoftwareFirmwareExport failed for %s', provider)
        
        csv = ''
        hostname = ''
        for type in ('software', 'firmware') :
            for l in ag_data.get(type,[]) :
                hostname = l['host']
                csv += l['host'] + ',' + type + ',' + l['name'].replace(',', ';') + ',' + l['description'].replace(',', ';') + ',' + l['version'].replace(',', ';') + '\r\n'
        
        web.header('Content-Type', 'text/csv')
        web.header('Content-Disposition', 'attachment; filename="firmware_report_'+hostname+'.csv"');
        return csv        

class ClusterSoftwareFirmwareExport :
    def GET(self) :
        q = web.input()
        ag_data = {}
        rest = Rest()
        log.debug('ClusterSoftwareFirmwareExport')
        for provider in config.config['public_config']['providers'] :
            try :
                if 'clusterswfwdetail' in config.config['public_config']['providers'][provider] :
                    url = config.config['public_config']['providers'][provider]['clusterswfwdetail']['url']+web.ctx.get('query')            
                    response, data = rest.get(url)
                    ag_data = merge_dict_list(ag_data, json.loads(data))
            except :
                log.exception('ClusterSoftwareFirmwareExport failed for %s', provider)
        
        csv = ''
        for l in ag_data.get('rows', []) :
            csv += l['host'] + ',' + l['type'] + ',' + l['name'].replace(',', ';') + ',' + l['description'].replace(',', ';') + ',' + l['version'].replace(',', ';') + '\r\n'
        
        web.header('Content-Type', 'text/csv')
        web.header('Content-Disposition', 'attachment; filename="firmware_report_'+q.moref+'.csv"');
        return csv        
        
class OrService :
    def GET(self, providers, service) :
        """Find the first provider with the service and query it.
           The url should be http://host:port/orservices/provider1/provider2[.../providerN]/service           
        """
        data = None
        rest = Rest()
        
        
        for provider in providers.split('/') :
            try :
                if provider in config.config['public_config']['providers'] :
                    if service in config.config['public_config']['providers'][provider] :
                        log.debug('OrService %s %s', provider, service)
                        url = config.config['public_config']['providers'][provider][service]['url']+web.ctx.get('query')            
                        response, data = rest.get(url)                
                        return data
            except :
                log.exception('OrService failed for %s %s', provider, service)
                
        
        log.warning('OrService %s %s no service found', providers, service)
        return web.notfound("No services found.")                
        

class SingleSignOn :
    def GET(self) :
        log.debug('SingleSignOn')
        q = web.input()
        
        if not hasattr(q, 'ssoip') or q.ssoip=='':
            raise web.badrequest()
        
        raise web.seeother(sso.url(host = q.ssoip, 
                            cert_file_name = config.config['private_config']['ssl_cert_file'], 
                            key_file_name = config.config['private_config']['ssl_key_file']))
        
class NewsFeedAPI :    

    def GET(self):
        log.debug('NewsFeedAPI GET')
        
        if not config.config['private_config'].get('db', None) :
            log.error('No database settings found in config.json')
            raise web.notfound()
        
        nf = Newsfeed()
        nf.dbconnect(user=get_db_username(), password=get_db_password(), host=get_db_host(), port=get_db_port())

        try:
            removeNodoEntries = config.config['private_config'].get('rm_nodo_entries', 60)
            nf.remove(removeNodoEntries)
        except:
            log.exception('Error removing old newsfeed entries')            

        q = web.input()
        
        if not hasattr(q, 'moref') or q.moref=='' :
            db.dbclose()
            log.error("Newsfeed get without moref")
            raise web.badrequest()
        if not hasattr(q, 'serverGuid') or q.serverGuid=='' :            
            db.dbclose()
            log.error("Newsfeed posted without serverGuid")
            raise web.badrequest()

        top = 0
        try:
            top = int(q.top)
        except:
            pass
        
        log.debug('NewsFeedAPI calling nf.get_events - vc_uuid: %s, objectid: %s, limit: %s', q.serverGuid, q.moref, top )
        events = nf.get_events(vc_uuid=q.serverGuid, objectid=q.moref, limit=top)        
        nf.dbclose()
        return json.dumps({'result': events})

    def POST(self):
        log.debug('NewsFeedAPI POST')
        if not config.config['private_config'].get('db', None) :
            log.error('No database settings found in config.json')
            raise web.notfound()
        
        nf = Newsfeed()
        nf.dbconnect(user=get_db_username(), password=get_db_password(), host=get_db_host(), port=get_db_port())

        
        q = web.input()
        if not hasattr(q, 'moref') or q.moref=='' :
            nf.dbclose()
            log.error("Newsfeed posted without moref")
            raise web.badrequest()
        if not hasattr(q, 'serverGuid') or q.serverGuid=='' :
            nf.dbclose()
            log.error("Newsfeed posted without serverGuid")
            raise web.badrequest()
        entry = json.loads(web.data())        
        for event in entry['events'] :       
            log.info("Newsfeed %s %s", event.get('eventSource',''), event.get('message',''))
            nf.add_object_event(vc_uuid=q.serverGuid, 
                    objectid=q.moref, 
                    objectname = event.get('objectName',''), 
                    timestamp=event.get('eventDate', time.time()),
                    pluginsource=event.get('pluginSource',''), 
                    eventsource=event.get('eventSource', ''),
                    status=event.get('status',''), 
                    message=event.get('message',''), 
                    messagearguments=event.get('messageArguments',[]) )
        
        nf.dbclose()
        
        return json.dumps({"errorCode": 0, "errorMessage":"The newsfeed event(s) were added successfully."})

class TasksAPI:   

    def GET(self):
        log.debug('TasksAPI GET')
        
        if not config.config['private_config'].get('db', None) :
            log.error('No database settings found in config.json')
            raise web.notfound()
        
        t = Tasks()
        t.dbconnect(user=get_db_username(), password=get_db_password(), host=get_db_host(), port=get_db_port())

        try:
            removeNodoEntries = config.config['private_config'].get('rm_nodo_entries', 60)
            t.remove(removeNodoEntries)
        except:
            log.exception('Error removing old tasks entries')            

        q = web.input()
        
        if not hasattr(q, 'moref') or q.moref=='':
            raise web.badrequest()
        if not hasattr(q, 'serverGuid') or q.serverGuid=='':
            raise web.badrequest()

        top = 0
        try:
            top = int(q.top)
        except:
            pass

        tasks = t.get_tasks(q.serverGuid, q.moref, top) 
            
        t.dbclose()
        
        return json.dumps({'result': {q.moref: tasks}})
            
    def POST(self):
        log.debug('TasksAPI POST')
        if not config.config['private_config'].get('db', None) :
            log.error('No database settings found in config.json')
            raise web.notfound()
        
        t = Tasks()
        t.dbconnect(user=get_db_username(), password=get_db_password(), host=get_db_host(), port=get_db_port())

        q = web.input()
        
        if hasattr(q, 'removeNodoEntries'):
            t.dbclose()
            return json.dumps(t.remove(q.removeNodoEntries))
        
        if not hasattr(q, 'moref') or q.moref=='':
            raise web.badrequest()
        if not hasattr(q, 'serverGuid') or q.serverGuid=='':
            raise web.badrequest()
            
        rdata = {
                    "ids": [],
                    "error": {
                            "errorCode": 0,
                            "errorMessage": "The task was added successfully."
                    }
                }            
                    
        if hasattr(q, 'taskId') :
            task = json.loads(web.data())
            t.update_task(q.taskId, task['taskName'], task['status'], task['taskDetails'], task['taskNameArguments'], task['taskDetailArguments'], task['completedTime'])
            rdata['ids'].append(q.taskId)
        else:
            tasks = json.loads(web.data())
            for task in tasks['tasks'] :
                rdata['ids'].append(json.dumps(t.add_task(q.serverGuid, q.moref, task['taskName'], task['startTime'], task['status'], task['userName'], task['taskDetails'], task['taskNameArguments'], task['taskDetailArguments'], task['completedTime'] )))

        t.dbclose()
        
        return json.dumps(rdata)
        
        
class Config :
    
    def get_config(self, d, keys) :
        "Recursivly traverses a dictionary along a list of keys"
        if not keys :
            raise web.notfound()
            
        if len(keys) > 1 :
            if keys[0] not in d :                
                raise web.notfound()
            return self.get_config(d[keys[0]], keys[1:])
        
        if keys[0] not in d :
            raise web.notfound()
            
        return d[keys[0]]
        

    def GET(self, path) :
        return json.dumps(self.get_config(config.config['public_config'],path.split('/')))

class Strings:
    def GET(self):
        """This service returns a merged javascript file of all files in static/js/i18n/[locale].  
           The locale directory should contain the strings files for each module and the jqgrid
           locale file.  If there is no directory for a locale then en (English) is used."""
           
        q = web.input()
        if not hasattr(q, 'locale') or q.locale =='':
            #raise web.badrequest()
            locale = 'en'
        else:
            locale = q.locale

        # Make sure we are using 'en' instead of 'en_US'
        # TODO: We should probably do some directory checking here.  I.e. if 'en_US' exists, use it.  If not, use 'en'.
        if len(locale) != 2:
            locale = locale[0:2]
        
        # If there are no translation files for this locale then default to English
        if not os.path.exists('static/js/i18n/%s' % locale) :
            locale = 'en'
        
        if not strings.check_strings_current(locale):
            strings.merge_strings(locale)
        strings_path = 'static/js/i18n/%s/%s.js' % (locale, locale)
        f = open(strings_path, 'r')
        mystring = f.read()
        f.close()
        return mystring

class ConsolidatedStatus:
    def get_status_value(self, status):
        uppercasestatus = status.strip().upper()
        if uppercasestatus in ('ERROR', 'FAILED'):
            return 4
        elif uppercasestatus in ('WARNING', 'DEGRADED'):
            return 3
        elif uppercasestatus == 'INFORMATION':
            return 2
        elif uppercasestatus == 'OK':
            return 1
        else:
            return 0
        
    def get_count(self, aghealth, errorname):
        count = 0
        for stat in aghealth['result']:
            count += stat['healthStatus'] in errorname
        return str(count) 
        
    def GET(self, service_name):
        log.debug('In ConsolidatedStatus')
        cstatus = {}
        try:
            agsrv = AggregateService()
            jsonstring = agsrv.GET(service_name)
            aghealth = json.loads(jsonstring)
            cstatus['ERROR'] = self.get_count(aghealth, ('ERROR', 'FAILED'))
            cstatus['WARNING'] = self.get_count(aghealth, ('WARNING', 'DEGRADED')) 
            cstatus['INFORMATION'] = self.get_count(aghealth, 'INFORMATION')
            cstatus['OK'] = self.get_count(aghealth, 'OK')

            if int(cstatus['ERROR']) > 0:
                cstatus['consolidated_status'] = 'ERROR'
            elif int(cstatus['WARNING']) > 0:
                cstatus['consolidated_status'] = 'WARNING'
            elif int(cstatus['OK']) > 0:
                cstatus['consolidated_status'] = 'OK'
            elif int(cstatus['INFORMATION']) > 0:
                cstatus['consolidated_status'] = 'INFORMATION'
            else:
                cstatus['consolidated_status'] = 'UNKNOWN'
                
            log.debug('Consolidated Status = %s', cstatus['consolidated_status'])
        except:
            log.debug('Exception in ConsolidateStatus.GET()')
            cstatus['consolidated_status'] = ''
            
        return json.dumps(cstatus)

class UserInfo:
    def GET(self):
        userInfo = {
            'userName': 'Unknown',
            'fullName': '',
            'locale': '',
            'key': '',
            'role': -2  # Default to read only
        }
        web.http.expires(0)
        web.header('Content-Type', 'text/javascript')
        q = web.input()

        # We can't do anything without a serverGuid. Return userInfo as is.
        if not hasattr(q, 'serverGuid') or q.serverGuid=='':
            return json.dumps(userInfo)

        if hasattr(q, 'sessionId') and hasattr(q, 'moref'):
            role = auth.get_effective_role(q.sessionId, q.moref, q.serverGuid)
            userInfo['role'] = role

        si = None
        # 4.x vSphere systems don't pass the vimSessionKey. In this case, we should have effectiveRole but no user info.
        if hasattr(q, 'vimSessionKey'):
            si = auth.get_user_session(q.vimSessionKey, q.serverGuid)

        if si:
            userInfo['userName'] = si.userName
            userInfo['fullName'] = si.fullName
            userInfo['locale'] = si.locale
            userInfo['key'] = si.key
        log.debug("UserInfo - userName: %s, fullName: %s, locale: %s, role: %d", userInfo['userName'], userInfo['fullName'], userInfo['locale'], userInfo['role'])
        return json.dumps(userInfo)

class Settings:
    
    def build_pw_dict_for_ui(self, username, host, pwtype, uuid, password):
        pw = {}
        pw['username'] = username
        pw['host'] = host
        pw['type'] = pwtype
        pw['id'] = uuid
        pw['password'] = password
        return pw

    def build_pw_dict_from_ui(self, inputdata):
        pw = {}
        pw['username'] = inputdata['username']
        pw['ip'] = inputdata['host']
        if inputdata['password'] != '****************' :
            pw['password'] = str_encrypt(inputdata['password'])
        return pw
        
    def GET(self, service):
        auth.check_authorization()
        log.debug(service)
        if service == 'vcpassword':
            pwlist = []
            idlist = []
            make_id = 1
            for vcconfig in config.config['private_config']['vcenters']:
                #log.debug( vcconfig )
                # Make an id to make jqgrid happy
                vcconfig.setdefault('id', str(make_id))
                while vcconfig['id'] in idlist :
                    make_id+=1
                    vcconfig['id'] = str(make_id)
                idlist.append(vcconfig['id'])                
                pw = self.build_pw_dict_for_ui(vcconfig['username'], vcconfig['ip'], 'vCenter', vcconfig['id'], '****************')
                #log.debug(pw)
                pwlist.append(pw)
            return json.dumps({'pwdb': pwlist})
        
        elif service == 'password':
            rest = Rest()
            url = config.config['public_config']['providers']['host'][service]['url']+ web.ctx.get('query')   
            #log.debug(url)         
            response, data = rest.get(url)
            #log.debug(data)
            objs = json.loads(data)
            pwlist = []
            for item in objs:
                log.debug( item)
                if item['type'] in ['icsp','hponeview']:
                    continue
                pw = self.build_pw_dict_for_ui(item['username'], item['host'], item['type'], item['id'], '****************')
                pwlist.append(pw)            
            return json.dumps({'pwdb': pwlist})
        
        elif service == 'icsp':
            rest = Rest()
            url = config.config['public_config']['providers']['host']['password']['url']+ web.ctx.get('query')   
            #log.info(url)         
            response, data = rest.get(url)
            #log.info(data)
            objs = json.loads(data)
            pwlist = []
            for item in objs:
                log.info( item)
                if item['type'].lower() == 'icsp':
                    #log.info("ICSP info dict keys: %s", item.keys())
                    try:
                      pw = self.build_pw_dict_for_ui(item['username'], item['host'], item['type'], item['id'], str_decrypt(item['epassword']))
                      pwlist.append(pw)   
                    except Exception as e:
                      log.error(" ICSP build_pw_dict_for_ui() %s", e)    
                      raise e
            return json.dumps({'pwdb': pwlist})

        elif service in ('config', 'hostproperties', 'clusterproperties'):
            rest = Rest()
            url = config.config['public_config']['providers']['host'][service]['url']+ web.ctx.get('query')               
            #log.debug(url)         
            response, data = rest.get(url)            
            #log.debug(data)
            objs = json.loads(data)
            return json.dumps(objs)

    def parse_data(self):
        op = ''
        obj = {}
        data = web.data()                
        data = data.split('&')
        for item in data:
            kv =  item.split('=')
            if kv[0] == 'oper':
                op = unquote(kv[1])
            else:
                obj[kv[0]] = unquote(kv[1])
        return op, obj

    def existing_vcenter(self, inputdata, uimconfig):        

        for vc in uimconfig['private_config']['vcenters']:                   
            if vc['ip'] == inputdata['host'] and inputdata.has_key('type') and inputdata['type'] == 'vCenter':
                return vc
        return None
            
    def process_vcenter_credentials(self, oper, inputdata):
                
        uimconfig = json.load(open('config.json'))
        vc_found = self.existing_vcenter(inputdata, uimconfig) 
        cred = self.build_pw_dict_from_ui(inputdata)
        if oper == 'del':
            if vc_found:
                log.debug("VCenter Credential Settings - DEL %s", vc_found['ip'])
                vc = vmware.get_vc_instance(server = vc_found['ip'], username = vc_found['username'])
                if vc :
                    try :
                        vc.unregister_plugin('com.hp.ic4vc')
                    except :    # Ignore errors and keep going.  
                        log.exception("Error unregistering plugin from vCenter %s", vc_found['ip'])
                    try :
                        vc.unregister_plugin(ngc_plugin_name)
                    except :    # Ignore errors and keep going.  
                        log.exception("Error unregistering Web Client plugin from vCenter %s", vc_found['ip'])
                uimconfig['private_config']['vcenters'].remove(vc_found)
                vmware.remove_vc_instance(server = vc_found['ip'], username = vc_found['username'])
                json.dump(uimconfig, open('config.json', 'w+'), indent=4)        
                UserInterfaceManager.loadConfig()
            else:
                log.error("VCenter Credential Settings: DEL credential not found for vCenter %s", cred['ip'])
                raise web.notfound()
        elif oper == 'edit':
            if vc_found:                                
                log.debug("VCenter Credential Settings - EDIT %s", vc_found['ip'])                
                if 'password' not in cred : # user the old password if the user didn't edit it.
                    cred['password'] = vc_found['password']
                uimconfig['private_config']['vcenters'].remove(vc_found)
                vmware.remove_vc_instance(server = vc_found['ip'], username = vc_found['username'])
                uimconfig['private_config']['vcenters'].append(cred)                                
                json.dump(uimconfig, open('config.json', 'w+'), indent=4)        
                UserInterfaceManager.loadConfig()
                try :
                    UserInterfaceManager.registerWithVCenter(cred)
                except :    # Log the error and add the credential anyway
                    log.exception("VCenter Credential Settings - Error registering with vCenter %s", cred['ip'])
            else:
                log.error("VCenter Credential Settings: EDIT credential not found for vCenter %s", cred['ip'])
                raise web.notfound()
        elif oper =='add':            
            log.debug("VCenter Credential Settings ADD %s", cred['ip'])
            if not vc_found :                                
                uimconfig['private_config']['vcenters'].append(cred)
                json.dump(uimconfig, open('config.json', 'w+'), indent=4)        
                UserInterfaceManager.loadConfig()
                try :
                    UserInterfaceManager.registerWithVCenter(cred)
                except :    # Log the error and add the credential anyway
                    log.exception("VCenter Credential Settings - Error registering with vCenter %s", cred['ip'])
            else :
                log.error("VCenter Credential Settings - vCenter credentials already exists for %s", cred['ip'])
                raise web.HTTPError('409 Conflict', {'Content-Type': 'text/html'}, 'vCenter Already Exists') 
        else:
            log.error("VCenter Credential Settings - Unknown oper %s", oper)
            raise web.HTTPError('400 Bad Request', {'Content-Type': 'text/html'},'Bad Request') 
        
    
    def POST(self, service):
        auth.check_authorization()
        webdata = web.data()
        
        if service == 'vcpassword':
            oper, inputdata = self.parse_data()            
                        
            if 'host' in config.config['public_config']['providers'] and service in config.config['public_config']['providers']['host'] :
                try:
                    url = config.config['public_config']['providers']['host'][service]['url']+ web.ctx.get('query')
                    log.debug("Sending vcpassword request to server module")   
                    rest = Rest()
                    response, retdata = rest.post(url, webdata)
                    log.debug("Server module vcpassword response %s", response)
                
                except:
                    log.exception('Error sending credentials to server module')
                    raise web.HTTPError('500 Bad Request', {'Content-Type': 'text/html'},'Error sending to Common Services') 
                
            self.process_vcenter_credentials(oper, inputdata)
            
            return            
                
        elif service in ('password', 'config', 'hostproperties', 'clusterproperties'):
            log.debug('sending password request to server module')            
            url = config.config['public_config']['providers']['host'][service]['url']+ web.ctx.get('query')            
            rest = Rest()
            response, retdata = rest.post(url, webdata)            

            raise web.HTTPError( response['status'], response, retdata )
        
class Certificate:
    def __init__(self):
        self.crt_file = self.getfilepath( config.config['private_config'].get('ssl_cert_file', 'server.pem') )           
        self.key_file = self.getfilepath( config.config['private_config'].get('ssl_key_file', 'server.key') )           
        self.ssc_file_prefix = self.getfilepath('server')
        self.req_file_prefix = self.getfilepath('request')
        
    def getfilepath(self, name):
        path = os.getcwd() + '/' + name
        path = path.replace('\\', '/')
        log.debug(path)
        return path
    
    def json(self, selfsigned):
        
        crt, csr = None, None           
        
        mycertinfo = {}
        mycert = None
        
        try:
            if selfsigned:
                mycert = X509.load_cert(self.ssc_file_prefix + '.pem')
                crt = open(self.ssc_file_prefix + '.pem').read()
            else:
                mycert = X509.load_request(self.req_file_prefix + '.pem')
                csr = open(self.req_file_prefix + '.pem').read()
                
            mycertinfo['C'] = mycert.get_subject().C
            mycertinfo['ST'] = mycert.get_subject().ST
            mycertinfo['L'] = mycert.get_subject().L
            mycertinfo['O'] = mycert.get_subject().O
            mycertinfo['CN'] = mycert.get_subject().CN
            mycertinfo['OU'] = mycert.get_subject().OU
            mycertinfo['Email'] = mycert.get_subject().Email
            mycertinfo['SN'] = mycert.get_subject().SN
            mycertinfo['GN'] = mycert.get_subject().GN
        except:
            log.debug('Error in parsing cert')
            log.debug(mycertinfo)
            log.debug(mycert)
                    
        obj = dict(
            certinfo = mycertinfo,
            cert = crt,
            csr = csr,
            selfsigned = True if crt != None else False
        )
        
        log.debug('In Certificate.json...')
        log.debug(obj)
        return obj

    def GET(self, service=None):
        val = None
        if service == 'json':
            webinput = web.input()
            web.header('Content-Type', 'text/javascript')
            web.http.expires(0)
            if webinput and webinput.get('type', '') == 'csr':
                val = json.dumps(self.json(False))
            else:
                val = json.dumps(self.json(True))
        return val

    def POST(self, service):
        if service != 'json':
            return None

        error = None
        data = {}
        cmd = json.loads(web.data())
        if cmd['method'] == 'generate':
            if cmd['selfsigned']:
                crt = cert.Certificate()
            else:
                crt = cert.Request()

            info = cmd['certinfo']
            for k,v in info.items():
                crt.update(k, v)

            if cmd['selfsigned']:                
                crt.save(self.ssc_file_prefix)
            else:
                log.debug(self.req_file_prefix)
                crt.save(self.req_file_prefix)
            data = self.json(cmd['selfsigned'])
                
        elif cmd['method'] == 'upload':
            temppath = self.getfilepath('/temp.pem')
            f = open(temppath, 'w')
            f.write(cmd['cert'])
            f.close()
            try:
                if cert.check(temppath, self.req_file_prefix + '.key'):
                    os.unlink(self.crt_file)
                    os.unlink(self.key_file)
                    os.rename(temppath, self.crt_file)
                    os.rename(self.req_file_prefix + '.key', self.key_file)
                    os.unlink(self.req_file_prefix + '.pem')
                    error = "Success"                    
                else:
                    error = "Certificate does not match the Certificate Request"
            except Exception, e:
                error = "The certificate is bad or is in the wrong format"
                log.exception("Error loading the certificate")
            data['error'] = error
                
        web.header('Content-Type', 'text/javascript')
        return json.dumps(data)        

class PostInstallConfig:
    def GET(self):
        auth.check_authorization()
        authvalue = str_encrypt('ic4vc_authenticated')
        web.setcookie('ic4vc-auth', authvalue, 300)
        raise web.seeother('/static/ic4vc-postconfig.html', '')
    
class Credentials:
    def GET(self ):
        url = config.config['public_config']['providers']['credentials']['credential']['url']+ web.ctx.get('query')
        webdata = web.data()
        hdr = {'Content-Type':'application/json',
                'Accept':'application/json'}
        r = requests.get(url, headers=hdr)
        for hdr in r.headers:
            web.header(hdr, r.headers[hdr])
        web.ctx.status = str(r.status_code)
        return r.content

    def POST(self):
        url = config.config['public_config']['providers']['credentials']['credential']['url']
        log.debug("Credentials POST URL = %s", url)
        log.debug("Credentials POST web data = %s", web.data())
        hdr = {'Content-Type':'application/json',
                'Accept':'application/json'}
        webdata = web.data()
        payload = json.loads(webdata)
        valids = ["username", "ip", "type", "action"]
        if (payload.has_key('action') and (payload['action'] == 'add')):
            valids.append("password")
        for k in list(set(payload.keys()) - set(valids)):
            del payload[k]
        webdata = json.dumps(payload)
        r = requests.post(url, data=webdata, headers=hdr)
        for hdr in r.headers:
            web.header(hdr, r.headers[hdr])
        web.ctx.status = str(r.status_code)
        result = r.content
        past_tense = lambda action:action+('ed' if (action.lower() == 'add') else 'd')
        if (r.status_code == 200):
            result = 'Successfully %s Credentials' % (past_tense(payload['action'].capitalize()))
        elif (r.status_code == 404):
            result = 'Cannot %s Credentials due to invalid host.' % (payload['action'].capitalize())
        return result

class HPOneView:
    def GET(self, action=None):
        try:
            url = config.config['public_config']['providers']['hponeview'][action]['url']+ web.ctx.get('query')
            log.debug("HP OneView GET %s", action)
            log.debug("HP OneView GET URL = %s", url)
            log.debug("HP OneView GET web data = %s", web.data())
            hdr = {'Content-Type':'application/json',
                'Accept':'application/json'}
            r = requests.get(url, headers=hdr)
            for hdr in r.headers:
                web.header(hdr, r.headers[hdr])
            web.ctx.status = str(r.status_code)
            return r.content
        except:
            raise

    def POST(self, action=None):
        #auth.check_authorization()
        log.debug("HP OneView POST %s", action)
        url = config.config['public_config']['providers']['hponeview'][action]['url']
        log.debug("HP OneView POST URL = %s", url)
        log.debug("HP OneView POST web data = %s", web.data())
        hdr = {'Content-Type':'application/json',
                'Accept':'application/json'}
        webdata = web.data()
        r = requests.post(url, data=webdata, headers=hdr)
        for hdr in r.headers:
            web.header(hdr, r.headers[hdr])
        if (not action):
            try:
                if r.status_code == 200:
                    auth_token = r.json()['auth']
                    #_cred =  credential.get_credential()
                    tiup = json.loads(webdata)
                    #_cred.add(ip = tiup['ip'], credtype = 'hponeview', 
                    #    credusername = tiup['username'], 
                    #    credpassword = tiup['password'])
                else:
                    web.ctx.status = str(r.status_code)
            #except credential.DuplicateCredError as e:
                #log.debug("Duplicate credential %s", e)
            except:
                raise
        return r.content

class UserInterfaceManager :            
    @staticmethod
    def registerWithVCenter(vc_config) :
        # Build url to vcenter wsdl file        
        vcenter_wsdl_file = 'file:///' + os.getcwd() + '/share/vmware/wsdl41/vimService.wsdl'            
        vcenter_wsdl_file = vcenter_wsdl_file.replace('\\', '/')    
        log.debug('Using wsdl file %s', vcenter_wsdl_file)
        
        # Get the server thumb print for NGC plugin registration
        server_thumbprint = ''
        try:
            if config.config['private_config']['web_roots']['uim_root'].lower().startswith('https://') :
                cert = X509.load_cert(config.config['private_config']['ssl_cert_file'])
                s = cert.get_fingerprint('sha1')
                log.debug('Raw Server Thumbprint: %s', s)
                if len(s) < 40 :
                    s = ('0' * (40 - len(s))) + s
                    log.debug('Padding Server Thumbprint to 160 bits: %s', s)
                server_thumbprint = ':'.join(a+b for a,b in zip(s[::2], s[1::2]))
                log.debug('Server Thumbprint: %s', server_thumbprint)
        except:
            log.exception("Error generating ssl server thumbprint")
        
        # Setup the vSphere client plugin configuration file    
        plugin_key = 'com.hp.ic4vc'
        ngc_plugin_key = ngc_plugin_name
        plugin_version = version
        plugin_zip_name = ngc_plugin_name + '.zip'
        plugin_config_url = '%s/static/plugin_config.xml' % config.config['private_config']['web_roots']['uim_root']
        ngc_plugin_package_url = '%s/static/vsphere-web-client/' % config.config['private_config']['web_roots']['uim_root'] + plugin_zip_name
        log.debug("NGC plugin package config url %s", ngc_plugin_package_url)
        plugin_description = 'Integrates HP ProLiant manageability into vCenter'
        plugin_name = 'HP Insight Management'        
        plugin_vendor = 'Hewlett-Packard Company'        
        
        # Convert web_roots and paths to urls
        UserInterfaceManager.buildExtensionURLs(config.config['private_config']['extensions'], config.config['private_config']['web_roots'])        
        
        # Create the .Net plugin object
        plugin_config = plugin.PluginConfig(version = plugin_version, key = plugin_key, 
                                            extensions = config.config['private_config']['extensions'],
                                            description = plugin_description, 
                                            name = plugin_name, 
                                            vendor = plugin_vendor, multiVCSupported=True, supportNonSecureCommunication=True)
        
        # Write the .Net plugin xml file that is read by vCenter
        f = open('static/plugin_config.xml', 'w')
        f.write(plugin_config.toxml())
        f.close()
        
        # Login to VCenter and Register Plugin        
        try :
            
            try :
                password = str_decrypt(vc_config['password'])
            except :
                log.exception("Error decrypting password for vCenter %s", vc_config['ip'])
                log.warning("Assuming password is not encrypted for vCenter %s", vc_config['ip'])
                password = vc_config['password']
            log.info('Connecting to vCenter: %s', vc_config['ip'])                
            vc = VCClient(server=vc_config['ip'], username=vc_config['username'], password=password, wsdl_location=vcenter_wsdl_file)                 
            
            log.info('Registering .Net plugin on vCenter: %s', vc_config['ip'])                
            vc.register_plugin(key = plugin_key, config_url = plugin_config_url, version = plugin_version, description=plugin_description, 
                                name = plugin_name, admin_email='no-email@hp.com', 
                                company=plugin_vendor, 
                                resources = config.config['private_config'].get('resources', [])
                               )    
            
            log.info('Registering NGC plugin on vCenter: %s', vc_config['ip'])                
            vc.register_ngc_plugin(key=ngc_plugin_key, package_url=ngc_plugin_package_url, version=plugin_version, 
                                        admin_email='no-email@hp.com', company=plugin_vendor, name=plugin_name, 
                                        description=plugin_description + ' Web Client',
                                        server_thumbprint=server_thumbprint)
            
            # Keep the vcenter instance for looking up cluster info and other things.
            vmware.vcenters.append(vc)
            vc.create_alarms()
        except :
            log.exception("Exception setting up vCenter %s", vc_config['ip'])
                
                
    @staticmethod
    def registerWithVCenters() :
        vmware.vcenters = []
        for vc_config in config.config['private_config']['vcenters'] :    
            UserInterfaceManager.registerWithVCenter(vc_config)

    @staticmethod
    def buildExtensionURLs(extensions, web_roots) :
        for ext in extensions :
            # Fill in format sting with hostname & port       
            if 'web_root' in ext['url'] :  # make sure this conversion wasn't already done
                ext['url'] = {'url':web_roots[ext['url']['web_root']] + ext['url']['path']}
            
            if 'extensions' in ext :    # Extensions can have nested extensions
                UserInterfaceManager.buildExtensionURLs(ext['extensions'], web_roots)
            
            if 'icon' in ext :
                if isinstance(ext['icon'], dict) :  # make sure this conversion wasn't already done
                    ext['icon'] = web_roots[ext['icon']['web_root']] + ext['icon']['path']
    
    @staticmethod
    def loadConfig() :
        
        # Load and merge config files    
        config.config = {}
        for config_file in ['config.json', 'server_config.json', 'storage_config.json', 'network_config.json'] :
            try : 
                config.config = merge_dict_list(config.config, json.load(open(config_file)))
            except IOError, e :
                if e.errno == 2 and config_file != 'config.json' :  #OK - plugin just not installed
                    log.debug("Config file '%s' not found", config_file)
                else :
                    log.error("Unable to load config file '%s': %s", config_file, os.strerror(e.errno))
                    sys.exit(1)
            except ValueError, e:
                log.exception("Unable to parse '%s': %s", config_file, e)
                sys.exit(1)
            except :    
                log.exception("Error processing config file '%s'", config_file)
                raise
                
        # Create the URLs for the provider services from web_root and path
        for provider in config.config['public_config']['providers'].values() :            
            for service in provider.values() :                
                service['url'] = config.config['private_config']['web_roots'][service['web_root']] + service['path']
        
        # Create the URLs for the action menus
        for page in config.config['public_config'].get('pages', {}).values() :
            for amenu in page.get('action_menu_items', [] ) :
                if 'path' and 'web_root' in amenu :
                    amenu['url'] = config.config['private_config']['web_roots'][amenu['web_root']] + amenu['path']
                    log.debug('Converting Action Menu Items to URL %s', amenu['url'])
                
            for setting in page.get('settings', [] ) :
                if 'path' and 'web_root' in setting :
                    setting['url'] = config.config['private_config']['web_roots'][setting['web_root']] + setting['path']
                    log.debug('Converting Settings Menu Items to URL %s', setting['url'])
                    
        # Create the URLs for the configuration pages
        for item in config.config['public_config'].get('configurationPages', []) :
                if 'url' and 'web_root' in item :
                     item['url'] = config.config['private_config']['web_roots'][item['web_root']] + item['url']
                     log.debug('Converting configurationPages Items to URL %s', item['url'])
    
    
    def start(self) :               
        log.info('Starting Insight Control for VMware vCenter UIM version %s', version)
        soap.setup()
        UserInterfaceManager.loadConfig()        
        
        # Login to VCenter and Register Plugin
        UserInterfaceManager.registerWithVCenters()

        # Make sure Postgres db is configured
        try :
            create_db_tables(user=get_db_username(), password=get_db_password(), database='ic4vc', host=get_db_host(), port=get_db_port())
        except :
            log.exception("Unable to created database tables")                   
        
        # We need to call the web.run() or the web.runsimple() functions in a separate thread.  
        # These function calls do not return. 
        log.info('UIMWEBapp thread Starting...')
        self.webappthread = Thread(target=self.start_webapp, name="UIM webapp thread")
        self.webappthread.daemon = True
        self.webappthread.start()
        log.info('UIMWEBapp Thread Started...')
       
    def start_webapp(self):
        # Start the web application        
        self.uimwebapp = UIMWEBApp(urls, globals())
        cert = {}
        log.debug( config.config['private_config']['web_roots']['uim_root'] )
        if config.config['private_config']['web_roots']['uim_root'].lower().startswith('https://') :
            log.debug('Setting certificate information')
            cert['cert'] = config.config['private_config']['ssl_cert_file']
            cert['key'] = config.config['private_config']['ssl_key_file']
        parsed_url = urlparse(config.config['private_config']['web_roots']['uim_root'])
        timeout = int(config.config.get('private_config', {}).get('webserver', {}).get('timeout', 60))
        rqs = int(config.config.get('private_config', {}).get('webserver', {}).get('req_queue', 10))
        nthreads = int(config.config.get('private_config', {}).get('webserver', {}).get('num_threads', 20))
        self.uimwebapp.run(cert, ('0.0.0.0', parsed_url.port), timeout=timeout,rqs =rqs, nthreads=nthreads)

    def stop(self):
        # Do clean up here
        try :
            log.debug('Shutting down')
            for vc in vmware.vcenters :
                # Make sure we have a vCenter session by logging out and back in.
                try :
                    log.debug('logging out of vCenter %s', vc.server)
                    vc.logout()
                except :
                    log.exception("Error logging out of vCenter %s", vc.server)
                try :
                    log.debug('logging in to vCenter %s', vc.server)
                    vc.login()
                except :
                    log.exception("Error logging in to vCenter %s", vc.server)
                try:                    
                    log.debug('Unregistering vCenter %s', vc.server)
                    vc.unregister_plugin(ngc_plugin_name)
                    vc.unregister_plugin('com.hp.ic4vc')
                    log.debug('Unregistered vCenter %s', vc.server)
                except :
                    log.exception("Error unregistering plugin from vCenter %s", vc.server)
                        
            log.info('Exiting Insight Control for VMware vCenter UIM version %s', version)
        except :            
            log.exception("Exception shutting down")
            pass
        
        
if __name__ == '__main__':
    uim = UserInterfaceManager()
    uim.start()        
    try:
        while(True):
            time.sleep(60)
    except KeyboardInterrupt :
        log.info("Key board interupt: exiting main loop")
    except:        
        log.exception("Exiting main loop")        
        
    uim.stop()    
    log.info('Exiting Insight Control for VMware vCenter UIM version %s', version)
    
    
   
