package com.hp.asi.hpic4vc.storage.provider.adapter.storagesystem;

import com.hp.asi.hpic4vc.provider.locale.I18NProvider;
import com.hp.asi.hpic4vc.provider.model.HeaderModel;
import com.hp.asi.hpic4vc.provider.model.HealthModel;
import com.hp.asi.ui.hpicsm.ws.data.StorageSystemStatusResult;
import com.hp.asi.ui.hpicsm.ws.data.StorageSystemStatusWSImpl;

public class HeaderDataAdapter extends BaseStorageSystemDataAdapter<StorageSystemStatusResult, HeaderModel>{

    private static final String SERVICE_NAME = "services/swd/storagesystemstatus";
    
    public HeaderDataAdapter (final String storageSystemUid) {
        super(StorageSystemStatusResult.class, storageSystemUid);
    }

    @Override
    public HeaderModel formatData (StorageSystemStatusResult rawData) {
        HeaderModel headerModel        = new HeaderModel();
        
        if (null != rawData.getErrorMessage() && !rawData.getErrorMessage().equals("")) {
            log.info("StorageSystemStatusResult has an error message.  Returning " +
                    "a HeaderModel with the error field set.");
            headerModel.errorMessage = rawData.getErrorMessage();
        }
        else if (null == rawData.getResult()) {
            log.info("StorageSystemStatusResult.getResult() is null.  Returning " +
                    "a HeaderModel with information message set.");
            headerModel.informationMessage = this.i18nProvider.getInternationalString
                    (locale, I18NProvider.Info_NotAvailable);
            
        } else {
            StorageSystemStatusWSImpl wsObject = rawData.getResult();
            headerModel.objReferenceName          = wsObject.getName();
            headerModel.objReferenceType          = wsObject.getStorageType();
            headerModel.health                    = new HealthModel();
            headerModel.health.consolidatedStatus = wsObject.getHealthStatus();
        }

        return headerModel;
    }

    @Override
    public HeaderModel getEmptyModel () {
        return new HeaderModel();
    }

    @Override
    public String getServiceName () {
        return SERVICE_NAME;
    }

}
