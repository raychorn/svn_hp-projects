package com.hp.asi.hpic4vc.ui.model
{
	import com.vmware.core.model.DataObject;
	
	import mx.collections.ArrayCollection;
	
	
	
	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.server.provider.model.HostConfigMismatchModel")]
	
	public class HostConfigMismatchModel extends DataObject
	{
		public var mismatch:ArrayCollection;
		public var connprofile:ArrayCollection;
		public var hostStatus:String;
		public var refHostMoid:String;
		public var serverStatus:String;
		public var refHostName:String;
		public var vCProfileStatus:String;
		public var errorMessage:String;
		//public var switchdata:SwitchTypeData;
		
		public function HostConfigMismatchModel()
		{
		}
	}
}