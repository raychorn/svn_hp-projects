package com.hp.asi.hpic4vc.storage.provider.adapter;

import com.hp.asi.hpic4vc.provider.adapter.TableDataAdapter;
import com.hp.asi.hpic4vc.provider.impl.SessionInfo.VObjectType;
import com.hp.asi.hpic4vc.provider.locale.I18NProvider;
import com.hp.asi.hpic4vc.provider.model.TableModel;
import com.hp.asi.hpic4vc.provider.model.TableModel.TableRow;
import com.hp.asi.hpic4vc.storage.provider.locale.I18NStorageProvider;
import com.hp.asi.ui.hpicsm.ws.data.StorageVolumeResult;
import com.hp.asi.ui.hpicsm.ws.data.StorageVolumeWSImpl;

public class StorageVolumesDataAdapter extends TableDataAdapter<StorageVolumeResult> {
    private static final String SERVICE_NAME = "services/swd/storagevolumes";

    public StorageVolumesDataAdapter() {
        super(StorageVolumeResult.class);
    }


    @Override
    public String getServiceName () {
        return SERVICE_NAME;
    }
    
    @Override
    public String getErrorMsg (StorageVolumeResult rawData) {
        return rawData.getErrorMessage();
    }


    @Override
    public boolean isResultEmpty (StorageVolumeResult rawData) {
        if (null == rawData.getResult()) {
            return true;
        }
        return false;
    }

