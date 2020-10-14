package assets.components
{
	import com.hp.asi.hpic4vc.ui.Hpic4vc_providerProxy;
	import com.hp.asi.hpic4vc.ui.Hpic4vc_server_providerProxy;
	import com.vmware.core.model.IResourceReference;
	
	import flash.events.EventDispatcher;
	
	import mx.logging.ILogger;
	import mx.logging.Log;

	public class Hpic4vc_SoftwareFirmwareMediator extends Hpic4vc_BaseMediator
	{
		private var _view:Hpic4vc_SoftwareFirmware;
		private var _proxyServer:Hpic4vc_server_providerProxy;
		private var count:int = 0;
		
		[View]
		/**
		 * The mediator's view.
		 */
		public function get view():Hpic4vc_SoftwareFirmware {
			return _view;
		}
		
		/** @private */
		public function set view(value:Hpic4vc_SoftwareFirmware):void {
			_view = value;
		}
		
		override protected function clearData():void {
			_view = null;
		}
	
	}	
}