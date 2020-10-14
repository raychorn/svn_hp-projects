package com.hp.asi.hpic4vc.provider.model;

public class HostDataModel extends BaseModel {
	public String          objReferenceName;
    public String          productInfo;
    public String          enclosureInfo;
    public boolean         isBladeServer;
    
    public HostDataModel() {
    	super();
    	objReferenceName = "";
    	productInfo      = null;
    	enclosureInfo    = null;
    	isBladeServer    = false;
    }

    @Override
    public String toString () {
        return "HostDataModel [objReferenceName=" + objReferenceName
                + ", productInfo=" + productInfo + ", enclosureInfo="
                + enclosureInfo + ", isBladeServer=" + isBladeServer
                + ", errorMessage=" + errorMessage + ", informationMessage="
                + informationMessage + "]";
    }
    
}
