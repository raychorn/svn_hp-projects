package com.hp.asi.hpic4vc.server.provider.data;

import java.util.ArrayList;
import java.util.List;


public class HostDetailResult extends HostSummaryResult
{
	
	private List<CpuInfo> cpus;
	private List<StorageDetail> storage_detail;
	private ManagementProcessor management_processor;
	private List<SoftwareFirmware> firmware;
	private List<SoftwareFirmware> software;
	private List<MemoryModule> memory_modules;	
	private ImlLog iml;
	private ImlLog ilolog;
	private String power_cost_advantage;
	
    public HostDetailResult()
    {
    	super();    	
    	this.cpus = new ArrayList<CpuInfo>();
    	this.storage_detail = new ArrayList<StorageDetail>();    	
    	this.management_processor = new ManagementProcessor();    	
    	this.firmware = new ArrayList<SoftwareFirmware>();
    	this.software = new ArrayList<SoftwareFirmware>();
    	this.memory_modules = new ArrayList<MemoryModule>();
    	this.iml = new ImlLog();
    	this.ilolog = new ImlLog();
    	this.power_cost_advantage = "";
    	
    }

	public List<CpuInfo> getCpus()
	{
		return cpus;
	}

	public void setCpus(List<CpuInfo> cpus)
	{
		this.cpus = cpus;
	}

	public List<StorageDetail> getStorage_detail()
	{
		return storage_detail;
	}

	public void setStorage_detail(List<StorageDetail> storage_detail)
	{
		this.storage_detail = storage_detail;
	}

	public List<SoftwareFirmware> getFirmware()
	{
		return firmware;
	}

	public void setFirmware(List<SoftwareFirmware> firmware)
	{
		this.firmware = firmware;
	}

	public List<SoftwareFirmware> getSoftware()
	{
		return software;
	}

	public void setSoftware(List<SoftwareFirmware> software)
	{
		this.software = software;
	}

	public List<MemoryModule> getMemory_modules()
	{
		return memory_modules;
	}

	public void setMemory_modules(List<MemoryModule> memory_modules)
	{
		this.memory_modules = memory_modules;
	}

	public ImlLog getIml()
	{
		return iml;
	}

	public void setIml(ImlLog iml)
	{
		this.iml = iml;
	}

	public ImlLog getIlolog()
	{
		return ilolog;
	}

	public void setIlolog(ImlLog ilolog)
	{
		this.ilolog = ilolog;
	}

	public ManagementProcessor getManagement_processor()
	{
		return management_processor;
	}

	public void setManagement_processor(ManagementProcessor management_processor)
	{
		this.management_processor = management_processor;
	}

	public String getPower_cost_advantage()
	{
		return power_cost_advantage;
	}

	public void setPower_cost_advantage(String power_cost_advantage)
	{
		this.power_cost_advantage = power_cost_advantage;
	}
    
    
    
    
}
