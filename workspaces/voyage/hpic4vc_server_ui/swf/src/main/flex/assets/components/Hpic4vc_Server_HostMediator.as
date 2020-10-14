package assets.components
{
	import com.hp.asi.hpic4vc.ui.Hpic4vc_server_providerProxy;
	import com.hp.asi.hpic4vc.ui.model.DataGridWrapper;
	import com.hp.asi.hpic4vc.ui.model.HostDetailModel;
	import com.hp.asi.hpic4vc.ui.model.HostDetailViewModel;
	import com.hp.asi.hpic4vc.ui.model.TableModel;
	import com.hp.asi.hpic4vc.ui.utils.Helper;
	import com.vmware.flexutil.events.MethodReturnEvent;
	
	import flash.events.EventDispatcher;
	import flash.utils.setTimeout;
	
	import mx.controls.Alert;
	import mx.logging.ILogger;
	import mx.logging.Log;

	public class Hpic4vc_Server_HostMediator extends Hpic4vc_BaseMediator
	{
		private var _view:Hpic4vc_Server_Host;
		private var _proxyServer:Hpic4vc_server_providerProxy;
		private var count:int = 0;
		
		private static var _logger:ILogger = Log.getLogger("Hpic4vc_Server_HostMediator");
		
		[View]
		/**
		 * The mediator's view.
		 */
		public function get view():Hpic4vc_Server_Host {
			return _view;
		}
		
		/** @private */
		public function set view(value:Hpic4vc_Server_Host):void {
			_view = value;
			_proxyServer = new Hpic4vc_server_providerProxy();
		}
		
		override protected function clearData():void {
			_view = null;
		}
		
		override protected function requestData():void {
			if (_contextObject != null) {
				_logger.debug("Requesting HPIC4VC data.");
				_proxyServer.getHostDetail(_contextObject.uid, onGettingHostDetail, _contextObject);
			} else {
				_logger.warn("ContextObject is null, hence not requesting data.");
				return;
			}
		}
		
		private function onGettingHostDetail(event:MethodReturnEvent):void {
			if (_view != null) {
				_logger.warn("Received HPIC4VC data in onGettingResult()");
				if (event == null){
	                _view.errorFound = Helper.getString("errorOccurred");
					_view.currentState = "errorFound";
	                return;
	            } 
	            else if (event.error != null) {
					_logger.warn("DeliveryInDoubt exception occurred.  Count: " + count.toString());
					if (event.error.toString().match("DeliveryInDoubt")) {
						// Re try to request data for not more than 2 times
						if (count < 2) {
							count ++;
							requestData();
							return;
						} else {
							_view.errorFound = event.error.message;
							_view.currentState = "errorFound";
							return;
						}
					} else {
						_view.errorFound = event.error.message;
						_view.currentState = "errorFound";
						return;
					}
				} 
	            else if (event.result == null) {
	                _view.errorFound= event.error.message;
					_view.currentState = "errorFound";
	            } 
	
	            var detail:HostDetailModel = event.result as HostDetailModel;
				if (detail){
	                if (detail.informationMessage != null)
	                    _view.dataNotFound = detail.informationMessage;
					_view.hostDetail = HostDetailViewModel.makeHostDetailViewModel( detail );
					_view.currentState = "showDetail";
	            } 
	            else {
	                _view.errorFound = 'Error in the mediator'
	                _view.hostDetail.errorMessage = 'Unable to convert';
					_view.currentState = "errorFound";
	            }
				
			}
			else {
				_logger.warn("View is null.  Returning from onGettingResult()");
				return;
			}
		}
	}
}