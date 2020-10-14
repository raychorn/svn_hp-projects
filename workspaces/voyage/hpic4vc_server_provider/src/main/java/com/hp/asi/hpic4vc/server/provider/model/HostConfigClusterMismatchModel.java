package com.hp.asi.hpic4vc.server.provider.model;

import java.util.ArrayList;
import java.util.List;

import com.hp.asi.hpic4vc.provider.model.BaseModel;
import com.hp.asi.hpic4vc.server.provider.data.ClusterMembersData;

public class HostConfigClusterMismatchModel extends BaseModel {
	
	public List<ClusterMembersData> mismatch;
	public String clusterUuid;
	public String refHostName;
	public String clustMismatch;
	//public List<ClusterMembersData> memberdata;
	
	
	public HostConfigClusterMismatchModel() {
		super();		
		mismatch= new ArrayList<ClusterMembersData>();		
		//memberdata= new ArrayList<ClusterMembersData>();
		
	}
	

}
