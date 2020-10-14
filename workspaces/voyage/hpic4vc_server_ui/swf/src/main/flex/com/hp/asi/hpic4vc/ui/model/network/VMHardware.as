package com.hp.asi.hpic4vc.ui.model.network
{
	
	import mx.collections.ArrayCollection;
	
	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.server.provider.data.network.VMHardware")]
	public class VMHardware
	{
		public var numCPU:Number;
		public var memoryMB:Number;
		[ArrayElementType("com.hp.asi.hpic4vc.ui.model.network.VMDisk")]
		public var disks:ArrayCollection;
	}
}