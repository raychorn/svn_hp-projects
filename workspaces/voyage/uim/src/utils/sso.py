#####################################################
#
# sso.py
#
# Copyright 2009 Hewlett-Packard Development Company, L.P.
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
#    Chris Frantz
# 
# Description:
#    SIM Single Sign on for HP managed devices
#
#####################################################
import logging
from M2Crypto import EVP, X509
import urllib2 as u2

log = logging.getLogger(__name__)

class SSO:
    def __init__(self, certfile, keyfile):                

        self.pkey = EVP.load_key(keyfile)
        self.cert = X509.load_cert(certfile)

    def getkey(self, host):
        url = u2.urlopen('https://%s/Proxy/GetKey' % host)
        key = url.read()
        url.close()
        return key

    def url(self, host, user='Administrator', url=''):
        # Make sure the strings are NOT unicode, since iLO and OA do the
        # hash computation in ASCII
        host = str(host)
        user = str(user)
        url = str(url)
        url = 'https://%s/%s' % (host, url)

        key = self.getkey(host)
        myname = self.cert.get_subject().CN
        prehash_token = '%s:%s:%s:%s:%s' % (key, myname, user, 4, url)
        self.pkey.reset_context('sha1')
        self.pkey.sign_init()
        self.pkey.sign_update(prehash_token)
        token = self.pkey.sign_final()

        token = ''.join(map(lambda x: '%02x'%ord(x), token))
        qs = 'TKN=%s&KEY=%s&XE=%s&UN=%s&UA=%s&URL=%s' % (token, key, myname, user, 4, url)
        qs = qs.replace(' ', '%20')
        return 'https://%s/Proxy/SSO?%s' % (host, qs)

def url(host, cert_file_name, key_file_name, user='Administrator', url=''):
    try:
        sso = SSO(str(cert_file_name), str(key_file_name))
        url = sso.url(host, user, url)
        return url
    except Exception, e:
        log.exception('Exception creating Single Sign-on URL')
        return 'https://%s/%s' % (host, url)

# vim: ts=4 sts=4 sw=4:
