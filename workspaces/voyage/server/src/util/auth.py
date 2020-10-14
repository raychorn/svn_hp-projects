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
import logging
from util.stringcrypt import str_decrypt
import config

from logging import getLogger
log = getLogger(__name__)

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
    
    cfg = config.config()
    
    mob = client.factory.create("ManagedObjectReference")
    mob.value = "ServiceInstance"
    mob._type = "ServiceInstance"
    
    for host in cfg.uimconfig['private_config']['vcenters'] :
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
            log.exception('Error logging into vCenter %s as user.', host['ip'])

    if vcenter == '':
        log.error("Unable to find vCenter with uuid %s", vc_uuid)
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
        log.error('Unable to find user session key')        
        return None
    except:
        log.exception('Error vCenter user session.')
        return None

def get_effective_role(sessionId, moref, vc_uuid) :
    wsdl_path = 'file:///' + os.getcwd() + '/share/vmware/wsdl41/auth.wsdl'
    vcenter = ''
    client = Client(wsdl_path)
    sc = None

    cfg = config.config()
    
    mob = client.factory.create("ManagedObjectReference")
    mob.value = "ServiceInstance"
    mob._type = "ServiceInstance"
    
    for host in cfg.uimconfig['private_config']['vcenters']:
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
            log.exception('Error logging into vCenter %s as user.', host['ip'])

    if vcenter == '':
        log.error("Unable to find vCenter with uuid %s", vc_uuid)
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
        log.exception('Error Retriving Properties of Effective Role')
        return -9999

