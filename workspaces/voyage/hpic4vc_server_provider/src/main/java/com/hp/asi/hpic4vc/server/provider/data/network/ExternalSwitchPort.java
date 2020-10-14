package com.hp.asi.hpic4vc.server.provider.data.network;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonSetter;

public class ExternalSwitchPort
{
	private String remote_port_desc;
	private String id;
	private String remote_port_id;
	
	@JsonProperty("remote-port-desc")
	public String getRemote_port_desc()
	{
		return remote_port_desc;
	}
	
	@JsonSetter("remote-port-desc")
	public void setRemote_port_desc(String remote_port_desc)
	{
		this.remote_port_desc = remote_port_desc;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	@JsonProperty("remote-port-id")
	public String getRemote_port_id()
	{
		return remote_port_id;
	}

	@JsonSetter("remote-port-id")
	public void setRemote_port_id(String remote_port_id)
	{
		this.remote_port_id = remote_port_id;
	}
}
