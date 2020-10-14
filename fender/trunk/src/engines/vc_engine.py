'''
Created on Oct 14, 2011

@author: IslamM
'''

from util import catalog
from vmware.vcenter import vCenter
from threading import Thread
from engines.threadpool import threadpool
import base64, time

from logging import getLogger
log = getLogger(__name__)

class vc_engine:
    def __init__(self, vcenters, decoder=None):
        self.decoder = decoder
        self.vcenters = vcenters
        self.threadpool = catalog.get_all(threadpool)[0]
        self.q = self.threadpool.get_q()
        catalog.insert(self, 'ic4vc-vc_engine')
        for vc in self.vcenters:
            vc_client = vCenter(vc.ip)
            vc_client.setup()
            catalog.insert(vc_client, vc.ip)
            try:
                vc_client.login(vc.username, vc.password)
            except:
                log.exception('Unable to login to vCenter %s', vc.ip)

        self.poll = Thread(name="vc_engine.vc_engine.poll", target=self.run)
        self.poll.daemon = True

    def decode(self, value):
        return self.decoder.decode(value) if self.decoder else base64.decodestring(value)
        
    def start(self):
        for vc in catalog.get_all(vCenter):
            self.q.put( (vc.generate_host_list, tuple()) )
            self.q.put( (vc.generate_cluster_list, tuple()) )
            time.sleep(1)
            
        self.poll.start()

    def delete_vc(self, vc):
        catalog.remove(key=vc['host'])
        
    def edit_vc(self, vc):
        vc_client = catalog.lookup(vc['host'])
        vc['password'] = base64.encodestring(vc['password'])
        vc_client.password = vc['password']

    def add_vc(self, vc):
        vc_client = vCenter(vc['host'])
        vc_client.setup()
        catalog.insert(vc_client, vc['host'])
        vc['username'] = base64.encodestring(vc['username'])
        vc['password'] = base64.encodestring(vc['password'])
        vc_client.login(vc['username'], vc['password'])

    def run(self):
        log.debug('Starting the keep_alive thread...')
        while True:
            time.sleep(300)
            log.debug('Calling vc.keep_alive...')
            for vc in catalog.get_all(vCenter):
                log.debug('Putting the vc.keep_alive in queue...')
                self.q.put( (vc.keep_alive, tuple()) )
                time.sleep(1)
        
    def initializing_vcenter(self):
        vcs = catalog.get_all(vCenter)
        for vc in vcs:
            if vc.initializing:
                return True
        return False
    
    def host_discovery_done(self):
        for vc in catalog.get_all(vCenter):
            if not vc.host_discovery_done:
                return False
        return True

def get_vcenter(host_moref):
    for vc in catalog.get_all(vCenter):
        vc.update()
        log.debug("Checking vCenter %s for host %s in %s", vc.host, host_moref, str([ h.moref() for h in vc.hosts]) )
        if vc.has_host(host_moref):
            return vc
    log.error("Unable to find vcenter for host '%s'", host_moref)
    return None

def get_vcenter_for_obj(obj):
    for vc in catalog.get_all(vCenter):
        vc.update()
        if vc.has_obj(obj):
            return vc
    log.error("Unable to find vcenter for object '%s'", obj)    
    return None

        