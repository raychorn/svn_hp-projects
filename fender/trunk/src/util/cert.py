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
#    Mohammed M. Islam
#    Modified Chris Frantz's cert module.  This module can used independently.
# 
# Description:
#
#####################################################
from M2Crypto import X509, ASN1, EVP, RSA
from time import time
from random import SystemRandom

class Request:
    '''
    X509 Certificate Request
    '''
    def __init__(self, bits=1024, **kwargs):
        '''
        Initialize a X509 Certificate Request

        @type bits: int
        @param bits: Number of bits in the public/private key pair.
        @type kwargs: keyword arguments
        @param kwargs: Key-value pairs for certificate fields.
                Example: CN='common name', Email='foo@bar.com'
        '''
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
        '''
        Update a certificate field or fields.

        @type item: str
        @param item: Field to update
        @type value: str
        @param value: New value for field
        @type kwargs: keyword arguments
        @param kwargs: Key-value pairs for certificate fields.
                Example: CN='common name', Email='foo@bar.com'
        '''
        name = self.req.get_subject()

        if item and value:
            setattr(name, item, value)
        for k,v in kwargs.items():
            setattr(name, k, v)
        self.req.set_subject_name(name)

    def save(self, filename=None):
        '''
        Save the Certificate Signing Request to a pair of files

        @type filename: str
        @param filename: The basename of the files to save.
            The request will be saved to filename.pem and the private
            key will be saved to filename.key
        '''

        csrfile = filename + '.pem'
        keyfile = filename + '.key'

        self.req.sign(self.pk, 'sha1')
        self.req.save(csrfile)
        self.pk.save_key(keyfile, None)

class Certificate:
    '''
    Self-Signed Certificate
    '''
    def __init__(self, bits=1024, **kwargs):
        '''
        Initialize a Self-Signed Certificate

        @type bits: int
        @param bits: Number of bits in the public/private key pair.
        @type kwargs: keyword arguments
        @param kwargs: Key-value pairs for certificate fields.
                Example: CN='common name', Email='foo@bar.com'
        '''
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
        '''
        Update a certificate field or fields.

        @type item: str
        @param item: Field to update
        @type value: str
        @param value: New value for field
        @type kwargs: keyword arguments
        @param kwargs: Key-value pairs for certificate fields.
                Example: CN='common name', Email='foo@bar.com'
        '''
        name = self.req.get_subject()
        if item and value:
            setattr(name, item, value)
        for k,v in kwargs.items():
            setattr(name, k, v)
        self.req.set_subject_name(name)
        self.req.set_issuer(name)

    def save(self, filename=None):
        '''
        Save the Self-Signed Certificate to a pair of files

        @type filename: str
        @param filename: The basename of the files to save.
            The request will be saved to filename.pem and the private
            key will be saved to filename.key
        '''

        crtfile = filename + '.pem'
        keyfile = filename + '.key'

        self.req.sign(self.pk, 'sha1')
        self.req.save(crtfile)
        self.pk.save_key(keyfile, None)


def check(certfile=None, keyfile=None):
    '''
    Check that the certificate file matches the keyfile

    @type certfile: str
    @param certfile: The file containing the certificate.
    @type keyfile: str
    @param keyfile: The file containing the key.
    @rtype: bool
    @return: True if the certfile and keyfile have the same modulus.
    '''

    cert = X509.load_cert(certfile)
    key = EVP.load_key(keyfile)
    m1 = cert.get_pubkey().get_modulus()
    m2 = key.get_modulus()
    return m1 == m2

def self_signed(certfile=None, keyfile=None):
    '''
    Check if the certificate is self-signed

    @type certfile: str
    @param certfile: The file containing the certificate.
    @type keyfile: str
    @param keyfile: The file containing the key.
    @rtype: bool
    @return: True if the certificate is self-signed.
    '''

    cert = X509.load_cert(certfile)
    cert.set_pubkey(EVP.load_key(keyfile))
    return cert.verify()

# vim: ts=4 sts=4 sw=4:
