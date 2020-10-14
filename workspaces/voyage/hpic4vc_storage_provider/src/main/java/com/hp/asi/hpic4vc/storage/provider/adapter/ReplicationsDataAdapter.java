package com.hp.asi.hpic4vc.storage.provider.adapter;

import com.hp.asi.hpic4vc.provider.adapter.TableDataAdapter;
import com.hp.asi.hpic4vc.provider.impl.SessionInfo.VObjectType;
import com.hp.asi.hpic4vc.provider.locale.I18NProvider;
import com.hp.asi.hpic4vc.provider.model.TableModel;
import com.hp.asi.hpic4vc.provider.model.TableModel.TableRow;
import com.hp.asi.hpic4vc.storage.provider.locale.I18NStorageProvider;
import com.hp.asi.ui.hpicsm.ws.data.ReplicationResult;
import com.hp.asi.ui.hpicsm.ws.data.ReplicationWSImpl;

public class ReplicationsDataAdapter extends TableDataAdapter<ReplicationResult> {
    private static final String SERVICE_NAME = "services/swd/replications";
    
    public ReplicationsDataAdapter() {
        super(ReplicationResult.class);
    }
    
    @Override
    public String getServiceName () {
        return SERVICE_NAME;
    }

    @Override
	public String getErrorMsg (ReplicationResult rawData) {
        return rawData.getErrorMessage();
    }
    
    @Override
    public boolean isResultEmpty (ReplicationResult rawData) {
        if (null == rawData.getResult()) {
            return true;
        }
        return false;
    }

    @Override
    public void setColumns (TableModel table) {
        String diskName                  = I18NStorageProvider.getInstance().getInternationalString(locale, I18NStorageProvider.Replications_DiskName);
        String arrayName                 = I18NStorageProvider.getInstance().getInternationalString(locale, I18NStorageProvider.Replications_ArrayName);
        String replicaType               = I18NStorageProvider.getInstance().getInternationalString(locale, I18NStorageProvider.Replications_ReplicaType);
        String replicaName               = I18NStorageProvider.getInstance().getInternationalString(locale, I18NStorageProvider.Replications_ReplicaName);
        String replicaArray              = I18NStorageProvider.getInstance().getInternationalString(locale, I18NStorageProvider.Replications_ReplicaArray);
        String replicaStatus             = I18NStorageProvider.getInstance().getInternationalString(locale, I18NStorageProvider.Replications_ReplicaStatus);
        String replicaGroup              = I18NStorageProvider.getInstance().getInternationalString(locale, I18NStorageProvider.Replications_ReplicaGroup);
        String datastoreOrRDMType        = I18NStorageProvider.getInstance().getInternationalString(locale, I18NStorageProvider.Replications_DatastoreOrRDMType);
        String datastoreName             = I18NStorageProvider.getInstance().getInternationalString(locale, I18NStorageProvider.Replications_DatastoreName);
        String creationTime              = I18NStorageProvider.getInstance().getInternationalString(locale, I18NStorageProvider.Replications_CreationTime);
        String expireTime                = I18NStorageProvider.getInstance().getInternationalString(locale, I18NStorageProvider.Replications_ExpireTime);
        String retentionTime             = I18NStorageProvider.getInstance().getInternationalString(locale, I18NStorageProvider.Replications_RetentionTime);
        
        String diskNameToolTip           = I18NStorageProvider.getInstance().getInternationalString(locale, I18NStorageProvider.Replications_TT_DiskName);
        String arrayNameToolTip          = I18NStorageProvider.getInstance().getInternationalString(locale, I18NStorageProvider.Replications_TT_ArrayName);
        String replicaTypeToolTip        = I18NStorageProvider.getInstance().getInternationalString(locale, I18NStorageProvider.Replications_TT_ReplicaType);
        String replicaNameToolTip        = I18NStorageProvider.getInstance().getInternationalString(locale, I18NStorageProvider.Replications_TT_ReplicaName);
        String replicaArrayToolTip       = I18NStorageProvider.getInstance().getInternationalString(locale, I18NStorageProvider.Replications_TT_ReplicaArray);
        String replicaStatusToolTip      = I18NStorageProvider.getInstance().getInternationalString(locale, I18NStorageProvider.Replications_TT_ReplicaStatus);
        String replicaGroupToolTip       = I18NStorageProvider.getInstance().getInternationalString(locale, I18NStorageProvider.Replications_TT_ReplicaGroup);
        String datastoreOrRDMTypeToolTip = I18NStorageProvider.getInstance().getInternationalString(locale, I18NStorageProvider.Replications_TT_DatastoreOrRDMType);
        String datastoreNameToolTip      = I18NStorageProvider.getInstance().getInternationalString(locale, I18NStorageProvider.Replications_TT_DatastoreName);
        String creationTimeToolTip       = I18NStorageProvider.getInstance().getInternationalString(locale, I18NStorageProvider.Replications_TT_CreationTime);
        String expireTimeToolTip         = I18NStorageProvider.getInstance().getInternationalString(locale, I18NStorageProvider.Replications_TT_ExpireTime);
        String retentionTimeToolTip      = I18NStorageProvider.getInstance().getInternationalString(locale, I18NStorageProvider.Replications_TT_RetentionTime);
        
        table.addColumn(diskName,           diskNameToolTip,           "140", null, true, false, false);
        table.addColumn(arrayName,          arrayNameToolTip,          "100", null, true, false, false);       
        table.addColumn(replicaType,        replicaTypeToolTip,        "100", null, true, false, false); 
        table.addColumn(replicaName,        replicaNameToolTip,        "160", null, true, false, false);
        table.addColumn(replicaArray,       replicaArrayToolTip,       "100", null, true, false, false);
        table.addColumn(replicaStatus,      replicaStatusToolTip,      "100", null, true, false, false);
        table.addColumn(replicaGroup,       replicaGroupToolTip,       "100", null, true, false, false);       
        table.addColumn(datastoreOrRDMType, datastoreOrRDMTypeToolTip, "120", null, true, false, false); 
        table.addColumn(datastoreName,      datastoreNameToolTip,      "120", configData.getVObjectActions(VObjectType.DATASTORE, sessionInfo, false).getMenuItems(), true, false, false);
        table.addColumn(creationTime,       creationTimeToolTip,       "130", null, true, true,  false);
        table.addColumn(expireTime,         expireTimeToolTip,         "130", null, true, true,  false);
        table.addColumn(retentionTime,      retentionTimeToolTip,      "130", null, true, true,  false);
    }

