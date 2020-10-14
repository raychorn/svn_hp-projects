package com.hp.asi.hpic4vc.server.provider.data;

import java.util.ArrayList;
import java.util.List;


public class ClusterInfrastructureSummaryResult extends ResultBase {
	
	private List<OasClusterInfraSummary> oas;
	


	public ClusterInfrastructureSummaryResult()
	{
		super();
		
		this.oas = new ArrayList<OasClusterInfraSummary>();
	}


	public List<OasClusterInfraSummary> getOas() {
		return oas;
	}



	public void setOas(List<OasClusterInfraSummary> oas) {
		this.oas = oas;
	}

	
}
