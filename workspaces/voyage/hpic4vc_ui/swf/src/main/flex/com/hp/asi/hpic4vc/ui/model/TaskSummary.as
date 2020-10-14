package com.hp.asi.hpic4vc.ui.model
{
	import com.vmware.core.model.DataObject;
	
	import mx.collections.ArrayCollection;
	
	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.provider.model.TaskSummary")]
	/**
	 * A data model of datagrid properties to display.
	 * This Flex class maps to the TaskSummary java type returned by
	 * the property provider in hpic4vc-provider.
	 */
	public class TaskSummary extends DataObject
	{
		public var taskItems:ArrayCollection;
		public var errorMessage:String;
		public var informationMessage:String;
	}
}