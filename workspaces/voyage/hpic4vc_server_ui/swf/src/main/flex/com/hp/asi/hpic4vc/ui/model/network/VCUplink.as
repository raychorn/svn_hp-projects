package com.hp.asi.hpic4vc.ui.model.network
{
	import mx.core.IVisualElement;
	
	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.server.provider.data.network.VCUplink")]
	public class VCUplink
	{
		public var portWWN:String;
		public var speedGb:Number;
		public var connectedToWWN:String;
		public var telemetry:PortTelemetry;
		
		public var portConnectStatus:String;
		public var portLabel:String;
		public var uplinkType:String;
		public var currentSpeed:String;
		public var id:String;
		public var duplexStatus:String;	
		public var supportedSpeeds:String;
		public var opSpeed:Number;
		public var connectorType:String;
		public var remoteChassisId:String;
		public var remotePortId:String;
		public var linkStatus:String;
		public var physicalLayer:String;
		
		//drawing object
		public var drawing:*;
	}
}