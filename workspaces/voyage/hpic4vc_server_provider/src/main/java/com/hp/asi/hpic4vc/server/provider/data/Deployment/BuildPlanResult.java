package com.hp.asi.hpic4vc.server.provider.data.Deployment;

import java.util.ArrayList;
import java.util.List;

public class BuildPlanResult {
	
	
	private List<BuildPlanData> members;

	public BuildPlanResult() {
		super();
		this.members = new ArrayList<BuildPlanData>();
	}

	public List<BuildPlanData> getMembers() {
		return members;
	}

	public void setMembers(List<BuildPlanData> members) {
		this.members = members;
	}

}
