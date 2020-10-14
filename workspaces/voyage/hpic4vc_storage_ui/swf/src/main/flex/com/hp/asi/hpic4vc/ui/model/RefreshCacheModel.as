/* Copyright 2012 Hewlett-Packard Development Company, L.P. */
package com.hp.asi.hpic4vc.ui.model
{
	
	import mx.collections.ArrayCollection;
	
	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.provider.model.RefreshCacheModel")]
	/**
	 * A data model of header properties to display.
	 * This Flex class maps to the RefreshCacheModel java type returned by
	 * the property provider in hpic4vc-provider.
	 */
	public class RefreshCacheModel extends BaseModel
	{
		public var isPopulating:Boolean;
		public var remainingTime:String;
		public var estimatedTimeLabel:String;
		public var summary:String;
	}
}