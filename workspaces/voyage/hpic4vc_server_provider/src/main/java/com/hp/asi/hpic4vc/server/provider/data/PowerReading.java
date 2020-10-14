package com.hp.asi.hpic4vc.server.provider.data;

public class PowerReading
{
	private String __classname__;
	private String unit;
	private String value;
	
	public String get__classname__()
	{
		return __classname__;
	}
	
	public void set__classname__(String __classname__)
	{
		this.__classname__ = __classname__;
	}

	public String getunit()
	{
		return unit;
	}

	public void setunit(String unit)
	{
		this.unit = unit;
	}

	public String getvalue()
	{
		return value;
	}

	public void setvalue(String value)
	{
		this.value = value;
	}
	
	@Override
	public String toString()
	{
		return value + " " + unit;		
	}
}
