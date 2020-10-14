package com.hp.asi.hpic4vc.ui.vo
{
	import mx.collections.ArrayCollection;

	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.server.provider.data.SwitchTypeData")]
	public class SwitchTypeData
	{
		
		public var  type:String;
		public var  supportedValues:ArrayCollection = new ArrayCollection();
		public var selectedValue:String;
		public var settingName:String;		
		public var switchSupportedValueData:ArrayCollection = new ArrayCollection();
		
		public function SwitchTypeData()
		{
		}
	}
}