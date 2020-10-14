package com.hp.asi.hpic4vc.provider.model;

import java.util.ArrayList;
import java.util.List;

public class ConfigurationListModel extends BaseModel {
    public List<ConfigurationModel> configurationLinks;
    
    public ConfigurationListModel() {
        super();
        configurationLinks = new ArrayList<ConfigurationModel>();
    }
    
    
    public void addLinkWithHelpLink(LinkModel link, LinkModel helpLink) {
        ConfigurationModel config = new ConfigurationModel();
        config.link     = link;
        config.helpLink = helpLink;
        configurationLinks.add(config);
    }


	@Override
	public String toString() {
		return "ConfigurationListModel [configurationLinks="
				+ configurationLinks + ", errorMessage=" + errorMessage
				+ ", informationMessage=" + informationMessage + "]";
	}

}
