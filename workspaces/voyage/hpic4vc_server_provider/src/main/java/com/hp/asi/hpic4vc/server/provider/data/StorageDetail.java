package com.hp.asi.hpic4vc.server.provider.data;

public class StorageDetail
{
	private String name;
	private String firmware;
	private String serial_number;
	private String model;
	private String manufacturer;
	
	private LogicalDrive logical_drive;
	private PhysicalDrive physical_drive;
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}

	public String getFirmware()
	{
		return firmware;
	}

	public void setFirmware(String firmware)
	{
		this.firmware = firmware;
	}

	public String getSerial_number()
	{
		return serial_number;
	}

	public void setSerial_number(String serial_number)
	{
		this.serial_number = serial_number;
	}

	public String getModel()
	{
		return model;
	}

	public void setModel(String model)
	{
		this.model = model;
	}

	public String getManufacturer()
	{
		return manufacturer;
	}

	public void setManufacturer(String manufacturer)
	{
		this.manufacturer = manufacturer;
	}

	public LogicalDrive getLogical_drive()
	{
		return logical_drive;
	}

	public void setLogical_drive(LogicalDrive logical_drive)
	{
		this.logical_drive = logical_drive;
	}

	public PhysicalDrive getPhysical_drive()
	{
		return physical_drive;
	}

	public void setPhysical_drive(PhysicalDrive physical_drive)
	{
		this.physical_drive = physical_drive;
	}
	
	
}
