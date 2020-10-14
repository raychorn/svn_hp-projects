'''
Created on Oct 14, 2011

@author: IslamM
'''
from uuid import UUID
import logging
log = logging.getLogger(__name__)

import re

less_than_smbios26_pat = re.compile('^.*[BDMS]L.{2,}((5[a-z]*\sG[2-6])|(0[a-z]*\sG[2-5]))', re.IGNORECASE)

class dataobj:pass

class vmwobj:
    def __init__(self, obj):
        self._type = obj._type
        self.value = obj.value

    def update(self, name, value):
        names = name.split('.')
        ref = self
        for name in names[:-1]:
            r2 = getattr(ref, name, None)
            if not r2:
                r2 = dataobj()
                setattr(ref, name, r2)
            ref = r2
        setattr(ref, names[-1], value)

    def setprop(self, psets):
        for ps in psets:
            self.update(ps.name, getattr(ps, 'val', None))        
        # Some of the properties might be in a format which is not suitable for
        # us to use.  For example, host object's uuid needs to be fixed. 
        self.fix_props()
            
    def moref(self):
        return '%s:%s' %(self._type, self.value)
    
    def fix_props(self):
        pass
    
class host(vmwobj):
    mo_type = 'HostSystem'
    pset_minimal = ['name', 'hardware.systemInfo.uuid', 'hardware.systemInfo.model', 'vm', 'datastore', 'config.ipmi', 'config.product.version', 'config.product.build']
    pset_detail = ['overallStatus', 'hardware', 'name', 'config.ipmi', 'config.network', 'config.product', 
                'config.storageDevice.hostBusAdapter', 'config.storageDevice.multipathInfo.lun', 'datastore', 
                'vm']
    def fix_props(self):
        try:
            log.debug(self.config.product.version);
            # Swap UUID bytes on 4.0, 4.1, 5.0 < U2
            # Swap UUID if less than SMBios 2.6            
            #   SMBios 2.6 == Intel >= G6 && AMD >= G7            
            #   Intel = model number ends with 0
            #   AMD = model number ends with 5
            
            if (self.config.product.version in ['4.0.0', '4.1.0']) or \
                    (self.config.product.version == '5.0.0' and int(self.config.product.build) < 914586) or \
                    ( re.match(less_than_smbios26_pat, self.hardware.systemInfo.model) ):
                log.debug('Swapping UUID bytes...');
                self.hardware.systemInfo.uuid = str(UUID(bytes=UUID(self.hardware.systemInfo.uuid).bytes_le))
        except:
            log.exception('Error processing vcenter UUID')
            
class vm(vmwobj):
    mo_type = 'VirtualMachine'
    pset_minimal = ['name', 'config.uuid']
    pset_detail = ['name', 'config.uuid', 'config.hardware']
    
class dvpg(vmwobj):
    mo_type = 'DistributedVirtualPortgroup'
    pset_minimal = ['name', 'configStatus']
    pset_detail = ['name', 'configStatus', 'config', 'host']
    
class datacenter(vmwobj):
    mo_type = 'Datacenter'
    pset_minimal = ['name']
    pset_detail = ['name']

class cluster(vmwobj):
    mo_type = 'ClusterComputeResource'
    pset_minimal = ['name']
    pset_detail = ['name', 'configStatus', 'host']
    
class ds(vmwobj):
    mo_type = 'Datastore'
    pset_minimal = ['name']
    pset_detail = ['name', 'info', 'host']
    
class vdvsw(vmwobj):
    mo_type = 'VmwareDistributedVirtualSwitch'
    pset_minimal = ['name']
    pset_detail = ['name', 'configStatus', 'config']
    
def factory(obj):
    vobj = None
    if obj._type == host.mo_type:
        vobj = host(obj)
    elif obj._type == vm.mo_type:
        vobj = vm(obj)
    elif obj._type == dvpg.mo_type:
        vobj = dvpg(obj)
    elif obj._type == cluster.mo_type:
        vobj = cluster(obj)
    elif obj._type == ds.mo_type:
        vobj = ds(obj)
    elif obj._type == vdvsw.mo_type:
        vobj = vdvsw(obj)
    return vobj

