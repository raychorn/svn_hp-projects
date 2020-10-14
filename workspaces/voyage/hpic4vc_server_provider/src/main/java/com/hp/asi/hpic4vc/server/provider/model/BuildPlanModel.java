package com.hp.asi.hpic4vc.server.provider.model;

import java.util.ArrayList;
import java.util.List;

import com.hp.asi.hpic4vc.provider.model.BaseModel;
import com.hp.asi.hpic4vc.server.provider.data.Deployment.BuildPlanData;

public class BuildPlanModel extends BaseModel {
	
	private List<BuildPlanData>  buildPlan;

	public List<BuildPlanData> getBuildPlan() {
		return buildPlan;
	}

	public void setBuildPlan(List<BuildPlanData> buildPlan) {
		this.buildPlan = buildPlan;
	}

	public BuildPlanModel() {
		super();
		this.buildPlan = new ArrayList<BuildPlanData>();
		// TODO Auto-generated constructor stub
	}
	

}
