/* Copyright 2012 Hewlett-Packard Development Company, L.P.  */
package com.hp.asi.hpic4vc.ui.model
{
	
	import mx.collections.ArrayCollection;
	
	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.provider.model.MenuModel")]
	/**
	 * A data model of menu properties to display.
	 * This Flex class maps to the MenuModel java type returned by
	 * the property provider in hpic4vc-provider.
	 */
	public class MenuModel extends BaseModel{
		public var menuItems:ArrayCollection;
	}
}