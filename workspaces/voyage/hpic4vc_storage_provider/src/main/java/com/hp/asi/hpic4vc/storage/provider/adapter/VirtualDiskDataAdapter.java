package com.hp.asi.hpic4vc.storage.provider.adapter;

import com.hp.asi.hpic4vc.provider.adapter.TableDataAdapter;
import com.hp.asi.hpic4vc.provider.impl.SessionInfo.VObjectType;
import com.hp.asi.hpic4vc.provider.model.TableModel;
import com.hp.asi.hpic4vc.provider.model.TableModel.TableRow;
import com.hp.asi.hpic4vc.storage.provider.locale.I18NStorageProvider;
import com.hp.asi.ui.hpicsm.ws.data.VirtualDiskWSImpl;
import com.hp.asi.ui.hpicsm.ws.data.VirtualDiskResult;

public class VirtualDiskDataAdapter extends TableDataAdapter<VirtualDiskResult> {
    private static final String SERVICE_NAME = "services/swd/virtualdisks";
    
    public VirtualDiskDataAdapter() {
        super(VirtualDiskResult.class);
    }
    
    @Override
    public String getServiceName () {
        return SERVICE_NAME;
    }

    @Override
    public String getErrorMsg (VirtualDiskResult rawData) {
        return rawData.getErrorMessage();
    }

    @Override
    public boolean isResultEmpty (VirtualDiskResult rawData) {
        if (null == rawData.getResult() || rawData.getResult().length < 1) {
            return true;
        }
        return false;
    }

    @Override
    public void setColumns (TableModel table) {
        String virtualDiskName                 = I18NStorageProvider.getInstance().getInternationalString(locale, I18NStorageProvider.VirtualDisk_VirtualDiskName);
        String vmfsType                        = I18NStorageProvider.getInstance().getInternationalString(locale, I18NStorageProvider.VirtualDisk_VmfsType);
        String virtualMachine                  = I18NStorageProvider.getInstance().getInternationalString(locale, I18NStorageProvider.VirtualDisk_VirtualMachine);
        String datastore                       = I18NStorageProvider.getInstance().getInternationalString(locale, I18NStorageProvider.VirtualDisk_Datastore);
        String vDiskProvisionedCapacity        = I18NStorageProvider.getInstance().getInternationalString(locale, I18NStorageProvider.VirtualDisk_VDiskProvisionedCapacity);
        String vDiskAllocatedCapacity          = I18NStorageProvider.getInstance().getInternationalString(locale, I18NStorageProvider.VirtualDisk_VDiskAllocatedCapacity);
        String vmwareProvisioned               = I18NStorageProvider.getInstance().getInternationalString(locale, I18NStorageProvider.VirtualDisk_VmwareProvisioned);
        String vDiskProvisionedCapacityToolTip = I18NStorageProvider.getInstance().getInternationalString(locale, I18NStorageProvider.VirtualDisk_TT_VDiskProvisionedCapacity);
        String vDiskAllocatedCapacityToolTip   = I18NStorageProvider.getInstance().getInternationalString(locale, I18NStorageProvider.VirtualDisk_TT_VDiskAllocatedCapacity);
        String vmwareProvisionedToolTip        = I18NStorageProvider.getInstance().getInternationalString(locale, I18NStorageProvider.VirtualDisk_TT_VmwareProvisioned);
        
        table.addColumn(virtualDiskName,          null,                            "280",   null, true, false, false);
        table.addColumn(vmfsType,                 null,                            "95",    null, true, false, false);       
        table.addColumn(virtualMachine,           null,                            "100",   configData.getVObjectActions(VObjectType.VM, sessionInfo, false).getMenuItems(), true, false, false); 
        table.addColumn(datastore,                null,                            "160",   configData.getVObjectActions(VObjectType.DATASTORE, sessionInfo, false).getMenuItems(), true, false, false);
        table.addColumn(vDiskProvisionedCapacity, vDiskProvisionedCapacityToolTip, "140",   null, true, true,  false);
        table.addColumn(vDiskAllocatedCapacity,   vDiskAllocatedCapacityToolTip,   "140",   null, true, true,  false);
        table.addColumn(vmwareProvisioned,        vmwareProvisionedToolTip,        "140",   null, true, false, false);
    }

    @Override
    public void setRows (TableModel table, VirtualDiskResult rawData) {
        String id    = null;
        
        for (VirtualDiskWSImpl virtualDiskWSImpl : rawData.getResult()) {
            TableRow row = new TableRow();
            row.addCell(id,    virtualDiskWSImpl.getVirtualDiskName(),          virtualDiskWSImpl.getFormattedVirtualDiskName(), 	      null);
            row.addCell(id,    virtualDiskWSImpl.getVMFSType(),                 virtualDiskWSImpl.getFormattedVMFSType(), 				  null);
            row.addCell(VObjectType.VM.getMoref()+":"+virtualDiskWSImpl.getVirtualMachineIdentifier(), 	   virtualDiskWSImpl.getVirtualMachine(),           virtualDiskWSImpl.getFormattedVirtualMachine());
            row.addCell(VObjectType.DATASTORE.getMoref()+":"+virtualDiskWSImpl.getDatastoreIdentifier(),      virtualDiskWSImpl.getDatastore(),                virtualDiskWSImpl.getFormattedDatastore());
            row.addCell(id,    virtualDiskWSImpl.getVDiskProvisionedCapacity(), virtualDiskWSImpl.getFormattedVDiskProvisionedCapacity(), null);
            row.addCell(id,    virtualDiskWSImpl.getVDiskAllocatedCapacity(),   virtualDiskWSImpl.getFormattedVDiskAllocatedCapacity(),   null);
            row.addCell(id,    virtualDiskWSImpl.getVMwareProvisioned(),        virtualDiskWSImpl.getFormattedVMwareProvisioned(),        null);
            table.addRow(row);
        }
        
    }
}
