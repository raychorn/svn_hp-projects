package com.hp.asi.hpic4vc.server.provider.model;

import java.util.ArrayList;
import java.util.List;

import com.hp.asi.hpic4vc.provider.model.BaseModel;

public class ClusterInfrastructureModel extends BaseModel{
	
	private List<ClusterInfrastructureDetailModel> clusterInfrastructure;

	

	public ClusterInfrastructureModel() {
		super();
		this.setClusterInfrastructure(new ArrayList<ClusterInfrastructureDetailModel>());
	}



	public List<ClusterInfrastructureDetailModel> getClusterInfrastructure() {
		return clusterInfrastructure;
	}



	public void setClusterInfrastructure(List<ClusterInfrastructureDetailModel> clusterInfrastructure) {
		this.clusterInfrastructure = clusterInfrastructure;
	}

}
