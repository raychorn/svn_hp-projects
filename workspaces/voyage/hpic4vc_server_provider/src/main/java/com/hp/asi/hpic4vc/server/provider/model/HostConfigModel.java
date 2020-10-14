package com.hp.asi.hpic4vc.server.provider.model;

import java.util.ArrayList;
import java.util.List;

import com.hp.asi.hpic4vc.provider.model.BaseModel;
import com.hp.asi.hpic4vc.server.provider.data.HostConfigurationData;

public class HostConfigModel extends BaseModel {
	
	public List<HostConfigurationData> networks;
	//public SwitchTypeData switchdata;
	
	public HostConfigModel() {
		super();
		// TODO Auto-generated constructor stub
		//networks= new TableModel();
		networks= new ArrayList<HostConfigurationData>();
		//switchdata= new SwitchTypeData();
	}
	

}
