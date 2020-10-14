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

class RediscoverNode:
    def __init__(self, server=True, ilo=True, oa=True, vcm=True):
        self.rediscover_server = server
        self.rediscover_ilo = ilo
        self.rediscover_oa = oa
        self.rediscover_vcm = vcm
        self.task_name = 'Rediscover Node'
        self.taskid = None
        self.dc = Collector.get_collector()
        self.start_time = time.time()
        self.user = self.dc.vcntr.get_current_session_username(self.dc.sessionId)
        self.objpath = 'moref=' + self.dc.moref + '&' + 'serverGuid=' + self.dc.serverGuid
        self.uim_url = urlparse(config().get_uim_root())        
        self.uimgr = UIManager(port=self.uim_url.port, protocol=self.uim_url.scheme )
        self.desc = 'Rediscover node triggered by ' + self.user

    def get_devices(self):
        nodes = {}
        if self.dc.server:
            nodes['server'] = True
        if self.dc.ilo:
            nodes['ilo'] = True
        if self.dc.oa:
            nodes['oa'] = True
        if self.dc.vcm:
            nodes['vcm'] = True
         
        return nodes

    def start_rediscovery(self):
        t = Thread(target=self.rediscover, name="Run Node Rediscovery")
        t.daemon = True
        t.start()

    def rediscover(self):
        self.taskid = self.uimgr.create_task(self.objpath, self.task_name, [], self.start_time, "RUNNING", self.user, self.desc,  [])
        #print self.dc.csce.entities
        try:
            if self.rediscover_server and self.dc.server:
                ip = self.dc.server.get('address', {}).get('ipv4','')
                if ip:
                    self.dc.csce.delete_entity('server', self.dc.server['uuid'])
                    self.dc.csce.entities[ip].discovered = False
        except:
            log.exception('Error rediscovering server')

        try:
            if self.rediscover_ilo and self.dc.ilo:
                ip = self.dc.ilo.get('address', {}).get('ipv4','')
                if ip:
                    self.dc.csce.delete_entity('ilo', self.dc.ilo['uuid'])
                    self.dc.csce.entities[ip].discovered = False
        except:
            log.exception('Error rediscovering ilo')

        try:
            if self.rediscover_oa and self.dc.oa:
                ip = self.dc.oa.get('address', {}).get('ipv4','')
                if ip:
                    self.dc.csce.delete_entity('oa', self.dc.oa['uuid'])
                    self.dc.csce.entities[ip].discovered = False
        except:
            log.exception('Error rediscovering oa')

        try:
            if self.rediscover_vcm and self.dc.vcm:
                ip = self.dc.vcm.get('address', {}).get('ipv4','')
                if ip:
                    self.dc.csce.delete_entity('vcm', self.dc.vcm['uuid'])
                    self.dc.csce.entities[ip].discovered = False
        except:
            log.exception('Error rediscovering vcm')

        time.sleep(30)
        self.dc.csce.start_cs_discovery()
        self.uimgr.post_event(self.objpath, self.desc, 'User Action', 'INFORMATION', time.time())
        self.uimgr.update_task(self.objpath, self.taskid['ids'][0], self.task_name, [], self.start_time, time.time(), "COMPLETED", self.user, self.desc,  [])
        remove_collector( '%s:%s' %(self.dc.sessionId, self.dc.moref) )

    def fail_task(self):
        if self.taskid:
            self.uimgr.update_task(self.objpath, self.taskid['ids'][0], self.task_name, [], self.start_time, time.time(), "ERROR", self.user, self.desc,  [])
        