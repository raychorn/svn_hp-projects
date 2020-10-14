package com.hp.asi.hpic4vc.server.provider.data;

import java.util.ArrayList;
import java.util.List;

public class HostServerProfileData {
	
	private List<ServerProfileConnections> connections;
	private String name;
	private String status;

	public HostServerProfileData() {
		super();
		this.connections = new ArrayList<ServerProfileConnections>();
	}

	public List<ServerProfileConnections> getConnections() {
		return connections;
	}

	public void setConnections(List<ServerProfileConnections> connections) {
		this.connections = connections;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
