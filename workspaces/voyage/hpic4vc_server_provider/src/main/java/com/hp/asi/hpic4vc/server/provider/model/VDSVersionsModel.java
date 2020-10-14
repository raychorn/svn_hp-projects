package com.hp.asi.hpic4vc.server.provider.model;

import java.util.ArrayList;
import java.util.List;

import com.hp.asi.hpic4vc.provider.model.BaseModel;

public class VDSVersionsModel extends BaseModel {
	
	public List<String> supportedValues;
	public String type;
	public String settingName;
	public String selectedValue;
	public VDSVersionsModel() {
		super();		
		supportedValues= new ArrayList<String>();	
	}
	

}