    @Override
    public void setColumns (final TableModel table) {
        I18NStorageProvider i18nProvider = I18NStorageProvider.getInstance();
        String arrayDiskName         = i18nProvider.getInternationalString(locale, I18NStorageProvider.SVols_ArrayDiskName);
        String arrayName             = i18nProvider.getInternationalString(locale, I18NStorageProvider.SVols_ArrayName);
        String arrayType             = i18nProvider.getInternationalString(locale, I18NStorageProvider.SVols_ArrayType);
        String dsRdm                 = i18nProvider.getInternationalString(locale, I18NStorageProvider.SVols_DsRdm);
        String type                  = i18nProvider.getInternationalString(locale, I18NStorageProvider.SVols_Type);
        String paths                 = i18nProvider.getInternationalString(locale, I18NStorageProvider.SVols_Paths);
        String vmDiskId              = i18nProvider.getInternationalString(locale, I18NStorageProvider.SVols_VmDiskId);
        String pathId                = i18nProvider.getInternationalString(locale, I18NStorageProvider.SVols_PathId);
        String arrayDiskRaid         = i18nProvider.getInternationalString(locale, I18NStorageProvider.SVols_ArrayDiskRaid);
        String arrayDiskTotal        = i18nProvider.getInternationalString(locale, I18NStorageProvider.SVols_ArrayDiskTotalCap);
        String arrayDiskAlloc        = i18nProvider.getInternationalString(locale, I18NStorageProvider.SVols_ArrayDiskAllocCap);
        String arrayDiskProv         = i18nProvider.getInternationalString(locale, I18NStorageProvider.SVols_ArrayDiskProv);
        String arrayDiskType         = i18nProvider.getInternationalString(locale, I18NStorageProvider.SVols_ArrayDiskType);
        String storagePool           = i18nProvider.getInternationalString(locale, I18NStorageProvider.SVols_StoragePool);
        String storagePoolAlloc      = i18nProvider.getInternationalString(locale, I18NStorageProvider.SVols_StoragePoolAlloc);
        String storagePoolDomainName = i18nProvider.getInternationalString(locale, I18NStorageProvider.SVols_StoragePoolDomainName);
        String copyPoolName          = i18nProvider.getInternationalString(locale, I18NStorageProvider.SVols_CopyPoolName);
        String arrayDiskEmType       = i18nProvider.getInternationalString(locale, I18NStorageProvider.SVols_ArrayDiskEmType);
        String replicated            = i18nProvider.getInternationalString(locale, I18NStorageProvider.SVols_Replicated);
        String physicalDeviceType    = i18nProvider.getInternationalString(locale, I18NStorageProvider.SVols_PhysicalDeviceType);
        String zeroDetection         = i18nProvider.getInternationalString(locale, I18NStorageProvider.SVols_ZeroDetection);
        String ttDiskName            = i18nProvider.getInternationalString(locale, I18NStorageProvider.SVols_TT_ArrayDiskName);
        String ttArrayName           = i18nProvider.getInternationalString(locale, I18NStorageProvider.SVols_TT_ArrayName);
        String ttDiskTotal           = i18nProvider.getInternationalString(locale, I18NStorageProvider.SVols_TT_ArrayDiskTotalCap);
        String ttDiskAlloc           = i18nProvider.getInternationalString(locale, I18NStorageProvider.SVols_TT_ArrayDiskAllocCap);
        String ttDiskProv            = i18nProvider.getInternationalString(locale, I18NStorageProvider.SVols_TT_ArrayDiskProv);
        String ttStoragePool         = i18nProvider.getInternationalString(locale, I18NStorageProvider.SVols_TT_StoragePool);
        String ttReplicated          = i18nProvider.getInternationalString(locale, I18NStorageProvider.SVols_TT_Replicated);
        String volumeId              = i18nProvider.getInternationalString(locale, I18NStorageProvider.SVols_Volume_Id);
        String volumeStatus          = i18nProvider.getInternationalString(locale, I18NStorageProvider.SVols_Volume_Status);
        String driveSpeed            = i18nProvider.getInternationalString(locale, I18NStorageProvider.SVols_Drive_Speed);
        String creationTime          = i18nProvider.getInternationalString(locale, I18NStorageProvider.SVols_Creation_Time);
        String expireTime            = i18nProvider.getInternationalString(locale, I18NStorageProvider.SVols_Expiration_Time);
        String retentionTime         = i18nProvider.getInternationalString(locale, I18NStorageProvider.SVols_Retention_Time);
        String userSpaceWarningPercentage 	= i18nProvider.getInternationalString(locale, I18NStorageProvider.SVols_UserSpace_Warning);
        String userSpaceLimitPercentage   	= i18nProvider.getInternationalString(locale, I18NStorageProvider.SVols_UserSpace_Limit);
        String copySpaceWarningPercentage 	= i18nProvider.getInternationalString(locale, I18NStorageProvider.SVols_CopySpace_Warning);
        String copySpaceLimitPercentage   	= i18nProvider.getInternationalString(locale, I18NStorageProvider.SVols_CopySpace_Limit);
        String ttVolumeId              	  	= i18nProvider.getInternationalString(locale, I18NStorageProvider.SVols_TT_Volume_Id);
        String ttVolumeStatus          	  	= i18nProvider.getInternationalString(locale, I18NStorageProvider.SVols_TT_Volume_Status);
        String ttDriveSpeed               	= i18nProvider.getInternationalString(locale, I18NStorageProvider.SVols_TT_Drive_Speed);
        String ttCreationTime 		 	  	= i18nProvider.getInternationalString(locale, I18NStorageProvider.SVols_TT_CreationTime);
        String ttRetentionTime 		 	  	= i18nProvider.getInternationalString(locale, I18NStorageProvider.SVols_TT_RetentionTime);
        String ttExpirationTime 			= i18nProvider.getInternationalString(locale, I18NStorageProvider.SVols_TT_ExpireTime);
        String ttUserSpaceWarningPercentage = i18nProvider.getInternationalString(locale, I18NStorageProvider.SVols_TT_UserSpace_Warning);
        String ttUserSpaceLimitPercentage   = i18nProvider.getInternationalString(locale, I18NStorageProvider.SVols_TT_UserSpace_Limit);
        String ttCopySpaceWarningPercentage = i18nProvider.getInternationalString(locale, I18NStorageProvider.SVols_TT_CopySpace_Warning);
        String ttCopySpaceLimitPercentage   = i18nProvider.getInternationalString(locale, I18NStorageProvider.SVols_TT_CopySpace_Limit);
        String ttStoragePoolDName 			= i18nProvider.getInternationalString(locale, I18NStorageProvider.SVols_TT_StoragePoolDomainName);
        String ttCopyPoolName          		= i18nProvider.getInternationalString(locale, I18NStorageProvider.SVols_TT_CopyPoolName);
        String ttZeroDetection        		= i18nProvider.getInternationalString(locale, I18NStorageProvider.SVols_TT_ZeroDetection);
        
        table.addColumn(arrayDiskName,         ttDiskName ,   		"140", configData.getVolumeActions(sessionInfo).getMenuItems(), true, false, false);
        table.addColumn(arrayName,             ttArrayName,   		"100", null, true, false, false);       
        table.addColumn(arrayType,             null,          		"100", null, true, false, false); 
        table.addColumn(dsRdm,                 null,          		"140", configData.getVObjectActions(VObjectType.DATASTORE, sessionInfo, false).getMenuItems(), true, false, false);
        table.addColumn(type,                  null,          		"70",  null, true, false, false);
        table.addColumn(paths,                 null,          		"50",  null, true, true,  false);
        table.addColumn(vmDiskId,              null,          		"270", null, true, false, false);
        table.addColumn(pathId,                null,          		"150", null, true, false, false);
        table.addColumn(arrayDiskRaid,         null,          		"100", null, true, false, false);
        table.addColumn(arrayDiskTotal,        ttDiskTotal,   		"150", null, true, true,  false);
        table.addColumn(arrayDiskAlloc,        ttDiskAlloc,   		"150", null, true, true,  false);
        table.addColumn(arrayDiskProv,         ttDiskProv,    		"130", null, true, false, false);
        table.addColumn(arrayDiskType,         null,          		"130", null, true, false, false);
        table.addColumn(storagePool,           ttStoragePool, 		"140", null, true, false, false);
        table.addColumn(storagePoolAlloc,      null,          		"190", null, true, true,  false);
        table.addColumn(storagePoolDomainName, ttStoragePoolDName, 	"210", null, true, false, false);
        table.addColumn(copyPoolName,          ttCopyPoolName,      "210", null, true, false, false);
        table.addColumn(arrayDiskEmType,       null,          		"170", null, true, false, false);
        table.addColumn(replicated,            ttReplicated, 		"70",  null, true, false, false);
        table.addColumn(physicalDeviceType,    null,         		"130", null, true, false, false);
        table.addColumn(zeroDetection,         ttZeroDetection,     "100", null, true, false, false);
        table.addColumn(volumeId,              ttVolumeId,          "120", null, true, true,  false);
        table.addColumn(volumeStatus,          ttVolumeStatus,      "120", null, true, false, false);
        table.addColumn(driveSpeed,            ttDriveSpeed,        "120", null, true, true,  false);
        table.addColumn(creationTime,          ttCreationTime,		"120", null, true, true,  false);
        table.addColumn(expireTime,            ttExpirationTime,    "120", null, true, true,  false);
        table.addColumn(retentionTime,         ttRetentionTime,     "120", null, true, true,  false);
        table.addColumn(userSpaceWarningPercentage, ttUserSpaceWarningPercentage, 	"120", null, true, true,  false);
        table.addColumn(userSpaceLimitPercentage,   ttUserSpaceLimitPercentage,     "120", null, true, true,  false);
        table.addColumn(copySpaceWarningPercentage, ttCopySpaceWarningPercentage,   "120", null, true, true,  false);
        table.addColumn(copySpaceLimitPercentage,   ttCopySpaceLimitPercentage,    	"120", null, true, true,  false);
    }

