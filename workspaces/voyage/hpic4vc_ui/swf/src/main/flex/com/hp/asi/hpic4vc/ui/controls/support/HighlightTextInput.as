package com.hp.asi.hpic4vc.ui.controls.support
{
	import com.flexicious.utils.UIUtils;
	
	import flash.display.DisplayObject;
	import flash.events.Event;
	import flash.events.KeyboardEvent;
	import flash.ui.Keyboard;
	
	import mx.core.FlexGlobals;
	import mx.events.MenuEvent;

	/**
	 * A textinput that instructs the grid  to highlight the 
	 * specified string in the specified columns.
	 */	
	public class HighlightTextInput extends FilterTextInput
	{
		public function HighlightTextInput()
		{
			super();
			menuDataProvider=[{label:"Highlight All",type:"check",toggled:true},{type:"separator"},{label:"Select Columns..."}];
		}
		protected override function onSearchClick(event:Event):void
		{
			dataGrid.globalHightlightString= text;
		}
		
		protected override function onKeyUp(event:KeyboardEvent):void
		{
			if(event.keyCode==Keyboard.ENTER){
				dataGrid.globalHightlightString = text;
				if(!dataGrid.globalHightlightAll){
					dataGrid.gotoNextHighlight(event.shiftKey);
				}
			}
			
		}
		protected override function onMenuClick(event:MenuEvent):void
		{
			if(event.item.label=="Highlight All"){
				//event.item.toggled = !event.item.toggled;
				dataGrid.globalHightlightAll = event.item.toggled;
				//this.text = dataGrid.globalHightlightString;
			}
			else {
				
				var pop:HighlightColumnPicker = new HighlightColumnPicker;
				pop.grid = this.dataGrid;
				pop.width=this.width;
				
				com.flexicious.utils.UIUtils.addPopUp(pop,FlexGlobals.topLevelApplication as DisplayObject);
				if(dataGrid.globalHighlightColumns.length==0)
					dataGrid.globalHighlightColumns=pop.cbList.dataProvider.source;
				pop.selectedItems = dataGrid.globalHighlightColumns;

				
				com.flexicious.utils.UIUtils.positionBelow(pop,this,0,0,true,"");
			}
			 
			
		}
		
	}
}