package com.hp.asi.hpic4vc.storage.provider.dam.provider;

import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;

import com.hp.asi.hpic4vc.provider.dam.model.HPModel;
import com.hp.asi.hpic4vc.provider.dam.model.ModelObject;
import com.hp.asi.hpic4vc.provider.dam.provider.BaseDamProvider;
import com.hp.asi.hpic4vc.provider.dam.provider.HPModelDamProvider;
import com.hp.asi.hpic4vc.storage.provider.api.Hpic4vc_storage_provider;
import com.hp.asi.hpic4vc.storage.provider.dam.model.AllStorageSystems;
import com.hp.asi.hpic4vc.storage.provider.dam.model.StorageArray;
import com.hp.asi.hpic4vc.storage.provider.impl.Hpic4vc_storage_providerImpl;
import com.vmware.vise.data.query.DataService;
import com.vmware.vise.data.query.type;
import com.vmware.vise.vim.data.VimObjectReferenceService;

@type("hpmodel:StorageArray")
public class StorageArrayDamProvider extends BaseDamProvider {

    public static final String ARRAY_TYPE = ModelObject.NAMESPACE + StorageArray.class.getSimpleName();
    private static final long FIVE_MINUTES   = 300000;
   
    private Hpic4vc_storage_provider storageProvider;
    private Timer dataExpirationTimer;
    private volatile boolean hasFreshData;    
    
    public StorageArrayDamProvider (DataService dataService,
                                    VimObjectReferenceService objRefService) {
        super(dataService, objRefService);
        storageProvider     = new Hpic4vc_storage_providerImpl();
        dataExpirationTimer = new Timer();
        hasFreshData        = false;
    }

    @Override
    public void initializeRelationships () {
        relationHolder.addRelation(HPModelDamProvider.HPModel_TYPE, "arrays", ARRAY_TYPE);
        relationHolder.addRelation(ARRAY_TYPE, "hpmodel", HPModelDamProvider.HPModel_TYPE);
    }

    @Override
    public boolean isSupportedType (String type) {
        return ARRAY_TYPE.equals(type)
                || HPModelDamProvider.HPModel_TYPE.equals(type);
    }

    /**
     * Returns property of the specified ModelObject or UNSUPPORTED_PROPERTY_FLAG.
     *
     */
    @Override
    public Object getProperty(ModelObject object, String propertyName) {
        if (object instanceof HPModel) {
            return getProperty((HPModel)object, propertyName);
        } else if (object instanceof StorageArray) {
            return getProperty((StorageArray)object, propertyName);
        }
        return UNSUPPORTED_PROPERTY_FLAG;
    }

    /**
     * Returns property of the specified Rack or UNSUPPORTED_PROPERTY_FLAG.
     */
    private Object getProperty(HPModel hpModel, String propertyName) {
        log.info("Requesting HPModel property: " + propertyName);
        assert (hpModel != null);
        if ("id".equals(propertyName)) {
            return hpModel.getId();
        } else if ("arrays".equals(propertyName)) {
            return getArrays();
        }
        return UNSUPPORTED_PROPERTY_FLAG;
    }

    /**
     * Returns property of the specified Storage Array or UNSUPPORTED_PROPERTY_FLAG.
     */
    private Object getProperty(StorageArray array, String propertyName) {
        log.info("Requesting StorageArray property: " + propertyName);
        assert (array != null);
        if ("arrayName".equals(propertyName) || "name".equals(propertyName)) {
            return array.getArrayName();
        } 
        return UNSUPPORTED_PROPERTY_FLAG;
    }
    
    private synchronized StorageArray[] getArrays() {
        if (!hasFreshData) {
            getArrayDataFromBackend();
        }
        Collection<ModelObject> arrayList = objectStore.getModelObjectsOfType(ARRAY_TYPE);
        StorageArray[] storageArrays = arrayList.toArray(new StorageArray[arrayList.size()]);
        return storageArrays;
    }


    private void getArrayDataFromBackend() {
        String serverGuid = this.getServerGuid();
        AllStorageSystems returnModel = storageProvider.getArraysBasicInfo(serverGuid);

        if (null != returnModel && 
            null != returnModel.getAllArrays() && 
            returnModel.getAllArrays().size() > 0) {
            log.info("Cleaning the objectstore before inserting fresh data.");
            this.objectStore.cleanObjectStore();
            for (StorageArray storageArray : returnModel.getAllArrays()) {
                this.objectStore.addObjectToStore(storageArray);
            }
            hasFreshData = true;
            log.info("getArrayDataFromBackend returned " +
            returnModel.getAllArrays().size() + " arrays.");
        } else {
            log.info("getArrayDataFromBackend had either null or empty results.");
        }
        dataExpirationTimer.schedule(new SetExpiredTask(), FIVE_MINUTES);
    }

    /**
     * Method to clear out old data.
     */
    protected void setDataToStale() {
        log.info("Setting hasFreshData to false.");
        this.hasFreshData = false;
    }
    
    private class SetExpiredTask extends TimerTask {        
        @Override
        public void run () {
            setDataToStale();          
        }        
    }
    
}
