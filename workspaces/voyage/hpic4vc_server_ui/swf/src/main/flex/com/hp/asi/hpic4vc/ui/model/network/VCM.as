package com.hp.asi.hpic4vc.ui.model.network
{
	import mx.collections.ArrayCollection;
	
	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.server.provider.data.network.VCM")]
	public class VCM
	{
		[ArrayElementType("com.hp.asi.hpic4vc.ui.model.network.ExternalStorage")] 
		public var externalStorage:ArrayCollection;
		[ArrayElementType("com.hp.asi.hpic4vc.ui.model.network.Enclosure")]
		public var enclosures:ArrayCollection;
		[ArrayElementType("com.hp.asi.hpic4vc.ui.model.network.ExternalSwitch")]
		public var externalSwitches:ArrayCollection;
	}
}