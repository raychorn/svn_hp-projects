package com.hp.asi.hpic4vc.provider.adapter;

import java.util.concurrent.CountDownLatch;

import com.hp.asi.hpic4vc.provider.locale.I18NProvider;
import com.hp.asi.hpic4vc.provider.model.HeaderModel;
import com.hp.asi.hpic4vc.provider.model.HealthModel;
import com.hp.asi.hpic4vc.provider.data.HealthSummary;

public class HealthSummaryDataAdapter extends AbstractHeaderDataAdapter<HealthSummary, HealthModel> {
    
    public HealthSummaryDataAdapter (final HeaderModel headerModel, 
                                     final CountDownLatch countdownLatch) {
        super(HealthSummary.class, headerModel, countdownLatch);
    }

    @Override
    public HealthModel formatData (HealthSummary rawData) {
        HealthModel healthModel      = new HealthModel();
        healthModel.infoCount        = Integer.toString(rawData.getInfoCount());
        healthModel.okCount          = Integer.toString(rawData.getOkCount());
        healthModel.warnCount        = Integer.toString(rawData.getWarnCount());
        healthModel.errorCount       = Integer.toString(rawData.getErrorCount());
        
        if (null == rawData.getcStatus()) {
            healthModel.consolidatedStatus = "UNKNOWN";
        } else {
            healthModel.consolidatedStatus = rawData.getcStatus();
        }
        
        if (healthModel.consolidatedStatus.equals("UNKNOWN")) {
            healthModel.informationMessage = this.i18nProvider.getInternationalString
                    (locale, I18NProvider.Health_NoData);
        }
        
        if (null != this.headerModelToUpdate) {
            this.headerModelToUpdate.health = healthModel;
        }

        return healthModel;
    }

    @Override
    public String getServiceName () {
        String serviceName = "";
        if (null != sessionInfo.getVobjectType()) {
            switch (sessionInfo.getVobjectType()) {
                case HOST:
                    serviceName = "cstatus/hoststatus"; 
                    break;
                case VM:
                    serviceName = "cstatus/hoststatus";
                    break;
                case DATASTORE:
                    serviceName = "cstatus/datastorestatus";
                    break;
                case CLUSTER:
                    serviceName = "cstatus/clusterstatus";
                    break;
                default:
                    break;
            }
        } 
        return serviceName;
    }
    
    @Override
    public HealthModel getEmptyModel () {
        return new HealthModel();
    }    
}
