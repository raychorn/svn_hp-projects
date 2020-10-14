package com.hp.asi.hpic4vc.server.provider.data;

import java.util.ArrayList;
import java.util.List;

public class ClusterSummaryHostConfigOverview {
	private String configStatus;
	private String refHost;
	private String refHostUuid;
	private String refHostMoid;
	private List<SummaryClusterMembersData> clusterMembers;

	public ClusterSummaryHostConfigOverview() {
		super();
		this.clusterMembers = new ArrayList<SummaryClusterMembersData>();
	}

	public String getConfigStatus() {
		return configStatus;
	}

	public void setConfigStatus(String configStatus) {
		this.configStatus = configStatus;
	}

	public String getRefHost() {
		return refHost;
	}

	public void setRefHost(String refHost) {
		this.refHost = refHost;
	}

	public String getRefHostUuid() {
		return refHostUuid;
	}

	public void setRefHostUuid(String refHostUuid) {
		this.refHostUuid = refHostUuid;
	}

	public String getRefHostMoid() {
		return refHostMoid;
	}

	public void setRefHostMoid(String refHostMoid) {
		this.refHostMoid = refHostMoid;
	}

	public List<SummaryClusterMembersData> getClusterMembers() {
		return clusterMembers;
	}

	public void setClusterMembers(List<SummaryClusterMembersData> clusterMembers) {
		this.clusterMembers = clusterMembers;
	}

}
