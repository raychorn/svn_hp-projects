package com.hp.asi.hpic4vc.server.provider.data.Deployment;

public class ServerNodesData {
	
	private String name;
	private String macAddress;
	private String UUID;
	private String deploymentServerID;
	private String hardwareModel;
	private String uri;
	private String managementIP;
	private String state;
	private String opswLifecycle;
	
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	public String getHardwareModel() {
		return hardwareModel;
	}
	public void setHardwareModel(String hardwareModel) {
		this.hardwareModel = hardwareModel;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMacAddress() {
		return macAddress;
	}
	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}
	public String getUUID() {
		return UUID;
	}
	public void setUUID(String uUID) {
		UUID = uUID;
	}
	public String getDeploymentServerID() {
		return deploymentServerID;
	}
	public void setDeploymentServerID(String deploymentServerID) {
		this.deploymentServerID = deploymentServerID;
	}
	public String getManagementIP() {
		return managementIP;
	}
	public void setManagementIP(String managementIP) {
		this.managementIP = managementIP;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getOpswLifecycle() {
		return opswLifecycle;
	}
	public void setOpswLifecycle(String opswLifecycle) {
		this.opswLifecycle = opswLifecycle;
	}

}
