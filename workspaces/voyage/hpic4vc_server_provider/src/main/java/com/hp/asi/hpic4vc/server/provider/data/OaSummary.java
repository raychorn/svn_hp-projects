package com.hp.asi.hpic4vc.server.provider.data;

public class OaSummary
{
	private OaEnclosureSummary enclosure_info;
	private OaPowerSummary power;
	
	public OaEnclosureSummary getEnclosure_info()
	{
		return enclosure_info;
	}
	
	public void setEnclosure_info(OaEnclosureSummary enclosure_info)
	{
		this.enclosure_info = enclosure_info;
	}

	public OaPowerSummary getPower()
	{
		return power;
	}

	public void setPower(OaPowerSummary power)
	{
		this.power = power;
	}
	
	
}
