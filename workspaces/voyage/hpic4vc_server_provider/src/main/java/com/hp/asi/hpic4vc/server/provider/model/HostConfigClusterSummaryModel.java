package com.hp.asi.hpic4vc.server.provider.model;

import java.util.ArrayList;
import java.util.List;

import com.hp.asi.hpic4vc.provider.model.BaseModel;
import com.hp.asi.hpic4vc.server.provider.data.SummaryClusterMembersData;

public class HostConfigClusterSummaryModel extends BaseModel {
	
	public List<SummaryClusterMembersData> mismatch;
	public String configStatus;
	public String refHostName;
	
	public HostConfigClusterSummaryModel() {
		super();		
		mismatch= new ArrayList<SummaryClusterMembersData>();		
	}
}
