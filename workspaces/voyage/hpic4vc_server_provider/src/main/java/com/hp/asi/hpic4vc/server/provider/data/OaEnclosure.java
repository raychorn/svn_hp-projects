package com.hp.asi.hpic4vc.server.provider.data;

public class OaEnclosure
{
	private int powerSupplyBays;
	private int fanBays;
	private String name;
	private String serialNumber;
	private int bladeBays;
	private int interconnectTrayBays;
	private String enclosureName;	
	private String rackName;	
	private String partNumber;	
	private String chassisSparePartNumber;
	private String uuid;
	
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

	public String getRackName()
	{
		return rackName;
	}

	public void setRackName(String rackName)
	{
		this.rackName = rackName;
	}

	public String getPartNumber()
	{
		return partNumber;
	}

	public void setPartNumber(String partNumber)
	{
		this.partNumber = partNumber;
	}
	
	public String getUuid()
	{
		return uuid;
	}

	public void setUuid(String enclosureUuid)
	{
		this.uuid = enclosureUuid;
	}

	public String getChassisSparePartNumber()
	{
		return chassisSparePartNumber;
	}

	public void setChassisSparePartNumber(String chassisSparePartNumber)
	{
		this.chassisSparePartNumber = chassisSparePartNumber;
	}

	public int getInterconnectTrayBays()
	{
		return interconnectTrayBays;
	}

	public void setInterconnectTrayBays(int interconnectTrayBays)
	{
		this.interconnectTrayBays = interconnectTrayBays;
	}
	
	
}
