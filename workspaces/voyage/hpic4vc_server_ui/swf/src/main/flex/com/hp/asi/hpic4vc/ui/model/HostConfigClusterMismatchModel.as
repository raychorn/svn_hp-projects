package com.hp.asi.hpic4vc.ui.model
{
	import com.vmware.core.model.DataObject;
	
	import mx.collections.ArrayCollection;
	
	
	
	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.server.provider.model.HostConfigClusterMismatchModel")]
	
	public class HostConfigClusterMismatchModel extends DataObject
	{
		public var mismatch:ArrayCollection;
		public var clusterUuid:String;
		public var refHostName:String;
		public var clustMismatch:String;
		//public var switchdata:SwitchTypeData;
		
		public function HostConfigClusterMismatchModel()
		{
		}
	}
}