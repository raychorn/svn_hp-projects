package com.hp.asi.hpic4vc.ui.model
{
	
	import mx.collections.ArrayCollection;
	
	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.provider.model.FullSummaryModel")]
	/**
	 * A data model of header properties to display.
	 * This Flex class maps to the SummaryPortletModel java type returned by
	 * the property provider in hpic4vc-provider.
	 */
	public class FullSummaryModel extends BaseModel
	{
		public var hpToVobjectTitle:String;
		public var hpToVobject:SummaryPortletModel;
		public var arrayTitle:String;
		public var arraySummaries:ArrayCollection;
		public var backupSystemSummariesTitle:String;
		public var backupSystemSummaries:ArrayCollection;
		public var vObjectToHpTitle:String;
		public var vObjectToHp:SummaryPortletModel;
		public var dsTitle:String;
		public var dsSummaries:ArrayCollection;
	}
}