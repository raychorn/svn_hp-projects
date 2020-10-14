package com.hp.asi.hpic4vc.ui.model
{
	
	import mx.collections.ArrayCollection;
	
	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.provider.model.SummaryPortletModel")]
	/**
	 * A data model of header properties to display.
	 * This Flex class maps to the SummaryPortletModel java type returned by
	 * the property provider in hpic4vc-provider.
	 */
	public class SummaryPortletModel extends BaseModel
	{
		public var fieldData:LabelValueListModel;
		public var pieChartData:PieChartModel;
	}
}