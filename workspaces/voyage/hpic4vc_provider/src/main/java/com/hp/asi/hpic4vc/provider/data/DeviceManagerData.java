/**
 * Copyright 2011 Hewlett-Packard Development Company, L.P.
 */
package com.hp.asi.hpic4vc.provider.data;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="launch_links") 
public class DeviceManagerData {
   
   private String lanchToolLabel;
   private String launchToolUrl;
   private String launchToolType;
      
	public DeviceManagerData() {

		this.lanchToolLabel = null;
		this.launchToolUrl = null;
		this.launchToolType = null;
	}
   
   
   public DeviceManagerData(String lanchToolLabel,
		String launchToolUrl, String launchToolType) {
		
		this.lanchToolLabel = lanchToolLabel;
		this.launchToolUrl = launchToolUrl;
		this.launchToolType = launchToolType;
}



@XmlElement(name="type")
   public String getType () {        
       
       return launchToolType;
   }

   @XmlElement(name="label")
   public String getLabel () {        
	   
	   return lanchToolLabel;
   }
  
   @XmlElement(name="url")
   public String getUrl () {
        
       return launchToolUrl;
   }


@Override
public String toString () {
    return "DeviceManagerData [lanchToolLabel=" + lanchToolLabel
            + ", launchToolUrl=" + launchToolUrl + ", launchToolType="
            + launchToolType + "]";
}
      

}
