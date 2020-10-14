package com.hp.asi.hpic4vc.provider.data;

import java.util.List;

public class PageResult {

    private List<TabResult> result;
    private List<TabResult> portlets;
    private String show_refresh_cache;
    
    public String getShow_refresh_cache () {
        return show_refresh_cache;
    }

    public void setShow_refresh_cache (String show_refresh_cache) {
        this.show_refresh_cache = show_refresh_cache;
    }

    public List<TabResult> getPages () {
        return result;
    }
    
    public void setPages (List<TabResult> pages) {
        this.result = pages;
    }
    
    public List<TabResult> getBoxes () {
        return portlets;
    }
    
    public void setBoxes (List<TabResult> portlets) {
        this.portlets = portlets;
    }

    @Override
    public String toString () {
        return "PageResult [result=" + result + ", portlets=" + portlets
                + ", show_refresh_cache=" + show_refresh_cache + "]";
    }

}
