/* Copyright 2012 Hewlett-Packard Development Company, L.P. */
package com.hp.asi.hpic4vc.ui.model
{
	
	import mx.collections.ArrayCollection;
	
	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.provider.model.HeaderModel")]
	/**
	 * A data model of header properties to display.
	 * This Flex class maps to the HeaderModel java type returned by
	 * the property provider in hpic4vc-provider.
	 */
	public class HeaderModel extends BaseModel
	{
		public var health:HealthModel;
		public var objReferenceName:String;
		public var objReferenceType:String;
		public var productInfo:String;
		public var enclosureInfo:String;
		public var showRefreshHover:Boolean;
		public var tasks:ArrayCollection;
		public var actions:ArrayCollection;
		public var refresh:ArrayCollection;
		public var configurations:ArrayCollection;
		public var helpUrl:String;
	}
}