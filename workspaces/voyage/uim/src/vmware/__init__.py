#####################################################
#
# __init__.py
#
# Copyright 2011 Hewlett-Packard Development Company, L.P.
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
#####################################################
from logging import getLogger
log = getLogger(__name__)

vcenters = []

def get_vc_instance(uuid=None, server=None, username=None):

    for vc in vcenters :
        if uuid :
            if vc.sc.about.instanceUuid.lower()==uuid.lower() :
                return vc
        if server and username :
            if vc.server == server and vc.username == username :
                return vc
        
    return None        

def remove_vc_instance(uuid=None, server=None, username=None) :
    for vc in vcenters :
        if (uuid and vc.sc.about.instanceUuid.lower()==uuid.lower() ) or (vc.server == server and vc.username == username) :
            log.info("Removing vCenter %s", vc.server)
            vcenters.remove(vc)                        
            return

    log.error("Error removing vCenter, %s %s %s not found", uuid, server, username)
            