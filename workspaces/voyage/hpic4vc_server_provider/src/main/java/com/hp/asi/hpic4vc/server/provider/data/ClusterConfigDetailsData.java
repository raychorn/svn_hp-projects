package com.hp.asi.hpic4vc.server.provider.data;

import java.util.ArrayList;
import java.util.List;
public class ClusterConfigDetailsData {

	private String configStatus;
	private String refHost;
	private String refHostUuid;
	private String refHostMoid;
	private String vmmUuid;
	private String clusterUuid;
	private String clusterMoid;
	private List<ClusterMembersData> clusterMembers;

	public ClusterConfigDetailsData() {
		super();
		this.clusterMembers = new ArrayList<ClusterMembersData>();
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

	public String getVmmUuid() {
		return vmmUuid;
	}

	public void setVmmUuid(String vmmUuid) {
		this.vmmUuid = vmmUuid;
	}

	public String getClusterUuid() {
		return clusterUuid;
	}

	public void setClusterUuid(String clusterUuid) {
		this.clusterUuid = clusterUuid;
	}

	public String getClusterMoid() {
		return clusterMoid;
	}

	public void setClusterMoid(String clusterMoid) {
		this.clusterMoid = clusterMoid;
	}

	public List<ClusterMembersData> getClusterMembers() {
		return clusterMembers;
	}

	public void setClusterMembers(List<ClusterMembersData> clusterMembers) {
		this.clusterMembers = clusterMembers;
	}

}
