package com.hp.asi.hpic4vc.storage.provider.adapter.storagesystem;

import com.hp.asi.hpic4vc.provider.locale.I18NProvider;
import com.hp.asi.hpic4vc.provider.model.LabelValueListModel;
import com.hp.asi.hpic4vc.provider.model.PieChartModel;
import com.hp.asi.hpic4vc.provider.model.SummaryPortletModel;
import com.hp.asi.hpic4vc.storage.provider.dam.model.StorageSystemOverviewModel;
import com.hp.asi.hpic4vc.storage.provider.locale.I18NStorageProvider;
import com.hp.asi.ui.hpicsm.ws.data.StorageSystemOverviewResult;
import com.hp.asi.ui.hpicsm.ws.data.StorageSystemOverviewWSImpl;

public class StorageSystemOverviewDataAdapter extends 
        BaseStorageSystemDataAdapter<StorageSystemOverviewResult, StorageSystemOverviewModel> {

    /** This is the address of the service. **/
    private static final String SERVICE_NAME = "services/swd/storagesystemoverview";
    
    public StorageSystemOverviewDataAdapter (String storageSystemUid) {
        super(StorageSystemOverviewResult.class, storageSystemUid);
    }

    @Override
    public StorageSystemOverviewModel formatData (StorageSystemOverviewResult rawData) {
        StorageSystemOverviewModel summaryModel = new StorageSystemOverviewModel();
        
        if (null != rawData.getErrorMessage() && !rawData.getErrorMessage().equals("")) {
            log.info("StorageSystemOverviewResult had an error message.  " +
                    "Returning a StorageSystemOverviewModel with an error message");
            summaryModel.errorMessage = rawData.getErrorMessage();
        } else if (null == rawData.getResult()) {
            log.info("StorageSystemOverviewResult has a null result.  " +
                    "Returning a StorageSystemOverviewModel with an information message");
 
            summaryModel.informationMessage = this.i18nProvider.getInternationalString
                    (locale, I18NProvider.Info_NotAvailable);
        } else {
            populateSummaryModel(summaryModel, rawData.getResult());
        }
        return summaryModel;
    }

    @Override
    public StorageSystemOverviewModel getEmptyModel () {
        return new StorageSystemOverviewModel();
    }

    @Override
    public String getServiceName () {
        return SERVICE_NAME;
    }

    private void populateSummaryModel (StorageSystemOverviewModel summaryModel,
                                       final StorageSystemOverviewWSImpl result) {
        
        if (mostDataFieldsAreNull(result)) {
            summaryModel.errorMessage = this.i18nProvider.getInternationalString
                    (locale, I18NProvider.Info_NotAvailable);
        } else {
            StorageTypeEnum typeEnum = 
                    StorageTypeEnum.getStorageTypeEnum(result.getStorageSystemType()); 
            
            summaryModel.storageType            = result.getStorageSystemType();
            summaryModel.summaryDetails         = createListData(typeEnum, result);
            summaryModel.storageSystemOverview  = createOverview(typeEnum, result);
            summaryModel.volumesOverProvisioned = result.getOverProvisionedVolumes();
            summaryModel.volumesProvisioned     = result.getTotalVolumes();
            summaryModel.volumesThinProvisioned = result.getThinProvisionedVolumes();
        }
    }

    private boolean mostDataFieldsAreNull (StorageSystemOverviewWSImpl result) {
        int score = 0;
        boolean mostLikelyArrayError = false;
        
        if (!isValidData(result.getDisplayName())) {
            score++;
        }
        
        if (!isValidData(result.getFirmwareVersion())) {
            score++;
        }
        
        if (result.getTotalCapacity() <= 0) {
            score++;
        }
        
        if (score >= 2 ) {
            mostLikelyArrayError = true;
        }
        return mostLikelyArrayError;
    }

    private LabelValueListModel createListData(final StorageTypeEnum type,
                                               final StorageSystemOverviewWSImpl result) {
        
        switch (type) {
            case HP_STOREONCE:
                return createStoreOnceListData(result);
                
            case HP_3PAR:
                return create3ParListData(result);
                
            case HP_LEFTHAND:
                return createLefthandListData(result);
                
           default:
               return createGenericListData(result);
        }
    }

    private LabelValueListModel createStoreOnceListData(final StorageSystemOverviewWSImpl result) {
        String model        = I18NStorageProvider.getInstance().getInternationalString
                (locale, I18NStorageProvider.DAM_SSO_MODEL);
        String systemId     = I18NStorageProvider.getInstance().getInternationalString
                (locale, I18NStorageProvider.DAM_SSO_SYSTEM_ID);
        String mgmtGroup    = I18NStorageProvider.getInstance().getInternationalString
                (locale, I18NStorageProvider.DAM_SSO_MANAGEMENT_GROUP);
        String firmware     = I18NStorageProvider.getInstance().getInternationalString
                (locale, I18NStorageProvider.DAM_SSO_FIRMWARE);
        
        LabelValueListModel list = new LabelValueListModel();
        list.addLabelValuePair(model,         result.getStorageSystemModel());
        list.addLabelValuePair(systemId,      result.getStorageSystemUID());
        list.addLabelValuePair(mgmtGroup,     printList(result.getManagementGroup()));
        list.addLabelValuePair(firmware,      result.getFirmwareVersion());
        return list;
    }

    private LabelValueListModel create3ParListData (StorageSystemOverviewWSImpl result) {
        String model        = I18NStorageProvider.getInstance().getInternationalString
                (locale, I18NStorageProvider.DAM_SSO_MODEL);
        String serialNumber = I18NStorageProvider.getInstance().getInternationalString
                (locale, I18NStorageProvider.DAM_SSO_SERIAL_NUMBER);
        String systemId     = I18NStorageProvider.getInstance().getInternationalString
                (locale, I18NStorageProvider.DAM_SSO_SYSTEM_ID);
        String systemWwn    = I18NStorageProvider.getInstance().getInternationalString
                (locale, I18NStorageProvider.DAM_SSO_SYSTEM_WWN);
        String firmware     = I18NStorageProvider.getInstance().getInternationalString
                (locale, I18NStorageProvider.DAM_SSO_FIRMWARE);
        
        LabelValueListModel list = new LabelValueListModel();
        list.addLabelValuePair(model,         result.getStorageSystemModel());
        list.addLabelValuePair(serialNumber,  result.getSerialNumber());
        list.addLabelValuePair(systemId,      result.getSystemId());
        list.addLabelValuePair(systemWwn,     result.getStorageSystemUID());
        list.addLabelValuePair(firmware,      result.getFirmwareVersion());
        return list;
    }
    
    private LabelValueListModel createLefthandListData (StorageSystemOverviewWSImpl result) {
        String model        = I18NStorageProvider.getInstance().getInternationalString
                (locale, I18NStorageProvider.DAM_SSO_MODEL);
        String systemId     = I18NStorageProvider.getInstance().getInternationalString
                (locale, I18NStorageProvider.DAM_SSO_SYSTEM_ID);
        String firmware     = I18NStorageProvider.getInstance().getInternationalString
                (locale, I18NStorageProvider.DAM_SSO_FIRMWARE);
        
        LabelValueListModel list = new LabelValueListModel();
        list.addLabelValuePair(model,         result.getStorageSystemModel());
        list.addLabelValuePair(systemId,      result.getStorageSystemUID());
        list.addLabelValuePair(firmware,      result.getFirmwareVersion());
        return list;
    }
    
    private LabelValueListModel createGenericListData (StorageSystemOverviewWSImpl result) {
        String model        = I18NStorageProvider.getInstance().getInternationalString
                (locale, I18NStorageProvider.DAM_SSO_MODEL);
        String systemId     = I18NStorageProvider.getInstance().getInternationalString
                (locale, I18NStorageProvider.DAM_SSO_SYSTEM_ID);
        String mgmtGroup    = I18NStorageProvider.getInstance().getInternationalString
                (locale, I18NStorageProvider.DAM_SSO_MANAGEMENT_GROUP);
        String firmware     = I18NStorageProvider.getInstance().getInternationalString
                (locale, I18NStorageProvider.DAM_SSO_FIRMWARE);
        
        LabelValueListModel list = new LabelValueListModel();
        list.addLabelValuePair(model,         result.getStorageSystemModel());
        list.addLabelValuePair(systemId,      result.getStorageSystemUID());
        list.addLabelValuePair(mgmtGroup,     printList(result.getManagementGroup()));
        list.addLabelValuePair(firmware,      result.getFirmwareVersion());
        return list;
    }
   
   
    private SummaryPortletModel createOverview (final StorageTypeEnum type,
                                                final StorageSystemOverviewWSImpl result) {        
        if (type == StorageTypeEnum.HP_STOREONCE) {
            return null;
        }
        
        SummaryPortletModel portletModel = new SummaryPortletModel();
        String provisioned = I18NStorageProvider.getInstance().getInternationalString
                (locale, I18NStorageProvider.DAM_SSO_PROVISIONED);
        String used        = I18NStorageProvider.getInstance().getInternationalString
                (locale, I18NStorageProvider.DAM_SSO_USED);
        String thinProvisioned = I18NStorageProvider.getInstance().getInternationalString
                (locale, I18NStorageProvider.DAM_SSO_THIN_PROVISIONED);
        String usedThinProvisioned = I18NStorageProvider.getInstance().getInternationalString
                (locale, I18NStorageProvider.DAM_SSO_USED_THIN_PROVISIONED);
        String savings     = I18NStorageProvider.getInstance().getInternationalString
                (locale, I18NStorageProvider.DAM_SSO_SAVINGS);

        portletModel.pieChartData = createPieChartModel(result.getUsedCapacity(), 
                                                        result.getTotalCapacity());
        
        LabelValueListModel overviewList = new LabelValueListModel();
        overviewList.addLabelValuePair(provisioned,  result.getFormattedTotalCapacity());
        overviewList.addLabelValuePair(used,         result.getFormattedUsedCapacity());
        overviewList.addLabelValuePair(thinProvisioned,  result.getFormattedTotalThinCapacity());
        overviewList.addLabelValuePair(usedThinProvisioned, result.getFormattedUsedThinCapacity());
        overviewList.addLabelValuePair(savings,      result.getFormattedThinProvisioningSavings());
        portletModel.fieldData = overviewList;
        
        return portletModel;
    }
    
    private PieChartModel createPieChartModel(final long amtUsed, final long amtTotal) {
        PieChartModel pieChartData = new PieChartModel();        
        if (amtUsed < amtTotal) {
            pieChartData.percentUsed = ((int) ( ((double) amtUsed / 
                    (double) amtTotal) * 100.0));
            pieChartData.percentFree = 100 - pieChartData.percentUsed;
        } else {
            pieChartData.percentUsed = 100;
        }
        
        return pieChartData;
    }
}
