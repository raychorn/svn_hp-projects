package com.hp.asi.hpic4vc.ui.skins
{
	import flash.display.Graphics;
	
	import mx.skins.spark.ProgressIndeterminateSkin;
	
	public class HPProgressIndeterminateSkin extends ProgressIndeterminateSkin
	{
		public function HPProgressIndeterminateSkin()
		{
			super();
		}
		
		//--------------------------------------------------------------------------
		//
		//  Overridden methods
		//
		//--------------------------------------------------------------------------
		
		private static var colors:Array = [0x31B7FF, 0x31B7FF];
		private static var alphas:Array = [1, 1];
		private static var ratios:Array = [255, 255];
		
		/**
		 *  @private
		 */        
		override protected function updateDisplayList(w:Number, h:Number):void
		{
			super.updateDisplayList(w, h);
			
			// User-defined styles
			var hatchInterval:Number = getStyle("indeterminateMoveInterval");
			
			if (isNaN(hatchInterval))
				hatchInterval = 28;
			
			var g:Graphics = graphics;
			
			g.clear();
			
			// Hatches
			for (var i:int = 0; i < w; i += hatchInterval)
			{
				g.beginGradientFill("linear", colors, alphas, ratios, 
					verticalGradientMatrix(i - 4, 2, 7, h - 4));
				g.moveTo(i, 2);
				g.lineTo(Math.min(i + 7, w), 2);
				g.lineTo(Math.min(i + 3, w), h - 2);
				g.lineTo(Math.max(i - 4, 0), h - 2);
				g.lineTo(i, 2);
				g.endFill();
				g.lineStyle(1, 0, 0.12);
				g.moveTo(i, 2);
				g.lineTo(Math.max(i - 4, 0), h - 2);
				g.moveTo(Math.min(i + 7, w), 2);
				g.lineTo(Math.min(i + 3, w), h - 2);
			}
		}
		
	}
}