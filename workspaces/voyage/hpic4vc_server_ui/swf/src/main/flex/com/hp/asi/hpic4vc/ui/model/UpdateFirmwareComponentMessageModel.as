package com.hp.asi.hpic4vc.ui.model
{
	import com.vmware.core.model.DataObject;
	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.server.provider.model.UpdateFirmwareComponentMessageModel")]
	public class UpdateFirmwareComponentMessageModel extends DataObject
	{
		public var status:String;
		public  var message:String;
		
		public function UpdateFirmwareComponentMessageModel()
		{
		}
	}
}