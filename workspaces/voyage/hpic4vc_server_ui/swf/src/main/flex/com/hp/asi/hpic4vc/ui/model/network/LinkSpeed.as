package com.hp.asi.hpic4vc.ui.model.network
{
	import com.vmware.core.model.DataObject;
	
	import mx.collections.ArrayCollection;
	
	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.server.provider.data.network.LinkSpeed")]	
	public class LinkSpeed extends DataObject
	{
		public var duplex:Boolean;
		public var speedMb:Number;
	}
}