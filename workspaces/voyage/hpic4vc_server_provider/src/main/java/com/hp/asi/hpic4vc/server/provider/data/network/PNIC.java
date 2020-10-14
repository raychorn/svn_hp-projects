package com.hp.asi.hpic4vc.server.provider.data.network;

import java.util.List;

public class PNIC
{
	private String deviceName;
	private Boolean wakeOnLanSupported;
	private Float speedGb;
	private Boolean vmDirectPathGen2Supported;
	private String driver;
	private List<LinkSpeed> validLinkSpecification;
	private LinkSpeed linkSpeed;
	private String mac;
	private String pci;
	private Boolean autoNegotiateSupported;
	private String key;
	private Boolean resourcePoolSchedulerAllowed;
	private String device;	
	private String vendorName;
	private PhysicalPortMapping physicalPortMapping;
	
	public String getDeviceName()
	{
		return deviceName;
	}
	
	public void setDeviceName(String deviceName)
	{
		this.deviceName = deviceName;
	}

	public Boolean getWakeOnLanSupport()
	{
		return wakeOnLanSupported;
	}

	public void setWakeOnLanSupport(Boolean wakeOnLanSupport)
	{
		this.wakeOnLanSupported = wakeOnLanSupport;
	}

	public Float getSpeedGb()
	{
		return speedGb;
	}

	public void setSpeedGb(Float speedGb)
	{
		this.speedGb = speedGb;
	}

	public Boolean getVmDirectPathGen2Supported()
	{
		return vmDirectPathGen2Supported;
	}

	public void setVmDirectPathGen2Supported(Boolean vmDirectPathGen2Supported)
	{
		this.vmDirectPathGen2Supported = vmDirectPathGen2Supported;
	}

	public String getDriver()
	{
		return driver;
	}

	public void setDriver(String driver)
	{
		this.driver = driver;
	}

	public List<LinkSpeed> getValidLinkSpecification()
	{
		return validLinkSpecification;
	}

	public void setValidLinkSpecification(List<LinkSpeed> validLinkSpecification)
	{
		this.validLinkSpecification = validLinkSpecification;
	}

	public LinkSpeed getLinkSpeed()
	{
		return linkSpeed;
	}

	public void setLinkSpeed(LinkSpeed linkSpeed)
	{
		this.linkSpeed = linkSpeed;
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

	public Boolean getAutoNegotiateSupported()
	{
		return autoNegotiateSupported;
	}

	public void setAutoNegotiateSupported(Boolean autoNegotiateSupported)
	{
		this.autoNegotiateSupported = autoNegotiateSupported;
	}

	public String getKey()
	{
		return key;
	}

	public void setKey(String key)
	{
		this.key = key;
	}

	public Boolean getResourcePoolSchedulerAllowed()
	{
		return resourcePoolSchedulerAllowed;
	}

	public void setResourcePoolSchedulerAllowed(
			Boolean resourcePoolSchedulerAllowed)
	{
		this.resourcePoolSchedulerAllowed = resourcePoolSchedulerAllowed;
	}

	public String getDevice()
	{
		return device;
	}

	public void setDevice(String device)
	{
		this.device = device;
	}

	public String getVendorName()
	{
		return vendorName;
	}

	public void setVendorName(String vendorName)
	{
		this.vendorName = vendorName;
	}

	public PhysicalPortMapping getPhysicalPortMapping()
	{
		return physicalPortMapping;
	}

	public void setPhysicalPortMapping(PhysicalPortMapping physicalPortMapping)
	{
		this.physicalPortMapping = physicalPortMapping;
	}
	
	
	
	
}
