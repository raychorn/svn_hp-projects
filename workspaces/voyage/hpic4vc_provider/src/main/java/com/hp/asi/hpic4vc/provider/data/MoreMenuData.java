package com.hp.asi.hpic4vc.provider.data;

import javax.xml.bind.annotation.XmlElement;

import com.hp.asi.hpic4vc.provider.model.BaseModel;

public class MoreMenuData extends BaseModel {
    private String display_name;
    private String appId;

    public MoreMenuData () {
    }

    @XmlElement(name = "display_name")
    public String getDisplay_name () {
        return display_name;
    }

    public void setDisplay_name (String display_name) {
        this.display_name = display_name;
    }

    @XmlElement(name = "appId")
    public String getAppId () {
        return appId;
    }

    public void setAppId (String appId) {
        this.appId = appId;
    }

}
