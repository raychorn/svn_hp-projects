package com.hp.asi.hpic4vc.ui.model
{
	import com.vmware.core.model.DataObject;
	
	import mx.collections.ArrayCollection;
	
	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.server.provider.model.SmartComponentUpdateModel")]
	public class SmartComponentUpdateModel extends DataObject
	{
		public var queue:ArrayCollection;
		public var jobs:ArrayCollection;
		public var errors:ArrayCollection;
		public var updateSoftwareComponentMessageModel:UpdateSoftwareComponentMessagModel
	}
}