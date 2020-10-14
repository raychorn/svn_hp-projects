/* Copyright 2012 Hewlett-Packard Development Company, L.P. */
package com.hp.asi.hpic4vc.ui.model
{
	
	import mx.collections.ArrayCollection;
	
	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.storage.provider.dam.model.HierarchialDataModel")]
	/**
	 * A data model of HierarchialDataModel.
	 * This Flex class maps to the HierarchialDataModel java type returned by
	 * the provider.
	 */
	public class HierarchialDataModel extends BaseModel
	{
		public var columnNames:ArrayCollection; // [Key, Value]
		
		public var columnWidth:ArrayCollection; // [150, 200]
		
		public var rowFormattedData:ArrayCollection; 
		
		public var rowIds:ArrayCollection;
		public var rowRawData:ArrayCollection;
	}	
}