package com.hp.asi.hpic4vc.ui.model.network
{
	import mx.collections.ArrayCollection;

	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.server.provider.data.network.ExternalSwitch")]
	public class ExternalSwitch
	{
		public var id:String;
		public var remote_system_desc:String;
		public var remote_system_capabilities:String;
		public var remote_chassis_id:String;
		public var remote_system_name:String;
		[ArrayElementType("com.hp.asi.hpic4vc.ui.model.network.ExternalSwitchPort")] 
		public var ports:ArrayCollection;
	}
}