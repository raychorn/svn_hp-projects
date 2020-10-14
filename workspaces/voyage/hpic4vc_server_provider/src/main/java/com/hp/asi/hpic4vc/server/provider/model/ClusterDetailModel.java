package com.hp.asi.hpic4vc.server.provider.model;

import java.util.ArrayList;
import java.util.List;

import com.hp.asi.hpic4vc.provider.model.BaseModel;

public class ClusterDetailModel extends BaseModel {
	
	public  List<ClusterModel> hosts;
	
	public ClusterDetailModel()
	{
		super();
		this.hosts = new ArrayList<ClusterModel>();
	}

}
