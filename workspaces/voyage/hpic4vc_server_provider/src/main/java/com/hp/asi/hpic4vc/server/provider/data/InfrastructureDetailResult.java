package com.hp.asi.hpic4vc.server.provider.data;

public class InfrastructureDetailResult extends ResultBase
{
	private OaDetail oa;

	public InfrastructureDetailResult()
	{
		super();
		oa = new OaDetail();
	}
	
	public OaDetail getOa()
	{
		return oa;
	}

	public void setOa(OaDetail oa)
	{
		this.oa = oa;
	}
}
