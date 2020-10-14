package com.hp.asi.hpic4vc.provider.data;

import javax.xml.bind.annotation.XmlElement;


public class ActionsData {
    
    private String url;
    private String display_name;
    private String type;
    private String web_root;
    private String path;
    private String appId;
    private String role;
    private String role_not;
    private String method;

    public ActionsData () {
    }

    @XmlElement(name="url") 
    public String getURL () {
        return this.url;
    }
    
    public void setURL(String url) {
        this.url = url;
    }

    @XmlElement(name="display_name") 
    public String getDisplay_name () {
        return display_name;
    }

    public void setDisplay_name (String display_name) {
        this.display_name = display_name;
    }

    @XmlElement(name="type") 
    public String getType () {
        return type;
    }

    public void setType (String type) {
        this.type = type;
    }

    @XmlElement(name="web_root") 
    public String getWeb_root () {
        return web_root;
    }

    public void setWeb_root (String web_root) {
        this.web_root = web_root;
    }
    
    @XmlElement(name="path") 
    public String getPath () {
        return path;
    }

    public void setPath (String path) {
        this.path = path;
    }

    @XmlElement(name="appId") 
	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

    @XmlElement(name="role") 
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
    
    @XmlElement(name="role_not") 
    public String getRole_not() {
        return role_not;
    }

    public void setRole_not(String roleNot) {
        this.role_not = roleNot;
    }
    
    @XmlElement(name="method") 
    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    @Override
    public String toString () {
        return "ActionsData [url=" + url + ", display_name=" + display_name
                + ", type=" + type + ", web_root=" + web_root + ", path="
                + path + ", appId=" + appId + ", role=" + role + ", role_not="
                + role_not + ", method=" + method + "]";
    }
	
}
