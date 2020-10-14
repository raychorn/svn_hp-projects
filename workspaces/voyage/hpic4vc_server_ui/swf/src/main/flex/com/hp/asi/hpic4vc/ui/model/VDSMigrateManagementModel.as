package com.hp.asi.hpic4vc.ui.model
{
	import com.hp.asi.hpic4vc.ui.vo.SwitchTypeData;
	import com.vmware.core.model.DataObject;
	
	import mx.collections.ArrayCollection;
	
	
	
	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.server.provider.model.VDSMigrateManagementModel")]

	public class VDSMigrateManagementModel extends DataObject
	{
		
		public var supportedValues:ArrayCollection;
		public var type:String;
		public var settingName:String;
		public var selectedValue:String;
		
		public function VDSMigrateManagementModel()
		{
		}
	}
}