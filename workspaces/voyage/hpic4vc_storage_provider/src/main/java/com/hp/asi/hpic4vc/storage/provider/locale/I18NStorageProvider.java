package com.hp.asi.hpic4vc.storage.provider.locale;

import java.util.Locale;
import java.util.ResourceBundle;

import com.hp.asi.hpic4vc.provider.locale.I18NCommon;

/**
 * Singleton that provides internationalized strings for HPIC4VC Provider.
 * Typical usage will be similar to:
 * <code>I18NStorageProvider.getInstance().getInternationalString(locale, I18NStorageProvider.SOME_CONSTANT)</code>
 * 
 * @author Andrew Khoury
 * 
 */
public class I18NStorageProvider extends I18NCommon {

    public static final String Info_NotAvailable           = "Info.NotAvailable";
    public static final String Hbas_HostNames              = "Hbas.HostNames";
    public static final String Hbas_Type                   = "Hbas.Type";
    public static final String Hbas_Id                     = "Hbas.Id";
    public static final String Hbas_Wwn                    = "Hbas.Wwn";
    public static final String Hbas_Label                  = "Hbas.Label";
    public static final String Hbas_TT_Type                = "Hbas.TT.Type";
    public static final String Hbas_TT_Id                  = "Hbas.TT.Id";

    public static final String Paths_ArrayDiskName         = "Paths.ArrayDiskName";
    public static final String Paths_VMwareDiskId          = "Paths.VMwareDiskId";
    public static final String Paths_ArrayName             = "Paths.ArrayName";
    public static final String Paths_Host                  = "Paths.Host";
    public static final String Paths_HostHBAPortWWN        = "Paths.HostHBAPortWWN";
    public static final String Paths_ArrayPortWWN          = "Paths.ArrayPortWWN";
    public static final String Paths_LUN                   = "Paths.LUN";
    public static final String Paths_ArrayType             = "Paths.ArrayType";
    public static final String Paths_ArrayController       = "Paths.ArrayController";
    public static final String Paths_ArrayPort             = "Paths.ArrayPort";
    public static final String Paths_HostGroup             = "Paths.HostGroup";
    public static final String Paths_HostAccess            = "Paths.HostAccess";
    public static final String Paths_ArrayPortSpeed        = "Paths.ArrayPortSpeed";
    public static final String Paths_HostMode              = "Paths.HostMode";
    public static final String Paths_PreferredPath         = "Paths.PreferredPath";
    public static final String Paths_ActivePath            = "Paths.ActivePath";
    public static final String Paths_PathId                = "Paths.PathId";
    public static final String Paths_TT_ArrayDiskName      = "Paths.TT.ArrayDiskName";
    public static final String Paths_TT_ArrayName          = "Paths.TT.ArrayName";
    public static final String Paths_TT_Host               = "Paths.TT.Host";
    public static final String Paths_TT_ArrayController    = "Paths.TT.ArrayController";
    public static final String Paths_TT_ArrayPort          = "Paths.TT.ArrayPort";
    public static final String Paths_TT_HostGroup          = "Paths.TT.HostGroup";
    public static final String Paths_TT_HostAccess         = "Paths.TT.HostAccess";
    public static final String Paths_TT_PreferredPath      = "Paths.TT.PreferredPath";
    public static final String Paths_TT_ActivePath         = "Paths.TT.ActivePath";
    public static final String Paths_TT_PathId             = "Paths.TT.PathId";

    public static final String Replications_DiskName       = "Replications.DiskName";
    public static final String Replications_ArrayName      = "Replications.ArrayName";
    public static final String Replications_ReplicaType    = "Replications.ReplicaType";
    public static final String Replications_ReplicaName    = "Replications.ReplicaName";
    public static final String Replications_ReplicaArray   = "Replications.ReplicaArray";
    public static final String Replications_ReplicaStatus  = "Replications.ReplicaStatus";
    public static final String Replications_ReplicaGroup   = "Replications.ReplicaGroup";
    public static final String Replications_DatastoreOrRDMType = "Replications.DatastoreOrRDMType";
    public static final String Replications_DatastoreName  = "Replications.DatastoreName";
    public static final String Replications_CreationTime = "Replications.CreationTime";
    public static final String Replications_ExpireTime = "Replications.ExpireTime";
    public static final String Replications_RetentionTime = "Replications.RetentionTime";
    
