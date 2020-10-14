package com.hp.asi.hpic4vc.ui.model
{
	import com.hp.asi.hpic4vc.ui.model.network.VCM;
	import com.vmware.core.model.DataObject;
	
	import mx.collections.ArrayCollection;
	
	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.server.provider.model.NetworkDetailModel")]
	/**
	 * A data model of NetworkDetail.
	 * This Flex class maps to the NetworkDetailModel java type returned by
	 * the server_provider.
	 */
	public class NetworkDetailModel extends DataObject
	{
        public var nics:TableModel;
        public var externalSwitches:TableModel;
        public var externalStorage:TableModel;
        public var virtualSwitches:ArrayCollection;
        public var distributedVirtualSwitches:ArrayCollection;
        public var vcms:ArrayCollection;
		[ArrayElementType("com.hp.asi.hpic4vc.ui.model.network.DataStore")]
		public var ds:ArrayCollection;
		public var vcm:VCM;
        public var errorMessage:String;
		public var informationMessage:String;
	}
}