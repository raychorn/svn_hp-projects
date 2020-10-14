package com.hp.asi.hpic4vc.ui.model.network
{
	 
	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.server.provider.data.network.ExternalSwitchPort")]
	public class ExternalSwitchPort
	{
		public var remote_port_desc:String;
		public var id:String;
		public var remote_port_id:String;
		
		public var drawing:*;
	}
}