package com.hp.asi.hpic4vc.server.provider.data;

import java.util.ArrayList;
import java.util.List;

public class ServerProfileConnections {
	private List<ServerProfileConnectionsNetworks> networks;
	private String uplink;
	private String status;

	public ServerProfileConnections() {
		super();
		this.networks = new ArrayList<ServerProfileConnectionsNetworks>();
	}

	public List<ServerProfileConnectionsNetworks> getNetworks() {
		return networks;
	}

	public void setNetworks(List<ServerProfileConnectionsNetworks> networks) {
		this.networks = networks;
	}

	public String getUplink() {
		return uplink;
	}

	public void setUplink(String uplink) {
		this.uplink = uplink;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
