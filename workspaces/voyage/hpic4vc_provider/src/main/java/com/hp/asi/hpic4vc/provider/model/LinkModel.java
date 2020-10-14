package com.hp.asi.hpic4vc.provider.model;

public class LinkModel extends BaseModel {

    public String displayName;
    public String type;
    public String url;
    public String dialogTitle;
    public String dialogText;
    public String urlBase;
    public String username;
    public String password;
    public String firmwareForHost;
    /**
     * An empty constructor is required for the AMF serialization to work!
     */
    public LinkModel() {
        super();
        this.displayName      = null;
        this.type             = null;
        this.url              = null;
        this.dialogTitle      = null;
        this.dialogText       = null;
        this.urlBase          = null;
        this.username         = null;
        this.password         = null;
        this.firmwareForHost  = null;
    }
    
    /**
     * Copy Constructor
     * @param linkModel
     */
    public LinkModel(LinkModel linkModel){
        if (linkModel != null) {
            this.errorMessage = linkModel.errorMessage;
            this.informationMessage = linkModel.informationMessage;

            this.displayName = linkModel.displayName;
            this.type = linkModel.type;
            this.url = linkModel.url;
            this.dialogTitle = linkModel.dialogTitle;
            this.dialogText = linkModel.dialogText;
            this.urlBase = linkModel.urlBase;
            this.username = linkModel.username;
            this.password = linkModel.password;
            this.firmwareForHost = linkModel.firmwareForHost;
        }
    }

	@Override
	public String toString() {
        return "LinkModel [displayName=" + displayName + ", type=" + type
                + ", url=" + url + ", dialogTitle=" + dialogTitle
                + ", dialogText=" + dialogText + ", urlBase=" + urlBase
                + ", username=" + username + ", password=" + password
                + ", errorMessage=" + errorMessage + ", informationMessage="
                + informationMessage + ", firmwareForHost=" + firmwareForHost
                + "]";
	}
       
}
