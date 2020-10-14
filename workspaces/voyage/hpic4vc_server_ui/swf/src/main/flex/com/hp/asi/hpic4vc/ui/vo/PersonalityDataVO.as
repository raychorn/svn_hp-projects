package com.hp.asi.hpic4vc.ui.vo
{
	import mx.collections.ArrayCollection;

	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.server.provider.data.Deployment.PersonalityDataVO")]
	public class PersonalityDataVO
	{
		
		public var displayName:String;
		
		public var dnsSuffix:String;
		
		public var domainName:String;
		
		public var domainType:String;
		
		public var hostName:String;
		
		public var groupName:String;
		
		public var nics:ArrayCollection = new ArrayCollection(); // NicsVO
		
		
		public function PersonalityDataVO()
		{
		}
	}
}