package com.hp.asi.hpic4vc.ui.model
{
	import com.vmware.core.model.DataObject;
	
	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.server.provider.model.ClusterHostDetail")]
	public class ClusterHostDetail extends DataObject
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
	}
}