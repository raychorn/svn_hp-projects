package com.hp.asi.hpic4vc.ui.controls.grids
{
	import com.flexicious.nestedtreedatagrid.cells.FlexDataGridDataCell;
	import com.hp.asi.hpic4vc.ui.controls.support.IHighlightableCell;
	
	import flash.events.Event;
	
	import mx.controls.Label;
	import mx.core.IUITextField;
	import mx.core.mx_internal;

	use namespace mx_internal;
	
	public class HPDataGridDataCell extends FlexDataGridDataCell implements IHighlightableCell
	{
		public function HPDataGridDataCell()
		{
			super();
		}
		
		public override function refreshCell():void             {
			if(this.colIcon){
				this.colIcon.source=null;
			}
			super.refreshCell();
			highlight(false);
			if(this.colIcon) {
				if(this.colIcon.visible){
					this.colIcon.setActualSize(14,14);
				}
				this.colIcon.addEventListener("complete",function (e:Event):void{
					if(this.colIcon.contentWidth>0){
						e.target.width = this.colIcon.contentWidth;
						e.target.height = this.colIcon.contentHeight;
					}else{
						this.colIcon.setActualSize(14,14);
					}
					
				});
			}
		}
		public function highlight(forceHighlight:Boolean):void{
			var hpdg:HPDataGrid = level && level.grid?level.grid as HPDataGrid:null;
			//if we're inside a HP datagrid, which has a globalHightlightString specified, and our column 
			//is in globalHighlightColumns or globalHighlightColumns is empty, and our text contains the 
			//globalHightlightString (case insensitive), then we highlight.
			if(hpdg && hpdg.globalHightlightString  && (hpdg.globalHightlightAll  || forceHighlight)
				&& (hpdg.globalHighlightColumns.length==0 || (hpdg.globalHighlightColumns.indexOf(column)>=0)) 
				&& text.toLocaleLowerCase().indexOf(hpdg.globalHightlightString.toLocaleLowerCase())>=0){
				
				var beginIndex:int=text.toLocaleLowerCase().indexOf(hpdg.globalHightlightString.toLocaleLowerCase());
				var endIndex:int=beginIndex+hpdg.globalHightlightString.length;
				(this.renderer as Label).getTextField().alwaysShowSelection=true;
				(this.renderer as Label).getTextField().setSelection(beginIndex,endIndex);
			}else{
				(this.renderer as Label).getTextField().setSelection(0,0);
			}
		}
		
		public function removeHighlight():void{
			(this.renderer as Label).getTextField().setSelection(0,0);
		}
	}
}