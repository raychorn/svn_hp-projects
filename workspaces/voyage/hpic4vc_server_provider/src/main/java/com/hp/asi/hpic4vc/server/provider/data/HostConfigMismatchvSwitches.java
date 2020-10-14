package com.hp.asi.hpic4vc.server.provider.data;

import java.util.ArrayList;
import java.util.List;

public class HostConfigMismatchvSwitches {
	private List<HostConfigportGroupsMismatchData> portGroups;
	private List<HostConfiguplinksMismatchData> uplinks;
	private String action;
	private String name;

	public HostConfigMismatchvSwitches() {
		super();
		this.portGroups = new ArrayList<HostConfigportGroupsMismatchData>();
		this.uplinks = new ArrayList<HostConfiguplinksMismatchData>();

	}

	public List<HostConfigportGroupsMismatchData> getPortGroups() {
		return portGroups;
	}

	public void setPortGroups(List<HostConfigportGroupsMismatchData> portGroups) {
		this.portGroups = portGroups;
	}

	public List<HostConfiguplinksMismatchData> getUplinks() {
		return uplinks;
	}

	public void setUplinks(List<HostConfiguplinksMismatchData> uplinks) {
		this.uplinks = uplinks;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