    public static final String Replications_TT_DiskName    = "Replications.TT.DiskName";
    public static final String Replications_TT_ArrayName   = "Replications.TT.ArrayName";
    public static final String Replications_TT_ReplicaType = "Replications.TT.ReplicaType";
    public static final String Replications_TT_ReplicaName = "Replications.TT.ReplicaName";
    public static final String Replications_TT_ReplicaArray = "Replications.TT.ReplicaArray";
    public static final String Replications_TT_ReplicaStatus = "Replications.TT.ReplicaStatus";
    public static final String Replications_TT_ReplicaGroup = "Replications.TT.ReplicaGroup";
    public static final String Replications_TT_DatastoreOrRDMType = "Replications.TT.DatastoreOrRDMType";
    public static final String Replications_TT_DatastoreName = "Replications.TT.DatastoreName";
    public static final String Replications_TT_CreationTime = "Replications.TT.CreationTime";
    public static final String Replications_TT_ExpireTime = "Replications.TT.ExpireTime";
    public static final String Replications_TT_RetentionTime = "Replications.TT.RetentionTime";

    public static final String SVols_ArrayDiskName         = "SVols.ArrayDiskName";
    public static final String SVols_ArrayName             = "SVols.ArrayName";
    public static final String SVols_ArrayType             = "SVols.ArrayType";
    public static final String SVols_DsRdm                 = "SVols.DsRdm";
    public static final String SVols_Type                  = "SVols.Type";
    public static final String SVols_Paths                 = "SVols.Paths";
    public static final String SVols_VmDiskId              = "SVols.VmDiskId";
    public static final String SVols_PathId                = "SVols.PathId";
    public static final String SVols_ArrayDiskRaid         = "SVols.ArrayDiskRaid";
    public static final String SVols_ArrayDiskTotalCap     = "SVols.ArrayDiskTotalCap"; 
    public static final String SVols_ArrayDiskAllocCap     = "SVols.ArrayDiskAllocCap";
    public static final String SVols_ArrayDiskProv         = "SVols.ArrayDiskProv";
    public static final String SVols_ArrayDiskType         = "SVols.ArrayDiskType";
    public static final String SVols_StoragePool           = "SVols.StoragePoolName";
    public static final String SVols_StoragePoolAlloc      = "SVols.StoragePoolAlloc";
    public static final String SVols_StoragePoolDomainName = "SVols.StoragePoolDomainName";
    public static final String SVols_CopyPoolName          = "SVols.CopyPoolName";
    public static final String SVols_ArrayDiskEmType       = "SVols.ArrayDiskEmType";
    public static final String SVols_Replicated            = "SVols.Replicated";
    public static final String SVols_PhysicalDeviceType    = "SVols.PhysicalDeviceType";
    public static final String SVols_ZeroDetection         = "SVols.ZeroDetection";
    public static final String SVols_TT_ArrayDiskName      = "SVols.TT.ArrayDiskName";
    public static final String SVols_TT_ArrayName          = "SVols.TT.ArrayName";
    public static final String SVols_TT_ArrayDiskTotalCap  = "SVols.TT.ArrayDiskTotalCap";
    public static final String SVols_TT_ArrayDiskAllocCap  = "SVols.TT.ArrayDiskAllocCap";
    public static final String SVols_TT_ArrayDiskProv      = "SVols.TT.ArrayDiskProv";
    public static final String SVols_TT_StoragePool        = "SVols.TT.StoragePool";
    public static final String SVols_TT_Replicated         = "SVols.TT.Replicated";
    public static final String SVols_Volume_Id             = "SVols.VolumeId";
    public static final String SVols_Volume_Status         = "SVols.VolumeStatus";
    public static final String SVols_Drive_Speed           = "SVols.DriveSpeed";
    public static final String SVols_Creation_Time         = "SVols.CreationTime";
    public static final String SVols_Expiration_Time       = "SVols.ExpirationTime";
    public static final String SVols_Retention_Time        = "SVols.RetentionTime";
    public static final String SVols_UserSpace_Warning     = "SVols.UserSpaceWarning";
    public static final String SVols_UserSpace_Limit       = "SVols.UserSpaceLimit";
    public static final String SVols_CopySpace_Warning     = "SVols.CopySpaceWarning";
    public static final String SVols_CopySpace_Limit       = "SVols.CopySpaceLimit";
    public static final String SVols_TT_StoragePoolDomainName = "SVols.TT.StoragePoolDomainName";
    public static final String SVols_TT_CopyPoolName          = "SVols.TT.CopyPoolName";
    public static final String SVols_TT_Volume_Id             = "SVols.TT.VolumeId";
    public static final String SVols_TT_Volume_Status         = "SVols.TT.VolumeStatus";
    public static final String SVols_TT_Drive_Speed           = "SVols.TT.DriveSpeed";
    public static final String SVols_TT_CreationTime          = "SVols.TT.CreationTime";
    public static final String SVols_TT_ExpireTime        	  = "SVols.TT.ExpireTime";
    public static final String SVols_TT_RetentionTime         = "SVols.TT.RetentionTime";
    public static final String SVols_TT_UserSpace_Warning     = "SVols.TT.UserSpaceWarning";
    public static final String SVols_TT_UserSpace_Limit       = "SVols.TT.UserSpaceLimit";
    public static final String SVols_TT_CopySpace_Warning     = "SVols.TT.CopySpaceWarning";
    public static final String SVols_TT_CopySpace_Limit       = "SVols.TT.CopySpaceLimit";
    public static final String SVols_TT_ZeroDetection         = "SVols.TT.ZeroDetection";

    
    public static final String VmsToVolumes_VmName         = "VmsToVolumes.VmName";
    public static final String VmsToVolumes_VmProvisionedCapacity = "VmsToVolumes.VmProvisionedCapacity";
    public static final String VmsToVolumes_VmAllocatedCapacity = "VmsToVolumes.VmAllocatedCapacity";
    public static final String VmsToVolumes_VDiskName      = "VmsToVolumes.VDiskName";
    public static final String VmsToVolumes_VDiskType      = "VmsToVolumes.VDiskType";
    public static final String VmsToVolumes_VDiskProvisionedSize = "VmsToVolumes.VDiskProvisionedSize";
    public static final String VmsToVolumes_VDiskAllocatedSize = "VmsToVolumes.VDiskAllocatedSize";
    public static final String VmsToVolumes_VmwareProvisioned = "VmsToVolumes.VmwareProvisioned";
    public static final String VmsToVolumes_DsName         = "VmsToVolumes.DsName";
    public static final String VmsToVolumes_VmwareDiskIdentifier = "VmsToVolumes.VmwareDiskIdentifier";
    public static final String VmsToVolumes_ArrayDiskName  = "VmsToVolumes.ArrayDiskName";
    public static final String VmsToVolumes_ArrayName      = "VmsToVolumes.ArrayName";
    public static final String VmsToVolumes_ArrayType      = "VmsToVolumes.ArrayType";
    public static final String VmsToVolumes_ArrayDiskType  = "VmsToVolumes.ArrayDiskType";
    public static final String VmsToVolumes_ArrayDiskTotalCapacity = "VmsToVolumes.ArrayDiskTotalCapacity";
    public static final String VmsToVolumes_ArrayDiskAllocatedCapacity = "VmsToVolumes.ArrayDiskAllocatedCapacity";
    public static final String VmsToVolumes_ArrayDiskProvisioned = "VmsToVolumes.ArrayDiskProvisioned";
    public static final String VmsToVolumes_ArrayController = "VmsToVolumes.ArrayController";
    public static final String VmsToVolumes_ArrayPortName  = "VmsToVolumes.ArrayPortName";
    public static final String VmsToVolumes_HostGroupName  = "VmsToVolumes.HostGroupName";
    public static final String VmsToVolumes_HostAccess     = "VmsToVolumes.HostAccess";
    public static final String VmsToVolumes_HostMode       = "VmsToVolumes.HostMode";
    public static final String VmsToVolumes_HostName       = "VmsToVolumes.HostName";
    public static final String VmsToVolumes_CurrentOwner   = "VmsToVolumes.CurrentOwner";
    public static final String VmsToVolumes_HostHbaPortWWN = "VmsToVolumes.HostHbaPortWWN";
    public static final String VmsToVolumes_ArrayPortWWN   = "VmsToVolumes.ArrayPortWWN";
    public static final String VmsToVolumes_LUNNumber      = "VmsToVolumes.LUNNumber";
    public static final String VmsToVolumes_PathID         = "VmsToVolumes.PathID";
    public static final String VmsToVolumes_PreferredPath  = "VmsToVolumes.PreferredPath";
    public static final String VmsToVolumes_ActivePath     = "VmsToVolumes.ActivePath";
    public static final String VmsToVolumes_TT_VmName      = "VmsToVolumes.TT.VmName";
    public static final String VmsToVolumes_TT_VmProvisionedCapacity = "VmsToVolumes.TT.VmProvisionedCapacity";
    public static final String VmsToVolumes_TT_VmAllocatedCapacity = "VmsToVolumes.TT.VmAllocatedCapacity";
    public static final String VmsToVolumes_TT_VDiskName   = "VmsToVolumes.TT.VDiskName";
    public static final String VmsToVolumes_TT_VDiskType   = "VmsToVolumes.TT.VDiskType";
    public static final String VmsToVolumes_TT_VDiskProvisionedSize = "VmsToVolumes.TT.VDiskProvisionedSize";
    public static final String VmsToVolumes_TT_VDiskAllocatedSize = "VmsToVolumes.TT.VDiskAllocatedSize";
    public static final String VmsToVolumes_TT_VmwareProvisioned = "VmsToVolumes.TT.VmwareProvisioned";
    public static final String VmsToVolumes_TT_DsName      = "VmsToVolumes.TT.DsName";
    public static final String VmsToVolumes_TT_VmwareDiskIdentifier = "VmsToVolumes.TT.VmwareDiskIdentifier";
    public static final String VmsToVolumes_TT_ArrayDiskName = "VmsToVolumes.TT.ArrayDiskName";
    public static final String VmsToVolumes_TT_ArrayName   = "VmsToVolumes.TT.ArrayName";
    public static final String VmsToVolumes_TT_ArrayType   = "VmsToVolumes.TT.ArrayType";
    public static final String VmsToVolumes_TT_ArrayDiskType = "VmsToVolumes.TT.ArrayDiskType";
    public static final String VmsToVolumes_TT_ArrayDiskTotalCapacity = "VmsToVolumes.TT.ArrayDiskTotalCapacity";
    public static final String VmsToVolumes_TT_ArrayDiskAllocatedCapacity = "VmsToVolumes.TT.ArrayDiskAllocatedCapacity";
    public static final String VmsToVolumes_TT_ArrayDiskProvisioned = "VmsToVolumes.TT.ArrayDiskProvisioned";
    public static final String VmsToVolumes_TT_ArrayController = "VmsToVolumes.TT.ArrayController";
    public static final String VmsToVolumes_TT_ArrayPortName = "VmsToVolumes.TT.ArrayPortName";
    public static final String VmsToVolumes_TT_HostGroupName = "VmsToVolumes.TT.HostGroupName";
    public static final String VmsToVolumes_TT_HostAccess  = "VmsToVolumes.TT.HostAccess";
    public static final String VmsToVolumes_TT_HostMode    = "VmsToVolumes.TT.HostMode";
    public static final String VmsToVolumes_TT_HostName    = "VmsToVolumes.TT.HostName";
    public static final String VmsToVolumes_TT_CurrentOwner = "VmsToVolumes.TT.CurrentOwner";
    public static final String VmsToVolumes_TT_HostHbaPortWWN = "VmsToVolumes.TT.HostHbaPortWWN";
    public static final String VmsToVolumes_TT_ArrayPortWWN = "VmsToVolumes.TT.ArrayPortWWN";
    public static final String VmsToVolumes_TT_LUNNumber    = "VmsToVolumes.TT.LUNNumber";
    public static final String VmsToVolumes_TT_PathID       = "VmsToVolumes.TT.PathID";
    public static final String VmsToVolumes_TT_PreferredPath = "VmsToVolumes.TT.PreferredPath";
    public static final String VmsToVolumes_TT_ActivePath  = "VmsToVolumes.TT.ActivePath";

