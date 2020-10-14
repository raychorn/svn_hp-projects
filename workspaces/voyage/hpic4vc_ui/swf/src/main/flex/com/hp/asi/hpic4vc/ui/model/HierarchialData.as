/* Copyright 2012 Hewlett-Packard Development Company, L.P. */
package com.hp.asi.hpic4vc.ui.model
{
	
	import mx.collections.ArrayCollection;
	
	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.storage.provider.dam.model.HierarchialData")]
	/**
	 * A data model of HierarchialData.
	 * This Flex class maps to the HierarchialData java type returned by
	 * the provider.
	 */	
	public class HierarchialData extends BaseModel
	{
		
		public var key:String; 
		public var rawData:String;
		public var formattedData:String;
		/** This is a list of HierarchialData */
		public var childrenData:ArrayCollection;
	}
	
}