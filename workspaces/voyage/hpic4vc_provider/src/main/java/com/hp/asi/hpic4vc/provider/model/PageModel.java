/**
 * Copyright 2012 Hewlett-Packard Development Company, L.P.
 */

package com.hp.asi.hpic4vc.provider.model;

import java.util.ArrayList;
import java.util.List;

public class PageModel extends BaseModel {
    public List<TabModel> tabs;
    public List<TabModel> portlets;
    public String show_refresh_cache;

    /**
     * An empty constructor is required for the AMF serialization to work!
     */
    public PageModel() {
        super();
        this.tabs               = new ArrayList<TabModel> ();
        this.portlets           = new ArrayList<TabModel>();
        this.show_refresh_cache = null;
    }

	@Override
	public String toString() {
		return "PageModel [tabs=" + tabs + ", portlets=" + portlets
				+ ", show_refresh_cache=" + show_refresh_cache
				+ ", errorMessage=" + errorMessage + ", informationMessage="
				+ informationMessage + "]";
	}    
     
}