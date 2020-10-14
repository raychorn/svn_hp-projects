/* Copyright 2012 Hewlett-Packard Development Company, L.P. */
package com.hp.asi.hpic4vc.ui.model {
	
	import mx.collections.ArrayCollection;
	
	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.provider.model.PageModel")]
	/**
	 * A data model of Page properties to display.
	 * This Flex class maps to the PageModel java type returned by
	 * the property provider in hpic4vc-provider.
	 */
	public class PageModel extends BaseModel{
		public var tabs:ArrayCollection;
		public var portlets:ArrayCollection;
		public var show_refresh_cache:String;
	}
}