package com.hp.asi.hpic4vc.ui.vo
{
	import com.vmware.core.model.DataObject;

	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.server.provider.data.DeploymentUserData")]
	public class DeploymentUserData extends DataObject
	{
		
		public var username:String;
		
		public var password:String;
		
	
		
		
		
	}
}