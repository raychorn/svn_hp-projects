package com.hp.asi.hpic4vc.ui.model
{
	import com.vmware.core.model.DataObject;
	
	public class BaseModel extends DataObject
	{
		public var errorMessage:String;
		public var informationMessage:String;
		
		public function BaseModel()
		{
			super();
		}
	}
}