package com.hp.asi.hpic4vc.ui.model.network
{
	import mx.collections.ArrayCollection;

	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.server.provider.data.network.Enclosure")]
	public class Enclosure
	{
		public var enclosureName:String;
		public var enclosureType:String;
		public var id:String;
		[ArrayElementType("com.hp.asi.hpic4vc.ui.model.network.VcModule")] 
		public var allVcModuleG1s:ArrayCollection;
	}
}