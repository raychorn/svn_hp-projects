package com.hp.asi.hpic4vc.ui.model {
	
	import mx.collections.ArrayCollection;
	
	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.provider.model.StringModel")]
	public class StringModel extends BaseModel {		
		public var data:String;
	}
}