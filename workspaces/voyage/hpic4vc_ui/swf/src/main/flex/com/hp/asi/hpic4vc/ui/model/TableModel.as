/* Copyright 2012 Hewlett-Packard Development Company, L.P. */
package com.hp.asi.hpic4vc.ui.model
{
	
	import mx.collections.ArrayCollection;

	[Bindable]
	[RemoteClass(alias="com.hp.asi.hpic4vc.provider.model.TableModel")]
	/**
	 * A data model of datagrid properties to display.
	 * This Flex class maps to the TableModel java type returned by
	 * the property provider in hpic4vc-provider.
	 */
	public class TableModel extends BaseModel
	{
/*		public var columnNames:ArrayCollection;
		public var columnToolTips:ArrayCollection;
		public var columnWidth:ArrayCollection;
		public var columnRighClickMenu:ArrayCollection;
		public var rowFormattedData:ArrayCollection;
		public var rowIds:ArrayCollection;
		public var rowRawData:ArrayCollection;
		public var errorMessage:String;
		public var informationMessage:String;*/
		
		public var columnNames:ArrayCollection;         //-> Names of the column headers – This ArrayCollection is a List<String>.
		public var columnToolTips:ArrayCollection;      //-> Tooltips to appear on the column headers – This ArrayCollection is a List<String>.
		public var columnWidth:ArrayCollection;         //-> Width of the columns – This ArrayCollection is a List<String>.
		
		//Each LinkModel item corresponds to a column. The right click menu is displayed if a given cell has a corresponding value in rowIds and the column has LinkModel in the columnRightClickMenu
		public var columnRightClickMenu:ArrayCollection; //-> Right click menu options for each cell – This ArrayCollection is a List<LinkModel> (defined below).
		
		public var rowFormattedData:ArrayCollection;    //-> Formatted row data (for example: 1 GB) – This ArrayCollection is a List<List<String>>.
		public var rowIds:ArrayCollection;              //-> Unique identifier for each cell – This ArrayCollection is a List<List<String>>.
		public var rowRawData:ArrayCollection;          //-> Raw row data (for example: 1073741824 Bytes)  - This ArrayCollection is a List<List<String>>.
		public var hasIcons:ArrayCollection;            //-> Indicates if the column needs an icon – This ArrayCollection is a List<Boolean> that will indicate if the column needs an icon.
		public var isColumnVisible:ArrayCollection;  // List of Boolean values to specify whether to display the column or not.
		public var isColumnNumeric:ArrayCollection;  // List of Boolean values to specify whether to display the column or not.
		
		public var rowIcons:ArrayCollection;//We will add a “rowIcons”
		//This ArrayCollection is a List<List<String>> where each String is a relative path for the icon. I.e. @Embed(source='/assets/images/hp-logo-small.png')
		
		public var currentRowIndex:int=-1;
		
	}
}