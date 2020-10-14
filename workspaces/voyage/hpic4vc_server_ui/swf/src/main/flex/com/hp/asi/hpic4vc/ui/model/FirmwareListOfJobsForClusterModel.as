package com.hp.asi.hpic4vc.ui.model
{
	import com.vmware.core.model.DataObject;
	
	import mx.collections.ArrayCollection;

	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.server.provider.model.FirmwareListOfJobsForClusterModel")]
	public class FirmwareListOfJobsForClusterModel extends DataObject
	{
		public var firmwareListOfJobsForClusterModel:ArrayCollection;
		public var hosts:ArrayCollection;
		public var updateFirmwareComponentMessageModel:UpdateFirmwareComponentMessageModel;
	}
}