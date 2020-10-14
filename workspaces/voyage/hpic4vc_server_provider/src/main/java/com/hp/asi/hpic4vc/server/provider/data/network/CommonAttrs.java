package com.hp.asi.hpic4vc.server.provider.data.network;

public class CommonAttrs
{
	private String overallStatus;
	private String managedStatus;
	private String id;
	private String vcOperationalStatus;
	
	public String getOverallStatus()
	{
		return overallStatus;
	}
	
	public void setOverallStatus(String overallStatus)
	{
		this.overallStatus = overallStatus;
	}

	public String getManagedStatus()
	{
		return managedStatus;
	}

	public void setManagedStatus(String managedStatus)
	{
		this.managedStatus = managedStatus;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getVcOperationalStatus()
	{
		return vcOperationalStatus;
	}

	public void setVcOperationalStatus(String vcOperationalStatus)
	{
		this.vcOperationalStatus = vcOperationalStatus;
	}
	
}
