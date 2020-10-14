package com.hp.asi.hpic4vc.server.provider.data;

public class OaPowerDetail
{
	private String redundancy;
	private int capacity;
	private int redundantCapacity;
	private int outputPower;
	private int powerConsumed;
	private int goodPowerSupplies;
	private int wantedPowerSupplies;
	private int neededPowerSupplies;
	
	public String getRedundancy()
	{
		return redundancy;
	}
	
	public void setRedundancy(String redundancy)
	{
		this.redundancy = redundancy;
	}

	public int getCapacity()
	{
		return capacity;
	}

	public void setCapacity(int capacity)
	{
		this.capacity = capacity;
	}

	public int getRedundantCapacity()
	{
		return redundantCapacity;
	}

	public void setRedundantCapacity(int redundantCapacity)
	{
		this.redundantCapacity = redundantCapacity;
	}

	public int getOutputPower()
	{
		return outputPower;
	}

	public void setOutputPower(int outputPower)
	{
		this.outputPower = outputPower;
	}

	public int getPowerConsumed() 
	{
		return powerConsumed;
	}

	public void setPowerConsumed(int powerConsumed) 
	{
		this.powerConsumed = powerConsumed;
	}
	

	public int getGoodPowerSupplies()
	{
		return goodPowerSupplies;
	}

	public void setGoodPowerSupplies(int goodPowerSupplies)
	{
		this.goodPowerSupplies = goodPowerSupplies;
	}

	public int getWantedPowerSupplies()
	{
		return wantedPowerSupplies;
	}

	public void setWantedPowerSupplies(int wantedPowerSupplies)
	{
		this.wantedPowerSupplies = wantedPowerSupplies;
	}

	public int getNeededPowerSupplies()
	{
		return neededPowerSupplies;
	}

	public void setNeededPowerSupplies(int neededPowerSupplies)
	{
		this.neededPowerSupplies = neededPowerSupplies;
	}
	
	
}
