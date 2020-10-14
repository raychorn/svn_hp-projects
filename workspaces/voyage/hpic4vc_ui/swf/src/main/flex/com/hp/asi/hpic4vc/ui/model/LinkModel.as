/* Copyright 2012 Hewlett-Packard Development Company, L.P.  */
package com.hp.asi.hpic4vc.ui.model
{
	
	import mx.collections.ArrayCollection;
	
	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.provider.model.LinkModel")]
	/**
	 * A data model of link properties to display.
	 * This Flex class maps to the LinkModel java type returned by
	 * the property provider in hpic4vc-provider.
	 */
	public class LinkModel extends BaseModel{		
		public static const LINK_MODEL_TYPE_HIDE_COL:String="HIDE_COLUMN";
		public static const LINK_MODEL_TYPE_SIZE_TO_FIT_COL:String="SIZE_TO_FIT";
		public static const LINK_MODEL_TYPE_SIZE_ALL_TO_FIT:String="ALL_TO_FIT";
		public static const LINK_MODEL_TYPE_SEPARATOR:String="SEPARATOR";
		
		public static const LINK_MODEL_TYPE_LOCK_COL:String="LOCK_COL";
		
		public static const LINK_MODEL_TYPE_SHOW_HIDE_COLS:String="SHOW_HIDE_COLS";
		public static const LINK_MODEL_TYPE_SHOW_HIDE_TOOLBAR:String="SHOW_HIDE_TOOLBAR";
		
		public static const LINK_MODEL_TYPE_POPUP:String="SHOW_HIDE_POPUP";
		
		
		public var displayName:String;                  //-> Name to be displayed in the dropdown menu.
		public var type:String;                         //-> To identify if this link model needs a pop-up dialog. ???
		public var url:String;                      	//-> URL to be launched in new browser window when this item is clicked by the user.
		public var dialogTitle:String;                  //-> Title of the dialog window if applicable.
		public var dialogText:String;                   //-> Text to be displayed in the dialog window if applicable.
		public var urlBase:String;                      //-> 
		public var username:String;
		public var password:String;
		public var firmwareForHost:String;
	}
}