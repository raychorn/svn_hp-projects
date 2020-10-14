package com.hp.asi.hpic4vc.server.provider.model;

public class VCNetworkModel
{
	public String name;
	public String uplinkVlanId;
	
	public VCNetworkModel()
	{
		
	}
	
	public VCNetworkModel(String name, String uplinkVlanId)
	{
		this.name = name;
		this.uplinkVlanId = uplinkVlanId;
	}
}
