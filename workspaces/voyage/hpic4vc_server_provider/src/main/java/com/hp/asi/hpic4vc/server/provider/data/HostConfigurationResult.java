package com.hp.asi.hpic4vc.server.provider.data;

import java.util.ArrayList;
import java.util.List;

public class HostConfigurationResult {
	private String type;
	private List<HostConfigurationData> networks;

	public HostConfigurationResult() {
		super();
		this.networks = new ArrayList<HostConfigurationData>();
		// TODO Auto-generated constructor stub
	}

	public List<HostConfigurationData> getNetworks() {
		return networks;
	}

	public void setNetworks(List<HostConfigurationData> networks) {
		this.networks = networks;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
