package com.hp.asi.hpic4vc.ui.model.network
{
	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.server.provider.data.network.PhysicalPortMapping")]
	public class PhysicalPortMapping
	{
		public var mezz:String;
		public var ioBay:Number;
		public var portType:String;
		public var physFunc:String;
		public var port:String;
	}
}