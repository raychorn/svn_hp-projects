package com.hp.asi.hpic4vc.ui.model.network
{
	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.server.provider.data.network.FcAttrs")]
	public class FcAttrs
	{
		public var moduleWwn:String;
		public var moduleType:String;
	}
}