package com.hp.asi.hpic4vc.provider.model;

public abstract class BaseModel {

    public String errorMessage;
    public String informationMessage;
    
    public BaseModel() {
        this.errorMessage = null;
        this.informationMessage = null;
    }
}
