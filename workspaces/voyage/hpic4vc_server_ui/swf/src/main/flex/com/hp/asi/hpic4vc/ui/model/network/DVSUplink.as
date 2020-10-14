package com.hp.asi.hpic4vc.ui.model.network
{
	import com.vmware.core.model.DataObject;
	
	import mx.collections.ArrayCollection;
	
	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.server.provider.data.network.DVSUplink")]
	
	public class DVSUplink extends DataObject
	{
		public var name:String;
		[ArrayElementType("com.hp.asi.hpic4vc.ui.model.network.PNIC")]
		public var pnics:ArrayCollection
	}
}