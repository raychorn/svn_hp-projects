package com.hp.asi.hpic4vc.ui.model.network
{
	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.server.provider.data.network.TelemetryProperties")]
	public class TelemetryProperties
	{
		public var port_telemetry_period:String;
		public var port_telemetry_entry_count:Number;
	}
}