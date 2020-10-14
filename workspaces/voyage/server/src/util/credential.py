'''
@author: Partho S Bhowmick
'''
from logging import getLogger
import catalog
log = getLogger(__name__)
import traceback


class credkey:
    def __init__(self, typ, ip, username):
        self.ip = ip
        self.username = username
        self.typ = typ

    def __eq__(self, other):
        if (self is other):
            return True
        elif (isinstance(other, credkey)):
            return self.ip == other.ip and self.typ == other.typ and self.username == other.username
        else:
            return False

    def __hash__(self):
        x = "%s::%s::%s" %(self.typ, self.ip, self.username)
        return hash(x)

class DuplicateCredError(Exception):
    def __init__(self, ip, credtype, credusername):
        self.ip = ip
        self.credtype = credtype
        self.credusername = credusername

    def __str__(self):
        return "ip = %s, credential type = %s, username = %s already exists. Delete the existing one first" % (self.ip, self.credtype, self.credusername)

    def __repr__(self):
        return self.__str__()

class NonUniqueCredMatch(Exception):
    def __init__(self, pwlist):
        self.pwlist = [{'username':pw['username'], 'host':pw['host'], 'type':pw['type']} for pw in pwlist]

    def __str__(self):
        return "Multiple credentials matched = %s" % self.pwlist

    def __repr__(self):
        return self.__str__()

class NoSuchCredError(Exception):
    def __init__(self, ip, credtype, credusername):
        self.ip = ip
        self.credtype = credtype
        self.credusername = credusername

    def __str__(self):
        return "ip = %s, credential type = %s, username = %s does not exist." % (self.ip, self.credtype, self.credusername)

    def __repr__(self):
        return self.__str__()

class NoSuchHostError(Exception):
    def __init__(self, ip, credtype, credusername):
        self.ip = ip
        self.credtype = credtype
        self.credusername = credusername

    def __str__(self):
        return "ip = %s does not exist, credential type = %s, username = %s." % (self.ip, self.credtype, self.credusername)

    def __repr__(self):
        return self.__str__()

class BadPasswordCredError(Exception):
    def __init__(self, ip, credtype, credusername):
        self.ip = ip
        self.credtype = credtype
        self.credusername = credusername

    def __str__(self):
        return "Bad password provided for ip = %s, credential type = %s, username = %s." % (self.ip, self.credtype, self.credusername)

    def __repr__(self):
        return self.__str__()

def _fetch_all_creds():
    csce = catalog.lookup('engines.csc_engine')
    db = {}
    pwlist = csce.get_passwords()
    #log.debug("Creds all passwds %s", pwlist)
    for pw in pwlist:
        ck = credkey(typ = pw['type'], ip=pw['host'], username = pw['username'])
        db[ck] = pw.copy()
    return db

def find_pw_in_cspw_list(data):
    csce = catalog.lookup('engines.csc_engine')
    pwlist = csce.get_passwords()
    for pw in pwlist:
        if (data.has_key('id') and pw['id'] == data['id']) or \
                (data.has_key('type') and pw['type'] == data['type'] and \
                 data.has_key('host') and pw['host'] == data['host']) or \
                 ((not 'type' in data) and \
                  ('username' in data and pw['username'] == data['username'] and \
                   'host' in data and pw['host'] == data['host'])):
            return pw
    return None

def find_pwlist_in_cspw_list(data):
    csce = catalog.lookup('engines.csc_engine')
    pwlist = csce.get_passwords()
    for pw in pwlist:
        if ('id' in data and pw['id'] == data['id']) or \
                ('type' in data and pw['type'] == data['type'] and \
                 data.has_key('host') and pw['host'] == data['host']) or \
                 ((not 'type' in data) and \
                  ('username' in data and pw['username'] == data['username'] and \
                   'host' in data and pw['host'] == data['host'])):
            return pw
    return None

def find_pw_for_host_and_type(host, pwtype):
    csce = catalog.lookup('engines.csc_engine')
    pwlist = csce.get_passwords()
    #log.debug('looking for password %s, %s', host, pwtype)
    globalpw = None
    
    for pw in pwlist:
        if (pw['type'] == pwtype):
            if (pw['host'] == host):
                return pw
            if (pwtype in ['iLo', 'vCenter', 'Onboard Administrator']) and pw['host']=='*':
                globalpw = pw
    return globalpw

def find_global_pw(pwtype):
    csce = catalog.lookup('engines.csc_engine')
    pwlist = csce.get_passwords()
    for pw in pwlist:
        if (pw['type'] == pwtype and pw['host'] == '*'):
            return pw
    return None

def pwtype_exists(pwtype):
    csce = catalog.lookup('engines.csc_engine')
    pwlist = csce.get_passwords()
    for pw in pwlist:
        if (pw['type'] == pwtype):
            return pw
    return None

