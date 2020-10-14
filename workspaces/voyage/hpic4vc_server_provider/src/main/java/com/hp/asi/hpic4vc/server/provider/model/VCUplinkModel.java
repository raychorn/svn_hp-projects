package com.hp.asi.hpic4vc.server.provider.model;

public class VCUplinkModel
{
	public String portLabel;
	public String uplinkType;
	public String status;
	public String connector;
	public String speedGb;

	public VCUplinkModel()
	{
		
	}
	
	public VCUplinkModel(String portLabel, String uplinkType, String status, String connector, String speedGb)
	{
		this.portLabel = portLabel;
		this.uplinkType = uplinkType;
		this.status = status;
		this.connector = connector;
		this.speedGb = speedGb;
	}
}
