package com.hp.asi.hpic4vc.ui.model {

	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.provider.model.BarChartModel")]
	
	public class BarChartModel extends BaseModel {		
		public var info:String;
		public var usedSpace:Number;
		public var freeSpace:Number;
		public var notProvisioned:Number;
		public var overProvisioned:Number;
		public var hoverData:String;
	}
}