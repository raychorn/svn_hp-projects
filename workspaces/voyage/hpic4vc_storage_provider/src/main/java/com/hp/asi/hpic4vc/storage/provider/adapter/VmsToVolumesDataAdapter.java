package com.hp.asi.hpic4vc.storage.provider.adapter;

import com.hp.asi.hpic4vc.provider.adapter.TableDataAdapter;
import com.hp.asi.hpic4vc.provider.impl.SessionInfo.VObjectType;
import com.hp.asi.hpic4vc.provider.model.TableModel;
import com.hp.asi.hpic4vc.provider.model.TableModel.TableRow;
import com.hp.asi.hpic4vc.storage.provider.locale.I18NStorageProvider;
import com.hp.asi.ui.hpicsm.ws.data.VmsToVolumesResult;
import com.hp.asi.ui.hpicsm.ws.data.VmsToVolumesWSImpl;

public class VmsToVolumesDataAdapter extends
        TableDataAdapter<VmsToVolumesResult> {
    private static final String SERVICE_NAME = "services/swd/vmstovolumes";

    public VmsToVolumesDataAdapter () {
        super(VmsToVolumesResult.class);
    }

    @Override
    public String getServiceName () {
        return SERVICE_NAME;
    }

    @Override
    public String getErrorMsg (VmsToVolumesResult rawData) {
        return rawData.getErrorMessage();
    }

    @Override
    public boolean isResultEmpty (VmsToVolumesResult rawData) {
        if (null == rawData.getResult() || rawData.getResult().length < 1) {
            return true;
        }
        return false;
    }

    @Override
    public void setColumns (TableModel table) {
        String vmName = I18NStorageProvider
                .getInstance()
                .getInternationalString(locale,
                                        I18NStorageProvider.VmsToVolumes_VmName);
        String vmNameToolTip = I18NStorageProvider
                .getInstance()
                .getInternationalString(locale,
                                        I18NStorageProvider.VmsToVolumes_TT_VmName);
        String vmProvisionedCapacity = I18NStorageProvider
                .getInstance()
                .getInternationalString(locale,
                                        I18NStorageProvider.VmsToVolumes_VmProvisionedCapacity);
        String vmProvisionedCapacityToolTip = I18NStorageProvider
                .getInstance()
                .getInternationalString(locale,
                                        I18NStorageProvider.VmsToVolumes_TT_VmProvisionedCapacity);
        String vmAllocatedCapacity = I18NStorageProvider
                .getInstance()
                .getInternationalString(locale,
                                        I18NStorageProvider.VmsToVolumes_VmAllocatedCapacity);
        String vmAllocatedCapacityToolTip = I18NStorageProvider
                .getInstance()
                .getInternationalString(locale,
                                        I18NStorageProvider.VmsToVolumes_TT_VmAllocatedCapacity);
        String vDiskName = I18NStorageProvider
                .getInstance()
                .getInternationalString(locale,
                                        I18NStorageProvider.VmsToVolumes_VDiskName);
        String vDiskNameToolTip = I18NStorageProvider
                .getInstance()
                .getInternationalString(locale,
                                        I18NStorageProvider.VmsToVolumes_TT_VDiskName);
        String vDiskType = I18NStorageProvider
                .getInstance()
                .getInternationalString(locale,
                                        I18NStorageProvider.VmsToVolumes_VDiskType);
        String vDiskTypeToolTip = I18NStorageProvider
                .getInstance()
                .getInternationalString(locale,
                                        I18NStorageProvider.VmsToVolumes_TT_VDiskType);
        String vDiskProvisionedSize = I18NStorageProvider
                .getInstance()
                .getInternationalString(locale,
                                        I18NStorageProvider.VmsToVolumes_VDiskProvisionedSize);
        String vDiskProvisionedSizeToolTip = I18NStorageProvider
                .getInstance()
                .getInternationalString(locale,
                                        I18NStorageProvider.VmsToVolumes_TT_VDiskProvisionedSize);
        String vDiskAllocatedSize = I18NStorageProvider
                .getInstance()
                .getInternationalString(locale,
                                        I18NStorageProvider.VmsToVolumes_VDiskAllocatedSize);
        String vDiskAllocatedSizeToolTip = I18NStorageProvider
                .getInstance()
                .getInternationalString(locale,
                                        I18NStorageProvider.VmsToVolumes_TT_VDiskAllocatedSize);
        String vmwareProvisioned = I18NStorageProvider
                .getInstance()
                .getInternationalString(locale,
                                        I18NStorageProvider.VmsToVolumes_VmwareProvisioned);
        String vmwareProvisionedToolTip = I18NStorageProvider
                .getInstance()
                .getInternationalString(locale,
                                        I18NStorageProvider.VmsToVolumes_TT_VmwareProvisioned);
        String dsName = I18NStorageProvider
                .getInstance()
                .getInternationalString(locale,
                                        I18NStorageProvider.VmsToVolumes_DsName);
        String dsNameToolTip = I18NStorageProvider
                .getInstance()
                .getInternationalString(locale,
                                        I18NStorageProvider.VmsToVolumes_TT_DsName);
        String vmwareDiskIdentifier = I18NStorageProvider
                .getInstance()
                .getInternationalString(locale,
                                        I18NStorageProvider.VmsToVolumes_VmwareDiskIdentifier);
        String vmwareDiskIdentifierToolTip = I18NStorageProvider
                .getInstance()
                .getInternationalString(locale,
                                        I18NStorageProvider.VmsToVolumes_TT_VmwareDiskIdentifier);
        String arrayDiskName = I18NStorageProvider
                .getInstance()
                .getInternationalString(locale,
                                        I18NStorageProvider.VmsToVolumes_ArrayDiskName);
        String arrayDiskNameToolTip = I18NStorageProvider
                .getInstance()
                .getInternationalString(locale,
                                        I18NStorageProvider.VmsToVolumes_TT_ArrayDiskName);
        String arrayName = I18NStorageProvider
                .getInstance()
                .getInternationalString(locale,
                                        I18NStorageProvider.VmsToVolumes_ArrayName);
        String arrayNameToolTip = I18NStorageProvider
                .getInstance()
                .getInternationalString(locale,
                                        I18NStorageProvider.VmsToVolumes_TT_ArrayName);
        String arrayType = I18NStorageProvider
                .getInstance()
                .getInternationalString(locale,
                                        I18NStorageProvider.VmsToVolumes_ArrayType);
        String arrayTypeToolTip = I18NStorageProvider
                .getInstance()
                .getInternationalString(locale,
                                        I18NStorageProvider.VmsToVolumes_TT_ArrayType);
        String arrayDiskType = I18NStorageProvider
                .getInstance()
                .getInternationalString(locale,
                                        I18NStorageProvider.VmsToVolumes_ArrayDiskType);
        String arrayDiskTypeToolTip = I18NStorageProvider
                .getInstance()
                .getInternationalString(locale,
                                        I18NStorageProvider.VmsToVolumes_TT_ArrayDiskType);
        String arrayDiskTotalCapacity = I18NStorageProvider
                .getInstance()
                .getInternationalString(locale,
                                        I18NStorageProvider.VmsToVolumes_ArrayDiskTotalCapacity);
        String arrayDiskTotalCapacityToolTip = I18NStorageProvider
                .getInstance()
                .getInternationalString(locale,
                                        I18NStorageProvider.VmsToVolumes_TT_ArrayDiskTotalCapacity);
        String arrayDiskAllocatedCapacity = I18NStorageProvider
                .getInstance()
                .getInternationalString(locale,
                                        I18NStorageProvider.VmsToVolumes_ArrayDiskAllocatedCapacity);
        String arrayDiskAllocatedCapacityToolTip = I18NStorageProvider
                .getInstance()
                .getInternationalString(locale,
                                        I18NStorageProvider.VmsToVolumes_TT_ArrayDiskAllocatedCapacity);
        String arrayDiskProvisioned = I18NStorageProvider
                .getInstance()
                .getInternationalString(locale,
                                        I18NStorageProvider.VmsToVolumes_ArrayDiskProvisioned);
        String arrayDiskProvisionedToolTip = I18NStorageProvider
                .getInstance()
                .getInternationalString(locale,
                                        I18NStorageProvider.VmsToVolumes_TT_ArrayDiskProvisioned);
        String arrayController = I18NStorageProvider
                .getInstance()
                .getInternationalString(locale,
                                        I18NStorageProvider.VmsToVolumes_ArrayController);
        String arrayControllerToolTip = I18NStorageProvider
                .getInstance()
                .getInternationalString(locale,
                                        I18NStorageProvider.VmsToVolumes_TT_ArrayController);
        String arrayPortName = I18NStorageProvider
                .getInstance()
                .getInternationalString(locale,
                                        I18NStorageProvider.VmsToVolumes_ArrayPortName);
        String arrayPortNameToolTip = I18NStorageProvider
                .getInstance()
                .getInternationalString(locale,
                                        I18NStorageProvider.VmsToVolumes_TT_ArrayPortName);
        String hostGroupName = I18NStorageProvider
                .getInstance()
                .getInternationalString(locale,
                                        I18NStorageProvider.VmsToVolumes_HostGroupName);
        String hostGroupNameToolTip = I18NStorageProvider
                .getInstance()
                .getInternationalString(locale,
                                        I18NStorageProvider.VmsToVolumes_TT_HostGroupName);
        String hostAccess = I18NStorageProvider
                .getInstance()
                .getInternationalString(locale,
                                        I18NStorageProvider.VmsToVolumes_HostAccess);
        String hostAccessToolTip = I18NStorageProvider
                .getInstance()
                .getInternationalString(locale,
                                        I18NStorageProvider.VmsToVolumes_TT_HostAccess);
        String hostMode = I18NStorageProvider
                .getInstance()
                .getInternationalString(locale,
                                        I18NStorageProvider.VmsToVolumes_HostMode);
        String hostModeToolTip = I18NStorageProvider
                .getInstance()
                .getInternationalString(locale,
                                        I18NStorageProvider.VmsToVolumes_TT_HostMode);
        String hostName = I18NStorageProvider
                .getInstance()
                .getInternationalString(locale,
                                        I18NStorageProvider.VmsToVolumes_HostName);
        String hostNameToolTip = I18NStorageProvider
                .getInstance()
                .getInternationalString(locale,
                                        I18NStorageProvider.VmsToVolumes_TT_HostName);
        String currentOwner = I18NStorageProvider
                .getInstance()
                .getInternationalString(locale,
                                        I18NStorageProvider.VmsToVolumes_CurrentOwner);
        String currentOwnerToolTip = I18NStorageProvider
                .getInstance()
                .getInternationalString(locale,
                                        I18NStorageProvider.VmsToVolumes_TT_CurrentOwner);
        String hostHbaPortWWN = I18NStorageProvider
                .getInstance()
                .getInternationalString(locale,
                                        I18NStorageProvider.VmsToVolumes_HostHbaPortWWN);
        String hostHbaPortWWNToolTip = I18NStorageProvider
                .getInstance()
                .getInternationalString(locale,
                                        I18NStorageProvider.VmsToVolumes_TT_HostHbaPortWWN);
        String arrayPortWWN = I18NStorageProvider
                .getInstance()
                .getInternationalString(locale,
                                        I18NStorageProvider.VmsToVolumes_ArrayPortWWN);
        String arrayPortWWNToolTip = I18NStorageProvider
                .getInstance()
                .getInternationalString(locale,
                                        I18NStorageProvider.VmsToVolumes_TT_ArrayPortWWN);
        String lunNumber = I18NStorageProvider
                .getInstance()
                .getInternationalString(locale,
                                        I18NStorageProvider.VmsToVolumes_LUNNumber);
        String lunNumberToolTip = I18NStorageProvider
                .getInstance()
                .getInternationalString(locale,
                                        I18NStorageProvider.VmsToVolumes_TT_LUNNumber);
        String pathID = I18NStorageProvider
                .getInstance()
                .getInternationalString(locale,
                                        I18NStorageProvider.VmsToVolumes_PathID);
        String pathIDToolTip = I18NStorageProvider
                .getInstance()
                .getInternationalString(locale,
                                        I18NStorageProvider.VmsToVolumes_TT_PathID);
        String preferredPath = I18NStorageProvider
                .getInstance()
                .getInternationalString(locale,
                                        I18NStorageProvider.VmsToVolumes_PreferredPath);
        String preferredPathToolTip = I18NStorageProvider
                .getInstance()
                .getInternationalString(locale,
                                        I18NStorageProvider.VmsToVolumes_TT_PreferredPath);
        String activePath = I18NStorageProvider
                .getInstance()
                .getInternationalString(locale,
                                        I18NStorageProvider.VmsToVolumes_ActivePath);
        String activePathToolTip = I18NStorageProvider
                .getInstance()
                .getInternationalString(locale,
                                        I18NStorageProvider.VmsToVolumes_TT_ActivePath);

        table.addColumn(vmName, vmNameToolTip, 					"180", configData.getVObjectActions(VObjectType.VM, sessionInfo, false).getMenuItems(), true, false, false);
        table.addColumn(vmProvisionedCapacity,
                        vmProvisionedCapacityToolTip, 			"120", null, false, true,  false);
        table.addColumn(vmAllocatedCapacity,
                        vmAllocatedCapacityToolTip, 			"120", null, false, true,  false);
        table.addColumn(vDiskName, vDiskNameToolTip, 			"230", null, true,  false, false);
        table.addColumn(vDiskType, vDiskTypeToolTip, 			"100", null, true,  false, false);
        table.addColumn(vDiskProvisionedSize,
                        vDiskProvisionedSizeToolTip, 			"160", null, false, true,  false);
        table.addColumn(vDiskAllocatedSize,
                        vDiskAllocatedSizeToolTip,				"160", null, false, true,  false);
        table.addColumn(vmwareProvisioned,
                        vmwareProvisionedToolTip,				"120", null, false, true,  false);
        table.addColumn(dsName, dsNameToolTip, 					"160", configData.getVObjectActions(VObjectType.DATASTORE, sessionInfo, false).getMenuItems(), true, false, false);
        table.addColumn(vmwareDiskIdentifier,
	                        vmwareDiskIdentifierToolTip,		"220", null, false, false, false);
        table.addColumn(arrayDiskName, arrayDiskNameToolTip, 	"140", configData.getVolumeActions(sessionInfo).getMenuItems(), true,  false, false);
        table.addColumn(arrayName, arrayNameToolTip, 			"140", null, true,  false, false);
        table.addColumn(arrayType, arrayTypeToolTip, 			"140", null, true,  false, false);
        table.addColumn(arrayDiskType, arrayDiskTypeToolTip, 	"140", null, true,  false, false);
        table.addColumn(arrayDiskTotalCapacity,
                        arrayDiskTotalCapacityToolTip,			"120", null, true,  true,  false);
        table.addColumn(arrayDiskAllocatedCapacity,
                        arrayDiskAllocatedCapacityToolTip, 		"120", null, true,  true,  false);
        table.addColumn(arrayDiskProvisioned,
                        arrayDiskProvisionedToolTip,			"120", null, false, true,  false);
        table.addColumn(arrayController, arrayControllerToolTip,"100", null, false, false, false);
        table.addColumn(arrayPortName, 	arrayPortNameToolTip, 	"100", null, false, false, false);
        table.addColumn(hostGroupName, 	hostGroupNameToolTip, 	"160", null, false, false, false);
        table.addColumn(hostAccess, 	hostAccessToolTip, 		"100", null, false, false, false);
        table.addColumn(hostMode, 		hostModeToolTip, 		"100", null, false, false, false);
        table.addColumn(hostName, 		hostNameToolTip, 		"160", configData.getVObjectActions(VObjectType.HOST, sessionInfo, false).getMenuItems(), true, false, false);
        table.addColumn(currentOwner, 	currentOwnerToolTip, 	"90",  null, false, false, false);
        table.addColumn(hostHbaPortWWN, hostHbaPortWWNToolTip, 	"140", null, true, false, false);
        table.addColumn(arrayPortWWN, 	arrayPortWWNToolTip, 	"200", null, true, false, false);
        table.addColumn(lunNumber, 		lunNumberToolTip, 		"90",  null, false, true,  false);
        table.addColumn(pathID, 		pathIDToolTip, 			"100", null, false, false, false);
        table.addColumn(preferredPath,  preferredPathToolTip, 	"120", null, false, false, false);
        table.addColumn(activePath, 	activePathToolTip, 		"100", null, false, false, false);
    }

    @Override
    public void setRows (TableModel table, VmsToVolumesResult rawData) {
        String id = null;
        String volumeType = I18NStorageProvider.getInstance().
                getInternationalString(locale, I18NStorageProvider.DeleteVolume_Type); 
        
        for (VmsToVolumesWSImpl vmsToVolumesWSImpl : rawData.getResult()) {
            TableRow row = new TableRow();

            row.addCell(VObjectType.VM.getMoref()+":"+vmsToVolumesWSImpl.getVmIdentifier(),
                        vmsToVolumesWSImpl.getVmName(),
                        vmsToVolumesWSImpl.getFormattedVmName(), null);
            row.addCell(id,
                        vmsToVolumesWSImpl.getVmProvisionedCapacity(),
                        vmsToVolumesWSImpl.getFormattedVmProvisionedCapacity(), null);
            row.addCell(id,
                        vmsToVolumesWSImpl.getVmAllocatedCapacity(),
                        vmsToVolumesWSImpl.getFormattedVmAllocatedCapacity(), null);
            row.addCell(id,
                        vmsToVolumesWSImpl.getvDiskName(),
                        vmsToVolumesWSImpl.getFormattedvDiskName(), null);
            row.addCell(id,
                        vmsToVolumesWSImpl.getvDiskType(),
                        vmsToVolumesWSImpl.getFormattedvDiskType(), null);
            row.addCell(id,
                        vmsToVolumesWSImpl.getvDiskProvisionedSize(),
                        vmsToVolumesWSImpl.getFormattedvDiskProvisionedSize(), null);
            row.addCell(id,
                        vmsToVolumesWSImpl.getvDiskAllocatedSize(),
                        vmsToVolumesWSImpl.getFormattedvDiskAllocatedSize(), null);
            row.addCell(id,
                        vmsToVolumesWSImpl.getVmwareProvisioned(),
                        vmsToVolumesWSImpl.getFormattedVmwareProvisioned(), null);
            row.addCell(VObjectType.DATASTORE.getMoref()+":"+vmsToVolumesWSImpl.getDatastoreIdentifier(),
                        vmsToVolumesWSImpl.getDatastoreName(),
                        vmsToVolumesWSImpl.getFormattedDatastoreName(), null);
            row.addCell(id,
                        vmsToVolumesWSImpl.getVmwareDiskIdentifier(),
                        vmsToVolumesWSImpl.getFormattedVmwareDiskIdentifier(), null);
            row.addCell(volumeType+":"+vmsToVolumesWSImpl.getVmwareDiskIdentifier(),
                        vmsToVolumesWSImpl.getArrayDiskName(),
                        vmsToVolumesWSImpl.getFormattedArrayDiskName(), null);
            row.addCell(id,
                        vmsToVolumesWSImpl.getArrayName(),
                        vmsToVolumesWSImpl.getFormattedArrayName(), null);
            row.addCell(id,
                        vmsToVolumesWSImpl.getArrayType(),
                        vmsToVolumesWSImpl.getFormattedArrayType(), null);
            row.addCell(id,
                        vmsToVolumesWSImpl.getArrayDiskType(),
                        vmsToVolumesWSImpl.getFormattedArrayDiskType(), null);
            row.addCell(id,
                        vmsToVolumesWSImpl.getArrayDiskTotalCapacity(),
                        vmsToVolumesWSImpl.getFormattedArrayDiskTotalCapacity(), null);
            row.addCell(id,
                        vmsToVolumesWSImpl.getArrayDiskAlloatedCapacity(),
                        vmsToVolumesWSImpl
                                .getFormattedArrayDiskAlloatedCapacity(), null);
            row.addCell(id,
                        vmsToVolumesWSImpl.getArrayDiskProvisioned(),
                        vmsToVolumesWSImpl.getFormattedArrayDiskProvisioned(), null);
            row.addCell(id,
                        vmsToVolumesWSImpl.getArrayControllerName(),
                        vmsToVolumesWSImpl.getFormattedArrayControllerName(), null);
            row.addCell(id,
                        vmsToVolumesWSImpl.getArrayPortName(),
                        vmsToVolumesWSImpl.getFormattedArrayPortName(), null);
            row.addCell(id,
                        vmsToVolumesWSImpl.getHostGroupName(),
                        vmsToVolumesWSImpl.getFormattedHostGroupName(), null);
            row.addCell(id,
                        vmsToVolumesWSImpl.getHostAccess(),
                        vmsToVolumesWSImpl.getFormattedHostAccess(), null);
            row.addCell(id,
                        vmsToVolumesWSImpl.getHostMode(),
                        vmsToVolumesWSImpl.getFormattedHostMode(), null);
            row.addCell(VObjectType.HOST.getMoref()+":"+vmsToVolumesWSImpl.getHostIdentifier(),
                        vmsToVolumesWSImpl.getHostName(),
                        vmsToVolumesWSImpl.getFormattedHostName(), null);
            row.addCell(id,
                        vmsToVolumesWSImpl.getFormattedCurrentOwner(),
                        vmsToVolumesWSImpl.getFormattedCurrentOwner(), null);
            row.addCell(id,
                        vmsToVolumesWSImpl.getHostHbaPortWWN(),
                        vmsToVolumesWSImpl.getFormattedHostHbaPortWWN(), null);
            row.addCell(id,
                        vmsToVolumesWSImpl.getArrayPortWWN(),
                        vmsToVolumesWSImpl.getFormattedArrayPortWWN(), null);
            row.addCell(id,
                        vmsToVolumesWSImpl.getLunNumber(),
                        vmsToVolumesWSImpl.getFormattedLunNumber(), null);
            row.addCell(id,
                        vmsToVolumesWSImpl.getPathID(),
                        vmsToVolumesWSImpl.getFormattedPathID(), null);
            row.addCell(id,
                        vmsToVolumesWSImpl.getPreferredPath(),
                        vmsToVolumesWSImpl.getFormattedPreferredPath(), null);
            row.addCell(id,
                        vmsToVolumesWSImpl.getActivePath(),
                        vmsToVolumesWSImpl.getFormattedActivePath(), null);

            table.addRow(row);
        }
    }
}
