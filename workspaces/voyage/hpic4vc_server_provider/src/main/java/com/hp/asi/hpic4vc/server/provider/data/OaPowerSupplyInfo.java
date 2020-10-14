package com.hp.asi.hpic4vc.server.provider.data;

import java.util.List;

public class OaPowerSupplyInfo
{
	private String modelNumber;
	private int bayNumber;
	private List<OaExtraData> extraData;
	private int capacity;
	private int actualOutput;
	private String serialNumber;
	private String partNumber;
	private String sparePartNumber;
	
	public String getProductName()
	{
		String pName = "";
		for(OaExtraData item:extraData)
		{
			if(item.get_name().equals("productName"))
			{
				pName = item.getValue();
				
			}
			
		}
		
		return pName;
	}
	
	public String getModelNumber() {
		return modelNumber;
	}

	public void setModelNumber(String modelNumber) {
		this.modelNumber = modelNumber;
	}
	
	
	public int getBayNumber()
	{
		return bayNumber;
	}
	
	public void setBayNumber(int bayNumber)
	{
		this.bayNumber = bayNumber;
	}

	public List<OaExtraData> getExtraData()
	{
		return extraData;
	}

	public void setExtraData(List<OaExtraData> extraData)
	{
		this.extraData = extraData;
	}

	public int getCapacity()
	{
		return capacity;
	}

	public void setCapacity(int capacity)
	{
		this.capacity = capacity;
	}

	public int getActualOutput()
	{
		return actualOutput;
	}

	public void setActualOutput(int actualOutput)
	{
		this.actualOutput = actualOutput;
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
