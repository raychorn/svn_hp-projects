package com.hp.asi.hpic4vc.server.provider.data;

public class OaFanInfo
{
	private String name;
	private int bayNumber;
	private int fanSpeed;
	private int maxFanSpeed;
	private int powerConsumed;
	private String partNumber;
	private String sparePartNumber;
	private String presence;
	


	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}

	public int getBayNumber()
	{
		return bayNumber;
	}

	public void setBayNumber(int bayNumber)
	{
		this.bayNumber = bayNumber;
	}

	public int getFanSpeed()
	{
		return fanSpeed;
	}

	public void setFanSpeed(int fanSpeed)
	{
		this.fanSpeed = fanSpeed;
	}

	public int getMaxFanSpeed()
	{
		return maxFanSpeed;
	}

	public void setMaxFanSpeed(int maxFanSpeed)
	{
		this.maxFanSpeed = maxFanSpeed;
	}

	public int getPowerConsumed()
	{
		return powerConsumed;
	}

	public void setPowerConsumed(int powerConsumed)
	{
		this.powerConsumed = powerConsumed;
	}

	public String getPartNumber()
	{
		return partNumber;
	}

	public void setPartNumber(String partNumber)
	{
		this.partNumber = partNumber;
	}

	public String getSparePartNumber()
	{
		return sparePartNumber;
	}

	public void setSparePartNumber(String sparePartNumber)
	{
		this.sparePartNumber = sparePartNumber;
	}
	
	public void setPresence(String presence) {
		this.presence = presence;
	}

	public String getPresence() {
		return presence;
	}

	
	
	
}
