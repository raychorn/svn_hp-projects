package com.hp.asi.hpic4vc.ui.model.network
{
	import com.vmware.core.model.DataObject;
	
	import mx.collections.ArrayCollection;
	
	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.server.provider.data.network.UplinkPortGroup")]
	/**
	 * A data model of UplinkPortGroup.
	 * This Flex class maps to the UplinkPortGroup java type returned by
	 * the server_provider.
	 */
	public class UplinkPortGroup extends DataObject
	{
		public var name:String;
		[ArrayElementType("com.hp.asi.hpic4vc.ui.model.network.DVSUplink")]
		public var uplinks:ArrayCollection;
	}
}