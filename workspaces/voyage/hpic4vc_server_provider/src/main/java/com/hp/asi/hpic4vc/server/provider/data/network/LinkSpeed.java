package com.hp.asi.hpic4vc.server.provider.data.network;

public class LinkSpeed
{
	private Boolean duplex;
	private int speedMb;
	
	public Boolean getDuplex()
	{
		return duplex;
	}
	
	public void setDuplex(Boolean duplex)
	{
		this.duplex = duplex;
	}

	public int getSpeedMb()
	{
		return speedMb;
	}

	public void setSpeedMb(int speedMb)
	{
		this.speedMb = speedMb;
	}
}
