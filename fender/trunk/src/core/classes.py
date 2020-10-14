'''
Created on Nov 4, 2011

@author: IslamM
'''

class obj:pass

class discovery_entity(obj):
    def __init__(self, addr, entity_type, discovered=False):
        self.address = addr
        self.entity_type = entity_type
        self.discovered = discovered