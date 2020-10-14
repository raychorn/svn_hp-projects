package com.hp.asi.hpic4vc.ui.model.network
{
	import mx.collections.ArrayCollection;
	
	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.server.provider.data.network.VM")]
	public class VM
	{
		public var hardware:VMHardware;
		[ArrayElementType("com.hp.asi.hpic4vc.ui.model.network.VMNics")]
		public var nics:ArrayCollection;
		public var name:String;
	}
}