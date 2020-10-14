package com.hp.asi.hpic4vc.ui.model.network
{
	import com.vmware.core.model.DataObject;
	
	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.server.provider.data.Nic")]
	public class Nic extends DataObject
	{
		public var slot:Number;
		public var vmnic:String;
		public var driver:String;
		public var mac:String;
		public var pci:String;
		public var physical_nic:String;
		public var speedMb:Number;
		public var speedGb:Number;
		public var vswitch:String;
	}
}