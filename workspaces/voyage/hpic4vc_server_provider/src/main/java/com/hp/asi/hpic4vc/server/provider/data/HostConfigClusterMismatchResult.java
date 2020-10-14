package com.hp.asi.hpic4vc.server.provider.data;

import java.util.ArrayList;
import java.util.List;

public class HostConfigClusterMismatchResult {

	private String type;
	private List<ClusterConfigDetailsData> clusterConfigDetails;

	public HostConfigClusterMismatchResult() {
		super();
		this.clusterConfigDetails = new ArrayList<ClusterConfigDetailsData>();
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<ClusterConfigDetailsData> getClusterConfigDetails() {
		return clusterConfigDetails;
	}

	public void setClusterConfigDetails(
			List<ClusterConfigDetailsData> clusterConfigDetails) {
		this.clusterConfigDetails = clusterConfigDetails;
	}

}
