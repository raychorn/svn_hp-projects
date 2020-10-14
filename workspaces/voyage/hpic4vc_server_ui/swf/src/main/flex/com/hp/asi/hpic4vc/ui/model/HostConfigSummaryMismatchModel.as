package com.hp.asi.hpic4vc.ui.model
{
	import com.vmware.core.model.DataObject;
	
	import mx.collections.ArrayCollection;
	
	
	
	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.server.provider.model.HostConfigSummaryMismatchModel")]
	
	public class HostConfigSummaryMismatchModel extends DataObject
	{
		public var mismatch:ArrayCollection;
		public var configStatus:String;
		public var errorMessage:String;
		public function HostConfigSummaryMismatchModel()
		{
		}
	}
}