    public static final String VirtualDisk_VirtualDiskName = "VirtualDisk.VirtualDiskName";
    public static final String VirtualDisk_VmfsType        = "VirtualDisk.VmfsType";
    public static final String VirtualDisk_VirtualMachine  = "VirtualDisk.VirtualMachine";
    public static final String VirtualDisk_Datastore       = "VirtualDisk.Datastore";
    public static final String VirtualDisk_VDiskProvisionedCapacity = "VirtualDisk.VDiskProvisionedCapacity";
    public static final String VirtualDisk_VDiskAllocatedCapacity = "VirtualDisk.VDiskAllocatedCapacity";
    public static final String VirtualDisk_VmwareProvisioned = "VirtualDisk.VmwareProvisioned";
    public static final String VirtualDisk_TT_VDiskProvisionedCapacity = "VirtualDisk.TT.VDiskProvisionedCapacity";
    public static final String VirtualDisk_TT_VDiskAllocatedCapacity = "VirtualDisk.TT.VDiskAllocatedCapacity";
    public static final String VirtualDisk_TT_VmwareProvisioned = "VirtualDisk.TT.VmwareProvisioned";

    public static final String Portlet_VolumesLabel        = "Portlet.VolumesLabel";
    public static final String Portlet_Volumes             = "Portlet.Volumes";
    public static final String Portlet_Thinprovisioned     = "Portlet.Thinprovisioned";
    public static final String Portlet_Overprovisioned     = "Portlet.Overprovisioned";
    public static final String Portlet_NoResult            = "Portlet.NoResult";

