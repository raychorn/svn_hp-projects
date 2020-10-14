package com.hp.asi.hpic4vc.ui.model.network
{
	import com.vmware.core.model.DataObject;
	
	import mx.collections.ArrayCollection;
	
	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.server.provider.data.network.VS")]
	public class VS extends DataObject
	{
		public var numPorts:Number;
		public var numPortsAvailable:Number;
		public var name:String;
		[ArrayElementType("com.hp.asi.hpic4vc.ui.model.network.PNIC")]
		public var pnics:ArrayCollection
		[ArrayElementType("com.hp.asi.hpic4vc.ui.model.network.PortGroup")]
		public var port_groups:ArrayCollection
	}
}