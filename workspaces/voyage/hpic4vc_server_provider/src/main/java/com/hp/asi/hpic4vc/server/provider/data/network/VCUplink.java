package com.hp.asi.hpic4vc.server.provider.data.network;

public class VCUplink
{
	private String portWWN;
	private float speedGb;
	private String connectedToWWN;
	private PortTelemetry telemetry;
	
	private String portConnectStatus;
	private String portLabel;
	private String uplinkType;
	private String currentSpeed;
	private String id;
	private String duplexStatus;	
	private String supportedSpeeds;
	private int opSpeed;
	private String connectorType;
	private String remoteChassisId;
	private String remotePortId;
	private String linkStatus;
	private String physicalLayer;
	
	

	public String getPortConnectStatus()
	{
		return portConnectStatus;
	}

	public void setPortConnectStatus(String portConnectStatus)
	{
		this.portConnectStatus = portConnectStatus;
	}

	public String getPortLabel()
	{
		return portLabel;
	}

	public void setPortLabel(String portLabel)
	{
		this.portLabel = portLabel;
	}

	public String getUplinkType()
	{
		return uplinkType;
	}

	public void setUplinkType(String uplinkType)
	{
		this.uplinkType = uplinkType;
	}

	public String getCurrentSpeed()
	{
		return currentSpeed;
	}

	public void setCurrentSpeed(String currentSpeed)
	{
		this.currentSpeed = currentSpeed;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getDuplexStatus()
	{
		return duplexStatus;
	}

	public void setDuplexStatus(String duplexStatus)
	{
		this.duplexStatus = duplexStatus;
	}

	public float getSpeedGb()
	{
		return speedGb;
	}

	public void setSpeedGb(float speedGb)
	{
		this.speedGb = speedGb;
	}

	public String getSupportedSpeeds()
	{
		return supportedSpeeds;
	}

	public void setSupportedSpeeds(String supportedSpeeds)
	{
		this.supportedSpeeds = supportedSpeeds;
	}

	public int getOpSpeed()
	{
		return opSpeed;
	}

	public void setOpSpeed(int opSpeed)
	{
		this.opSpeed = opSpeed;
	}

	public String getConnectorType()
	{
		return connectorType;
	}

	public void setConnectorType(String connectorType)
	{
		this.connectorType = connectorType;
	}

	public String getRemoteChassisId()
	{
		return remoteChassisId;
	}

	public void setRemoteChassisId(String remoteChassisId)
	{
		this.remoteChassisId = remoteChassisId;
	}

	public String getRemotePortId()
	{
		return remotePortId;
	}

	public void setRemotePortId(String remotePortId)
	{
		this.remotePortId = remotePortId;
	}

	public String getLinkStatus()
	{
		return linkStatus;
	}

	public void setLinkStatus(String linkStatus)
	{
		this.linkStatus = linkStatus;
	}

	public String getPhysicalLayer()
	{
		return physicalLayer;
	}

	public void setPhysicalLayer(String physicalLayer)
	{
		this.physicalLayer = physicalLayer;
	}

	public String getPortWWN()
	{
		return portWWN;
	}

	public void setPortWWN(String portWWN)
	{
		this.portWWN = portWWN;
	}

	public String getConnectedToWWN()
	{
		return connectedToWWN;
	}

	public void setConnectedToWWN(String connectedToWWN)
	{
		this.connectedToWWN = connectedToWWN;
	}

	public PortTelemetry getTelemetry()
	{
		return telemetry;
	}

	public void setTelemetry(PortTelemetry telemetry)
	{
		this.telemetry = telemetry;
	}
}
