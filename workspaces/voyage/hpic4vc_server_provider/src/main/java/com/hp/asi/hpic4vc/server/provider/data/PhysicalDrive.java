package com.hp.asi.hpic4vc.server.provider.data;


public class PhysicalDrive
{
	private String iface;
	private String serial_number;
	private String size;
	private String negotiated_speed;
	private String name;
		
	public String getInterface()
	{
		return iface;
	}
		
	public void setInterface(String iface)
	{
		this.iface = iface;
	}

	public String getSerial_number()
	{
		return serial_number;
	}

	public void setSerial_number(String serial_number)
	{
		this.serial_number = serial_number;
	}

	public String getSize()
	{
		return size;
	}

	public void setSize(String size)
	{
		this.size = size;
	}

	public String getNegotiated_speed()
	{
		return negotiated_speed;
	}

	public void setNegotiated_speed(String negotiated_speed)
	{
		this.negotiated_speed = negotiated_speed;
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