    @Override
    public void setRows (final TableModel table, final StorageVolumeResult rawData) {
        String id = null;
        String volumeType = I18NStorageProvider.getInstance().
                getInternationalString(locale, I18NStorageProvider.DeleteVolume_Type);
        String creationTimeStamp;
        String expirationTimeStamp;
        String retentionTimeStamp;
        String volumeId;

        for (StorageVolumeWSImpl svSwImpl : rawData.getResult()) {
            volumeId          = getVolumeId(volumeType, svSwImpl.getVMDiskName());
            creationTimeStamp = I18NProvider.getInstance().getLocaleDateFromEpochTime
                    (svSwImpl.getCreationTime(), locale);
            expirationTimeStamp = I18NProvider.getInstance().getLocaleDateFromEpochTime
                    (svSwImpl.getExpirationTime(), locale);
            retentionTimeStamp = I18NProvider.getInstance().getLocaleDateFromEpochTime
                    (svSwImpl.getRetentionTime(), locale);
            
            TableRow row = new TableRow();
            row.addCell(volumeId, svSwImpl.getArrayDiskName(),
                        svSwImpl.getFormattedArrayDiskName(), null);
            row.addCell(id, svSwImpl.getArrayName(),
                        svSwImpl.getFormattedArrayName(), null);
            row.addCell(id, svSwImpl.getFormattedArrayType(), 
                        svSwImpl.getFormattedArrayType(), null);
            row.addCell(VObjectType.DATASTORE.getMoref()+":"+svSwImpl.getDatastoreID(), svSwImpl.getDatastoreOrRDMName(),  
                        svSwImpl.getFormattedDatastoreOrRDMName(), null);
            row.addCell(id, svSwImpl.getVMDiskType(), 
                        svSwImpl.getFormattedVMDiskType(), null);
            row.addCell(id, svSwImpl.getVMNumberOfPaths(), 
                        svSwImpl.getFormattedVMNumberOfPaths(), null);
            row.addCell(id, svSwImpl.getVMDiskName(), 
                        svSwImpl.getFormattedVMDiskName(), null);
            row.addCell(id, svSwImpl.getPathID(), 
                        svSwImpl.getFormattedPathID(), null);
            row.addCell(id, svSwImpl.getArrayDiskRaidLevel(),
                        svSwImpl.getFormattedArrayDiskRaidLevel(), null);
            row.addCell(id, svSwImpl.getArrayDiskTotalCapacity(),
                        svSwImpl.getFormattedArrayDiskTotalCapacity(), null);
            row.addCell(id, svSwImpl.getArrayDiskAllocatedCapacity(),
                        svSwImpl.getFormattedArrayDiskAllocatedCapacity(), null);
            row.addCell(id, svSwImpl.getArrayDiskProvisioned(),
                        svSwImpl.getFormattedArrayDiskProvisioned(), null);
            row.addCell(id, svSwImpl.getArrayDiskType(),
                        svSwImpl.getFormattedArrayDiskType(), null);
            row.addCell(id, svSwImpl.getStoragePoolName(),
                        svSwImpl.getFormattedStoragePoolName(), null);
            row.addCell(id, svSwImpl.getStoragePoolAllocatedCapacity(),
                        svSwImpl.getFormattedStoragePoolAllocatedCapacity(), null);
            row.addCell(id, svSwImpl.getStoragePoolDomainName(),
                        svSwImpl.getFormattedStoragePoolDomainName(), null);
            row.addCell(id, svSwImpl.getCopyPoolName(),
                        svSwImpl.getFormattedCopyPoolName(), null);
            row.addCell(id, svSwImpl.getArrayDiskEmulationType(),
                        svSwImpl.getFormattedArrayDiskEmulationType(), null);
            row.addCell(id, svSwImpl.getFormattedIsReplicated(),
                        svSwImpl.getFormattedIsReplicated(), null);
            row.addCell(id, svSwImpl.getPhysicalDeviceType(),
                        svSwImpl.getFormattedPhysicalDeviceType(), null);
            row.addCell(id, svSwImpl.getZeroDetection(),
                        svSwImpl.getFormattedZeroDetection(), null);
            row.addCell(id, svSwImpl.getVolumeID(), 
                        svSwImpl.getFormattedVolumeID(), null);
            row.addCell(id, svSwImpl.getVolumeHealth(), 
                        svSwImpl.getFormattedVolumeHealth(), null);
            row.addCell(id, svSwImpl.getPhysicalDeviceSpeed(), 
                        svSwImpl.getFormattedPhysicalDeviceSpeed(), null);
            row.addCell(id, svSwImpl.getCreationTime(),   creationTimeStamp,   null);
            row.addCell(id, svSwImpl.getExpirationTime(), expirationTimeStamp, null);
            row.addCell(id, svSwImpl.getRetentionTime(),  retentionTimeStamp,  null);
            row.addCell(id, svSwImpl.getUserSpaceWarningPercentage(), 
                        svSwImpl.getFormattedUserSpaceWarningPercentage(), null);
            row.addCell(id, svSwImpl.getUserSpaceLimitPercentage(), 
                        svSwImpl.getFormattedUserSpaceLimitPercentage(), null);
            row.addCell(id, svSwImpl.getCopySpaceWarningPercentage(), 
                        svSwImpl.getFormattedCopySpaceWarningPercentage(), null);
            row.addCell(id, svSwImpl.getCopySpaceLimitPercentage(), 
                        svSwImpl.getFormattedCopySpaceLimitPercentage(), null);
            table.addRow(row);
        }
        
    }


    private String getVolumeId (final String volumeType,
                                final String vmDiskName) {
        String displayId  = volumeType + ":";
        if (null != vmDiskName) {
            displayId += vmDiskName;
        }
        
        return displayId;
    }
}
