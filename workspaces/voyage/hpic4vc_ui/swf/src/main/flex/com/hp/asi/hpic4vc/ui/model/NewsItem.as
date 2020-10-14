package com.hp.asi.hpic4vc.ui.model
{

	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.provider.model.NewsItem")]
	/**
	 * A data model of datagrid properties to display.
	 * This Flex class maps to the NewsItem java type returned by
	 * the property provider in hpic4vc-provider.
	 */
	public class NewsItem extends BaseModel
	{
		public var rawStatus:String;
		public var formattedStatus:String;
		public var formattedMessage:String;
		public var formattedTimeStamp:String;
	}
}