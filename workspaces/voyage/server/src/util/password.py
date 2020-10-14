#####################################################
#
# password.py
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
#    Chris Frantz
#    Andy Yates
#    Mohammed M. Islam
# 
# Description:
#    Manage the password database
#
#####################################################
from util import config
from util.xsd import xmlObject, xsd_helper
from util import SudsEncoder
import json
from logging import getLogger
from M2Crypto import EVP
import base64, random
from hp.mgmt.cs import CommonServices, CommonServicesServerError

log = getLogger(__name__)

VCENTER = 1
ILO = 2
OA = 3
SIM = 4
VCM = 5
CS = 6
ESX_HOST = 7
SNMP_COMMUNITY = 8

cs_password_types = {    
    VCENTER:'vCenter', 
    ILO:'iLO', 
    OA:'Onboard Administrator', 
    SIM:'HP SIM', 
    VCM:'Virtual Connect', 
    CS:'HP Common Services',
    ESX_HOST:'ProLiant Server',
    SNMP_COMMUNITY: 'SNMP Community String',
    }
    
class PWEncoder(SudsEncoder):
    def default(self, obj):
        if obj.__class__.__name__ == 'password':
            d = {}
            for k in obj.__keylist__:
                if k in ('password', 'epassword'):
                    continue
                d[k] = obj.__dict__[k]
            return d
        return SudsEncoder.default(self, obj)

