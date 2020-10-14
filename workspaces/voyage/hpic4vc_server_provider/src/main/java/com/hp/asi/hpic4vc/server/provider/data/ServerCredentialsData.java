package com.hp.asi.hpic4vc.server.provider.data;

public class ServerCredentialsData {

    private String username;
    private String host;
    private String password; 
    private String type;
    private String id;
    
    public ServerCredentialsData () {
        this.username = null;
        this.host     = null;
        this.password = null;
        this.type     = null;
        this.id       = null;
    }
    
    
    public String getUsername () {
        return username;
    }
    
    public void setUsername (String username) {
        this.username = username;
    }
    
    public String getHost () {
        return host;
    }
    
    public void setHost (String host) {
        this.host = host;
    }
    
    public String getPassword () {
        return password;
    }
    
    public void setPassword (String password) {
        this.password = password;
    }
    
    public String getType () {
        return type;
    }
    
    public void setType (String type) {
        this.type = type;
    }
    
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
