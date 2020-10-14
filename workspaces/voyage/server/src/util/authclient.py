'''
@author: Partho S Bhowmick
'''

from logging import getLogger
log = getLogger(__name__)


class authclient:
    def __init__(self):
        self.db={}

    def add(self, auth, client):
        assert(hasattr(client, '_copycore'))
        self.db[auth] = client._copycore()
        return client

    def get(self, auth):
        try:
            y = self.db[auth]
        except:
            return None
        return y[0](y[1]) 

    def delete(self, auth):
        try:
            y = self.db[auth]
            del self.db[auth]
            return y
        except:
            return None

def get_authclient():
    global __instance 
    if not __instance:
        __instance = authclient()
    return __instance

__instance = None

