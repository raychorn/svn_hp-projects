package com.hp.asi.hpic4vc.server.provider.data.network;

public class HBA
{
	private String status;
	private float speedGb;
	private String pathState;
	private int bus;
	private String driver;
	private String portWorldWideName;
	private String pci;
	private String key;
	private String device;
	private String model;
	private PhysicalPortMapping physicalPortMapping;
	private String type;
	private String nodeWorldWideName;
	private int speed;
	private String mac;

	public HBA()
	{
		bus = 0;
		speedGb = 0;
		speed = 0;
	}
	
	public String getStatus()
	{
		return status;
	}
	
	public void setStatus(String status)
	{
		this.status = status;
	}

	public float getSpeedGb()
	{
		return speedGb;
	}

	public void setSpeedGb(float speedGb)
	{
		this.speedGb = speedGb;
	}

	public String getPathState()
	{
		return pathState;
	}

	public void setPathState(String pathState)
	{
		this.pathState = pathState;
	}

	public int getBus()
	{
		return bus;
	}

	public void setBus(int bus)
	{
		this.bus = bus;
	}

	public String getDriver()
	{
		return driver;
	}

	public void setDriver(String driver)
	{
		this.driver = driver;
	}

	public String getPortWorldWideName()
	{
		return portWorldWideName;
	}

	public void setPortWorldWideName(String portWorldWideName)
	{
		this.portWorldWideName = portWorldWideName;
	}

	public String getPci()
	{
		return pci;
	}

	public void setPci(String pci)
	{
		this.pci = pci;
	}

	public String getKey()
	{
		return key;
	}

	public void setKey(String key)
	{
		this.key = key;
	}

	public String getDevice()
	{
		return device;
	}

	public void setDevice(String device)
	{
		this.device = device;
	}

	public String getModel()
	{
		return model;
	}

	public void setModel(String model)
	{
		this.model = model;
	}

	public PhysicalPortMapping getPhysicalPortMapping()
	{
		return physicalPortMapping;
	}

	public void setPhysicalPortMapping(PhysicalPortMapping physicalPortMapping)
	{
		this.physicalPortMapping = physicalPortMapping;
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public String getNodeWorldWideName()
	{
		return nodeWorldWideName;
	}

	public void setNodeWorldWideName(String nodeWorldWideName)
	{
		this.nodeWorldWideName = nodeWorldWideName;
	}

	public int getSpeed()
	{
		return speed;
	}

	public void setSpeed(int speed)
	{
		this.speed = speed;
	}
	
	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}
	
}
