package com.hp.asi.hpic4vc.server.provider.model;


import com.hp.asi.hpic4vc.provider.model.BaseModel;
import com.hp.asi.hpic4vc.server.provider.data.SwitchTypeData;

public class SwitchTypeModel extends BaseModel {
	
	public SwitchTypeData switchdata;
	
	public SwitchTypeModel() {
		super();
		// TODO Auto-generated constructor stub
		//networks= new TableModel();
		switchdata= new SwitchTypeData();
	}
	

}
