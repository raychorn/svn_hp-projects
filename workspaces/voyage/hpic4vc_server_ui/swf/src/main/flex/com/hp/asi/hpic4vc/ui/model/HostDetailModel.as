package com.hp.asi.hpic4vc.ui.model
{
	import com.vmware.core.model.DataObject;
	
	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.server.provider.model.HostDetailModel")]
	/**
	 * A data model of HostDetail.
	 * This Flex class maps to the HostDetailModel java type returned by
	 * the server_provider.
	 */
	public class HostDetailModel extends DataObject
	{
        public var hostInfo:LabelValueListModel;
        public var serverStatus:LabelValueListModel;
        public var serverPower:LabelValueListModel;
        public var memoryInfo:TableModel;
        public var cpuInfo:TableModel;
        public var firmwareInfo:TableModel;
        public var softwareInfo:TableModel;
        public var iloLog:TableModel;
        public var imlLog:TableModel;
        public var errorMessage:String;
        public var powerCostAdvantage:LabelValueModel;
		public var informationMessage:String;
	}
}