    public static final String SO_NullResult               = "SO.NullResult";
    public static final String SO_NoResults                = "SO.NoResults";

    public static final String FS_ARRAYS                   = "FS.ARRAYS";
    public static final String FS_BACKUP_SYSTEMS           = "FS.BACKUP_SYSTEMS";
    public static final String FS_DATASTORES               = "FS.DATASTORES";
    public static final String FS_NO_ARRAYS                = "FS.NO_ARRAYS";
    public static final String FS_NO_BACKUP_SYSTEMS        = "FS.NO_BACKUP_SYSTEMS";
    
    public static final String DAM_SSO_FIRMWARE              = "DAM.SSO.Firmware";
    public static final String DAM_SSO_MANAGEMENT_GROUP      = "DAM.SSO.ManagementGroup";
    public static final String DAM_SSO_MODEL                 = "DAM.SSO.Model";
    public static final String DAM_SSO_PROVISIONED           = "DAM.SSO.Provisioned";
    public static final String DAM_SSO_THIN_PROVISIONED      = "DAM.SSO.ThinProvisioned";
    public static final String DAM_SSO_SAVINGS               = "DAM.SSO.Savings";
    public static final String DAM_SSO_SERIAL_NUMBER         = "DAM.SSO.SerialNumber";
    public static final String DAM_SSO_SYSTEM_ID             = "DAM.SSO.SystemId";
    public static final String DAM_SSO_SYSTEM_NUMBER         = "DAM.SSO.SystemNumber";
    public static final String DAM_SSO_SYSTEM_WWN            = "DAM.SSO.SystemWWN";    
    public static final String DAM_SSO_USED                  = "DAM.SSO.Used";
    public static final String DAM_SSO_USED_THIN_PROVISIONED = "DAM.SSO.UsedThinProvisioned";
    
