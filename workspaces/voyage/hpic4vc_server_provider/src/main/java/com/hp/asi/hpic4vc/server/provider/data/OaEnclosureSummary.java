package com.hp.asi.hpic4vc.server.provider.data;

public class OaEnclosureSummary
{
	private int powerSupplyBays;
	private int fanBays;
	private String name;
	private String serialNumber;
	private int bladeBays;
	private String enclosureName;
	private int bladesPresent;
	private int fansPresent;
	private String rackName;
	private int powerSuppliesPresent;
	
	public int getPowerSupplyBays()
	{
		return powerSupplyBays;
	}
	
	public void setPowerSupplyBays(int powerSupplyBays)
	{
		this.powerSupplyBays = powerSupplyBays;
	}
	
	public int getFanBays()
	{
		return fanBays;
	}
	
	public void setFanBays(int fanBays)
	{
		this.fanBays = fanBays;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getSerialNumber()
	{
		return serialNumber;
	}
	
	public void setSerialNumber(String serialNumber)
	{
		this.serialNumber = serialNumber;
	}

	public int getBladeBays()
	{
		return bladeBays;
	}

	public void setBladeBays(int bladeBays)
	{
		this.bladeBays = bladeBays;
	}

	public String getEnclosureName()
	{
		return enclosureName;
	}

	public void setEnclosureName(String enclosureName)
	{
		this.enclosureName = enclosureName;
	}

	public int getBladesPresent()
	{
		return bladesPresent;
	}

	public void setBladesPresent(int bladesPresent)
	{
		this.bladesPresent = bladesPresent;
	}

	public int getFansPresent()
	{
		return fansPresent;
	}

	public void setFansPresent(int fansPresent)
	{
		this.fansPresent = fansPresent;
	}

	public String getRackName()
	{
		return rackName;
	}

	public void setRackName(String rackName)
	{
		this.rackName = rackName;
	}

	public int getPowerSuppliesPresent()
	{
		return powerSuppliesPresent;
	}

	public void setPowerSuppliesPresent(int powerSuppliesPresent)
	{
		this.powerSuppliesPresent = powerSuppliesPresent;
	}
	
	
}
