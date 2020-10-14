package com.hp.asi.hpic4vc.ui.model.network
{
	
	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.server.provider.data.network.CommonAttrs")]
	
	public class CommonAttrs
	{
		public var overallStatus:String;
		public var managedStatus:String;
		public var id:String;
		public var vcOperationalStatus:String;
	}
}