package com.hp.asi.hpic4vc.server.provider.model;

import java.util.ArrayList;
import java.util.List;

import com.hp.asi.hpic4vc.provider.model.BaseModel;
import com.hp.asi.hpic4vc.server.provider.data.OasClusterInfraSummary;

public class ClusterInfraSummaryModel extends BaseModel{
	
	private List<OasClusterInfraSummary> oas;

	public List<OasClusterInfraSummary> getOas() {
		return oas;
	}

	public void setOas(List<OasClusterInfraSummary> oas) {
		this.oas = oas;
	}

	public ClusterInfraSummaryModel() {
		super();
		this.oas = new ArrayList<OasClusterInfraSummary>();
	}

}
