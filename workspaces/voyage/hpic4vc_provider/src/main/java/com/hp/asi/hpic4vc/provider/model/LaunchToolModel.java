package com.hp.asi.hpic4vc.provider.model;

import java.util.ArrayList;
import java.util.List;

public class LaunchToolModel extends BaseModel {
    public String id;
    public List<LinkModel> links;
    
    public LaunchToolModel() {
        super();
        id    = null;
        links = new ArrayList<LinkModel>();
    }

	@Override
	public String toString() {
		return "LaunchToolModel [id=" + id + ", links=" + links
				+ ", errorMessage=" + errorMessage + ", informationMessage="
				+ informationMessage + "]";
	}
    
}
