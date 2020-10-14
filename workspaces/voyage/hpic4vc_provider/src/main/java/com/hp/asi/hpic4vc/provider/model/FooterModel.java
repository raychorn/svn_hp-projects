/**
 * Copyright 2012 Hewlett-Packard Development Company, L.P.
 */

package com.hp.asi.hpic4vc.provider.model;

import java.util.ArrayList;
import java.util.List;


public class FooterModel extends BaseModel {
          
      public List<LaunchToolModel> launchTools;

      
      /**
       * An empty constructor is required for the AMF serialization to work!
       */
       public FooterModel() {
           super();
           this.launchTools  = new ArrayList<LaunchToolModel>();
       }


	@Override
	public String toString() {
		return "FooterModel [launchTools=" + launchTools + ", errorMessage="
				+ errorMessage + ", informationMessage=" + informationMessage
				+ "]";
	}

 


}
