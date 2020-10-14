#####################################################
#
# cert.py
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
#    Mohammed M. Islam
# 
# Description:
#    This is a modified version of the original code in the brek project
#####################################################
from M2Crypto import X509, ASN1, EVP, RSA
from time import time
from random import SystemRandom

class Request:
    def __init__(self, bits=2048, **kwargs):
        req = X509.Request()
        rsa = RSA.gen_key(bits, 0x10001, lambda x, y: True)
        pk = EVP.PKey()
        pk.assign_rsa(rsa)
        req.set_pubkey(pk)
        req.set_version(2)
        
        name = X509.X509_Name()
        for k,v in kwargs.items():
            setattr(name, k, v)

        req.set_subject_name(name)

        self.req = req
        self.pk = pk

    def update(self, item=None, value=None, **kwargs):
        name = self.req.get_subject()
        if item and value:
            setattr(name, item, value)
        for k,v in kwargs.items():
            setattr(name, k, v)
        self.req.set_subject_name(name)

    def save(self, filename):
        csrfile = filename + '.pem'
        keyfile = filename + '.key'

        self.req.sign(self.pk, 'sha1')
        self.req.save(csrfile)
        self.pk.save_key(keyfile, None)

class Certificate:
    def __init__(self, bits=2048, **kwargs):
        req = X509.X509()
        rsa = RSA.gen_key(bits, 0x10001, lambda x, y: True)
        pk = EVP.PKey()
        pk.assign_rsa(rsa)
        req.set_pubkey(pk)
        req.set_version(2)
        
        name = X509.X509_Name()
        for k,v in kwargs.items():
            setattr(name, k, v)

        req.set_subject_name(name)
        req.set_issuer(name)
        req.set_serial_number(int(SystemRandom().getrandbits(28)))

        t = int(time())
        not_before = ASN1.ASN1_UTCTIME()
        not_before.set_time(t)
        not_after = ASN1.ASN1_UTCTIME()
        not_after.set_time(t+365*24*60*60)

        req.set_not_before(not_before)
        req.set_not_after(not_after)

        self.req = req
        self.pk = pk

    def update(self, item=None, value=None, **kwargs):
        name = self.req.get_subject()
        if item and value:
            setattr(name, item, value)
        for k,v in kwargs.items():
            setattr(name, k, v)
        self.req.set_subject_name(name)
        self.req.set_issuer(name)

    def save(self, filename):

        crtfile = filename + '.pem'
        keyfile = filename + '.key'

        self.req.sign(self.pk, 'sha1')
        self.req.save(crtfile)
        self.pk.save_key(keyfile, None)

def check(certfile, keyfile):

    cert = X509.load_cert(certfile)
    key = EVP.load_key(keyfile)
    m1 = cert.get_pubkey().get_modulus()
    m2 = key.get_modulus()
    return m1 == m2

def self_signed(certfile, keyfile):
    cert = X509.load_cert(certfile)
    cert.set_pubkey(EVP.load_key(keyfile))
    return cert.verify()

# vim: ts=4 sts=4 sw=4:
