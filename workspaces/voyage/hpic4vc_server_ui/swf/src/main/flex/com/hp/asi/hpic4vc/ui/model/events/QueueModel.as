package com.hp.asi.hpic4vc.ui.model.events
{
	import com.vmware.core.model.DataObject;
	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.server.provider.model.QueueModel")]
	public class QueueModel extends DataObject
	{
		public  var package_url:String;
	}
}