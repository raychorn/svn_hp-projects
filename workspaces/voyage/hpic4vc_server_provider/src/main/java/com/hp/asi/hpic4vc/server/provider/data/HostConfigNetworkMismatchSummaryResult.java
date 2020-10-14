package com.hp.asi.hpic4vc.server.provider.data;

import java.util.ArrayList;
import java.util.List;

public class HostConfigNetworkMismatchSummaryResult {

	private String type;
	private List<NetworkSummaryHostConfigOverview> hostConfigOverview;
	private String message;

	public HostConfigNetworkMismatchSummaryResult() {
		super();
		this.hostConfigOverview = new ArrayList<NetworkSummaryHostConfigOverview>();
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<NetworkSummaryHostConfigOverview> getHostConfigOverview() {
		return hostConfigOverview;
	}

	public void setHostConfigOverview(
			List<NetworkSummaryHostConfigOverview> hostConfigOverview) {
		this.hostConfigOverview = hostConfigOverview;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
