#####################################################
#
# catalog.py
#
# Copyright 2007 Hewlett-Packard Development Company, L.P.
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
#    Mike Dickson
#    Chris Frantz
# 
# Description:
#    Implement a singleton resource catalog or cache of resources in the
#    system.  We instantiate the class here so there's a single copy.
#    Other callers can simply use the module level api to share the class
#    instance below (singleton pattern)
#
#####################################################

class ResourceCatalog: 
    def __init__(self):
        self.catalog = {}
    
    def insert(self, obj, key=None):
        if key:
            self.catalog[key] = obj

        uuid = getattr(obj, 'uuid', None)
        if uuid:
            self.catalog[uuid] = obj
        else:
            if not key:
                raise Exception("Object doesn't have an uuid")
        
    def remove(self, obj=None, key=None):
        if obj:
            keys = [k for k,v in self.catalog.items() if v == obj]
            for k in keys:
                del self.catalog[k]

        if key in self.catalog:
            del self.catalog[key]
            
    def lookup(self, key):
        if key in self.catalog:
            return self.catalog[key]
        else:
            return None

    def get_all(self, cls=None):
        val = set(self.catalog.values())
        if cls:
            return [v for v in val if isinstance(v, cls)]
        return list(val)
        
    def print_catalog(self):
        keys = self.catalog.keys()
        for key in keys:
            print "key=> ", key, " value=> ", self.catalog[key]

''' The singleton instance '''

__catalog = ResourceCatalog()

def get_singleton():
    return (__catalog)

def insert(obj, key=None):
    '''
    Insert an object into the catalog under the name 'key'.
    If the object has a 'uuid' field, the insert field will
    automatically create an entry with the uuid in addition
    to the 'key'.
    '''
    __catalog.insert(obj, key)
    
def remove(obj=None, key=None):
    '''
    Remove an object from the catalog.
    The 'key' (if not None) will always be removed from the catalog.

    If 'obj' is given, all keys referencing object will be removed.
    '''

    __catalog.remove(obj, key)
    
def lookup(key):
    '''
    Look up the object under the name 'key'.  Return the object or None.
    '''
    return (__catalog.lookup(key))

def print_catalog():
    __catalog.print_catalog()

def get_all(cls=None):
    '''
    Returns a tuple of all objects that are instances of cls,
    or all objects if cls is None

    Duplicates are removed: if the same item is in the catalog
    under different keys, the returned tuple will contain only
    once reference to that item.
    '''
    return __catalog.get_all(cls)

def keys():
    '''
    Return the list of all keys known to the catalog
    '''
    return __catalog.catalog.keys()

# vim: ts=4 sts=4 sw=4:
