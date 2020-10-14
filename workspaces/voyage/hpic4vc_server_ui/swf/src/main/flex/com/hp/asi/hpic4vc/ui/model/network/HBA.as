package com.hp.asi.hpic4vc.ui.model.network
{
	import mx.collections.ArrayCollection;
	
	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.server.provider.data.network.HBA")]
	public class HBA
	{
		public var status:String;
		public var speedGb:Number;
		public var pathState:String;
		public var bus:Number;
		public var driver:String;
		public var portWorldWideName:String;
		public var pci:String;
		public var key:String;
		public var device:String;
		public var model:String;
		public var physicalPortMapping:PhysicalPortMapping;;
		public var type:String;
		public var nodeWorldWideName:String;
		public var speed:Number;
		public var mac:String;
		
		public var drawing:*;
	}
}