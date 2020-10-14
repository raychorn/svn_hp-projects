package com.hp.asi.hpic4vc.server.provider.data.network;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonSetter;

public class ExternalSwitch
{
	private String id;
	private String remote_system_desc;
	private String remote_system_capabilities;
	private String remote_chassis_id;
	private String remote_system_name;
	private List<ExternalSwitchPort> ports;
	
	public ExternalSwitch()
	{
		ports = new ArrayList<ExternalSwitchPort>();
	}
	
	public String getId()
	{
		return id;
	}
	
	public void setId(String id)
	{
		this.id = id;
	}

	@JsonProperty("remote-system-desc")
	public String getRemote_system_desc()
	{
		return remote_system_desc;
	}

	@JsonSetter("remote-system-desc")
	public void setRemote_system_desc(String remote_system_desc)
	{
		this.remote_system_desc = remote_system_desc;
	}
	
	@JsonProperty("remote-system-capabilities")
	public String getRemote_system_capabilities()
	{
		return remote_system_capabilities;
	}

	@JsonSetter("remote-system-capabilities")
	public void setRemote_system_capabilities(String remote_system_capabilities)
	{
		this.remote_system_capabilities = remote_system_capabilities;
	}

	@JsonProperty("remote-chassis-id")
	public String getRemote_chassis_id()
	{
		return remote_chassis_id;
	}

	@JsonSetter("remote-chassis-id")
	public void setRemote_chassis_id(String remote_chassis_id)
	{
		this.remote_chassis_id = remote_chassis_id;
	}

	@JsonProperty("remote-system-name")
	public String getRemote_system_name()
	{
		return remote_system_name;
	}

	@JsonSetter("remote-system-name")
	public void setRemote_system_name(String remote_system_name)
	{
		this.remote_system_name = remote_system_name;
	}

	public List<ExternalSwitchPort> getPorts()
	{
		return ports;
	}

	public void setPorts(List<ExternalSwitchPort> ports)
	{
		this.ports = ports;
	}
	
}
