package com.hp.asi.hpic4vc.server.provider.data.network;

import java.util.ArrayList;
import java.util.List;

public class VS
{
	private int numPorts;
	private int numPortsAvailable;
	private String name;
	private List<PNIC> pnics;
	private List<PortGroup> port_groups;
	
	public VS()
	{
		pnics = new ArrayList<PNIC>();
		port_groups = new ArrayList<PortGroup>();
		numPorts = 0;
		numPortsAvailable = 0;		
		
	}
	
	public int getNumPorts()
	{
		return numPorts;
	}
	
	public void setNumPorts(int numPorts)
	{
		this.numPorts = numPorts;
	}

	public int getNumPortsAvailable()
	{
		return numPortsAvailable;
	}

	public void setNumPortsAvailable(int numPortsAvailable)
	{
		this.numPortsAvailable = numPortsAvailable;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public List<PNIC> getPnics()
	{
		return pnics;
	}

	public void setPnics(List<PNIC> pnics)
	{
		this.pnics = pnics;
	}

	public List<PortGroup> getPort_groups()
	{
		return port_groups;
	}

	public void setPort_groups(List<PortGroup> port_groups)
	{
		this.port_groups = port_groups;
	}
	
	
}
