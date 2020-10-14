package com.hp.asi.hpic4vc.storage.provider.dam.model;

import com.hp.asi.hpic4vc.provider.model.BaseModel;
import com.hp.asi.hpic4vc.provider.model.LabelValueListModel;
import com.hp.asi.hpic4vc.provider.model.SummaryPortletModel;

/**
 * Contains summary information on any Storage System.
 * 
 * @author Andrew Khoury
 * 
 */
public class StorageSystemOverviewModel extends BaseModel {

    /** Example would be "HP EVA" or "HP StoreServ" */
    public String storageType;
    /**
     * Key-value information regarding the storage system. This can include the
     * model name, firmware version, etc.
     */
    public LabelValueListModel summaryDetails;
    /** Provisioning information on the storage system (if applicable) */
    public SummaryPortletModel storageSystemOverview;
    /** number of volumes on storage system (if applicable)*/
    public int volumesProvisioned;
    /** number of volumes that are thin-provisioned (if applicable)*/
    public int volumesThinProvisioned;
    /** number of volumes that are over-provisioned (if applicable)*/
    public int volumesOverProvisioned;

    /**
     * Creates an empty Storage System Summary
     */
    public StorageSystemOverviewModel () {
        this.storageType              = new String();
        this.summaryDetails           = new LabelValueListModel();
        this.storageSystemOverview    = null;
    }

    /**
     * Creates a Storage System Summary with provisioning information. This can
     * be used for storage arrays.
     * 
     * @param storageType
     *            Example would be "HP EVA" or "HP StoreServ"
     * @param summaryDetails
     *            Key-value information regarding the storage system. This can
     *            include the model name, firmware version, etc.
     * @param storageSystemOverview
     *            Provisioning information on the storage system
     * @param volumesProvisioned
     *            number of volumes on storage system
     * @param volumesThinProvisioned
     *            number of volumes that are thin-provisioned
     * @param volumesOverProvisioned
     *            number of volumes that are over-provisioned
     */
    public StorageSystemOverviewModel (String storageType,
                                 LabelValueListModel summaryDetails,
                                 SummaryPortletModel storageSystemOverview,
                                 int volumesProvisioned,
                                 int volumesThinProvisioned,
                                 int volumesOverProvisioned) {
        this.storageType              = storageType;
        this.summaryDetails           = summaryDetails;
        this.storageSystemOverview    = storageSystemOverview;
        this.volumesProvisioned       = volumesProvisioned;
        this.volumesThinProvisioned   = volumesThinProvisioned;
        this.volumesOverProvisioned   = volumesOverProvisioned;
    }
    
    /**
     * Creates a Storage System Summary without provisioning information. This
     * can be used for backup systems.
     * 
     * @param storageType
     *            Example would be "HP EVA" or "HP StoreServ"
     * @param summaryDetails
     *            Key-value information regarding the storage system. This can
     *            include the model name, firmware version, etc.
     */
    public StorageSystemOverviewModel (String storageType,
                                 LabelValueListModel summaryDetails) {
        this.storageType              = storageType;
        this.summaryDetails           = summaryDetails;
    }
    
    @Override
    public String toString () {
        return "StorageSystemOverviewModel [storageType=" + storageType
                + ", summaryDetails=" + summaryDetails
                + ", storageSystemOverview=" + storageSystemOverview
                + ", volumesProvisioned=" + volumesProvisioned
                + ", volumesThinProvisioned=" + volumesThinProvisioned
                + ", volumesOverProvisioned=" + volumesOverProvisioned
                + ", informationMessage=" + informationMessage
                + ", errorMessage=" + errorMessage
                + "]";
    }

}
