package com.hp.asi.hpic4vc.ui.model.network
{
	import mx.collections.ArrayCollection;

	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.server.provider.data.network.Fabric")]
	public class Fabric
	{
		public var displayName:String;
		public var id:String;
		[ArrayElementType("com.hp.asi.hpic4vc.ui.model.network.FabricDownlink")]
		public var downlinks:ArrayCollection;
		[ArrayElementType("String")]
		public var portlinks:ArrayCollection;
		
		public var drawing:*;
	}
}