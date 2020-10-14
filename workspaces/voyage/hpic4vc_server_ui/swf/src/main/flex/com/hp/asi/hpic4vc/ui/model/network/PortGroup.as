package com.hp.asi.hpic4vc.ui.model.network
{
	import com.vmware.core.model.DataObject;
	
	import mx.collections.ArrayCollection;
	
	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.server.provider.data.network.PortGroup")]
	public class PortGroup extends DataObject
	{
		public var key:String;
		public var name:String;
		public var vlanId:String;
		[ArrayElementType("com.hp.asi.hpic4vc.ui.model.network.VM")]
		public var vms:ArrayCollection;
	}
}