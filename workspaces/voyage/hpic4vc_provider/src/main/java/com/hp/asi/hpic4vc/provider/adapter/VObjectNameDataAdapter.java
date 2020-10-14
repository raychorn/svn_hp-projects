package com.hp.asi.hpic4vc.provider.adapter;

import java.util.concurrent.CountDownLatch;

import com.hp.asi.hpic4vc.provider.data.VObjectNameData;
import com.hp.asi.hpic4vc.provider.impl.Hpic4vc_providerImpl;
import com.hp.asi.hpic4vc.provider.locale.I18NProvider;
import com.hp.asi.hpic4vc.provider.model.HeaderModel;
import com.hp.asi.hpic4vc.provider.model.HostDataModel;


public class VObjectNameDataAdapter extends AbstractHeaderDataAdapter<VObjectNameData, HostDataModel> {
    
    public VObjectNameDataAdapter (final HeaderModel headerModel,
                                   final CountDownLatch countdownLatch) {
        super(VObjectNameData.class, headerModel, countdownLatch);
    }

    @Override
    public HostDataModel formatData (final VObjectNameData rawData) {
    	HostDataModel nameData    = new HostDataModel();
        nameData.objReferenceName = getvObjectName(rawData);        
        nameData.enclosureInfo    = getEnclosureInfo(rawData);
        nameData.productInfo      = getProductInfo(rawData);
        nameData.isBladeServer    = rawData.isBlade();
        
        Hpic4vc_providerImpl.updateVObjectDataMap(sessionInfo.getObjReferenceId(), nameData);
        
        if (null != this.headerModelToUpdate) {
            updateTableModel(nameData);
        }
        
        return nameData;
    }
    
    @Override
    public String getServiceName () {
        String serviceName = "";
        if (null != sessionInfo && null != sessionInfo.getVobjectType()) {
            switch (sessionInfo.getVobjectType()) {
                case HOST:
                    serviceName = "orservices/host/swd/hostsummary"; 
                    break;
                case VM:
                    serviceName = "orservices/swd/vmsummary";
                    break;
                case DATASTORE:
                    serviceName = "orservices/swd/datastoresummary";
                    break;
                case CLUSTER:
                    serviceName = "orservices/host/swd/clustersummary";
                    break;
                default:
                    break;
            }
        } 
        return serviceName;
    }

    @Override
    public HostDataModel getEmptyModel () {
        return new HostDataModel();
    }
    
    private String getvObjectName(final VObjectNameData rawData) {
        String name = null;
        
        if (null != sessionInfo && null != sessionInfo.getVobjectType()) {
            switch (sessionInfo.getVobjectType()) {
                case HOST:
                    if (null != rawData.getIlo_server_name() &&
                    !rawData.getIlo_server_name().equals("")) {
                        name = rawData.getIlo_server_name();
                    } else {
                        name = rawData.getVmware_name();
                    }
                    break;
                case CLUSTER:
                    if (null != rawData.getCluster_name()) {
                        name = rawData.getCluster_name(); 
                    } else {
                        name = rawData.getVmware_name();
                    }
                    break;
                case VM:
                case DATASTORE:            
                default:
                    name = rawData.getVmware_name();
                    break;
            }
        } else{
            name = rawData.getVmware_name();
        }
        if (null == name) {
            name = "";
        }
        return name;
    }

    private String getEnclosureInfo(final VObjectNameData rawData) {
    	StringBuilder sb = new StringBuilder();
    	if (null != rawData.getEnclosure() && !rawData.getEnclosure().equals("")) {
    		sb.append(this.i18nProvider.getInternationalString(locale, I18NProvider.HEADER_ENCLOSURE));
    		sb.append(": ");
    		sb.append(rawData.getEnclosure());
    		sb.append(" ");
    	}
    	
    	if (null != rawData.getBay() && !rawData.getBay().equals("")) {
    		sb.append(this.i18nProvider.getInternationalString(locale,  I18NProvider.HEADER_BAY));
    		sb.append(": ");
    		sb.append(rawData.getBay());
    	}

    	if (sb.length() < 1) {
    		return null;
    	} else {
    		return sb.toString();
    	}
    }
    
    private String getProductInfo(final VObjectNameData rawData) {
    	if (null != rawData.getProduct_name() && !rawData.getProduct_name().equals("")) {
    		return rawData.getProduct_name();
    	} else {
    		return null;
    	}			
    }
    
    private void updateTableModel (HostDataModel nameData) {
        this.headerModelToUpdate.objReferenceName = nameData.objReferenceName;
        this.headerModelToUpdate.enclosureInfo    = nameData.enclosureInfo;
        this.headerModelToUpdate.productInfo      = nameData.productInfo;
    }
}
