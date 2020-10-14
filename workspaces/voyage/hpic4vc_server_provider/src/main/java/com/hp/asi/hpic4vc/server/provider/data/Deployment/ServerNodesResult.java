package com.hp.asi.hpic4vc.server.provider.data.Deployment;

import java.util.ArrayList;
import java.util.List;

public class ServerNodesResult {
	
	private String type;
	
	private List<DeploymentServerDetail> members;
	
	public ServerNodesResult() {
		super();
		this.members = new ArrayList<DeploymentServerDetail>();
	}

	public List<DeploymentServerDetail> getMembers() {
		return members;
	}

	public void setMembers(List<DeploymentServerDetail> members) {
		this.members = members;
	}


	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}


}
