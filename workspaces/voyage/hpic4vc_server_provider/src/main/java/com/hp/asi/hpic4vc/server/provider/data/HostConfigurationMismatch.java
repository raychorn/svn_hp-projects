package com.hp.asi.hpic4vc.server.provider.data;


public class HostConfigurationMismatch {

	private String hostConfigStatus;
	private HostConfigMismatchDetailedView hostConfigDetailedView;

	public String getHostConfigStatus() {
		return hostConfigStatus;
	}

	public void setHostConfigStatus(String hostConfigStatus) {
		this.hostConfigStatus = hostConfigStatus;
	}

	public HostConfigurationMismatch() {
		super();

		this.hostConfigDetailedView = new HostConfigMismatchDetailedView();
	}

	public HostConfigMismatchDetailedView getHostConfigDetailedView() {
		return hostConfigDetailedView;
	}

	public void setHostConfigDetailedView(
			HostConfigMismatchDetailedView hostConfigDetailedView) {
		this.hostConfigDetailedView = hostConfigDetailedView;
	}
}
