package assets.renderers
{
	import assets.events.ColumnSelectedEvent;
	
	import com.hp.asi.hpic4vc.ui.vo.ServerNodesData;
	
	import flash.events.Event;
	
	import mx.collections.ArrayCollection;
	import mx.controls.AdvancedDataGrid;
	import mx.controls.CheckBox;
	import mx.controls.advancedDataGridClasses.AdvancedDataGridColumn;
	import mx.controls.advancedDataGridClasses.AdvancedDataGridHeaderRenderer;
	
	public class CheckBoxHeaderRenderer extends AdvancedDataGridHeaderRenderer
	{
		
		private var selector:CheckBox;
		
		
		public function CheckBoxHeaderRenderer()
		{
			super();
		}
		
		override protected function createChildren():void
		{
			super.createChildren();
			this.selector = new CheckBox();
			this.selector.x = 4;
			this.addChild(this.selector);
			this.selector.addEventListener(Event.CHANGE, dispatchColumnSelected);
		}
		
		override public function set data(value:Object):void
		{
			super.data = value;
			if(value is AdvancedDataGridColumn)
			{
				if (this.owner is AdvancedDataGrid)
				{
					if (AdvancedDataGrid(this.owner).dataProvider)
					{
						var dataList:ArrayCollection = AdvancedDataGrid(this.owner).dataProvider as ArrayCollection;
						for each (var item:ServerNodesData in dataList)
						{
							if(!item.deploy)
							{
								this.selector.selected = false;
								break;
							}
							
							this.selector.selected = true;
						}
						//invalidateDisplayList();
					}
					
				}
			}
		}
		override protected function updateDisplayList(unscaledWidth:Number, unscaledHeight:Number):void
		{
			super.updateDisplayList(unscaledWidth,unscaledHeight);
			this.selector.setActualSize(this.selector.getExplicitOrMeasuredWidth(), this.selector.getExplicitOrMeasuredHeight());
		}
		
		protected function dispatchColumnSelected (e:Event):void
		{
			dispatchEvent(new ColumnSelectedEvent(ColumnSelectedEvent.COLUMN_SELECTED,0,this.selector.selected));
		}
		
		
	}
}