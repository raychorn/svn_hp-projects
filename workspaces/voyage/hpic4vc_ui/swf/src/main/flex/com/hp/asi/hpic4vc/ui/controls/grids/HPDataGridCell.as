package com.hp.asi.hpic4vc.ui.controls.grids
{
	import com.flexicious.nestedtreedatagrid.cells.FlexDataGridDataCell3;
	import com.flexicious.nestedtreedatagrid.events.FlexDataGridEvent;
	import com.hp.asi.hpic4vc.ui.controls.grids.HPDataGrid;
	import com.hp.asi.hpic4vc.ui.controls.support.IHighlightableCell;
	
	import flash.text.TextFormat;
	
	import mx.controls.textClasses.TextRange;
	import mx.core.UITextField;

	/**
	 * This is a customized version of the FlexDataGridDataCell3 class.
	 * In here, we use the grid's globalHightlightString and globalHighlightColumns properties
	 * to test to see if we are rendering text that the user has asked us to highlight. If so, 
	 * we use the htmlText of the textField we encapsutlate to render the text in a stylized manner. 
	 */	
	public class HPDataGridCell extends FlexDataGridDataCell3 implements IHighlightableCell
	{
		public function HPDataGridCell()
		{
			super();
			textField.alwaysShowSelection=true;
		}
		public override function set selectable(val:Boolean):void
		{
			//textField.selectable=val;
		}
		
		/**
		 * In here, we perform our logic that determines the htmlText (if any) to display
		 */		 
		protected override function updateDisplayList(unscaledWidth:Number, unscaledHeight:Number):void
		{
			super.updateDisplayList(unscaledWidth,unscaledHeight);
			highlight(false);
		}
		
		
		public function highlight(forceHighlight:Boolean):void{
			var hpdg:HPDataGrid = level && level.grid?level.grid as HPDataGrid:null;
			//if we're inside a HP datagrid, which has a globalHightlightString specified, and our column 
			//is in globalHighlightColumns or globalHighlightColumns is empty, and our text contains the 
			//globalHightlightString (case insensitive), then we highlight.
			if(hpdg && hpdg.globalHightlightString && (hpdg.globalHightlightAll  || forceHighlight)
				&& (hpdg.globalHighlightColumns.length==0 || (hpdg.globalHighlightColumns.indexOf(column)>=0)) 
				&& text.toLocaleLowerCase().indexOf(hpdg.globalHightlightString.toLocaleLowerCase())>=0){
				 
				var beginIndex:int=text.toLocaleLowerCase().indexOf(hpdg.globalHightlightString.toLocaleLowerCase());
				var endIndex:int=beginIndex+hpdg.globalHightlightString.length;
				this.textField.setSelection(beginIndex,endIndex);
			}else{
				this.textField.setSelection(0,0);
			}
		}
		public function removeHighlight():void{
			this.textField.setSelection(0,0);
		}
	}
}