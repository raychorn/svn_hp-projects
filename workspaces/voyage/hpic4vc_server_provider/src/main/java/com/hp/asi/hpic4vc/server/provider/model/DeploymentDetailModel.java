package com.hp.asi.hpic4vc.server.provider.model;

import java.util.ArrayList;
import java.util.List;

import com.hp.asi.hpic4vc.provider.model.BaseModel;
import com.hp.asi.hpic4vc.server.provider.data.Deployment.DeploymentServerDetail;
import com.hp.asi.hpic4vc.server.provider.data.Deployment.ServerNodesData;

public class DeploymentDetailModel extends BaseModel {
	
	private List<ServerNodesData> serverNodes;

	public List<ServerNodesData> getServerNodes() {
		return serverNodes;
	}

	public void setServerNodes(List<ServerNodesData> serverNodes) {
		this.serverNodes = serverNodes;
	}

	public DeploymentDetailModel() {
		super();

		this.serverNodes = new ArrayList<ServerNodesData>();
	}

	

}
