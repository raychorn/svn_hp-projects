package com.hp.asi.hpic4vc.ui.controls.support
{
	import com.hp.asi.hpic4vc.ui.skins.HPTitleWindowSkin;
	
	import mx.events.CloseEvent;
	import mx.managers.PopUpManager;
	
	import spark.components.TitleWindow;

	/**
	 * A title window with a downward pointing tail
	 */	
	public class HPTitleWindow extends TitleWindow
	{
		public function HPTitleWindow()
		{
			super();
			addEventListener(CloseEvent.CLOSE,onClose);
			this.setStyle("skinClass",HPTitleWindowSkin);
		}
		
		[Bindable]
		/**
		 * Message to show 
		 */
		public function get message():String
		{
			return _message;
		}

		/**
		 * @private
		 */
		public function set message(value:String):void
		{
			_message = value;
		}

		/**
		 * @private 
		 */		
		protected function onClose(event:CloseEvent):void
		{
			PopUpManager.removePopUp(this);
		}	
		
		private var _message:String;
	}
}