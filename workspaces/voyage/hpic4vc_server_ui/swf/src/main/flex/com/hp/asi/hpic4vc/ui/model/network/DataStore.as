package com.hp.asi.hpic4vc.ui.model.network
{
	import mx.collections.ArrayCollection;
	
	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.server.provider.data.network.DataStore")]
	public class DataStore
	{
		public var freeSpace:Number;
		public var name:String;
		public var maxFileSize:Number;
		public var key:String;
		[ArrayElementType("com.hp.asi.hpic4vc.ui.model.network.HBA")]
		public var hbas:ArrayCollection;
		[ArrayElementType("com.hp.asi.hpic4vc.ui.model.network.VM")]
		public var vms:ArrayCollection;
	}
}