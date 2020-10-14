package assets.components
{
	import com.hp.asi.hpic4vc.ui.Hpic4vc_server_providerProxy;
	import com.hp.asi.hpic4vc.ui.model.ClusterDetailModel;
	import com.hp.asi.hpic4vc.ui.model.LabelValueListModel;
	import com.hp.asi.hpic4vc.ui.utils.Helper;
	import com.vmware.core.model.IResourceReference;
	import com.vmware.flexutil.events.MethodReturnEvent;
	
	import mx.collections.ArrayCollection;
	import mx.logging.ILogger;
	import mx.logging.Log;

	public class Hpic4vc_ClusterMediator extends Hpic4vc_BaseMediator
	{
		private var _view:Hpic4vc_Cluster;
		private var _proxyServer:Hpic4vc_server_providerProxy;
		private var count:int = 0;
		
		private static var _logger:ILogger = Log.getLogger("Hpic4vc_Cluster");
		
		[View]
		/**
		 * The mediator's view.
		 */
		public function get view():Hpic4vc_Cluster {
			return _view;
		}
		
		/** @private */
		public function set view(value:Hpic4vc_Cluster):void {
			_view = value;
			_proxyServer = new Hpic4vc_server_providerProxy();
		}
		
		override public function set contextObject(value:Object):void {
			if (_view == null){
				_logger.warn('Hpic4vc Cluster Detail view is null');
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
				_proxyServer.getClusterDetailPortletData(_contextObject.uid, onGettingClusterDetails, _contextObject);
			} else {
				_logger.warn("ContextObject is null, hence not requesting data.");
				return;
			}
		}
		
		private function onGettingClusterDetails(event:MethodReturnEvent):void {
			
			if (_view != null) {
				_logger.warn("Received HPIC4VC data in onGettingResult()");
				if (event == null){
					_view.errorFound = Helper.getString("errorOccurred");
					return;
				} 
				else if ( event.error != null) {
					_logger.warn("DeliveryInDoubt exception occurred.  Count: " + count.toString());
					if (event.error.toString().match("DeliveryInDoubt")) {
						// Re try to request data for not more than 2 times
						if (count < 2) {
							count ++;
							requestData();
							return;
						} else {
							_view.errorFound = event.error.message;
							return;
						}
					} else {
						_view.errorFound = event.error.message;
						return;
					}
				} 
				else if (event.result == null) {
					_view.errorFound= event.error.message;
				} 
				
				var clusterDetail:ClusterDetailModel = event.result as ClusterDetailModel;
				//var hostCollection:ArrayCollection = Helper.createHierarchialArrayCoMllection(clusterDetail);
				if (clusterDetail)
					_view.clusterDetail = clusterDetail;
			}
			else {
				_logger.warn("View is null.  Returning from onGettingResult()");
				return;
			}
		}
	}
	
}