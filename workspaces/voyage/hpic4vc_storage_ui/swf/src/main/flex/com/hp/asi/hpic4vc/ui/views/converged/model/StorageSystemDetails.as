package com.hp.asi.hpic4vc.ui.views.converged.model {
	
	import com.hp.asi.hpic4vc.ui.model.BaseModel;
	import com.hp.asi.hpic4vc.ui.model.LabelValueListModel;
	import com.hp.asi.hpic4vc.ui.model.SummaryPortletModel;
	
	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.storage.provider.dam.model.StorageSystemDetails")]
	
	public class StorageSystemDetails extends BaseModel {
		public var storageType:String;
		public var summaryDetails:LabelValueListModel;
		public var storageSystemOverview:SummaryPortletModel;
	}
}