    public static final String DAM_SSD_TITLE                    = "DAM.SSD.Title";
    public static final String DAM_SSD_CONTROLLER_NODES         = "DAM.SSD.ControllerNodes";
    public static final String DAM_SSD_STATUS                   = "DAM.SSD.Status";
    public static final String DAM_SSD_EVENT_STATUS             = "DAM.SSD.EventStatus";
    public static final String DAM_SSD_USER                     = "DAM.SSD.User";
    public static final String DAM_SSD_DEDUP_RATIO              = "DAM.SSD.DedupRatio";
    public static final String DAM_SSD_IP_ADDRESS               = "DAM.SSD.IpAddress";
    public static final String DAM_SSD_TOTAL_CAPACITY           = "DAM.SSD.TotalCapacity";
    public static final String DAM_SSD_FREE_SPACE               = "DAM.SSD.FreeSpace";
    public static final String DAM_SSD_USER_DATA_STORED         = "DAM.SSD.UserDataStored";
    public static final String DAM_SSD_SIZE_ON_DISK             = "DAM.SSD.SizeOnDisk";
    public static final String DAM_SSD_SVPIP                    = "DAM.SSD.SvpIp";
    public static final String DAM_SSD_SYSTEMSTATUS             = "DAM.SSD.SystemStatus";
    public static final String DAM_SSD_CONTROLLERS              = "DAM.SSD.Controllers";
    public static final String DAM_SSD_CONTROLLER               = "DAM.SSD.Controller";
    public static final String DAM_SSD_NODES_STORAGE_SYSTEMS    = "DAM.SSD.NodesStorageSystems";
    public static final String DAM_SSD_NODES_ONLINE             = "DAM.SSD.NodesOnline";
    public static final String DAM_SSD_CAPACITY                 = "DAM.SSD.Capacity";
    public static final String DAM_SSD_IDENTIFIER               = "DAM.SSD.Identifier";
    public static final String DAM_SSD_NODE	                	= "DAM.SSD.SingleNode";
    
