package com.hp.asi.hpic4vc.server.provider.model;

import com.hp.asi.hpic4vc.provider.model.BaseModel;

public class UpdateSoftwareComponentMessagModel extends BaseModel {
	private String status;
	private String message;
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
}
