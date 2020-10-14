package com.hp.asi.hpic4vc.ui.model
{
	
	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.provider.model.PieChartModel")]
	/**
	 * A data model of header properties to display.
	 * This Flex class maps to the PieChartModel java type returned by
	 * the property provider in hpic4vc-provider.
	 */
	public class PieChartModel extends BaseModel
	{
		public var percentUsed:int;
		public var percentFree:int;
	}
}