package com.hp.asi.hpic4vc.server.provider.data.network;

import java.util.List;

public class PortTelemetry
{
	private List<String> tx_kbps;
	private List<String> rx_kbps;
	private TelemetryProperties properties;
	
	public List<String> getTx_kbps()
	{
		return tx_kbps;
	}
	
	public void setTx_kbps(List<String> tx_kbps)
	{
		this.tx_kbps = tx_kbps;
	}

	public List<String> getRx_kbps()
	{
		return rx_kbps;
	}

	public void setRx_kbps(List<String> rx_kbps)
	{
		this.rx_kbps = rx_kbps;
	}

	public TelemetryProperties getProperties()
	{
		return properties;
	}

	public void setProperties(TelemetryProperties properties)
	{
		this.properties = properties;
	}
	
	
}
