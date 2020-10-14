package com.hp.asi.hpic4vc.provider.data;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

public class HPOneViewCredentialsData {

    private String username;
    private String host;
    private String ip;
    private String password; 
    private String type;
    private String id;
    private String action;
    
    public HPOneViewCredentialsData () {
        this.username = null;
        this.host     = null;
        this.ip       = null;
        this.password = null;
        this.type     = null;
        this.id       = null;
        this.action   = null;
    }
    
    
    @XmlElement(name="username")
    public String getUsername () {
        return username;
    }
    
    public void setUsername (String username) {
        this.username = username;
    }
    
    @XmlElement(name="host")
    public String getHost () {
        return host;
    }
    
    public void setHost (String host) {
        this.host = host;
    }
    
    @XmlElement(name="ip")
    public String getIp () {
        return ip;
    }
    
    public void setIp (String ip) {
        this.ip = ip;
    }
    
    @XmlElement(name="password")
    public String getPassword () {
        return password;
    }
    
    public void setPassword (String password) {
        this.password = password;
    }
    
    @XmlElement(name="type")
    public String getType () {
        return type;
    }
    
    public void setType (String type) {
        this.type = type;
    }
    
    @XmlElement(name="id")
    public String getId () {
        return id;
    }
    
    public void setId (String id) {
        this.id = id;
    }

    @XmlElement(name="action")
    public String getAction() {
        return action;
    }
    
	public void setAction(String action) {
        this.action = action;
	}

    @Override
    public String toString () {
        return "HPOneViewCredentialsData [username=" + username + ", host=" + host
                + ", password=" + password + ", type=" + type + ", id=" + id
                + "]";
    }


}