class PasswordDB(xmlObject(config.resource('datatypes.xsd'), 'pwlist')):
    filename=config.resource('password.xml')
    IV = '\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0'
    KEY ='\x74\x6f\x9b\x22\xf0\x2f\x7e\x9d\x13\x21\x4b\x20\x1a\x1b\x7a\x86'

    def load(self, filename=None):
        self.default(password=[])
        xsd_helper.load(self, filename)
        self.pwdb = {}
        need_save = False
        for p in self.password:
            if 'epassword' in p:
                self.decrypt(p)
            else:
                # If we load a clear text password, encrypt it and
                # re-save the password file
                self.encrypt(p)
                need_save = True
            key = (p.username, p.host, p.type)
            self.pwdb[key] = p

        if need_save:
            self.save()

    def hide_pw(self, pwent):
        # Remove password from the keylist
        if 'password' in pwent.__keylist__:
            pwent.__keylist__.remove('password')

        # And remove it from pwent.__metadata__.ordering if it exists
        meta = getattr(pwent, '__metadata__', None)
        ordering = getattr(meta, 'ordering', [])
        if 'password' in ordering:
            ordering.remove('password')

    def encrypt(self, pwent):
        cipher = EVP.Cipher(alg='aes_128_cbc', key=self.KEY, iv=self.IV, op=1)
        salt = random.getrandbits(64)
        p = "%016x" % salt
        p += (pwent.password or '')
        p = p.encode('utf8')
        p = cipher.update(p) + cipher.final()
        pwent.epassword = base64.b64encode(p)
        self.hide_pw(pwent)
        return pwent

    def decrypt(self, pwent):
        cipher = EVP.Cipher(alg='aes_128_cbc', key=self.KEY, iv=self.IV, op=0)
        p = base64.b64decode(pwent.epassword)
        p = cipher.update(p) + cipher.final()
        p = p.decode('utf8')
        pwent.password = p[16:]
        self.hide_pw(pwent)
        return pwent
    
    def add(self, username, password, host, _type, save=True):
        _type = int(_type)
        key = (username, host, _type)
        pw = self.pwdb.get(key)
        already_saved = pw in self.password
        if not pw:
            if not password:
                log.info("Attempt to update a non-existing password entry")
            pw = self.build('password')

        pw.username = username
        pw.password = password
        pw.host = host
        pw.type = _type
        self.encrypt(pw)

        self.pwdb[key] = pw
        if not save or already_saved:
            return
        
        self.password.append(pw)

        # Don't send our common services password to common services.
        if _type != CS:
            cspw = self.find('','*',CS)
            cs = CommonServices(username=cspw.username, password=cspw.password)
            try :
                log.debug('adding cs credential: %s %s %s' % (pw.username, pw.type, pw.host) )
                cs.add_password(username=pw.username, password=pw.password, type=cs_password_types[pw.type], host=pw.host)
            except CommonServicesServerError as e :
                if '409' == e.status :
                    cs.delete_password(id=e.response, username = pw.username, type = cs_password_types[pw.type], host = pw.host)
                    cs.add_password(username=pw.username, password=pw.password, type=cs_password_types[pw.type], host=pw.host)
                else :
                    raise

    def remove(self, username, host, _type):
        _type = int(_type)
        key = (username, host, _type)
        pw = self.pwdb.get(key)
        if pw:            
            del self.pwdb[key]
            if pw in self.password:
                self.password.remove(pw)
            cspw = self.find('','*',CS)
            if cspw:
                cs = CommonServices(username=cspw.username, password=cspw.password)
                log.debug('removing cs credential: %s %s %s' % (pw.username, pw.type, pw.host))
                cs_pw = cs.get_password(username=pw.username, type=cs_password_types[pw.type], host=pw.host)
                if cs_pw :
                    cs.delete_password(**cs_pw)
                else :
                    log.error('cs credential not found: %s %s %s' % (pw.username, pw.type, pw.host))

    def update(self, old_username, old_host, old_type, new_username, new_host, new_type, new_password = None) :
        old_type = int(old_type)
        new_type = int(new_type)
        
        old_key = (old_username, old_host, old_type)
        new_key = (new_username, new_host, new_type)
        pw = self.pwdb.get(old_key)
        
        if pw :
            self.password.remove(pw)
            pw.username = new_username
            pw.host = new_host
            pw.type = new_type
            if new_password :   # keep the old password if new_password is None
                pw.password = new_password
                self.encrypt(pw)
            del self.pwdb[old_key]
            self.pwdb[new_key] = pw
            self.password.append(pw)  
                        
            cspw = self.find('','*',CS)
            if cspw:
                cs = CommonServices(username=cspw.username, password=cspw.password)
                log.debug('updating cs credential: %s %s %s' % (old_username, old_type, old_host))
                cs_pw = cs.get_password(username=old_username, type=cs_password_types[old_type], host=old_host)
                if cs_pw :
                    cs.delete_password(**cs_pw)
                cs.add_password(username=pw.username, password=pw.password, type=cs_password_types[pw.type], host=pw.host)                
            
    def json(self, cls=PWEncoder):
        return xsd_helper.json(self, cls)

    def find(self, username, host, _type, exact=False):
        _type = int(_type)
        k = (username, host, _type)

        # If the user insists on an exact match, then try it
        if k in self.pwdb:
            return self.pwdb.get(k)

        # Otherwise, try an exact match first, then
        # a username only match
        k = (username, '*', _type)
        if k in self.pwdb:
            return self.pwdb[k]

        # If that fails, iterate over the whole list.  The
        # type of password must always match.
        # If we have a host match consider it better than
        # just a type match
        plist = [pw for pw in self.pwdb.values() if pw.type == _type]
        def pwprio(pw):
            if pw.host == host:
                return 0
            elif pw.host == '*':
                return 1
            else:
                return 2
        plist.sort(lambda a,b: pwprio(a)-pwprio(b))

        # If exact is true, then we only want to return an entry where the host matches.
        # If exact is false, then we will return the global entry or none.
        p_entry = plist and plist[0] or None
        if p_entry:
            if p_entry.host == host:
                return p_entry
            elif not exact and p_entry.host == '*':
                return p_entry
            elif host == '*':
                return p_entry
            else:
                return None
        return None

    def find_global_cred(self, _type):
        _type = int(_type)
        for k, v in self.pwdb.items():
            if k[1] == '*' and k[2] == _type:
                return v
        return None

pw = PasswordDB()

def load(filename=None):
    pw.load(filename)

def save(filename=None):
    pw.save(filename)

def add(username, password, host, _type, save=True):
    pw.add(username, password, host, _type, save)

def find(username, host, _type, exact=False):
    return pw.find(username, host, _type, exact)

def remove(username, host, _type):
    pw.remove(username, host, _type)

def update(old_username, old_host, old_type, new_username, new_host, new_type, new_password = None) :
    pw.update(old_username, old_host, old_type, new_username, new_host, new_type, new_password)

def get():
    return pw

def find_global_cred(_type):
    return pw.find_global_cred(_type)

# vim: ts=4 sts=4 sw=4:
