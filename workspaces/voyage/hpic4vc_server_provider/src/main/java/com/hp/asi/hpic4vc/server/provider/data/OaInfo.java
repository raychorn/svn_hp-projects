package com.hp.asi.hpic4vc.server.provider.data;

public class OaInfo
{
	private int bayNumber;
	private String name;
	private String fwVersion;
	private String serialNumber;
	private String partNumber;
	private String sparePartNumber;
	
	public int getBayNumber()
	{
		return bayNumber;
	}
	
	public void setBayNumber(int bayNumber)
	{
		this.bayNumber = bayNumber;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getFwVersion()
	{
		return fwVersion;
	}

	public void setFwVersion(String fwVersion)
	{
		this.fwVersion = fwVersion;
	}

	public String getSerialNumber()
	{
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber)
	{
		this.serialNumber = serialNumber;
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
}
