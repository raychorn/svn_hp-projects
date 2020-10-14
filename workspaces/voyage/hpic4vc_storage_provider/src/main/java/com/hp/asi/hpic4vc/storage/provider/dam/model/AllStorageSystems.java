package com.hp.asi.hpic4vc.storage.provider.dam.model;

import java.util.ArrayList;
import java.util.Collection;

import com.hp.asi.hpic4vc.provider.model.BaseModel;


public class AllStorageSystems extends BaseModel {
    
    private Collection<StorageArray> allArrays;
    
    public AllStorageSystems() {
        allArrays = new ArrayList<StorageArray>();
    }
    
    public Collection<StorageArray> getAllArrays() {
        return this.allArrays;
    }
    
    public void addStorageArray(final StorageArray anArray) {
        this.allArrays.add(anArray);
    }

    @Override
    public String toString () {
        StringBuilder data = new StringBuilder();
        data.append("AllStorageSystems [errorMessage=");
        data.append(errorMessage);
        data.append(", informationMessage=");
        data.append(informationMessage);
        data.append(", allArrays=[");
        for (StorageArray anArray : allArrays) {
            data.append(anArray.toString());
        }
        data.append("]]");
        return data.toString();
    }

    
}
