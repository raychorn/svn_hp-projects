'''
Created on Oct 14, 2011

@author: IslamM
'''
from uuid import UUID
import logging
log = logging.getLogger(__name__)

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

    pset_minimal = ['name', 'hardware.systemInfo.uuid', 'hardware.systemInfo.model', 'vm', 'datastore', 'config.storageDevice', 'config.ipmi', 'config.product.version', 'config.product.build']
    pset_detail = ['overallStatus', 'hardware', 'name', 'config.ipmi', 'config.network', 'config.product', 'config.storageDevice',
                'config.storageDevice.hostBusAdapter', 'config.storageDevice.multipathInfo.lun', 'datastore', 
                'vm']
    def fix_props(self):
        try:
            log.debug(self.config.product.version);
            # Swap UUID bytes on 4.0, 4.1, 5.0 U1 & 5.0 or 5.1 for certain G6 harware
            # Don't swap bytes on 5.0 U2 or higher
            if (self.config.product.version in ['4.0.0', '4.1.0']) or \
                    (self.config.product.version == '5.0.0' and int(self.config.product.build) < 914586) or \
                    ( self.hardware.systemInfo.model in 
                    ['ProLiant BL465c G6', 'ProLiant BL495c G6', 'ProLiant BL685c G6', 
                    'ProLiant DL385 G6', 'ProLiant DL585 G6', 'ProLiant DL785 G6' ]):
                log.debug('Swapping UUID bytes...');
                self.hardware.systemInfo.uuid = str(UUID(bytes=UUID(self.hardware.systemInfo.uuid).bytes_le))
        except:
            log.exception('Error processing vcenter UUID')
            
class vm(vmwobj):
    mo_type = 'VirtualMachine'
    pset_minimal = ['name', 'config.uuid']
    pset_detail = ['name', 'config.uuid', 'config.hardware', 'guest']
    
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
    pset_minimal = ['name', '']
    pset_detail = ['name', 'configStatus', 'host']
    
class ds(vmwobj):
    mo_type = 'Datastore'
    pset_minimal = ['name', '']
    pset_detail = ['name', 'info', 'host']
    
class vdvsw(vmwobj):
    mo_type = 'VmwareDistributedVirtualSwitch'
    pset_minimal = ['name', '']
    pset_detail = ['name', 'configStatus', 'config']
    
class hds(vmwobj):
    mo_type = 'HostDatastoreSystem'
    pset_minimal = ['capabilities', 'datastore']
    pset_detail = ['capabilities', 'datastore']
    
class guest_operations_manager(vmwobj):
    mo_type = 'GuestOperationsManager'
    pset_minimal = ['authManager', 'fileManager', 'processManager']
    pset_detail = ['authManager', 'fileManager', 'processManager']
    
def factory(obj):
    vobj = None
    if obj._type == host.mo_type:
        vobj = host(obj)
    elif obj._type == vm.mo_type:
        vobj = vm(obj)
    elif obj._type == dvpg.mo_type:
        vobj = dvpg(obj)
    elif obj._type == datacenter.mo_type:
        vobj = datacenter(obj)    
    elif obj._type == cluster.mo_type:
        vobj = cluster(obj)
    elif obj._type == ds.mo_type:
        vobj = ds(obj)
    elif obj._type == vdvsw.mo_type:
        vobj = vdvsw(obj)
    elif obj._type == hds.mo_type:
        vobj = hds(obj)
    elif obj._type == guest_operations_manager.mo_type:
        vobj = guest_operations_manager(obj)
    return vobj

