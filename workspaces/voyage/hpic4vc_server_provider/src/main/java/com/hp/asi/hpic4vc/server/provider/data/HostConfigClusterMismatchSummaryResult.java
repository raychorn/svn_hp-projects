package com.hp.asi.hpic4vc.server.provider.data;

import java.util.ArrayList;
import java.util.List;

public class HostConfigClusterMismatchSummaryResult {

	private String type;
	private List<ClusterSummaryHostConfigOverview> clusterConfigOverview;

	public HostConfigClusterMismatchSummaryResult() {
		super();
		this.clusterConfigOverview = new ArrayList<ClusterSummaryHostConfigOverview>();
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<ClusterSummaryHostConfigOverview> getClusterConfigOverview() {
		return clusterConfigOverview;
	}

	public void setClusterConfigOverview(
			List<ClusterSummaryHostConfigOverview> clusterConfigOverview) {
		this.clusterConfigOverview = clusterConfigOverview;
	}

}
