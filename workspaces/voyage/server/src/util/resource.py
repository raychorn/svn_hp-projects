'''
Created on Oct 3, 2011

@author: IslamM
'''

import os

def resource(name):
    cur_dir = os.getenv("PROJECT_HOME", os.getcwd())
    if os.name == 'nt':
        # suds uses urllib2 to open files, this "fixes" the path
        # so it can be turned into a proper 'file://' url.
        # change backslash to slash remove the drive letter
        cur_dir = cur_dir.replace('\\', '/')
        cur_dir = cur_dir[2:]
    return "%s/share/%s" % (cur_dir, name)

class Resources:
    def __init__(self):
        cwd = os.getcwd()
        self.parent = cwd.rstrip(cwd.split('\\')[-1])
        self.config = self.parent + 'config'                
        self.hpcs = self.parent + 'hpcs'
        self.server = self.parent + 'server'
        self.uim = self.parent + 'uim'

    def server_path(self):
        return self.server
        
    def config_path(self):
        return self.config
    
    def hpcs_path(self):
        return self.hpcs
        
    def uim_path(self):
        return self.uim