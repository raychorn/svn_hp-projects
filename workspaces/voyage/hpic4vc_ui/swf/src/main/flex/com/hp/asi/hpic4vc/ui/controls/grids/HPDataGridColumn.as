package com.hp.asi.hpic4vc.ui.controls.grids
{
	import com.flexicious.nestedtreedatagrid.FlexDataGridColumn;
	
	import mx.collections.ArrayCollection;
	import mx.core.ClassFactory;
	import mx.core.IFactory;
	import mx.core.UIFTETextField;
	
	public class HPDataGridColumn extends FlexDataGridColumn
	{
		private static var static_FlexDataGridDataCell3:IFactory=new ClassFactory(HPDataGridCell);
		private static var static_FlexDataGridDataCellUIComponent:IFactory=new ClassFactory(HPDataGridDataCell);

		public function HPDataGridColumn()
		{
			super();
			itemEditorApplyOnValueCommit=true;
			setStyle("paddingLeft",5);
		}
		/**
		 * Container for the item renderer. Needs to implement IFlexDataGridDataCell.
		 * Defaults to FlexDataGridDataCell  
		 * @return 
		 */			
		public override function get dataCellRenderer():IFactory
		{
			return (truncateToFit||selectable||useHandCursor||useUnderLine||wordWrap||itemRenderer||enableExpandCollapseIcon||enableIcon)?
				static_FlexDataGridDataCellUIComponent:enableDataCellOptmization?static_FlexDataGridDataCell3:super.dataCellRenderer;
		}
		public var isLink:Boolean;
		/**
		 * A list of link models associated with this column 
		 */		
		public var linkModels:ArrayCollection=new ArrayCollection;
	}
}