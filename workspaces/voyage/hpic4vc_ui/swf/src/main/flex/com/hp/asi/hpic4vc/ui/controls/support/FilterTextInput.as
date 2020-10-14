package com.hp.asi.hpic4vc.ui.controls.support
{
	import com.flexicious.controls.TextInput;
	import com.flexicious.grids.dependencies.IExtendedDataGrid;
	import com.flexicious.utils.UIUtils;
	import com.hp.asi.hpic4vc.ui.controls.grids.HPDataGrid;
	import com.hp.asi.hpic4vc.ui.utils.IconRepository;
	
	import flash.display.DisplayObject;
	import flash.events.Event;
	import flash.events.KeyboardEvent;
	import flash.ui.Keyboard;
	
	import mx.controls.Menu;
	import mx.core.FlexGlobals;
	import mx.core.mx_internal;
	import mx.events.MenuEvent;

	use namespace mx_internal;
	/**
	 * This class inherits from the Flexicious Text Input class and adds the following functionality to it:
	 * 1) Ability to associate with a grid and show its columns in a picker
	 * 2) Associate search and dropdown icons.
	 */	
	public class FilterTextInput extends TextInput
	{
		
		/**
		 * The grid associated with this search control 
		 */		
		public var dataGrid:HPDataGrid;
		/**
		 * The menu to launch our picker 
		 */		
		private var menu:Menu;
		/**
		 * @private 
		 */
		protected var menuDataProvider:Object=[{label:"Select Columns..."}];
		public function FilterTextInput()
		{
			
			super();
			
			/*setStyle("insideIcon", IconRepository.magnifyingGlassIcon);
			setStyle("outsideIcon", IconRepository.downArrow);*/
			addEventListener("outsideIconClick",onDownArrowClick);
			addEventListener("insideIconClick",onSearchClick);
			addEventListener(KeyboardEvent.KEY_UP,onKeyUp);
		}
		
		protected function onSearchClick(event:Event):void
		{
			dataGrid.globalFilterString = text;
		}
		
		protected function onKeyUp(event:KeyboardEvent):void
		{
			if(event.keyCode==Keyboard.ENTER){
				dataGrid.globalFilterString = text;
			}
		}
		/*
		protected override function createChildren():void{
			
		}*/
		
		protected function onDownArrowClick(event:Event):void
		{
			if(!menu){
				menu = Menu.createMenu(null, menuDataProvider , true);
				menu.addEventListener(MenuEvent.ITEM_CLICK,onMenuClick);
				menu.variableRowHeight=true;
			}
			menu.width=this.width;
			menu.show();
			com.flexicious.utils.UIUtils.positionBelow(menu,this,0,0,true,"");
		}
		
		protected function onMenuClick(event:MenuEvent):void
		{
			var pop:FilterColumnPicker = new FilterColumnPicker;
			pop.grid = this.dataGrid;
			pop.width=this.width;
			com.flexicious.utils.UIUtils.addPopUp(pop,FlexGlobals.topLevelApplication as DisplayObject);
			if(dataGrid.globalFilterColumns.length==0)
				dataGrid.globalFilterColumns=pop.cbList.dataProvider.source;
			pop.selectedItems = dataGrid.globalFilterColumns;
			com.flexicious.utils.UIUtils.positionBelow(pop,this,0,0,true,"");
		}
		
		override protected function updateDisplayList(unscaledWidth:Number, unscaledHeight:Number):void{
			super.updateDisplayList(unscaledWidth,unscaledHeight);
			if(outsideIconImg||insideIconImg){
				var horizontalGap:Number=getStyle("iconGap")||2;
				
				textField.width=watermarkLabel.width=(width-(insideIconImg?insideIconImg.width+horizontalGap:0)-(outsideIconImg?outsideIconImg.width+horizontalGap:0))
				textField.x=watermarkLabel.x=insideIconImg.width+horizontalGap+horizontalGap;
				insideIconImg.x=horizontalGap+horizontalGap;
				outsideIconImg.x=width-horizontalGap-horizontalGap-outsideIconImg.width;
				watermarkLabel.y = textField.y;
				if(border){
					border.x=0;
					border.width=width;
				}
			}
		}
			
	}
}