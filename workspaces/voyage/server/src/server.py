#!/usr/bin/python
'''
Created on Aug 26, 2011

@author: IslamM

###########################################################################################
#
# server.py
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
# 
# Description:
#    Insight Control for vCenter main program!
#
##############################################################################################
'''

# the root directory needs to be set when running as a service.
# Next three lines will take care of that.
import logging, time, socket, os, base64

from util.environment import Environment
env = Environment()
env.set_root_dir()

from util.config import config, crypy
thisconfig = config()
import util.verbose_logging
import logging.config
logging.config.fileConfig( thisconfig.get_log_config_file() )   # Must be before imports that log

from engines.vc_engine import vc_engine
from engines.csc_engine import csc_engine
from engines import ic4vc_webserver
#from engines import data
from engines import threadpool
#from util import cmdline_opt as cmdoptions
import scomponent_server
from threading import Thread
from engines.deployment_connector import ALDeploymentConnector

log = logging.getLogger(__name__)
from util import catalog, soap
from util.smartcomponent import get_scm

from core.classes import obj 

class ICVCServer:
    def start(self):        
        log.info('Initializing ...')
        if not os.path.exists('static/sc_share'):
            os.makedirs('static/sc_share')
        socket.setdefaulttimeout(300)
        self.cfg = config()
        threadpool.create()
        self.crp = crypy()
        soap.setup()
        
        self.vcenters = []
        try:
            for vc in self.cfg.get_vcenters():
                vcobj = obj()
                setattr(vcobj, '__dict__', vc)
                self.vcenters.append(vcobj)
        except:
            log.exception('Unable to retrieve vcenter list from config.json')
            
        for vc in self.vcenters:
            try:
                vc.username = base64.encodestring(vc.username)
                vc.password = base64.encodestring(self.crp.decode(vc.password))
            except:
                log.exception('Error reading credentials from config.json for vcenter %s', vc.ip)
        
        log.info('Starting vCenters...')    
        vce = vc_engine(self.vcenters)
        vce.start()
        log.info('vCenters Started')    
        
        csce_t = Thread(target=self.start_csce, name='ICVCServer.start_csce')
        csce_t.deamon = True
        csce_t.start()
        
        log.info('Starting webserver...')
        self.serverwebapp = ic4vc_webserver.ServerWebApp()
        self.serverwebapp.start()
        log.info('webserver started')

        # Start the smart component server
        sc_t = Thread(target=scomponent_server.run_server, name="Smart Component HTTP server")
        sc_t.daemon = True
        sc_t.start()
    
        # Create the deployment connector
        ALDeploymentConnector.create()

        # Unpack any smart components that the user has added to IC4vC
        get_scm().discover_components()

    def start_csce(self):
        cs_webcfg = self.cfg.get_cswebcfg()
        cs_cred = self.cfg.get_cspw()
        
        cs_un = base64.encodestring(cs_cred.username)
        cs_pw = base64.encodestring(self.crp.decode(cs_cred.epassword))
        csce = csc_engine(cs_un, cs_pw, cs_webcfg)
        while True:
            try:
                csce.send_vcenter_list(self.vcenters, base64.decodestring)
                break 
            except:
                log.debug('Unable to start csc_engine, will retry in 120 secs ...', exc_info=1)
                time.sleep(120)
        csce.start()

    def stop(self):
        self.serverwebapp.stop()    
        
if __name__ == '__main__':

    ic4vc = ICVCServer()
    ic4vc.start()

    while True:
        time.sleep(60)
    ic4vc.stop()
    
