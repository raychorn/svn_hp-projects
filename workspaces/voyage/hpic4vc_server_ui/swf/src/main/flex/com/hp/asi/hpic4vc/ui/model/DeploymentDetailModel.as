package com.hp.asi.hpic4vc.ui.model
{
	import mx.collections.ArrayCollection;
	
	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.server.provider.model.DeploymentDetailModel")]
	
	public class DeploymentDetailModel
	{
		
	
		public var serverNodes:ArrayCollection = new ArrayCollection();
		
		[Transient]
		public var selectedServers:ArrayCollection = new ArrayCollection(); // collection of DeploymentVO
		
		public var cacheData:ArrayCollection = new ArrayCollection();
		
		public var vCenterTaskData:ArrayCollection = new ArrayCollection();
		
		
		[Transient]
		public var auth:String;
		
		[Transient]
		public var hostName:String;
		
		
		
	}
}