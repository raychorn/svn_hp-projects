package com.hp.asi.hpic4vc.ui.model.network
{
	import mx.collections.ArrayCollection;

	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.server.provider.data.network.PortTelemetry")]
	public class PortTelemetry
	{
		public var tx_kbps:ArrayCollection;
		public var rx_kbps:ArrayCollection;
		public var properties:TelemetryProperties;
	}
}