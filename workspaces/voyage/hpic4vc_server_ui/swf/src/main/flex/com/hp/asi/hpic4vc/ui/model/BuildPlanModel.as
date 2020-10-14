package com.hp.asi.hpic4vc.ui.model
{
	import com.vmware.core.model.DataObject;
	
	import mx.collections.ArrayCollection;

	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.server.provider.model.BuildPlanModel")]
	
	public class BuildPlanModel extends DataObject
	{
		
		
		public var buildPlan:ArrayCollection = new ArrayCollection();
		
		public function BuildPlanModel()
		{
		}
	}
}