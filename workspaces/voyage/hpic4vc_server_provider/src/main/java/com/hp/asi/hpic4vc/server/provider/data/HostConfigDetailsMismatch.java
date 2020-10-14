package com.hp.asi.hpic4vc.server.provider.data;

public class HostConfigDetailsMismatch {
	private String configStatus;
	private String refHostMoid;
	private String refHostName;
	private HostConfigurationMismatch hostConfiguration;
	private HostServerProfileData serverProfile;

	public HostConfigDetailsMismatch() {
		this.hostConfiguration = new HostConfigurationMismatch();
		this.serverProfile = new HostServerProfileData();
	}

	public HostConfigurationMismatch getHostConfiguration() {
		return hostConfiguration;
	}

	public void setHostConfiguration(HostConfigurationMismatch hostConfiguration) {
		this.hostConfiguration = hostConfiguration;
	}

	public String getConfigStatus() {
		return configStatus;
	}

	public void setConfigStatus(String configStatus) {
		this.configStatus = configStatus;
	}

	public HostServerProfileData getServerProfile() {
		return serverProfile;
	}

	public void setServerProfile(HostServerProfileData serverProfile) {
		this.serverProfile = serverProfile;
	}

	public String getRefHostMoid() {
		return refHostMoid;
	}

	public void setRefHostMoid(String refHostMoid) {
		this.refHostMoid = refHostMoid;
	}

	public String getRefHostName() {
		return refHostName;
	}

	public void setRefHostName(String refHostName) {
		this.refHostName = refHostName;
	}
	

}
