package com.hp.asi.hpic4vc.provider.data;

import java.util.ArrayList;
import java.util.List;

public class TabResult {
    
    private String component;
    private String help_topic;
    private String display_name_key;
    private String order;
    private List<TabResult> sub_pages;
    private String column;
    private boolean bladeOnly;
    
	public TabResult() {
        this.component        = null;
        this.help_topic       = null;
        this.display_name_key = null;
        this.order            = null;
        this.sub_pages        = new ArrayList<TabResult>();
        this.column           = null;
        this.bladeOnly        = false;
    }
    
    public String getComponent () {
        return component;
    }
    public void setComponent (String component) {
        this.component = component;
    }
    public String getHelp_topic () {
        return help_topic;
    }
    public void setHelp_topic (String help_topic) {
        this.help_topic = help_topic;
    }
    public String getDisplay_name_key () {
        return display_name_key;
    }
    public void setDisplay_name_key (String display_name_key) {
        this.display_name_key = display_name_key;
    }
    public String getOrder () {
        return order;
    }
    public void setOrder (String order) {
        this.order = order;
    }
    public List<TabResult> getSub_pages () {
        return sub_pages;
    }
    public void setSub_pages (List<TabResult> sub_pages) {
        this.sub_pages = sub_pages;
    }
    public String getColumn() {
		return column;
	}
	public void setColumn(String column) {
		this.column = column;
	}

    public boolean isBladeOnly () {
        return bladeOnly;
    }

    public void setBladeOnly (boolean bladeOnly) {
        this.bladeOnly = bladeOnly;
    }

    @Override
    public String toString () {
        return "TabResult [component=" + component + ", help_topic="
                + help_topic + ", display_name_key=" + display_name_key
                + ", order=" + order + ", sub_pages=" + sub_pages + ", column="
                + column + ", bladeOnly=" + bladeOnly + "]";
    }


}
