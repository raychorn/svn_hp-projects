package com.hp.asi.hpic4vc.server.provider.data.network;

import java.util.List;

public class VCNetwork
{
	private int maxPortSpeed;
	private int preferredPortSpeed;
	private String uplinkVLANId;
	private String displayName;
	private List<String> portlinks;
	private List<VCDownlink> downlinks;
	private String id;
	private Boolean bottleNeck;
	
	public int getMaxPortSpeed()
	{
		return maxPortSpeed;
	}
	
	public void setMaxPortSpeed(int maxPortSpeed)
	{
		this.maxPortSpeed = maxPortSpeed;
	}

	public int getPreferredPortSpeed()
	{
		return preferredPortSpeed;
	}

	public void setPreferredPortSpeed(int preferredPortSpeed)
	{
		this.preferredPortSpeed = preferredPortSpeed;
	}

	public String getUplinkVLANId()
	{
		return uplinkVLANId;
	}

	public void setUplinkVLANId(String uplinkVLANId)
	{
		this.uplinkVLANId = uplinkVLANId;
	}

	public String getDisplayName()
	{
		return displayName;
	}

	public void setDisplayName(String displayName)
	{
		this.displayName = displayName;
	}

	public List<String> getPortlinks()
	{
		return portlinks;
	}

	public void setPortlinks(List<String> portlinks)
	{
		this.portlinks = portlinks;
	}

	public List<VCDownlink> getDownlinks()
	{
		return downlinks;
	}

	public void setDownlinks(List<VCDownlink> downlinks)
	{
		this.downlinks = downlinks;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public Boolean getBottleNeck()
	{
		return bottleNeck;
	}

	public void setBottleNeck(Boolean bottleNeck)
	{
		this.bottleNeck = bottleNeck;
	}
}
