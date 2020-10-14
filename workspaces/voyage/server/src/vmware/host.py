'''
Created on Oct 14, 2011

@author: IslamM
'''

class Host:
    mo_type = 'HostSystem'
    pset_minimal = ['name', 'hardware.systemInfo.uuid']
    pset_detail = ['overallStatus', 'hardware', 'name', 'config.ipmi', 'config.network', 'config.product', 
                'config.storageDevice.hostBusAdapter', 'config.storageDevice.multipathInfo.lun',
                'vm']
    def __init__(self, obj):
        self._type = obj._type
        self.value = obj.value

    def setprop(self, psets):
        for ps in psets:
            setattr(self, ps.name, ps.val)