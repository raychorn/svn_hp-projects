package com.hp.asi.hpic4vc.server.provider.data;

import java.util.ArrayList;
import java.util.List;

public class ClusterInfrastructureDetailResult extends ResultBase{
	
	private List<OaClusterInfraDetail> oas;

	public List<OaClusterInfraDetail> getOas() {
		return oas;
	}

	public void setOas(List<OaClusterInfraDetail> oas) {
		this.oas = oas;
	}

	public ClusterInfrastructureDetailResult() {
		super();
		oas = new ArrayList<OaClusterInfraDetail>();

	}

}
