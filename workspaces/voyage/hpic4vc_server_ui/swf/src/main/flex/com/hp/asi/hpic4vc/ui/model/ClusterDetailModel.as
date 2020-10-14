package com.hp.asi.hpic4vc.ui.model
{
	import com.vmware.core.model.DataObject;
	
	import mx.collections.ArrayCollection;
	
	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.server.provider.model.ClusterDetailModel")]
	/**
	 * A data model of HostDetail.
	 * This Flex class maps to the HostDetailModel java type returned by
	 * the server_provider.
	 */
	
	public class ClusterDetailModel extends DataObject
	{
		
		public var hosts:ArrayCollection;
		
	}
}