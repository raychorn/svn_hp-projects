package com.hp.asi.hpic4vc.provider.model;

public class StringModel extends BaseModel {
    public String data;
    
    public StringModel() {
        super();
        this.data = null;
    }

	@Override
	public String toString() {
		return "StringModel [data=" + data + ", errorMessage=" + errorMessage
				+ ", informationMessage=" + informationMessage + "]";
	}

}
