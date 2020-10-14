package com.hp.asi.hpic4vc.server.provider.data;

public class OaInfoStatus
{
	private OaStatus oa_status;
	private OaNetwork oa_network;
	private OaInfo oa_info;
	
	public OaStatus getOa_status()
	{
		return oa_status;
	}
	
	public void setOa_status(OaStatus oa_status)
	{
		this.oa_status = oa_status;
	}

	public OaNetwork getOa_network()
	{
		return oa_network;
	}

	public void setOa_network(OaNetwork oa_network)
	{
		this.oa_network = oa_network;
	}

	public OaInfo getOa_info()
	{
		return oa_info;
	}

	public void setOa_info(OaInfo oa_info)
	{
		this.oa_info = oa_info;
	}
}
