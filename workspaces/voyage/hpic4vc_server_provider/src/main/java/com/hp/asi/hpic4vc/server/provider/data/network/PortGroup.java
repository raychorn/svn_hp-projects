package com.hp.asi.hpic4vc.server.provider.data.network;

import java.util.List;

public class PortGroup
{
	private String key;
	private String name;
	private String vlanId;
	private List<VM> vms;
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}

	public String getVlanId()
	{
		return vlanId;
	}

	public void setVlanId(String vlanId)
	{
		this.vlanId = vlanId;
	}

	public List<VM> getVms()
	{
		return vms;
	}

	public void setVms(List<VM> vms)
	{
		this.vms = vms;
	}

	public String getKey()
	{
		return key;
	}

	public void setKey(String key)
	{
		this.key = key;
	}
}
