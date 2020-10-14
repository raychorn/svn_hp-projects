package com.hp.asi.hpic4vc.server.provider.data.network;

public class PhysicalPortMapping
{
	private String mezz;
	private int ioBay;
	private String portType;
	private String physFunc;
	private String port;
	
	public String getMezz()
	{
		return mezz;
	}
	
	public void setMezz(String mezz)
	{
		this.mezz = mezz;
	}

	public int getIoBay()
	{
		return ioBay;
	}

	public void setIoBay(int ioBay)
	{
		this.ioBay = ioBay;
	}

	public String getPortType()
	{
		return portType;
	}

	public void setPortType(String portType)
	{
		this.portType = portType;
	}

	public String getPhysFunc()
	{
		return physFunc;
	}

	public void setPhysFunc(String physFunc)
	{
		this.physFunc = physFunc;
	}

	public String getPort()
	{
		return port;
	}

	public void setPort(String port)
	{
		this.port = port;
	}
	
	
}
