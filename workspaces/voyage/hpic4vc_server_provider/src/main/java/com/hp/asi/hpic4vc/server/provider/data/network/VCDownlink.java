package com.hp.asi.hpic4vc.server.provider.data.network;

public class VCDownlink
{
	private String macAddress;
	private PhysicalPortMapping physicalPortMapping;
	private String id;
	private Float speedGb;
	
	public String getMacAddress()
	{
		return macAddress;
	}
	
	public void setMacAddress(String macAddress)
	{
		this.macAddress = macAddress;
	}

	public PhysicalPortMapping getPhysicalPortMapping()
	{
		return physicalPortMapping;
	}

	public void setPhysicalPortMapping(PhysicalPortMapping physicalPortMapping)
	{
		this.physicalPortMapping = physicalPortMapping;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public Float getSpeedGb()
	{
		return speedGb;
	}

	public void setSpeedGb(Float speedGb)
	{
		this.speedGb = speedGb;
	}
	
}
