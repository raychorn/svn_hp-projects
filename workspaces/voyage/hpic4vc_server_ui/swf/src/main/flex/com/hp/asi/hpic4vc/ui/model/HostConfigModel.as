package com.hp.asi.hpic4vc.ui.model
{
	import com.hp.asi.hpic4vc.ui.vo.SwitchTypeData;
	import com.vmware.core.model.DataObject;
	
	import mx.collections.ArrayCollection;
	
	
	
	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.server.provider.model.HostConfigModel")]
	
	public class HostConfigModel extends DataObject
	{
		public var networks:ArrayCollection;
		//public var switchdata:SwitchTypeData;
		
		public function HostConfigModel()
		{
		}
	}
}