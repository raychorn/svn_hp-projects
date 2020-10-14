package com.hp.asi.hpic4vc.ui.model.events
{
	import flash.events.Event;
	
	
	public class UpdateNetworkEvent extends Event
	{
		public static const VSWTICHEVENT:String = "vswtichEvent"; 		
		public var vSwtichType:String;
		public static const MGMTEVENT:String = "mgmtevent"; 		
		public var mgmtPurposeValue:String;
		public static const FTEVENT:String = "ftevent"; 		
		public var ftPurposeValue:String;
		public static const VMOTIONEVENT:String = "vmotionevent"; 		
		public var vmotionPurposeValue:String;
		public var uuidSelectedValue:String;
		public var mgmtselectedValue:Boolean;

		public function UpdateNetworkEvent(type:String, bubbles:Boolean=true, cancelable:Boolean=false)
		{
			super(type, bubbles, cancelable);
		}		
	}
}