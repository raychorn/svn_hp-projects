package com.hp.asi.hpic4vc.ui.model.network
{
	import com.vmware.core.model.DataObject;
	
	import mx.collections.ArrayCollection;
	
	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.server.provider.data.network.PNIC")]	
	public class PNIC extends DataObject
	{
		public var deviceName:String;
		public var wakeOnLanSupported:Boolean;
		public var speedGb:Number;
		public var vmDirectPathGen2Supported:Boolean;
		public var driver:String;
		[ArrayElementType("com.hp.asi.hpic4vc.ui.model.network.LinkSpeed")]
		public var validLinkSpecification:ArrayCollection; 
		public var linkSpeed:LinkSpeed;
		public var mac:String;
		public var pci:String;
		public var autoNegotiateSupported:Boolean;
		public var key:String;
		public var resourcePoolSchedulerAllowed:Boolean;
		public var device:String;	
		public var vendorName:String;
		public var physicalPortMapping:PhysicalPortMapping ;
	}
}