    @Override
    public void setRows (TableModel table, ReplicationResult rawData) {
        String id    = null;
        String dsID;
        String creationTimeStamp;
        String expirationTimeStamp;
        String retentionTimeStamp;
        
        for (ReplicationWSImpl replicationWSImpl : rawData.getResult()) {
            dsID = getDatastoreId(replicationWSImpl.getDatastoreIdentifier());
            creationTimeStamp = I18NProvider.getInstance().getLocaleDateFromEpochTime
                    (replicationWSImpl.getCreationTime(), locale);
            expirationTimeStamp = I18NProvider.getInstance().getLocaleDateFromEpochTime
                    (replicationWSImpl.getExpirationTime(), locale);
            retentionTimeStamp = I18NProvider.getInstance().getLocaleDateFromEpochTime
                    (replicationWSImpl.getRetentionTime(), locale);
            
            TableRow row = new TableRow();
            row.addCell(id,    replicationWSImpl.getDiskName(),           replicationWSImpl.getFormattedDiskName(), 			null);
            row.addCell(id,    replicationWSImpl.getArrayName(),          replicationWSImpl.getFormattedArrayName(), 			null);
            row.addCell(id,    replicationWSImpl.getReplicaType(),        replicationWSImpl.getFormattedReplicaType(), 			null);
            row.addCell(id,    replicationWSImpl.getReplicaName(),        replicationWSImpl.getFormattedReplicaName(), 			null);
            row.addCell(id,    replicationWSImpl.getReplicaArray(),       replicationWSImpl.getFormattedReplicaArray(), 		null);
            row.addCell(id,    replicationWSImpl.getReplicaStatus(),      replicationWSImpl.getFormattedReplicaStatus(), 		null);
            row.addCell(id,    replicationWSImpl.getReplicationGroup(),   replicationWSImpl.getFormattedReplicationGroup(), 	null);
            row.addCell(id,    replicationWSImpl.getDatastoreOrRDMType(), replicationWSImpl.getFormattedDatastoreOrRDMType(), 	null);
            row.addCell(dsID,  replicationWSImpl.getDatastoreName(),      replicationWSImpl.getFormattedDatastoreName(),        null);
            row.addCell(id,    replicationWSImpl.getCreationTime(),       creationTimeStamp, 		                            null);
            row.addCell(id,    replicationWSImpl.getExpirationTime(),     expirationTimeStamp, 		                            null);
            row.addCell(id,    replicationWSImpl.getRetentionTime(),      retentionTimeStamp,                           		null);
            table.addRow(row);
        }
        
    }
    
    private String getDatastoreId(final String rawDsId) {
        String displayId  = VObjectType.DATASTORE.getMoref() + ":";
        if (null != rawDsId) {
            displayId += rawDsId;
        }
        
        return displayId;
    }


}
