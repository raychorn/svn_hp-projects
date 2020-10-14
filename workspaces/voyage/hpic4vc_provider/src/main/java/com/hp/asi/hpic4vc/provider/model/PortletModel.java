package com.hp.asi.hpic4vc.provider.model;

import java.util.ArrayList;
import java.util.List;

public class PortletModel extends BaseModel {

    public List<TabModel> portlets;
    
    public PortletModel() {
        this.portlets = new ArrayList<TabModel>();
    }

	@Override
	public String toString() {
		return "PortletModel [portlets=" + portlets + ", errorMessage="
				+ errorMessage + ", informationMessage=" + informationMessage
				+ "]";
	}

}
