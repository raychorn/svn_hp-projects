package assets.components
{
	import com.hp.asi.hpic4vc.ui.Hpic4vc_providerProxy;
	import com.hp.asi.hpic4vc.ui.Hpic4vc_server_providerProxy;
	import com.hp.asi.hpic4vc.ui.Hpic4vc_storage_providerProxy;
	import com.hp.asi.hpic4vc.ui.model.PageModel;
	import com.hp.asi.hpic4vc.ui.model.TabModel;
	import com.hp.asi.hpic4vc.ui.utils.Helper;
	import com.vmware.core.model.IResourceReference;
	import com.vmware.flexutil.events.MethodReturnEvent;
	
	import flash.display.DisplayObjectContainer;
	import flash.events.EventDispatcher;
	import flash.utils.setTimeout;
	
	import mx.collections.ArrayCollection;
	import mx.core.UIComponent;
	import mx.logging.ILogger;
	import mx.logging.Log;
	
	public class Hpic4vc_OverviewMediator extends Hpic4vc_BaseMediator
	{
		private var _view:Hpic4vc_Overview;
		private var _proxy:Hpic4vc_providerProxy;
		private var _proxyStorage:Hpic4vc_storage_providerProxy;
		private var _proxyServer:Hpic4vc_server_providerProxy;
		private var count:int = 0;
		
		private static var _logger:ILogger = Log.getLogger("Hpic4vc_OverviewMediator");
		
		[View]
		/**
		 * The mediator's view.
		 */
		public function get view():Hpic4vc_Overview {
			return _view;
		}
		
		/** @private */
		public function set view(value:Hpic4vc_Overview):void {
			_view = value;
			_proxy = new Hpic4vc_providerProxy();
			_proxyServer = new Hpic4vc_server_providerProxy();
			_proxyStorage = new Hpic4vc_storage_providerProxy();
		}
		
		override public function set contextObject(value:Object):void {
			if (_view == null){
				_logger.warn('Overview view is null');
				return;
			}
			_contextObject = _view._contextObject;
			
			if (_contextObject != null && IResourceReference(value) != null) {  
				if (_contextObject.uid != IResourceReference(value).uid) {
					clearData();
					return;
				}
			}
			requestData();
		}
		
		override protected function clearData():void {
			_view = null;
		}
		
		override protected function requestData():void {
			if (_contextObject != null) {
				_logger.debug("Requesting HPIC4VC data.");
				_proxy.getManageTabPages(_contextObject.uid, onGettingPortlets, _contextObject);
			} else {
				_logger.warn("ContextObject is null, hence not requesting data.");
				return;
			}
		}
		
		private function onGettingPortlets(event:MethodReturnEvent):void {
			if (_view != null) {
				_logger.warn("Received HPIC4VC data in onGettingResult()");
				_view.noPortletsFound = "";
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
				} else if ((event.result as PageModel).errorMessage != null) {
					_view.errorFoundLabel = (event.result as PageModel).errorMessage;
					return;
				} else if ((event.result as PageModel).informationMessage != null) {
					_view.noPortletsFound = (event.result as PageModel).informationMessage;
					return;
				} 
				
				var result:PageModel = event.result as PageModel;
				_view.portlets = result.portlets;
			}
			else {
				_logger.warn("View is null.  Returning from onGettingResult()");
				return;
			}
		}
	}
}