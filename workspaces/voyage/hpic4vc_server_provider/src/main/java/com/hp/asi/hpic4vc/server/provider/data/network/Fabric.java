package com.hp.asi.hpic4vc.server.provider.data.network;

import java.util.List;

public class Fabric
{
	private String displayName;
	private String id;
	private List<FabricDownlink> downlinks;
	private List<String> portlinks;
	
	public String getDisplayName()
	{
		return displayName;
	}
	
	public void setDisplayName(String displayName)
	{
		this.displayName = displayName;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public List<FabricDownlink> getDownlinks()
	{
		return downlinks;
	}

	public void setDownlinks(List<FabricDownlink> downlinks)
	{
		this.downlinks = downlinks;
	}

	public List<String> getPortlinks()
	{
		return portlinks;
	}

	public void setPortlinks(List<String> portlinks)
	{
		this.portlinks = portlinks;
	}
}
