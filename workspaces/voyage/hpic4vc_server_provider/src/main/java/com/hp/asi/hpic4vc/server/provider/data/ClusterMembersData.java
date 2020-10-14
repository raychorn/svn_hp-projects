package com.hp.asi.hpic4vc.server.provider.data;

public class ClusterMembersData {

	private String hostName;
	private String hostMoid;
	private String serverProfileConsistencyStatus;
	private String hostConfigConsistencyStatus;

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

	public String getServerProfileConsistencyStatus() {
		return serverProfileConsistencyStatus;
	}

	public void setServerProfileConsistencyStatus(
			String serverProfileConsistencyStatus) {
		this.serverProfileConsistencyStatus = serverProfileConsistencyStatus;
	}

	public String getHostConfigConsistencyStatus() {
		return hostConfigConsistencyStatus;
	}

	public void setHostConfigConsistencyStatus(
			String hostConfigConsistencyStatus) {
		this.hostConfigConsistencyStatus = hostConfigConsistencyStatus;
	}

}
