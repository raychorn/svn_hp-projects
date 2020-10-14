/**
 * Copyright 2012 Hewlett-Packard Development Company, L.P.
 */

package com.hp.asi.hpic4vc.provider.model;

import java.util.ArrayList;
import java.util.List;

public class TabModel extends BaseModel {
    
    public String displayNameKey;
    public String displayNameValue;
    public String order;
    public String column;
    public String component;
    public String helpUrl;
    public List<TabModel> subTabs;
    
    /**
     * An empty constructor is required for the AMF serialization to work!
     */
    public TabModel() {
        super();
        this.displayNameKey   = new String();
        this.displayNameValue = new String();
        this.order            = new String();
        this.column           = new String();
        this.component        = new String();
        this.helpUrl          = new String();
        this.subTabs          = new ArrayList<TabModel>();
    }

	@Override
	public String toString() {
		return "TabModel [displayNameKey=" + displayNameKey
				+ ", displayNameValue=" + displayNameValue + ", order=" + order
				+ ", column=" + column + ", component=" + component
				+ ", helpUrl=" + helpUrl + ", subTabs=" + subTabs
				+ ", errorMessage=" + errorMessage + ", informationMessage="
				+ informationMessage + "]";
	}   

}
