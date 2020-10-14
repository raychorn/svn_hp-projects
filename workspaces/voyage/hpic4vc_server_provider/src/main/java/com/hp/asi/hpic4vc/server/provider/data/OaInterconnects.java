package com.hp.asi.hpic4vc.server.provider.data;

import java.util.List;

public class OaInterconnects
{
	private int bayNumber;
	private String name;
	private String serialNumber;
	private String partNumber;
	private String sparePartNumber;
	private List<OaExtraData> extraData;
	
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

	public List<OaExtraData> getExtraData()
	{
		return extraData;
	}

	public void setExtraData(List<OaExtraData> extraData)
	{
		this.extraData = extraData;
	}
}
