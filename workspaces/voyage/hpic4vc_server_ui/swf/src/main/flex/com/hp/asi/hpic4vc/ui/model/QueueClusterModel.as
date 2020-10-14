package com.hp.asi.hpic4vc.ui.model
{
	import com.vmware.core.model.DataObject;
	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.server.provider.model.QueueClusterModel")]
	public class QueueClusterModel extends DataObject
	{
		public  var package_url:String;
		public var host:String;
	}
}