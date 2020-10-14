package com.hp.asi.hpic4vc.server.provider.data.network;

import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonSetter;


public class ExternalStorage
{
	private List<String> portWWN;
	
	private String wwn;
	
	@JsonProperty("portWWN")
	public List<String> getPortWWN()
	{
		return portWWN;
	}
	
	@JsonSetter("portWWN")
	public void setPortWWN(List<String> portWWN)
	{
		this.portWWN = portWWN;
	}
	
	
	public String getWWN() {
		return wwn;
	}
	
	@JsonProperty("wwn")
	public void setWWN(String value) {
		this.wwn = value;
	}
}
