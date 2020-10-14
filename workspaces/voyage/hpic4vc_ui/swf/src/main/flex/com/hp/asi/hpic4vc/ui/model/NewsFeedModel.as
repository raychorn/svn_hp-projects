package com.hp.asi.hpic4vc.ui.model
{
	
	import mx.collections.ArrayCollection;
	
	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.provider.model.NewsFeedModel")]
	/**
	 * A data model of datagrid properties to display.
	 * This Flex class maps to the NewsFeedModel java type returned by
	 * the property provider in hpic4vc-provider.
	 */
	public class NewsFeedModel extends BaseModel
	{
		public var newsItems:ArrayCollection;
	}
}