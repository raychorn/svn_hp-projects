/* Copyright 2012 Hewlett-Packard Development Company, L.P. */
package com.hp.asi.hpic4vc.ui.model {
	
	import mx.collections.ArrayCollection;
	
	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.provider.model.PortletModel")]
	/**
	 * A data model of Portlet properties to display.
	 * This Flex class maps to the PortletModel java type returned by
	 * the property provider in hpic4vc-provider.
	 */
	public class PortletModel extends BaseModel{
		public var portlets:ArrayCollection;
	}
}