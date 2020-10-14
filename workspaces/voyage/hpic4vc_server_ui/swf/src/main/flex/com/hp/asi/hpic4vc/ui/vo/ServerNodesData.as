package com.hp.asi.hpic4vc.ui.vo
{
	import mx.collections.ArrayCollection;
	
	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.server.provider.data.Deployment.ServerNodesData")]
	
	public class ServerNodesData
	{
		
		public var name:String;
		
		public var macAddress:String;
		
		public var UUID:String;
		
		public var deploymentServerID:String;
		
		public var uri:String;
		
		public var managementIP:String;
		
		public var opswLifecycle:String;
		
		public var state:String;
		
		/*[Transient]
		public var buildPlans:ArrayCollection = new ArrayCollection();*/
		
		[Transient]
		public var deploy:Boolean;
		
		public function ServerNodesData()
		{
		}
	}
}