package com.hp.asi.hpic4vc.ui.controls.support
{
	import mx.controls.ProgressBar;
	import mx.core.mx_internal;
	
	use namespace mx_internal;
	
	public class HPProgressBar extends ProgressBar
	{
		public function HPProgressBar()
		{
			super();
		}
		override protected function updateDisplayList(unscaledWidth:Number,
													  unscaledHeight:Number):void
		{
			if(_track)
			super.updateDisplayList(unscaledWidth,unscaledHeight);
			
		}
	}
}