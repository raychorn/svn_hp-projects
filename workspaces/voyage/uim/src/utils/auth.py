#####################################################
#
# auth.py
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
#    James Abendroth
# 
# Description:
#    Some functions for authentication and authorization.
#
#####################################################
from suds.client import Client
import urllib2
import cookielib
import os
import json
from utils.stringcrypt import str_decrypt

from logging import getLogger
log = getLogger(__name__)

import web
import base64
import win32api
import win32security
import win32net

class HTTPErrorUnauthorized(web.HTTPError):
    '''
    A 401 Unauthorized response that will cause a web browser to prompt
    for Basic Authentication
    '''
    def __init__(self):
        web.HTTPError.__init__(
                self,
                '401 Unauthorized',
                {'WWW-Authenticate': 'Basic realm="HP Insight Control for vCenter"'},
                "Unauthorized"
        )

def basic_auth_win32():
    '''
    Check the credentials for Basic Authentication.  The credentials must
    be those of the user running the service.

    @rtype: boolean
    @return: True if the credentials are good.  Otherwise returns False.
    '''
    
    environ = web.ctx.environ
    auth = environ.get('HTTP_AUTHORIZATION')
    if not auth:
        return False
    if auth.startswith('Basic '):
        auth = auth[6:]
    else:
        return False
    auth = base64.decodestring(auth)
    username, passwd = auth.split(':')

    username = username.lower()
    tmp = username.replace('\\', '/')
    if '/' in tmp:
        domain, username = tmp.split('/')
    else:
        domain = None

    allowed = set(win32api.GetUserName().lower())
    resumehandle = None
    while resumehandle != 0:
        (info, total, resumehandle) = win32net.NetLocalGroupGetMembers(
                None, "Administrators", 1, resumehandle)
        for i in info:
            allowed.add(i['name'].lower())

    if username.lower() not in allowed:
        return False

    try:
        hUser = win32security.LogonUser(
                   username, domain, passwd,
                   win32security.LOGON32_LOGON_NETWORK,
                   win32security.LOGON32_PROVIDER_DEFAULT)
        win32api.CloseHandle(hUser);
    except win32security.error:
        return False

    return True

def cookie_auth():
    authcookie = web.cookies().get('ic4vc-auth', '')
    authvalue = str_decrypt(authcookie) if authcookie else ''
    if authvalue == 'ic4vc_authenticated':
        return True
    return False            

def check_authorization():
    valid = basic_auth_win32() or cookie_auth()
    if not valid:
        q = web.input()
        if q:
            sessionId = getattr(q, 'sessionId', None)
            moref = getattr(q, 'moref', None)
            serverGuid = getattr(q, 'serverGuid', None)
            if sessionId and moref and serverGuid:        
                valid = get_effective_role(sessionId, moref, serverGuid) == -1
        if not valid:
            raise HTTPErrorUnauthorized()
    return valid


def mkcookie(name, value, domain):
    if ':' in domain:
        (domain, port) = domain.split(':')
        port_specified = True
    else:
        port = None
        port_specified = False

    c = cookielib.Cookie(None, name, value,
            port, port_specified,
            domain, True, False,
            '/', True,
            False,
            None,
            False,
            None,
            None,
            None)
    return c

def get_user_session(vimSessionKey, vc_uuid):
    wsdl_path = 'file:///' + os.getcwd() + '/share/vmware/wsdl41/auth.wsdl'
    vcenter = ''
    username = ''
    password = ''
    sc = None
    client = Client(wsdl_path)

    mob = client.factory.create("ManagedObjectReference")
    mob.value = "ServiceInstance"
    mob._type = "ServiceInstance"

    hosts = get_vcenter_hosts()
    for host in hosts:
        try:
            client.set_options(location="https://%s/sdk" % (host['ip']))
            sc = client.service.RetrieveServiceContent(mob)
            # We need to make a few calls until we find the vCenter with the UUID that matches the one we're looking for.
            if sc.about.instanceUuid.lower() == vc_uuid.lower():
                vcenter = host['ip']
                username = host['username']
                password = host['password']
                break
        except:
            pass

    if vcenter == '':
        return None

    ospec = client.factory.create('ns0:ObjectSpec')
    ospec.obj = sc.sessionManager

    pspec = client.factory.create("ns0:PropertySpec")
    pspec.pathSet = ['sessionList']
    pspec.type = "SessionManager"

    pfs = client.factory.create("ns0:PropertyFilterSpec")
    pfs.propSet = [pspec]
    pfs.objectSet = [ospec]

    try:
        client.service.Login(sc.sessionManager, username, str_decrypt(password))
        sm = client.service.RetrieveProperties(sc.propertyCollector, pfs)
        client.service.Logout(sc.sessionManager)
        session_list = sm[0].propSet[0].val['UserSession']
        for session in session_list:
            if session.key.lower()==vimSessionKey.lower():
                return session
        return None
    except:
        return None

def get_effective_role(sessionId, moref, vc_uuid):
    wsdl_path = 'file:///' + os.getcwd() + '/share/vmware/wsdl41/auth.wsdl'
    vcenter = ''
    client = Client(wsdl_path)
    sc = None

    mob = client.factory.create("ManagedObjectReference")
    mob.value = "ServiceInstance"
    mob._type = "ServiceInstance"

    hosts = get_vcenter_hosts()
    for host in hosts:
        try:
            client.set_options(location="https://%s/sdk" % (host['ip']))

            handler = urllib2.HTTPCookieProcessor()
            handler.cookiejar.set_cookie(mkcookie('vmware_soap_session', sessionId, host['ip']))
            client.options.transport.urlopener = urllib2.build_opener(handler)

            sc = client.service.RetrieveServiceContent(mob)
            # We need to make a few calls until we find the vCenter with the UUID that matches the one we're looking for.
            if sc.about.instanceUuid.lower() == vc_uuid.lower():
                vcenter = host['ip']
                break
        except:
            log.exception('Error finding vCenter')
            pass

    if vcenter == '':
        return -9999

    ospec = client.factory.create('ns0:ObjectSpec')
    mob._type = moref.split(':')[0]
    mob.value = moref.split(':')[1]
    ospec.obj = mob

    pspec = client.factory.create("ns0:PropertySpec")
    pspec.pathSet = ['effectiveRole']
    pspec.type = mob._type

    pfs = client.factory.create("ns0:PropertyFilterSpec")
    pfs.propSet = [pspec]
    pfs.objectSet = [ospec]

    try:
        props = client.service.RetrieveProperties(sc.propertyCollector, pfs)
        return props[0].propSet[0].val.int
    except Exception as ex:
        log.exception('Error getting effective role')
        return -9999

def get_vcenter_hosts():
    try:
        f = open('config.json')
        config = json.load(f)
        return config['private_config']['vcenters']
    except:
        return []
