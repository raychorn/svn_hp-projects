package com.hp.asi.hpic4vc.server.provider.data;

import java.util.List;

public class ManageSmartComponentsResult extends ResultBase {
	private String status;
	private String message;
	private List<Component> components;
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public List<Component> getComponents() {
		return components;
	}
	public void setComponents(List<Component> components) {
		this.components = components;
	}
	
}
