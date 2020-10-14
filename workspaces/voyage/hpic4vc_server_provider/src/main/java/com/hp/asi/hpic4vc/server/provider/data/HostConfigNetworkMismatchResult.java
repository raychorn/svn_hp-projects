package com.hp.asi.hpic4vc.server.provider.data;

import java.util.ArrayList;
import java.util.List;


public class HostConfigNetworkMismatchResult {

	private String type;
	private List<HostConfigDetailsMismatch> hostConfigDetails;
	private String message;

	public HostConfigNetworkMismatchResult() {
		super();
		this.hostConfigDetails = new ArrayList<HostConfigDetailsMismatch>();
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<HostConfigDetailsMismatch> getHostConfigDetails() {
		return hostConfigDetails;
	}

	public void setHostConfigDetails(
			List<HostConfigDetailsMismatch> hostConfigDetails) {
		this.hostConfigDetails = hostConfigDetails;
	}
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
