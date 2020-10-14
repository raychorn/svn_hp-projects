package com.hp.asi.hpic4vc.ui.controls.support
{
	
	import mx.core.IToolTip;
	import mx.core.IUIComponent;
	
	import spark.components.BorderContainer;
	import spark.components.Label;
	import spark.layouts.VerticalLayout;
	/**
	 * The default renderer for the datatip.
	 * This is a border container and will support all border container styles.
	 */	
	public class DataTipRenderer extends BorderContainer implements IToolTip
	{
		
		public var lbl:Label=new Label;
		public function DataTipRenderer()
		{
			super();
			addElement(lbl);
			var vl:VerticalLayout= new VerticalLayout();
			vl.paddingBottom=vl.paddingLeft=vl.paddingRight=vl.paddingTop=5;
			layout=vl;
			height=50;
		}
		
		private var _text:String;
		/**
		 * The text to show in the middle
		 */		
		public function get text():String
		{
			return lbl.text;
		}
		/**
		 * @private
		 */		
		public function set text(value:String):void
		{
			lbl.text = value;
		}
		
		private var _tooltipOwner:IUIComponent;
		/**
		 * The owner component (the bar chart) mostly.
		 */		
		public function get tooltipOwner():IUIComponent
		{
			return _tooltipOwner;
		}
		
		public function set tooltipOwner(value:IUIComponent):void
		{
			_tooltipOwner = value;
		}
		
	}
	
}