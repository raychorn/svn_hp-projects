'''
Created on Nov 3, 2011

@author: IslamM
'''
import os
import json
from suds.sax.parser import Parser
from suds.umx.basic import Basic as bumx
from suds.mx.basic import Basic as bmx
from M2Crypto import EVP
import base64, random, uuid

class crypy:
    KEY ='\x74\x6f\x9b\x22\xf0\x2f\x7e\x9d\x13\x21\x4b\x20\x1a\x1b\x7a\x86'
    def encode(self, cleartext, key=None):
        if key is None:
            key = crypy.KEY
        cipher = EVP.Cipher(alg='aes_128_cbc', key=key, iv='\0'*16, op=1)
        salt = random.getrandbits(64)
        p = "%016x%s" % (salt, cleartext)
        p = p.encode('utf8')
        p = cipher.update(p) + cipher.final()
        return base64.b64encode(p)

    def decode(self, ciphertext, key=None):
        if key is None:
            key = crypy.KEY
        cipher = EVP.Cipher(alg='aes_128_cbc', key=key, iv='\0'*16, op=0)
        p = base64.b64decode(ciphertext)
        p = cipher.update(p) + cipher.final()
        p = p.decode('utf8')
        p = p[16:]
        return p

# Class to get the correct path for various resources
# The assumptions are hpcs, uim and server are all installed
# in the same directory
class Resources:
    def __init__(self):
        self.cwd = os.getenv("PROJECT_HOME", os.getcwd())
        self.parent = self.cwd.rstrip(self.cwd.split('\\')[-1])
        self.hpcs = self.parent + 'hpcs'
        self.server = self.parent + 'server'
        self.uim = self.parent + 'uim'
        self.vmw_wsdl41 = self.uim + "\\share\\vmware\\wsdl41"

    def server_path(self):
        return self.server
        
    def hpcs_path(self):
        return self.hpcs
        
    def uim_path(self):
        return self.uim
    
    def vmw_wsdl41_path(self):
        return self.vmw_wsdl41
    
    def get_file_url(self, path, name):
        cur_dir = path
        if os.name == 'nt':
            # suds uses urllib2 to open files, this "fixes" the path
            # so it can be turned into a proper 'file://' url.
            # change backslash to slash remove the drive letter
            cur_dir = cur_dir.replace('\\', '/')
            cur_dir = cur_dir[2:]
        return "%s/%s" % (cur_dir, name)

# config class to retrieve various config information        
class config:
    def __init__(self):
        self.resource = Resources()
        self.uimconfig = json.load(open(self.resource.uim_path() + '/config.json'))
        self.serverconfig = json.load(open(self.resource.uim_path() + '/server_config.json'))
        sax = Parser()
        self.pwfile = self.resource.get_file_url( self.resource.hpcs_path(), 'share/password.xml' )
        xml = sax.parse(self.pwfile).root()
        self.cspwdb = bumx().process(xml)
   
        self.cscfgfile = self.resource.get_file_url( self.resource.hpcs_path(), 'share/config.xml' )
        xml = sax.parse(self.cscfgfile).root()
        self.cscfg = bumx().process(xml)
        
        self.masterxml_path = self.resource.get_file_url( self.resource.hpcs_path(), 'share/MasterXML.xml' )
        self.logging_config = self.resource.get_file_url( self.resource.server_path(), 'share/logging.conf')
    
    def get_simport(self) :
        try :
            return self.uimconfig['private_config']['hpsim']['port']
        except :
            return '50000'
    
    def get_oversubscription_factor(self) :
        try :            
            return float(self.serverconfig['private_config']['bottleneck_oversub'])
        except :
            return 1.2
    
    def get_power_cost(self) :
        try :            
            return float(self.serverconfig['private_config']['power_cost'])
        except :
            return 0.0
    
    def get_vcenters(self):
        return self.uimconfig['private_config']['vcenters']
    
    def get_server_root(self):
        return self.uimconfig['private_config']['web_roots']['server_root']

    def get_uim_root(self):
        return self.uimconfig['private_config']['web_roots']['uim_root']

    def get_cert_info(self):
        return self.cscfg.web.certinfo
    
    def get_cspw(self):
        if not isinstance(self.cspwdb.password, list):
            self.cspwdb.password = [self.cspwdb.password] 
        for pw in self.cspwdb.password:
            if pw.type == 'HP Common Services':
                return pw
                
    def find_pw(self, mytype, host=None) :        
        if not isinstance(self.cspwdb.password, list):
            self.cspwdb.password = [self.cspwdb.password] 
        
        rpw = None
            
        for pw in self.cspwdb.password :               
            if mytype == pw.type :
                if host and host == pw.host :
                    rpw = pw
                    break
                elif host and pw.host == '*' :
                    rpw = pw
                elif not host :
                    rpw = pw
                    break;

        if rpw :
            c = crypy()
            rpw.password = c.decode(rpw.epassword)
        return rpw
                    
    def get_simpw(self):
        if not isinstance(self.cspwdb.password, list):
            self.cspwdb.password = [self.cspwdb.password] 
        for pw in self.cspwdb.password:
            if pw.type == 'HP SIM':
                return pw

    def get_cspw_types(self):
        return self.cspwdb.types

    def get_cswebcfg(self):
        return self.cscfg.web
            
    def get_log_config_file(self):
        return self.logging_config
    
    def add_cs(self, user, pw):
        newp = []
        newp['id'] = str(uuid.uuid1())
        newp['type'] = 'HP Common Services'
        newp['host'] = '*'
        newp['username'] = user
        newp['password'] = pw
        self.cspwdb.password.append(newp)

    def save_cspw(self):
        xml = bmx().process(self.cspwdb)
        f = open(self.pwfile, 'w')
        f.write(str(xml))
        f.close()
        
    def get_server_config(self):
        return self.serverconfig['private_config']
    
    def set_server_config(self, cfgdata):
        self.serverconfig['private_config'] = cfgdata
        json.dump(self.serverconfig, open(self.resource.uim_path() + '/server_config.json', 'w+'), indent=4)
            