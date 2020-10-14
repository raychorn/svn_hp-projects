package assets.components
{
	import com.hp.asi.hpic4vc.ui.Hpic4vc_server_providerProxy;
	import com.hp.asi.hpic4vc.ui.model.DataGridWrapper;
	import com.hp.asi.hpic4vc.ui.model.HostDetailModel;
	import com.hp.asi.hpic4vc.ui.model.NetworkDetailModel;
	import com.hp.asi.hpic4vc.ui.model.NetworkDetailViewModel;
	import com.hp.asi.hpic4vc.ui.model.TableModel;
	import com.hp.asi.hpic4vc.ui.utils.Helper;
	import com.vmware.flexutil.events.MethodReturnEvent;
	
	import flash.events.EventDispatcher;
	import flash.utils.setTimeout;
	
	import mx.controls.Alert;
	import mx.logging.ILogger;
	import mx.logging.Log;
	
	public class Hpic4vc_Server_NetworkingMediator extends Hpic4vc_BaseMediator
	{
		private var _view:Hpic4vc_Server_Networking;
		private var _proxyServer:Hpic4vc_server_providerProxy;
		private var count:int = 0;
		
		private static var _logger:ILogger = Log.getLogger("Hpic4vc_Server_NetworkingMediator");
		
		[View]
		/**
		 * The mediator's view.
		 */
		public function get view():Hpic4vc_Server_Networking {
			return _view;
		}
		
		/** @private */
		public function set view(value:Hpic4vc_Server_Networking):void {
			_view = value;
		}
		
		override protected function clearData():void {
			_view = null;
		}
		
	}
}