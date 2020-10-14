package com.hp.asi.hpic4vc.provider.dam.util;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.hp.asi.hpic4vc.provider.dam.model.HPModel;
import com.hp.asi.hpic4vc.provider.dam.model.ModelObject;

public class ObjectStore {    
    // Used to resolve URIs of resource objects provided by this adapter.
    private static final ModelObjectUriResolver RESOURCE_RESOLVER =
          new ModelObjectUriResolver();
    
    private final ConcurrentMap<String, ModelObject> objectStore;
    
    public ObjectStore() {
        this.objectStore = new ConcurrentHashMap<String, ModelObject>();
        addObjectToStore(HPModel.getInstance());
    }
    
    public ModelObject getObjectByUid(final String uid) {
        return objectStore.get(uid);
    }
    
    public Collection<ModelObject> getModelObjectsOfType(final String type) {
        Collection<ModelObject> objects = new ArrayList<ModelObject>();
    
        for (Entry<String, ModelObject> entry : objectStore.entrySet()) {
            ModelObject modelObject = entry.getValue();
            
            if (null == modelObject) {
                continue;
            }
            
            if (modelObject.getUtil().getType().equals(type)) {
                objects.add(entry.getValue());
            }
        }
        return objects;
    }
    
    /**
     * Add the supplied object to the store.
     */
    public void addObjectToStore(ModelObject object) {
       assert (object != null);
       URI objectUri = object.getUtil().getUri(RESOURCE_RESOLVER);
       String uid    = RESOURCE_RESOLVER.getUid(objectUri);
       objectStore.put(uid, object);
    }
    
    /**
     * Method to dump all of the objects from the store except for the 
     * main HPModel object instance.  The single HPModel object instance
     * is necessary to relate objects to each other.
     */
    public void cleanObjectStore() {
        this.objectStore.clear();
        addObjectToStore(HPModel.getInstance());
    }

}
