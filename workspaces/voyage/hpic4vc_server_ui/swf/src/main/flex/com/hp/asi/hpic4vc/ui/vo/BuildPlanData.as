package com.hp.asi.hpic4vc.ui.vo
{
	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.server.provider.data.Deployment.BuildPlanData")]
	
	public class BuildPlanData
	{
		
	
		public var uri:String;
		
		public var name:String;
		
		public var os:String;
		
		public function BuildPlanData()
		{
		}
	}
}