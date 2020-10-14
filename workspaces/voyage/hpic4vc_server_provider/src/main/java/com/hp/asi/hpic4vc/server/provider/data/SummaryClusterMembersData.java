package com.hp.asi.hpic4vc.server.provider.data;

public class SummaryClusterMembersData {
	private String hostName;
	private String hostMoid;
	private String consistencyStatus;

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getHostMoid() {
		return hostMoid;
	}

	public void setHostMoid(String hostMoid) {
		this.hostMoid = hostMoid;
	}

	public String getConsistencyStatus() {
		return consistencyStatus;
	}

	public void setConsistencyStatus(String consistencyStatus) {
		this.consistencyStatus = consistencyStatus;
	}

}
