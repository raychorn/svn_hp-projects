package com.hp.asi.hpic4vc.ui.model
{
	
	import mx.collections.ArrayCollection;
	
	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.provider.model.HealthModel")]
	/**
	 * A data model of datagrid properties to display.
	 * This Flex class maps to the HealthModel java type returned by
	 * the property provider in hpic4vc-provider.
	 */
	public class HealthModel extends BaseModel
	{
		public var consolidatedStatus:String;
		public var warnCount:String;
		public var errorCount:String;
		public var okCount:String;
		public var infoCount:String;
	}
}