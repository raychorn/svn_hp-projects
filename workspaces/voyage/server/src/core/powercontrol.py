'''
Created on Oct 18, 2011

@author: IslamM
'''
from logging import getLogger
log = getLogger(__name__)

from util import catalog
from util.config import config
from portlets.collector import Collector, remove_collector
from core.uim import UIManager
from urlparse import urlparse
from threading import Thread
import time

class PowerControl:
    def __init__(self):
        self.task_name = 'Power Control'
        self.taskid = None
        self.dc = Collector.get_collector()
        self.start_time = time.time()
        self.user = self.dc.vcntr.get_current_session_username(self.dc.sessionId)
        self.cstate = self.get_current_state()
        self.setstate = 'off' 
        if self.cstate.lower() == 'off':
            self.setstate = 'on'
        log.debug('Setting Power Control to: %s', self.setstate)
        self.objpath = 'moref=' + self.dc.moref + '&' + 'serverGuid=' + self.dc.serverGuid
        self.uim_url = urlparse(config().get_uim_root())        
        self.uimgr = UIManager(port=self.uim_url.port, protocol=self.uim_url.scheme )
        self.desc = 'Server Power switched from ' + self.cstate.capitalize() + ' to ' + self.setstate.capitalize() + ' by ' + self.user

    def get_current_state(self):
        data = 'Unknown'
        if self.dc.ilo:
            data = self.dc.ilo.get('data', {}).get('get_pwreg', {}).get('get_host_power', {}).get('host_power','Unknown')
        log.debug('Current power state: %s', data)
        return data.capitalize()

    def start_toggle_power(self):
        if self.cstate.lower() in ('on','off'):
            t = Thread(target=self.control_power, name="Run Control Power")
            t.daemon = True
            t.start()
        else:
            self.uimgr.post_event(self.objpath, 'Unable to switch power from its current state ' + self.cstate, 'User Action', 'INFORMATION', time.time())

    def control_power(self):
        self.taskid = self.uimgr.create_task(self.objpath, self.task_name, [], self.start_time, "RUNNING", self.user, self.desc,  [])
        self.dc.csce.entity_control('ilo', self.dc.ilo['uuid'], 'power', self.setstate)
        for i in range(10):
            time.sleep(10)
            try:
                self.dc.csce.force_update_entity('ilo', self.dc.ilo['uuid'])
                ilodata = self.dc.csce.get_hostinfo('ilo', self.dc.ilo['uuid'])
                if ilodata:
                    pwr_state = ilodata.get('data', {}).get('get_pwreg', {}).get('get_host_power', {}).get('host_power','Unknown')
                    log.debug('Latest power state after updatenow: %s', pwr_state)
                    if pwr_state.lower() == self.setstate.lower():
                        break
            except:
                log.exception('Error getting latest power state')
        
        #time.sleep(30)
        self.uimgr.post_event(self.objpath, self.desc, 'User Action', 'INFORMATION', time.time())
        self.uimgr.update_task(self.objpath, self.taskid['ids'][0], self.task_name, [], self.start_time, time.time(), "COMPLETED", self.user, self.desc,  [])
        remove_collector( '%s:%s' %(self.dc.sessionId, self.dc.moref) )

    def fail_task(self):
        if self.taskid:
            self.uimgr.update_task(self.objpath, self.taskid['ids'][0], self.task_name, [], self.start_time, time.time(), "ERROR", self.user, self.desc,  [])
        