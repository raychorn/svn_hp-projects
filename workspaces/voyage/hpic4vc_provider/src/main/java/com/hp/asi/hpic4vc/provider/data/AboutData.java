package com.hp.asi.hpic4vc.provider.data;

import javax.xml.bind.annotation.XmlElement;

public class AboutData {
    private String version;
//    private String display_name;

    public AboutData () {
    }
    
    @XmlElement(name="version") 
    public String getVersion () {
        return version;
    }
    public void setVersion (String version) {
        this.version = version;
    }

    @Override
    public String toString () {
        return "AboutData [version=" + version + "]";
    }
}
