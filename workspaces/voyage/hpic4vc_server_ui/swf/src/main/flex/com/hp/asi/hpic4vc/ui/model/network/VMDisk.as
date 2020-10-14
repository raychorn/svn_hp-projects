package com.hp.asi.hpic4vc.ui.model.network
{
	
	import mx.collections.ArrayCollection;
	
	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.server.provider.data.network.VMDisk")]
	public class VMDisk
	{
		public var summary:String;
		public var label:String;
		
	}
}