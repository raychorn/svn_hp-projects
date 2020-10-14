package com.hp.asi.hpic4vc.server.provider.data.network;

public class FabricDownlink
{
	private String portWWN;
	private Float speedGb;
	private int physicalServerBay;
	private String id;
	private PhysicalPortMapping physicalPortMapping;
	
	public String getPortWWN()
	{
		return portWWN;
	}
	
	public void setPortWWN(String portWWN)
	{
		this.portWWN = portWWN;
	}

	public Float getSpeedGb()
	{
		return speedGb;
	}

	public void setSpeedGb(Float speedGb)
	{
		this.speedGb = speedGb;
	}

	public int getPhysicalServerBay()
	{
		return physicalServerBay;
	}

	public void setPhysicalServerBay(int physicalServerBay)
	{
		this.physicalServerBay = physicalServerBay;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public PhysicalPortMapping getPhysicalPortMapping()
	{
		return physicalPortMapping;
	}

	public void setPhysicalPortMapping(PhysicalPortMapping physicalPortMapping)
	{
		this.physicalPortMapping = physicalPortMapping;
	}
}
