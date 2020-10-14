package com.hp.asi.hpic4vc.server.provider.data.network;

import java.util.ArrayList;
import java.util.List;

public class VCM
{
	private List<ExternalStorage> externalStorage;
	private List<Enclosure> enclosures;
	private List<ExternalSwitch> externalSwitches;
	
	public VCM()
	{
		externalStorage = new ArrayList<ExternalStorage>();
		enclosures = new ArrayList<Enclosure>();
		externalSwitches = new ArrayList<ExternalSwitch>();
	}
	
	public List<ExternalStorage> getExternalStorage()
	{
		return externalStorage;
	}
	
	public void setExternalStorage(List<ExternalStorage> externalStorage)
	{
		this.externalStorage = externalStorage;
	}

	public List<Enclosure> getEnclosures()
	{
		return enclosures;
	}

	public void setEnclosures(List<Enclosure> enclosures)
	{
		this.enclosures = enclosures;
	}

	public List<ExternalSwitch> getExternalSwitches()
	{
		return externalSwitches;
	}

	public void setExternalSwitches(List<ExternalSwitch> externalSwitches)
	{
		this.externalSwitches = externalSwitches;
	}
}
