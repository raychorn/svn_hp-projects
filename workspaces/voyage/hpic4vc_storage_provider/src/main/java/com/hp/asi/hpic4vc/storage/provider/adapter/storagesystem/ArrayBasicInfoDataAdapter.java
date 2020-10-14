package com.hp.asi.hpic4vc.storage.provider.adapter.storagesystem;

import com.hp.asi.hpic4vc.provider.adapter.DataAdapter;
import com.hp.asi.hpic4vc.provider.dam.model.HPModel;
import com.hp.asi.hpic4vc.provider.dam.util.ModelObjectUriResolver;
import com.hp.asi.hpic4vc.storage.provider.dam.model.AllStorageSystems;
import com.hp.asi.hpic4vc.storage.provider.dam.model.StorageArray;
import com.hp.asi.hpic4vc.storage.provider.locale.I18NStorageProvider;
import com.hp.asi.ui.hpicsm.ws.data.ArrayBasicInfoWSImpl;
import com.hp.asi.ui.hpicsm.ws.data.ArrayBasicInfoWSImpl.ArrayInfoObj;

public class ArrayBasicInfoDataAdapter extends DataAdapter<ArrayBasicInfoWSImpl, AllStorageSystems>{
    /** This is the address of the service. **/
    private static final String SERVICE_NAME = "services/swd/allArraysBasicInfo";
    
    private static final ModelObjectUriResolver RESOURCE_RESOLVER =
            new ModelObjectUriResolver();

    public ArrayBasicInfoDataAdapter () {
        super(ArrayBasicInfoWSImpl.class);
    }

    @Override
    public AllStorageSystems formatData (ArrayBasicInfoWSImpl rawData) {
        AllStorageSystems model = new AllStorageSystems();
        
        ArrayInfoObj[] results = null;
        
        if (rawData != null ) {
            results = rawData.getArrayInfo();
        }
        
        if (rawData == null || null == results) {
            model.errorMessage = I18NStorageProvider.getInstance().getInternationalString
                    (locale, I18NStorageProvider.SO_NullResult);
            log.info("ArrayManagementDetailsResult.getArrayInfo() is null.  " +
                    "Returning a blank model.");
            return model;
        }
        
        if (0 == results.length ) {
            model.informationMessage = I18NStorageProvider.getInstance().getInternationalString
                    (locale, I18NStorageProvider.SO_NoResults);
            log.info("ArrayManagementDetailsResult.getArrayInfo() has size zero.  " +
                    "Returning a blank model.");
            return model;
        }
        
        for (ArrayInfoObj arraySummaryWSImpl : results) {
            if (null != arraySummaryWSImpl) {
               model.addStorageArray(makeStorageArray(arraySummaryWSImpl));
            }
        }
        
        return model;
    }
    
    private StorageArray makeStorageArray(final ArrayInfoObj arraySummaryWSImpl) {
        StorageArray anArray = new StorageArray
                (RESOURCE_RESOLVER.createId(this.sessionInfo.getServerGuid(),
                                            arraySummaryWSImpl.getUid()));
        anArray.setArrayName(arraySummaryWSImpl.getDisplayName());
        anArray.setParent(HPModel.getInstance());
        return anArray;
    }

    /**
     * This is a method to create a new object for the model.
     */
    @Override
    public AllStorageSystems getEmptyModel () {
        return new AllStorageSystems();
    }

    /**
     * This is an accessor method for the name of the service.
     * @return - The name of the service.
     */
    @Override
    public String getServiceName () {
        return SERVICE_NAME;
    }

}
