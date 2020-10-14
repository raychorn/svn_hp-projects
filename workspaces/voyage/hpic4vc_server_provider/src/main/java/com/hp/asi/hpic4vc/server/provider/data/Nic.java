package com.hp.asi.hpic4vc.server.provider.data;

public class Nic
{
	private int slot;
	private String vmnic;
	private String driver;
	private String mac;
	private String pci;
	private String physical_nic;
	private int speedMb;
	private float speedGb;
	private String vswitch;
	
	public int getSlot()
	{
		return slot;
	}
	
	public void setSlot(int slot)
	{
		this.slot = slot;
	}

	public String getVmnic()
	{
		return vmnic;
	}

	public void setVmnic(String vmnic)
	{
		this.vmnic = vmnic;
	}

	public String getDriver()
	{
		return driver;
	}

	public void setDriver(String driver)
	{
		this.driver = driver;
	}

	public String getMac()
	{
		return mac;
	}

	public void setMac(String mac)
	{
		this.mac = mac;
	}

	public String getPci()
	{
		return pci;
	}

	public void setPci(String pci)
	{
		this.pci = pci;
	}

	public String getPhysical_nic()
	{
		return physical_nic;
	}

	public void setPhysical_nic(String physical_nic)
	{
		this.physical_nic = physical_nic;
	}

	public int getSpeedMb()
	{
		return speedMb;
	}

	public void setSpeedMb(int speedMb)
	{
		this.speedMb = speedMb;
	}

	public String getVswitch()
	{
		return vswitch;
	}

	public void setVswitch(String vswitch)
	{
		this.vswitch = vswitch;
	}

	public float getSpeedGb()
	{
		return speedGb;
	}

	public void setSpeedGb(float speedGb)
	{
		this.speedGb = speedGb;
	}
	
	
}
