package assets.components
{
	import com.hp.asi.hpic4vc.ui.Hpic4vc_providerProxy;
	import com.hp.asi.hpic4vc.ui.model.DataGridWrapper;
	import com.hp.asi.hpic4vc.ui.model.FirmwareModel;
	import com.hp.asi.hpic4vc.ui.model.TableModel;
	import com.hp.asi.hpic4vc.ui.utils.Helper;
	import com.vmware.core.model.IResourceReference;
	import com.vmware.flexutil.events.MethodReturnEvent;
	
	import flash.events.EventDispatcher;
	import flash.utils.setTimeout;
	
	import mx.controls.Alert;
	import mx.logging.ILogger;
	import mx.logging.Log;

	public class Hpic4vc_SoftwareFirmware_Detail_Mediator extends Hpic4vc_BaseMediator
	{
		private var _view:Hpic4vc_SoftwareFirmware_Detail;
		private var _proxy:Hpic4vc_providerProxy;
		private var count:int = 0;
		
		private static var _logger:ILogger = Log.getLogger("Hpic4vc_SoftwareFirmware_Detail_Mediator");
		
		[View]
		/**
		 * The mediator's view.
		 */
		public function get view():Hpic4vc_SoftwareFirmware_Detail {
			return _view;
		}
		
		/** @private */
		public function set view(value:Hpic4vc_SoftwareFirmware_Detail):void {
			_view = value;
			_proxy = new Hpic4vc_providerProxy();
		}
		
		override protected function clearData():void {
			_view = null;
		}
		
		override protected function requestData():void {
			if (_contextObject != null) {
				_logger.debug("Requesting HPIC4VC data.");
				_proxy.getSoftwareFirmwareDetail(_contextObject.uid, onGettingResult, _contextObject);
			} else {
				_logger.warn("ContextObject is null, hence not requesting data.");
				return;
			}
		}
		
		private function onGettingResult(event:MethodReturnEvent):void {
			if (_view != null) {
				_logger.debug("Received HPIC4VC data in onGettingResult()");
				_view.errorFoundLabel = "";
				if (event == null) {
					_view.errorFoundLabel = Helper.getString("errorOccurred");
					return;
				} else if (event.error != null) {
					_logger.warn("DeliveryInDoubt exception occurred.  Count: " + count.toString());
					if (event.error.toString().match("DeliveryInDoubt")) {
						// Re try to request data for not more than 2 times
						if (count < 2) {
							count ++;
							requestData();
							return;
						} else {
							_view.errorFoundLabel = event.error.message;
							return;
						}
					} else {
						_view.errorFoundLabel = event.error.message;
						return;
					}
				} else if (event.result == null) {
					_view.errorFoundLabel = Helper.getString("errorOccurred");
					return;
				} 
				
				_view.tableModel = event.result as TableModel;
			} else {
				_logger.warn("View is null.  Returning from onGettingResult()");
				return;
			}
		}
	}
}