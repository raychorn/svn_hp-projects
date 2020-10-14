package com.hp.asi.hpic4vc.ui.model {
	
	import mx.collections.ArrayCollection;
	
	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.provider.model.BarChartGroupModel")]
	public class BarChartGroupModel extends BaseModel {		
		public var groupTitle:String;
		public var barChartData:ArrayCollection;
	}
}