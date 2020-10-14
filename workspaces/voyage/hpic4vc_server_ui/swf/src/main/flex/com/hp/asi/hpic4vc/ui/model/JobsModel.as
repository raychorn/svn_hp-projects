package com.hp.asi.hpic4vc.ui.model
{
	import com.vmware.core.model.DataObject;
	
	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.server.provider.model.JobsModel")]
	public class JobsModel extends DataObject
	{
		public var jobDescription:String;
		public var status:String;
		public var statusDescriptions:String;
		public var errorDescription:String;
		public var percentComplete:String;
		public var noJobs:String;
		
	}
}