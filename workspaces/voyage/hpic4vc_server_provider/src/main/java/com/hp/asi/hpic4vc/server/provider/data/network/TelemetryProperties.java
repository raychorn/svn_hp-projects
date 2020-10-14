package com.hp.asi.hpic4vc.server.provider.data.network;

public class TelemetryProperties
{
	private String port_telemetry_period;
	private int port_telemetry_entry_count;
	
	public String getPort_telemetry_period()
	{
		return port_telemetry_period;
	}
	
	public void setPort_telemetry_period(String port_telemetry_period)
	{
		this.port_telemetry_period = port_telemetry_period;
	}

	public int getPort_telemetry_entry_count()
	{
		return port_telemetry_entry_count;
	}

	public void setPort_telemetry_entry_count(int port_telemetry_entry_count)
	{
		this.port_telemetry_entry_count = port_telemetry_entry_count;
	}
}
