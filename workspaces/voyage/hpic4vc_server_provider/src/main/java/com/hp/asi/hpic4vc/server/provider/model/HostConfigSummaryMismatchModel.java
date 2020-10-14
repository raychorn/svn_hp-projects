package com.hp.asi.hpic4vc.server.provider.model;

import java.util.ArrayList;
import java.util.List;

import com.hp.asi.hpic4vc.provider.model.BaseModel;
import com.hp.asi.hpic4vc.server.provider.data.NetworkSummaryHostConfigOverview;

public class HostConfigSummaryMismatchModel extends BaseModel {
	
	public List<NetworkSummaryHostConfigOverview> mismatch;	
	public String configStatus;
	public String errorMessage;
	
	public HostConfigSummaryMismatchModel() {
		super();		
		mismatch= new ArrayList<NetworkSummaryHostConfigOverview>();
		
	}
	

}
