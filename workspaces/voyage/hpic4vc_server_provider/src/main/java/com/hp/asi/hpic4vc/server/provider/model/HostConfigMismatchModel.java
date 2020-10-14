package com.hp.asi.hpic4vc.server.provider.model;

import java.util.ArrayList;
import java.util.List;

import com.hp.asi.hpic4vc.provider.model.BaseModel;
import com.hp.asi.hpic4vc.server.provider.data.HostConfigMismatchvSwitches;
import com.hp.asi.hpic4vc.server.provider.data.ServerProfileConnections;

public class HostConfigMismatchModel extends BaseModel {
	
	public List<HostConfigMismatchvSwitches> mismatch;
	public List<ServerProfileConnections> connprofile;
	public String hostStatus;
	public String refHostMoid;
	public String serverStatus;
	public String refHostName;
	public String vCProfileStatus;
	public String errorMessage;
	public HostConfigMismatchModel() {
		super();		
		mismatch= new ArrayList<HostConfigMismatchvSwitches>();		
		connprofile= new ArrayList<ServerProfileConnections>();
	}
	

}
