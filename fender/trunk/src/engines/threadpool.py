'''
Created on Mar 17, 2011

@author: islamm
'''
from threading import Thread
import Queue
from util import catalog
from util.config import config
import logging
log = logging.getLogger(__name__)


class threadpool:
    def __init__(self, name='ic4vc-server-threadpool', no_threads=20):
        self.q = Queue.Queue()
        for i in range(no_threads):
            t = Thread(name='%s:%d' %(name, i), target=self.run)
            t.daemon = True
            t.start()
        
    def run(self):
        while True:
            try:
                func, args = self.q.get()
                if (func):
                    func(*args)
                    self.q.task_done()
            except Exception as e:                
                log.exception('Exception in threadpool, %s', str(e))
                
    
    def get_q(self):
        return self.q 
    
def create():
    srvcfg = config().get_server_config()
    num_threads = int(srvcfg.get('threadpool', {}).get('num_threads', 20))
    tp = threadpool(no_threads = num_threads)
    catalog.insert(tp, 'ic4vc-server-threadpool')
    