def add(ip, credtype, credusername, credpassword):
    csce = catalog.lookup('engines.csc_engine')
    vce = catalog.lookup('ic4vc-vc_engine')
    ck = credkey(typ = credtype, ip=ip, username = credusername)
    db = _fetch_all_creds()
    if (credtype == 'HP SIM' and pwtype_exists(credtype)) or (ck in db):
        raise DuplicateCredError(ip = ip, credtype = credtype,
            credusername = credusername)
    db[ck] = {'host':ip, 'username':credusername,
            'type':credtype, 'password':credpassword}
    try :
        csce.add_password_to_cs(db[ck]['username'], db[ck]['password'], 
                db[ck]['type'], db[ck]['host'])
        return ck
    except:
        log.exception("Error adding credential: Error adding credential for %s", db[ck])
        log.error("Error adding credential: traceback is %s", traceback.format_exc())
        raise
            
def delete(ip = None, credtype = None, credusername = None, checkunique = True, credid = None):
    csce = catalog.lookup('engines.csc_engine')
    vce = catalog.lookup('ic4vc-vc_engine')
    pwlist = get(ip = ip, credtype = credtype, credusername = credusername, credid = credid)
    log.debug("credential.delete - matching for ip = %s, credtype = %s, credusername = %s >>> %s", 
            ip, credtype, credusername, pwlist)
    if checkunique  and len(pwlist) > 1:
        raise NonUniqueCredMatch(pwlist)
    if (len(pwlist) == 0):
        log.error('Error deleting credential: Unable to find credential')
        raise NoSuchCredError(ip = ip, credtype = credtype,
            credusername = credusername)

    for pw in pwlist:
        csce.del_password_from_cs(pw['id'], pw['username'], pw['type'], pw['ip'])
        if pw['type'] == 'vCenter':
            vce.delete_vc(pw)

def get(ip = None, credtype = None, credusername = None, credid = None):
    csce = catalog.lookup('engines.csc_engine')
    vce = catalog.lookup('ic4vc-vc_engine')
    #log.debug("Creds get %s,%s,%s", ip, credtype, credusername)
    collector = []
    db = _fetch_all_creds()
    for val in db.values():
        if credid:
            if val['id'] != credid:
                continue
            else:
                collector.append({'username':val['username'], 'ip':val['host'], 
                'type':val['type'], 'id':val['id']})
                return collector
        if (credtype) and val['type'] != credtype:
            continue
        global_cred = find_global_pw(credtype)
        if (ip) and val['host'] != ip and (not global_cred):
            continue
        if (credusername) and val['username'] != credusername and (not global_cred):
            continue
        val2 = {'username':val['username'], 'ip':val['host'], 
                'type':val['type'], 'id':val['id']} 
        collector.append(val2)
    log.debug("credentials get(): %s", collector)
    return collector

def change( ip, credtype, credusername, credpassword_new = None,
            credusername_new = None, ip_new = None):
    csce = catalog.lookup('engines.csc_engine')
    log.debug("change: looking for ip = %s, type = %s, username = %s", ip, credtype, credusername)
    #try:
    found_list = get(ip = ip, credtype = credtype, credusername = credusername)
    if found_list:
        for found in found_list:
            log.debug("change: found %s", found)
            if ip != found['ip'] or credusername != found['username']:
                continue
            log.debug("Matched!!")
            csce.update_password(found['id'], found['username'], 
                    credpassword_new, found['type'], found['ip'])
            if credtype == 'vCenter':
                vce = catalog.lookup('ic4vc-vc_engine')
                vce.edit_vc({'host':found['host'], 'password':credpassword_new})
            return    
    raise NoSuchCredError(ip = ip, credtype = credtype,
            credusername = credusername)
    '''
        if found:
            csce.update_password(pw['id'], data['username'], data['password'], data['type'], data['host'])
            if pw['type'] == 'vCenter':
                vce.edit_vc(data)
        ck = credkey(typ = credtype, ip=ip, username = credusername)
        x = db[ck].copy()
        if credpassword_new:
            db[ck]['password'] = credpassword_new
        if credusername_new:
            db[ck]['username'] = credusername_new
        if ip_new:
            db[ck]['host'] = ip_new
        
        pw = find_pw_in_cspw_list(x)
        if pw:
            csce.del_password_from_cs(pw['id'], pw['username'], pw['type'], pw['host'])
            try :
                csce.add_password_to_cs(credusername_new, 
                        credpassword_new, credtype_new, ip_new)
            except:
                log.exception("Error adding credential: Error adding credential for service %s at %s", data['host'])
                raise
        else :
            log.error('Error deleting credential: Unable to find credential in HPCS')
            return
    except KeyError as e:
        raise NoSuchCredError(ip = ip, credtype = credtype,
                credusername = credusername)
        '''


