package com.hp.asi.hpic4vc.ui.model
{

	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.provider.model.TaskModel")]
	/**
	 * A data model of Tasks properties to display.
	 * This Flex class maps to the TaskModel java type returned by
	 * the property provider in hpic4vc-provider.
	 */
	public class TaskModel extends BaseModel {
		
		public var status:String;
		public var taskName:String;
		public var taskDetails:String;
		public var username:String;
		public var startTime:String;
		public var completedTime:String;
		public var name:String;
	}
}