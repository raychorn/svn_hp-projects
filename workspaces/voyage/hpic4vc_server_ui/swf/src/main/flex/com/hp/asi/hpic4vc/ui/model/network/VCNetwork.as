package com.hp.asi.hpic4vc.ui.model.network
{
	import mx.collections.ArrayCollection;

	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.server.provider.data.network.VCNetwork")]
	public class VCNetwork
	{
		public var maxPortSpeed:Number;
		public var preferredPortSpeed:Number;
		public var uplinkVLANId:String;
		public var displayName:String;
		public var portlinks:ArrayCollection;
		[ArrayElementType("com.hp.asi.hpic4vc.ui.model.network.VCDownlink")]
		public var downlinks:ArrayCollection;
		public var id:String;
		public var bottleNeck:Boolean;
		
		public var drawing:*;
	}
}