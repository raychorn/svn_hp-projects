/* Copyright 2012 Hewlett-Packard Development Company, L.P. */
package com.hp.asi.hpic4vc.ui.model {
	
	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.provider.model.UserInfoModel")]
	/**
	 * A data model of UserInfo properties to display.
	 * This Flex class maps to the UserInfoModel java type returned by
	 * the property provider in hpic4vc-provider.
	 */
	public class UserInfoModel extends BaseModel{
		public var username:String;
		public var roleId:String;
	}
}