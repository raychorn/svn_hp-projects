package com.hp.asi.hpic4vc.ui.model.network
{
	
	import mx.collections.ArrayCollection;
	
	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.server.provider.data.network.VMNics")]
	public class VMNics
	{
		public var macAddress:String;
	}
}