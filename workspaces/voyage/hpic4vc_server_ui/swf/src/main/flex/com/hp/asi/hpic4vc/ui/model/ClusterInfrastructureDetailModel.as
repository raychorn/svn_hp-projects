package com.hp.asi.hpic4vc.ui.model
{
	import com.vmware.core.model.DataObject;
	
	import mx.collections.ArrayCollection;

	public class ClusterInfrastructureDetailModel extends DataObject
	{
		public var enclosureName:String;
		public var rackName:String;
		public var powerConsumed:int;
		public var powerRedundancy:String;
		public var thermalRedundancy:String;
		
		
		public var clusterInfrastructureDetails:ArrayCollection = new ArrayCollection();
		public var errorMessage:String;
		public function ClusterInfrastructureDetailModel()
		{
		}
	}
}