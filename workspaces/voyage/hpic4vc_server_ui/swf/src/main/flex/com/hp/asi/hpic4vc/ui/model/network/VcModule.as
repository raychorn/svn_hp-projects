package com.hp.asi.hpic4vc.ui.model.network
{
	import mx.collections.ArrayCollection;

	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.server.provider.data.network.VcModule")]
	public class VcModule
	{
		public var macAddress:String;
		public var commonIoModuleAttrs:CommonIoModulesAttrs;
		public var dipSwitchSettings:Number;
		public var powerState:String;
		public var uid:String;
		public var commStatus:String;
		public var commonAttrs:CommonAttrs;
		public var bay:Number;
		public var enclosureId:String;
		public var fcAtters:FcAttrs;
		[ArrayElementType("com.hp.asi.hpic4vc.ui.model.network.VCNetwork")]
		public var networks:ArrayCollection;
		public var id:String;
		public var moduleType:String;
		public var oaReportedStatus:String;
		[ArrayElementType("com.hp.asi.hpic4vc.ui.model.network.Fabric")]
		public var fabrics:ArrayCollection;
		[ArrayElementType("com.hp.asi.hpic4vc.ui.model.network.VCUplink")]
		public var uplinks:ArrayCollection;
		
	}
}