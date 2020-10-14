package com.hp.asi.hpic4vc.ui.model.network
{
	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.server.provider.data.network.CommonIoModulesAttrs")]
	public class CommonIoModulesAttrs
	{
		public var bay:Number;
		public var hostname:String;
		public var poserState:String;
		public var uid:String;
		public var sparePartNumber:String;
		public var ipaddress:String;
		public var urlToMgmt:String;
		public var serialNumber:String;
		public var productName:String;
		public var enclosureId:String;
		public var enclosureName:String;
		public var isRemovable:Boolean;
		public var fwRev:String;
		public var rackName:String;
		public var partNumber:String;
		public var vcManagerRole:String;
		public var commStatus:String;
		public var oaReportedStatus:String;
		public var manufacturer:String;
	}
}