    public static final String DAM_SSetD_Size_On_Disk			= "DAM.SSetD.SizeOnDisk";
    public static final String DAM_SSetD_Main_Node 				= "DAM.SSetD.MainNode";
    public static final String DAM_SSetD_Failover_Node 			= "DAM.SSetD.FailoverNode";
    public static final String DAM_SSetD_Libraries	 			= "DAM.SSetD.Libraries";
    public static final String DAM_SSetD_Shares		 			= "DAM.SSetD.Shares";
    public static final String DAM_SSetD_Stores		 			= "DAM.SSetD.Stores";
    public static final String DAM_SSetD_Service_Set_Health		= "DAM.SSetD.ServiceSetHealth";
    public static final String DAM_SSetD_Serial_Number 			= "DAM.SSetD.SerialNumber";
    public static final String DAM_SSetD_Capacity	 			= "DAM.SSetD.Capacity";
    public static final String DAM_SSetD_Node_Name	 			= "DAM.SSetD.NodeName";
    public static final String DAM_SSetD_Service_Set_Status 	= "DAM.SSetD.ServiceSetStatus";
    public static final String DAM_SSetD_VTL_Status				= "DAM.SSetD.VTLStatus";
    public static final String DAM_SSetD_NAS_Status 			= "DAM.SSetD.NASStatus";
    public static final String DAM_SSetD_StoreOnce_Catalyst_Status	= "DAM.SSetD.StoreOnceCatalystStatus";
    public static final String DAM_SSetD_Replication_Status		= "DAM.SSetD.ReplicationStatus";
    public static final String DAM_SSetD_Deduplication_Ratio	= "DAM.SSetD.DeduplicationRatio";
    public static final String DAM_SSetD_Service_Set_Name		= "DAM.SSetD.ServiceSetName";
    
    public static final String DAM_SSetD_SSD		 			= "DAM.SSetD.SSD";
    public static final String DAM_SSetD_Fast_Class 			= "DAM.SSetD.FastClass";
    public static final String DAM_SSetD_Near_Line	 			= "DAM.SSetD.NearLine";
    public static final String DAM_SSetD_CPGs		 			= "DAM.SSetD.CPGs";
    public static final String DAM_SSetD_Access		 			= "DAM.SSetD.Access";
    public static final String DAM_SSetD_Raid_Level 			= "DAM.SSetD.RaidLevel";
    public static final String DAM_SSetD_Device_Type 			= "DAM.SSetD.DeviceType";
    public static final String DAM_SSetD_CPG_Name	 			= "DAM.SSetD.CPGName";
    
    public static final String DAM_SSetD_Storage_Pools 			= "DAM.SSetD.StoragePools";
    public static final String DAM_SSetD_Storage_Pool_Name		= "DAM.SSetD.Storage_Pool_Name";
    
    public static final String DeleteVolume_Type 				= "DeleteVolume.Type";
    
    private static final I18NStorageProvider INSTANCE = new I18NStorageProvider();

    private I18NStorageProvider () {
    }
    
    @Override
    protected Class<? extends I18NCommon> getChildClass () {
        return I18NStorageProvider.class;
    }

    public static I18NStorageProvider getInstance () {
        return INSTANCE;
    }

    @Override
    protected ResourceBundle getResourceBundle (Locale locale) {
        return ResourceBundle.getBundle(String
                                        .format(RESOURCE_BUNDLE_BASE_NAME, getChildClass().getPackage()
                                                .getName()), locale);
    }

}
