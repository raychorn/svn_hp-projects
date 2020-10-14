package com.hp.asi.hpic4vc.ui.model
{
	import com.vmware.core.model.DataObject;
	
	import mx.collections.ArrayCollection;

	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.server.provider.model.ClusterInfraSummaryModel")]
	public class ClusterInfraSummaryModel extends DataObject
	{
		public var oas:ArrayCollection = new ArrayCollection();
		public var errorMessage:String;
		public var informationMessage:String;
	}
}