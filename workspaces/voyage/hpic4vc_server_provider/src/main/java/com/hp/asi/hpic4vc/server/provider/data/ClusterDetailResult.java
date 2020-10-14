package com.hp.asi.hpic4vc.server.provider.data;

import java.util.List;

public class ClusterDetailResult extends ResultBase {
	
	private List<Host> hosts;

	public List<Host> getHosts() {
		return hosts;
	}

	public void setHosts(List<Host> hosts) {
		this.hosts = hosts;
	}

}
