package com.hp.asi.hpic4vc.server.provider.model;

import java.util.ArrayList;
import java.util.List;

import com.hp.asi.hpic4vc.server.provider.data.network.PortGroup;

public class VirtualSwitchModel
{
	public int numPorts;
	public int numPortsAvailable;
	public String name;	
	public List<PortGroup> portGroups;
	
	public VirtualSwitchModel()
	{
		portGroups = new ArrayList<PortGroup>();		
	}
}
