/**
 * Copyright 2011 Hewlett-Packard Development Company, L.P.
 */
package com.hp.asi.hpic4vc.provider.data;

public class LaunchLink {

//	private static final String STORAGE_ICON_URL = "\\static\\apps\\storage\\images\\hpstorage_icon.png";
//	private static final String STORAGE_LAUNCH_ID = "Storage_Links";
	
	private String  type;
    private String  url;
    private String  label;
    private String  url_base;
    private String  username;
    private String  password;
	

    public LaunchLink () {
        this.type = null;
        this.url = null;
        this.label = null;
        this.url_base = null;
        this.username = null;
        this.password = null;
    }
    
    public LaunchLink (String type, String url, String label, 
    		String url_base, String username, String password) {
        super();
        this.type = type;
        this.url = url;
        this.label = label;
        this.url_base = url_base;
        this.username = username;
        this.password = password;
    }
    
    public String getUrl () {
        return url;
    }

    public void setUrl (String url) {
        this.url = url;
    }

    public String getLabel () {
        return label;
    }

    public void setLabel (String label) {
        this.label = label;
    }

    public void setType (String type) {
        this.type = type;
    }    

    public String getType () {
        return type;
    }

    @Override
    public String toString () {
        return "LaunchLink [type=" + type + ", url=" + url + ", label=" + label
                + "]";
    }

	public String getUrl_base()
	{
		return url_base;
	}

	public void setUrl_base(String url_base)
	{
		this.url_base = url_base;
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}
}
