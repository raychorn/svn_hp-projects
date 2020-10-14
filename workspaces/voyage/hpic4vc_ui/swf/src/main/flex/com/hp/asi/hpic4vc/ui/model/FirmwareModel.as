/* Copyright 2012 Hewlett-Packard Development Company, L.P. */
package com.hp.asi.hpic4vc.ui.model
{
	
	import mx.collections.ArrayCollection;
	
	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.provider.model.FirmwareModel")]
	/**
	 * A data model of datagrid properties to display.
	 * This Flex class maps to the FirmwareModel java type returned by
	 * the property provider in hpic4vc-provider.
	 */
	public class FirmwareModel extends BaseModel
	{
		public var softwareTable:TableModel;
		public var softwareTableTitle:String;
		
		public var firmwareTable:TableModel;
		public var firmwareTableTitle:String;
	}
}