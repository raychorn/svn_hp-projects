package com.hp.asi.hpic4vc.server.provider.data.Deployment;

import java.util.ArrayList;
import java.util.List;

public class DeploymentServerDetail {
	
private String name;
	
	private String hardwareModel;
	
	private String uuid;
	
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
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	private List<InterfaceData> interfaces;
	
	public List<InterfaceData> getInterfaces() {
		return interfaces;
	}
	public void setInterfaces(List<InterfaceData> interfaces) {
		this.interfaces = interfaces;
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
	
	public DeploymentServerDetail() {
		super();
		this.interfaces = new ArrayList<InterfaceData>();
		// 
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
