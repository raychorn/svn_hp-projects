package com.hp.asi.hpic4vc.ui.model
{
	import com.vmware.core.model.DataObject;
	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.server.provider.model.FirmwareListOfJobsForClusterModel")]
	public class FirmwareJobsForClusterModel extends DataObject
	{
		public  var firmwareJobsModel:FirmwareJobsModel;
		public var host:String;
	}
}