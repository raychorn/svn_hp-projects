package com.hp.asi.hpic4vc.ui.controls
{
	import com.hp.asi.hpic4vc.ui.model.BarChartGroupModel;
	import com.hp.asi.hpic4vc.ui.model.BarChartModel;
	
	import mx.collections.ArrayCollection;
	import mx.collections.ICollectionView;
	import mx.collections.IList;
	import mx.collections.ListCollectionView;
	import mx.collections.XMLListCollection;
	import mx.core.UIComponent;
	import mx.events.ResizeEvent;
	
	import spark.components.HGroup;
	import spark.components.Label;
	import spark.components.VGroup;
	import spark.layouts.VerticalLayout;
	
	/**
	 * Chart Repeater is a component that is optimized to show Divided Bar Charts.
	 * The data provider is expected to be an array of ArraySummaryModel objects.
	 * For the hp storate array view, the dataprovider consists of a number of ArraySummaryModel 
	 * objects. For the datastores view, we simply pass in a top level ArraySummaryModel that
	 * contains a list of BarChartModels. 
	 */	
	public class ChartRepeater extends VGroup
	{
		public function ChartRepeater()
		{
			addEventListener(ResizeEvent.RESIZE,onResize);
		}	
		
		protected function onResize(event:ResizeEvent):void
		{
			if(_dataProvider){
				invalidateData();
			}
		}
		private var _bDataDirty:Boolean=false;
		private var _dataProvider:Object;
		
		[Inspectable(category="Data", arrayType="Object")]
		
		/**
		 *  Specifies the data provider for the chart.
		 *  
		 *  <p>This property can accept an array
		 *  or any other object that implements the IList or ICollectionView interface.</p>
		 *  
		 *  @langversion 3.0
		 *  @playerversion Flash 9
		 *  @playerversion AIR 1.1
		 *  @productversion Flex 3
		 */
		public function get dataProvider():Object
		{
			return _dataProvider;
		}
		
		/**
		 *  @private
		 */
		public function set dataProvider(value:Object):void
		{
			if (value is Array)
			{
				value = new ArrayCollection(value as Array);
			}
			else if (value is ICollectionView)
			{
			}
			else if (value is IList)
			{
				value = new ListCollectionView(value as IList);
			}
			else if (value is XMLList)
			{
				value = new XMLListCollection(XMLList(value));
			}
			else if (value != null)
			{
				value = new ArrayCollection([ value ]);
			}
			else
			{
				value = new ArrayCollection();
			}
			_dataProvider = ICollectionView(value);
			invalidateData();        
			
		}
		/**
		 *  Triggers a redraw of the chart.
		 *  Call this method when you add or change
		 *  the chart's series or data providers.
		 *  
		 *  @langversion 3.0
		 *  @playerversion Flash 9
		 *  @playerversion AIR 1.1
		 *  @productversion Flex 3
		 */
		public function invalidateData():void
		{
			_bDataDirty=true;
			invalidateDisplayList();
		}
		/**
		 * @private
		 */		
		protected override function updateDisplayList(unscaledWidth:Number, unscaledHeight:Number):void{
			super.updateDisplayList(unscaledWidth,unscaledHeight);
			if(_bDataDirty){
				_bDataDirty=false;
				removeAllElements(); //take out all elements
				
				for each(var asm:BarChartGroupModel in dataProvider){
					if(isTree){//this is the storage array view 
						if(asm.groupTitle){
							var title:Label = new Label; //add the top label
							title.maxDisplayedLines=1;
							title.percentHeight=100;
							title.percentWidth=100;
							title.height=titleHeight;
							title.text=asm.groupTitle;
							title.styleName=titleStyleName;
							addElement(title);
						}
						if(asm.errorMessage){
							//something went wrong, so show the error message instead of the bar charts.
							var em:Label = new Label;
							em.maxDisplayedLines=1;
							em.percentHeight=100;
							em.percentWidth=100;
							em.height=titleHeight;
							em.text=asm.errorMessage;
							em.toolTip=asm.errorMessage;
							em.styleName=titleErrorStyleName;
							addElement(em);
						}
					}
					if(!asm.errorMessage){
						//no error, show barcharts
						for each(var bcm:BarChartModel in asm.barChartData){
							var grp:HGroup = new HGroup;
							grp.percentWidth=100;
							//add the bar chart label
							var lbl:Label = new Label;
							lbl.maxDisplayedLines=1;
							lbl.percentHeight=100;
							lbl.styleName=chartLabelStyleName;
							lbl.percentWidth=50;
							lbl.text=bcm.info;
							lbl.toolTip=bcm.info;
							lbl.maxDisplayedLines=1;
							lbl.percentHeight=100;
							grp.addElement(lbl);
							if(bcm.errorMessage){
								//some thing went wrong, do not show bar chart, show error label only
								var em1:Label = new Label;
								em1.styleName=chartLabelErrorStyleName;
								em1.maxDisplayedLines=1;
								em1.percentWidth=50;
								em1.text=bcm.errorMessage;
								em1.toolTip=bcm.errorMessage;
								grp.addElement(em1);
							}else{
								//no error, show barchart
								var dbc:DividedBarChart=new DividedBarChart;
								dbc.percentWidth=50;
								dbc.height=barChartHeight;
								dbc.dataProvider=[bcm.usedSpace,bcm.freeSpace,bcm.notProvisioned,bcm.overProvisioned]//order of the items is important since it goes with the colors in the fillColors style
								dbc.toolTipText = bcm.hoverData;
								grp.addElement(dbc);
							}
							addElement(grp);
							
						}
						
					}
					
				}
			}
		}
		
		
		
		/**
		 * Style name to apply for the info message for array summary labels - only applicable if showArraySummaryModel=true (for Storage Array Component) 
		 */
		public var titleStyleName:String="titleStyle";
		/**
		 * Style name to apply for the error message for array summary labels  - only applicable if showArraySummaryModel=true (for Storage Array Component) 
		 */
		public var titleErrorStyleName:String="titleErrorStyle";
		/**
		 * Style name to apply for the info message for chart labels 
		 */
		public var chartLabelStyleName:String="chartLabelStyle";
		
		/**
		 * Style name to apply for the error message for chart labels 
		 */
		public var chartLabelErrorStyleName:String="chartLabelErrorStyle";
		/**
		 * Height of the array summary section.
		 */
		public var titleHeight:Number=20;
		/**
		 * Height of each barchart 
		 */		
		public var barChartHeight:Number=20;
		/**
		 * Whether to show the array summary header. Set to true for HP Storage Arrrays, false for Datastores.
		 */	
		public var isTree:Boolean=true;
		/**
		 * The vertical gap between the charts.
		 */		
		public function get verticalGap():Number
		{
			return (layout as VerticalLayout).gap;
		}
		/**
		 * @private
		 */		
		public function set verticalGap(value:Number):void
		{
			(layout as VerticalLayout).gap = value;
		}

		
	}
}