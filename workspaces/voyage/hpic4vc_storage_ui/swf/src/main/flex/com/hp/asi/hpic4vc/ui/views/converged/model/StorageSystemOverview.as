package com.hp.asi.hpic4vc.ui.views.converged.model {
	
	import com.hp.asi.hpic4vc.ui.model.BaseModel;
	import com.hp.asi.hpic4vc.ui.model.LabelValueListModel;
	import com.hp.asi.hpic4vc.ui.model.SummaryPortletModel;
	
	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.storage.provider.dam.model.StorageSystemOverviewModel")]
	
	public class StorageSystemOverview extends BaseModel {
		public var storageType:String;
		public var summaryDetails:LabelValueListModel;
		public var storageSystemOverview:SummaryPortletModel;
		public var volumesProvisioned:int;
		public var volumesThinProvisioned:int;
		public var volumesOverProvisioned:int;
	}
}