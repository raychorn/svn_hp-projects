package com.hp.asi.hpic4vc.ui.vo
{
	import mx.collections.ArrayCollection;

	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.server.provider.data.Deployment.DeploymentVO")]
		
	public class DeploymentVO
	{
		public var buildPlanUri:String;
		
		public var buildPlanName:String;
		public var vCenterFolderName:String;
		
		public var hostData:HostDataVO =new HostDataVO();
		
		
		
		public var serverUri:String;
		
		public var useNIC0:Boolean;
		
		public var personalityData:PersonalityDataVO = new PersonalityDataVO();
		
		[Transient]
		public var copiedByImage:Boolean = false;
		
		
		public function DeploymentVO()
		{
		}
	}
}