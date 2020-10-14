package com.hp.asi.hpic4vc.ui.model
{
	import com.vmware.core.model.DataObject;
	
	import mx.collections.ArrayCollection;
	
	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.server.provider.model.InfrastructureDetailModel")]
	/**
	 * A data model of HostDetail.
	 * This Flex class maps to the HostDetailModel java type returned by
	 * the server_provider.
	 */
	public class InfrastructureDetailModel extends DataObject
	{
        public var enclosure:LabelValueListModel;
        public var power:LabelValueListModel;
        public var thermal:LabelValueListModel;
        public var fans:TableModel;
        public var powerSupplies:TableModel;
        public var interconnects:TableModel;
        public var oaModules:TableModel;
        public var syslog:ArrayCollection;
        public var errorMessage:String;
		public var informationMessage:String;
	}
}