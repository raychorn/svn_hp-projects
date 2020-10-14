package com.hp.asi.hpic4vc.ui.model.network
{
	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.server.provider.data.network.FabricDownlink")]
	public class FabricDownlink
	{
		public var portWWN:String;
		public var speedGb:Number;
		public var physicalServerBay:Number;
		public var id:String;
		public var physicalPortMapping:PhysicalPortMapping
	}
}