/* Copyright 2012 Hewlett-Packard Development Company, L.P. */
package com.hp.asi.hpic4vc.ui.model {
	
	import mx.collections.ArrayCollection;
	
	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.provider.model.FooterModel")]
	/**
	 * A data model of footer properties to display.
	 * This Flex class maps to the FooterModel java type returned by
	 * the property provider in hpic4vc-provider.
	 */
	public class FooterModel extends BaseModel{
		public var launchTools:ArrayCollection;
	}
}