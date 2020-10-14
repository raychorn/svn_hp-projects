package com.hp.asi.hpic4vc.storage.provider.dam.model;

import com.hp.asi.hpic4vc.provider.dam.model.ModelObject;

public class StorageArray extends ModelObject {

    private Object  parent;
    private String  arrayName;

    public StorageArray(final String id) {
        super(id);
    }

    /**
     * Name of the rack.
     */
    public String getArrayName() {
        return arrayName;
    }

    public void setArrayName(final String value) {
        arrayName = value;
    }

    public Object getParent () {
        return parent;
    }

    public void setParent (final Object parentObject) {
        this.parent = parentObject;
    }

    @Override
    public String toString () {
        return "StorageArray [parent=" + parent + ", arrayName=" + arrayName
                + ", getId()=" + getId() + "]";
    }
}