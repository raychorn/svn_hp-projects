package com.hp.asi.hpic4vc.server.provider.data.network;

import java.util.List;

public class UplinkPortGroup
{
	private String name;
	private List<DVSUplink> uplinks;
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}

	public List<DVSUplink> getUplinks()
	{
		return uplinks;
	}

	public void setUplinks(List<DVSUplink> uplinks)
	{
		this.uplinks = uplinks;
	}	
}
