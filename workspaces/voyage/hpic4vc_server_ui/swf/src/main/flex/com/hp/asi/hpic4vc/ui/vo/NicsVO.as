package com.hp.asi.hpic4vc.ui.vo
{
	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.server.provider.data.Deployment.NicsVO")]
	public class NicsVO
	{
		public var dhcp:Boolean = false ;
		
		public var dns:String;
		
		public var dnsDomain:String;
		
		public var gateway:String ;
		
		public var ip4Address:String;
		
		public var macAddress:String;
		
		public var mask:String ;
		
		public var state:String;
		
		public function NicsVO()
		{
		}
	}
}