package com.hp.asi.hpic4vc.ui.model.network
{
	import com.hp.asi.hpic4vc.ui.model.network.VCM;
	import com.vmware.core.model.DataObject;
	
	import mx.collections.ArrayCollection;
	
	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.server.provider.model.NetworkDiagramDetailModel")]
	/**
	 * A data model of NetworkDetail.
	 * This Flex class maps to the NetworkDetailModel java type returned by
	 * the server_provider.
	 */
	public class NetworkDiagramDetailModel extends DataObject
	{

		[ArrayElementType("com.hp.asi.hpic4vc.ui.model.network.DVS")]
		public var dvss:ArrayCollection;
		
		[ArrayElementType("com.hp.asi.hpic4vc.ui.model.network.VS")]
		public var vss:ArrayCollection;
		
		[ArrayElementType("com.hp.asi.hpic4vc.ui.model.network.Nic")]
		public var nics:ArrayCollection;
		
		public var vcm:VCM;
		
		[ArrayElementType("com.hp.asi.hpic4vc.ui.model.network.DataStore")]
		public var ds:ArrayCollection;


		public var errorMessage:String;
		public var informationMessage:String;
	}
}