/* Copyright 2012 Hewlett-Packard Development Company, L.P. */
package com.hp.asi.hpic4vc.ui.model {
	
	import mx.collections.ArrayCollection;
	
	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.provider.model.TabModel")]
	/**
	 * A data model of tab properties to display.
	 * This Flex class maps to the TabModel java type returned by
	 * the property provider in hpic4vc-provider.
	 */
	public class TabModel extends BaseModel{
		public var displayNameKey:String;
		public var displayNameValue:String;
		public var order:String;
		public var component:String;
		public var helpUrl:String;
		public var subTabs:ArrayCollection;
		public var column:String;
	}
}