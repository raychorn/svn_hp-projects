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

class UIDControl:
    def __init__(self):
        self.task_name = 'UID Control'
        self.taskid = None
        self.dc = Collector.get_collector()
        self.start_time = time.time()
        self.user = self.dc.vcntr.get_current_session_username(self.dc.sessionId)
        self.cstate = self.get_current_state()
        log.debug('Current UID state: %s', self.cstate)
        self.setstate = 'off' 
        if self.cstate.lower() == 'off':
            self.setstate = 'on'
        log.debug('Setting UID state to: %s', self.setstate)

        self.objpath = 'moref=' + self.dc.moref + '&' + 'serverGuid=' + self.dc.serverGuid
        self.uim_url = urlparse(config().get_uim_root())        
        self.uimgr = UIManager(port=self.uim_url.port, protocol=self.uim_url.scheme )
        self.desc = 'UID state toggled from ' + self.cstate.capitalize() + ' to ' + self.setstate.capitalize() + ' by ' + self.user

    def get_current_state(self):
        data = 'Unknown'
        if self.dc.ilo:
            data = self.dc.ilo.get('data', {}).get('get_uid_status', {}).get('uid', 'Unknown')
        return data.capitalize()

    def start_toggle_uid(self):
        if self.cstate.lower() in ('on','off'):
            t = Thread(target=self.toggle_uid, name="Run Toggle UID")
            t.daemon = True
            t.start()
        else:
            self.uimgr.post_event(self.objpath, 'Unable to toggle UID from current state ' + self.cstate, 'User Action', 'INFORMATION', time.time())
    
    def toggle_uid(self):
        self.taskid = self.uimgr.create_task(self.objpath, self.task_name, [], self.start_time, "RUNNING", self.user, self.desc,  [])
        self.dc.csce.entity_control('ilo', self.dc.ilo['uuid'], 'uid', self.setstate)
        time.sleep(10)
        self.dc.csce.force_update_entity('ilo', self.dc.ilo['uuid'])
        time.sleep(30)
        self.uimgr.post_event(self.objpath, self.desc, 'User Action', 'INFORMATION', time.time())
        self.uimgr.update_task(self.objpath, self.taskid['ids'][0], self.task_name, [], self.start_time, time.time(), "COMPLETED", self.user, self.desc,  [])
        remove_collector( '%s:%s' %(self.dc.sessionId, self.dc.moref) )
        
    def fail_task(self):
        if self.taskid:
            self.uimgr.update_task(self.objpath, self.taskid['ids'][0], self.task_name, [], self.start_time, time.time(), "ERROR", self.user, self.desc,  [])
