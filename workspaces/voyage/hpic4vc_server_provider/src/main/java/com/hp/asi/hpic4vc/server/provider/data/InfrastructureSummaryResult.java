package com.hp.asi.hpic4vc.server.provider.data;

public class InfrastructureSummaryResult extends ResultBase
{	
	private OaSummary oa;

	public InfrastructureSummaryResult()
	{
		super();		
	}
	
	public OaSummary getOa()
	{
		return oa;
	}

	public void setOa(OaSummary oa)
	{
		this.oa = oa;
	}
	
}
