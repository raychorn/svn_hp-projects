package com.hp.asi.hpic4vc.server.provider.data;

public class LogicalDrive
{
	private String stripe_size;
	private String fault_tolerance;
	private String capacity;
	private String name;
	
	public String getStripe_size()
	{
		return stripe_size;
	}
	
	public void setStripe_size(String stripe_size)
	{
		this.stripe_size = stripe_size;
	}

	public String getFault_tolerance()
	{
		return fault_tolerance;
	}

	public void setFault_tolerance(String fault_tolerance)
	{
		this.fault_tolerance = fault_tolerance;
	}

	public String getCapacity()
	{
		return capacity;
	}

	public void setCapacity(String capacity)
	{
		this.capacity = capacity;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
	
	
}
