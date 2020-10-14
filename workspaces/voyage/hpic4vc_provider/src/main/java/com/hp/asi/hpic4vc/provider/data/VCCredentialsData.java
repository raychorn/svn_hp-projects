package com.hp.asi.hpic4vc.provider.data;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="pwdb")
public class VCCredentialsData {

    private String username;
    private String host;
    private String password; 
    private String type;
    private String id;
    
    public VCCredentialsData () {
        this.username = null;
        this.host     = null;
        this.password = null;
        this.type     = null;
        this.id       = null;
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


    @Override
    public String toString () {
        return "VCCredentialsData [username=" + username + ", host=" + host
                + ", password=" + password + ", type=" + type + ", id=" + id
                + "]";
    }
    
}
