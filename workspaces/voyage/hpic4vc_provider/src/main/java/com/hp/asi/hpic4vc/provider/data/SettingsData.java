package com.hp.asi.hpic4vc.provider.data;

import javax.xml.bind.annotation.XmlElement;


public class SettingsData {
    
    private String url;
    private String display_name;
    private String type;
    private String web_root;
    private String path;
    private String role;
    private String appId;


    public SettingsData () {
    }
    
    public String getURL () {
        return this.url;
    }
    
    public void setURL(String url) {
        this.url = url;
    }
    
    public String getDisplay_name () {
        return display_name;
    }

    public void setDisplay_name (String display_name) {
        this.display_name = display_name;
    }

    public String getType () {
        return type;
    }

    public void setType (String type) {
        this.type = type;
    }

    public String getWeb_root () {
        return web_root;
    }

    public void setWeb_root (String web_root) {
        this.web_root = web_root;
    }
    
    public String getPath () {
        return path;
    }

    public void setPath (String path) {
        this.path = path;
    }
    
    @XmlElement(name="role") 
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
    
    @XmlElement(name="appId") 
    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    @Override
    public String toString () {
        return "SettingsData [url=" + url + ", display_name=" + display_name
                + ", type=" + type + ", web_root=" + web_root + ", path="
                + path + ", role=" + role + ", appId=" + appId + "]";
    }
}
