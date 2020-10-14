package com.hp.asi.hpic4vc.server.provider.data.network;

import java.util.ArrayList;
import java.util.List;

public class DVS
{
	private String host;
	private String name;
	private List<UplinkPortGroup> uplink_port_groups; 
	private List<PortGroup> downlink_port_groups;
	
	public DVS()
	{
		uplink_port_groups = new ArrayList<UplinkPortGroup>();
		downlink_port_groups = new ArrayList<PortGroup>();
	}
	
	public String getHost()
	{
		return host;
	}
	
	public void setHost(String host)
	{
		this.host = host;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public List<UplinkPortGroup> getUplink_port_groups()
	{
		return uplink_port_groups;
	}

	public void setUplink_port_groups(List<UplinkPortGroup> uplink_port_groups)
	{
		this.uplink_port_groups = uplink_port_groups;
	}

	public List<PortGroup> getDownlink_port_groups()
	{
		return downlink_port_groups;
	}

	public void setDownlink_port_groups(List<PortGroup> downlink_port_groups)
	{
		this.downlink_port_groups = downlink_port_groups;
	}
}
