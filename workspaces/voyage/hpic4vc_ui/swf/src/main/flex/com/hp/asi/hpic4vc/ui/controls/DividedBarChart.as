package com.hp.asi.hpic4vc.ui.controls
{	
	import com.hp.asi.hpic4vc.ui.controls.support.DataTipRenderer;
	import com.hp.asi.hpic4vc.ui.controls.support.TooltipBehavior;
	import com.hp.asi.hpic4vc.ui.controls.support.UIUtils;
	
	import flash.events.MouseEvent;
	import flash.geom.Point;
	
	import mx.collections.ArrayCollection;
	import mx.collections.ICollectionView;
	import mx.collections.IList;
	import mx.collections.ListCollectionView;
	import mx.collections.XMLListCollection;
	import mx.core.IToolTip;
	import mx.core.UIComponent;
	import mx.events.ResizeEvent;
	
	/**
	 * Colors for the divider bars 
	 */	
	[Style(name="fillColors",type="Array", arrayType="color")]
	
	
	/**
	 * A simple UI Component that renders a stack of divided bars on basis
	 * of a dataprovider that consist of an array of numbers.
	 */	
	public class DividedBarChart extends UIComponent
	{
		public var tooltipBehavior:TooltipBehavior;
		public var currentTip:IToolTip;

		public function DividedBarChart()
		{
			tooltipBehavior=new TooltipBehavior(this);
			addEventListener(MouseEvent.MOUSE_MOVE, mouseMoveHandler);
			addEventListener(MouseEvent.MOUSE_OUT, mouseOutHandler);
			addEventListener(ResizeEvent.RESIZE,onResize)
		}
		/**
		 * So we redraw after a resize event
		 */		
		protected function onResize(event:ResizeEvent):void
		{
			invalidateData();
		}
		/**
		 * Hide the tooltip 
		 */		
		protected function mouseOutHandler(event:MouseEvent):void
		{
			if(currentTip){
				tooltipBehavior.hideTooltip();
				currentTip=null;
			}
		}
		/**
		 * Position the toolip
		 */		
		protected function mouseMoveHandler(event:MouseEvent):void
		{
			
			//var items:Array = findDataPoints(event.localX,event.localY,true);
			if(!currentTip)
				currentTip = new DataTipRenderer();
			tooltipBehavior.showTooltip(this,currentTip,null, new Point(event.stageX+16,event.stageY+16));
			currentTip.text=toolTipText;//findDataPoints(event.localX,event.localY);
		}

		
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
				drawChart();
			}
		}
		/**
		 * This method does the actual drawing of the chart. 
		 */		
		public function drawChart():void{
			if(width==0||height==0)return;
			graphics.clear();
			var hdp:Object=dataProvider;
			var hdpLen:Number=(dataProvider.length);
			graphics.moveTo(0,height);
			var i:int=0;
			var currentXPos:int=0;
			var sum:Number=UIUtils.sum(hdp);
			for each(var item:Object in  hdp){
				var itemVal:Number=item as Number;
				if(isNaN(itemVal))itemVal=0;
				if(isNaN(sum) || sum==0)sum=1;
				var xPos:Number = (width*itemVal)/sum;; 

				//figure out the color
				graphics.beginFill(fillColors[i%fillColors.length]);
				graphics.moveTo(currentXPos,height); //fill the rectangle with the chosen color
				graphics.lineTo(currentXPos, 0);
				graphics.lineTo(xPos+currentXPos, 0);
				graphics.lineTo(xPos+currentXPos, height);
				graphics.lineTo(currentXPos, height);
				graphics.endFill();
				i++;
				currentXPos+=xPos;
			}
		}
		/**
		 * We can build this function if we need section specific tooltips or highlights.
		 * Per Zach, this is currently not needed.
		 */		
		public function findDataPoints(x:Number,y:Number):String{
			var hdp:Object=dataProvider;
			var sum:int=UIUtils.sum(hdp);
			var i:int=0;
			var currentXpos:Number=0;
			for each(var item:Object in  hdp){
				var itemVal:Number=item as Number;
				var xPosForItem:Number=(width*itemVal)/sum;
				if(x>=currentXpos&& x<(currentXpos+xPosForItem)){
					return i.toString();
				}
				currentXpos+=xPosForItem;
				i++;
			}
			return "";
		}
	
		private var _bDataDirty:Boolean=false;
		
		/**
		 *  Colors for the bars
		 */
		public function get fillColors():Array
		{
			return [0x0060BF,0xA7C3DF,0xDCDCDC,0xFFFF00];
		}
		/**
		 * The text for the tooltip; 
		 */		
		public var toolTipText:String="";
	}
}