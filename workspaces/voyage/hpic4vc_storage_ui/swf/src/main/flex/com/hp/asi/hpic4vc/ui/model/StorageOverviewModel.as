package com.hp.asi.hpic4vc.ui.model {
	
	import mx.collections.ArrayCollection;
	
	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.provider.model.StorageOverviewModel")]
	
	public class StorageOverviewModel extends BaseModel {
		public var arraySummaries:ArrayCollection;
	}
}