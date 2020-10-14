package com.hp.asi.hpic4vc.server.provider.data.network;

import java.util.ArrayList;
import java.util.List;

public class DataStore
{
	private long freeSpace;
	private String name;
	private long maxFileSize;
	private String key;
	private List<HBA> hbas;
	private List<VM> vms;
	
	
	
	public DataStore()
	{
		hbas = new ArrayList<HBA>();
		freeSpace = 0;
		maxFileSize = 0;		
	}
	
	public long getFreeSpace()
	{
		return freeSpace;
	}
	
	public void setFreeSpace(long freeSpace)
	{
		this.freeSpace = freeSpace;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public long getMaxFileSize()
	{
		return maxFileSize;
	}
	
	public void setMaxFileSize(long maxFileSize)
	{
		this.maxFileSize = maxFileSize;
	}
	
	public String getKey()
	{
		return key;
	}
	
	public void setKey(String key)
	{
		this.key = key;
	}
	
	public List<HBA> getHbas()
	{
		return hbas;
	}
	
	public void setHbas(List<HBA> hbas)
	{
		this.hbas = hbas;
	}

	public List<VM> getVms() {
		return vms;
	}

	public void setVms(List<VM> vms) {
		this.vms = vms;
	}
	
	
		
}
