/**
 * Copyright 2011 Hewlett-Packard Development Company, L.P.
 */
package com.hp.asi.hpic4vc.provider.data;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="firmware") 
public class SoftwareFirmware {

    private String name;
	private String version;
	private String[] versions;
	private String description;	
	private String type;
	private String host;
	
	public SoftwareFirmware(){
		
		this.name        = null;
		this.version     = null;
		this.versions    = null;
		this.description = null;
		this.type        = null;
		this.host        = null;
	}

	@XmlElement(name="name")
	public String getName() {
		return this.name;
	}	
	
    public void setName (String name) {
        this.name = name;
    }
	
	@XmlElement(name="version")
	public String getVersion() {
		return this.version;
	}
	
    public void setVersion (String version) {
        this.version = version;
    }
    
    @XmlElement(name="versions")
    public String[] getVersions() {
        return this.versions;
    }
    
    public void setVersions (String[] versions) {
        this.versions = versions;
    }
	
	@XmlElement(name="host")
    public String getHost() {
        return host;
    }
	
    public void setHost (String host) {
        this.host = host;
    }
	
	@XmlElement(name="type")
    public String getType() {
        return type;
    }	
	
    public void setType (String type) {
        this.type = type;
    }
	
    @XmlElement(name="description")
	public String getDescription() {
		return this.description;
	}
    
    public void setDescription (String description) {
        this.description = description;
    }

    @Override
    public String toString () {
        return "SoftwareFirmware [name=" + name + ", version=" + version
                + ", versions=" + Arrays.toString(versions) + ", description="
                + description + ", type=" + type + ", host=" + host + "]";
    }
}
