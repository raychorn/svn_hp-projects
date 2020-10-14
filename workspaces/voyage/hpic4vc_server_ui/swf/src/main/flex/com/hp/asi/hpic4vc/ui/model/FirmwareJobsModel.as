package com.hp.asi.hpic4vc.ui.model
{
	import com.vmware.core.model.DataObject;
	
	import mx.collections.ArrayCollection;

	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.server.provider.model.FirmwareJobsModel")]
	public class FirmwareJobsModel extends DataObject
	{
		public var queue:ArrayCollection;
		public var jobs:ArrayCollection;
		public var errors:ArrayCollection;
	}
}