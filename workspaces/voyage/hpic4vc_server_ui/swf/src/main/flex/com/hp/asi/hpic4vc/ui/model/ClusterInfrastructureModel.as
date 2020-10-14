package com.hp.asi.hpic4vc.ui.model
{
	import com.vmware.core.model.DataObject;
	
	import mx.collections.ArrayCollection;
	
	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.server.provider.model.ClusterInfrastructureModel")]

	public class ClusterInfrastructureModel extends DataObject
	{
		
		public var clusterInfrastructure:ArrayCollection = new ArrayCollection();
		
		
		
		
		
	}
}