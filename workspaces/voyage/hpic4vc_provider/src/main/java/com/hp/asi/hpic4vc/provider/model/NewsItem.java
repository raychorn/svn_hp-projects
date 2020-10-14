package com.hp.asi.hpic4vc.provider.model;

public class NewsItem extends BaseModel {

    public String rawStatus;
    public String formattedStatus;
    public String formattedMessage;
    public String formattedTimeStamp;
    
    public NewsItem() {
    	super();
    	this.rawStatus 			= new String();
    	this.formattedStatus 	= new String();
    	this.formattedMessage	= new String();
    	this.formattedTimeStamp	= new String();
    }
    
    public NewsItem(final String rawStatus, 
                    final String formattedStatus, 
                    final String formattedMessage, 
                    final String formattedTimeStamp) {
        super();
        this.rawStatus          = rawStatus;
        this.formattedStatus    = formattedStatus;
        this.formattedMessage   = formattedMessage;
        this.formattedTimeStamp = formattedTimeStamp;
    }

	@Override
	public String toString() {
		return "NewsItem [rawStatus=" + rawStatus + ", formattedStatus="
				+ formattedStatus + ", formattedMessage=" + formattedMessage
				+ ", formattedTimeStamp=" + formattedTimeStamp
				+ ", errorMessage=" + errorMessage + ", informationMessage="
				+ informationMessage + "]";
	}
    
}
