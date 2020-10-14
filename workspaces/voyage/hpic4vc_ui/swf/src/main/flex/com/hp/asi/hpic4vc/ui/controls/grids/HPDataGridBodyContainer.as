package com.hp.asi.hpic4vc.ui.controls.grids
{
	import com.flexicious.nestedtreedatagrid.FlexDataGrid;
	import com.flexicious.nestedtreedatagrid.FlexDataGridVirtualBodyContainer;
	
	public class HPDataGridBodyContainer extends FlexDataGridVirtualBodyContainer
	{
		public function HPDataGridBodyContainer(grid:FlexDataGrid)
		{
			super(grid);
		}
		/**
		 * @private
		 */
		override protected function scrollChildren():void
		{
			super.scrollChildren();
			
			if(horizontalScrollBar ){
				horizontalScrollBar.move(0-grid.leftLockedContent.width,height-horizontalScrollBar.height);
				horizontalScrollBar.setActualSize(grid.width - (verticalScrollBar?verticalScrollBar.width:0),horizontalScrollBar.height);
			}
			grid.pagerContainer.move(0,grid.height-grid.pagerContainer.height-1);

		}
	}
}