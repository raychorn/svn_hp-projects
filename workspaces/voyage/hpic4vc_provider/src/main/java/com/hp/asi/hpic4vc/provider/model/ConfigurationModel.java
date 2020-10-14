package com.hp.asi.hpic4vc.provider.model;

public class ConfigurationModel {
    public LinkModel link;
    public LinkModel helpLink;
    
    public ConfigurationModel() {
        link     = null;
        helpLink = null;
    }

	@Override
	public String toString() {
		return "ConfigurationModel [link=" + link + ", helpLink=" + helpLink
				+ ", getClass()=" + getClass() + ", hashCode()=" + hashCode()
				+ ", toString()=" + super.toString() + "]";
	}


}
