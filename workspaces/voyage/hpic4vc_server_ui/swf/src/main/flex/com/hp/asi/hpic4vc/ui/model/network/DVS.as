package com.hp.asi.hpic4vc.ui.model.network
{
	import com.vmware.core.model.DataObject;
	
	import mx.collections.ArrayCollection;

	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.server.provider.data.network.DVS")]
	
	public class DVS extends DataObject
	{
		public var host:String;
		public var name:String;
		[ArrayElementType("com.hp.asi.hpic4vc.ui.model.network.UplinkPortGroup")]
		public var uplink_port_groups:ArrayCollection;
		[ArrayElementType("com.hp.asi.hpic4vc.ui.model.network.PortGroup")]
		public var downlink_port_groups:ArrayCollection
	}
}