package com.hp.asi.hpic4vc.ui.model.network
{
	import mx.collections.ArrayCollection;

	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.server.provider.data.network.VCDownlink")]
	public class VCDownlink
	{
		public var macAddress:String;
		[ArrayElementType("com.hp.asi.hpic4vc.ui.model.network.PhysicalPortMapping")]
		public var physicalPortMapping:ArrayCollection;
		public var id:String;
		public var speedGb:Number;
	}
}