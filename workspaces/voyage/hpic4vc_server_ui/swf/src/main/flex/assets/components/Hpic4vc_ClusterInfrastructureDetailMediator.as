package assets.components
{
	import com.hp.asi.hpic4vc.ui.Hpic4vc_server_providerProxy;
	import com.hp.asi.hpic4vc.ui.model.ClusterInfrastructureDetailModel;
	import com.hp.asi.hpic4vc.ui.model.ClusterInfrastructureModel;
	import com.hp.asi.hpic4vc.ui.utils.Helper;
	import com.vmware.core.model.IResourceReference;
	import com.vmware.flexutil.events.MethodReturnEvent;
	
	import mx.logging.ILogger;
	import mx.logging.Log;
	

	public class Hpic4vc_ClusterInfrastructureDetailMediator extends Hpic4vc_BaseMediator
	{
		private var _view:Hpic4vc_ClusterInfrastructure_Detail;
		private var _proxyServer:Hpic4vc_server_providerProxy;
		private var count:int = 0;
		
		private static var _logger:ILogger = Log.getLogger("Hpic4vc_ClusterInfrastructureDetailMediator");
		
		[View]
		/**
		 * The mediator's view.
		 */
		public function get view():Hpic4vc_ClusterInfrastructure_Detail {
			return _view;
		}
		
		/** @private */
		public function set view(value:Hpic4vc_ClusterInfrastructure_Detail):void {
			_view = value;
			_proxyServer = new Hpic4vc_server_providerProxy();
		}
		
		override public function set contextObject(value:Object):void {
			if (_view == null){
				_logger.warn('Cluster_Infrastructure_Detail_view is null');
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
		
		override protected function requestData():void 
		{
			if (_contextObject != null) {
				_view.currentState = "loadingData";
				_logger.debug("Requesting HPIC4VC data.");
				_proxyServer.getClusterInfrastructureDetail(_contextObject.uid, onGettingClusterInfrastructureDetail, _contextObject);
			} else {
				_logger.warn("ContextObject is null, hence not requesting data.");
				return;
			}
		}
		
		private function onGettingClusterInfrastructureDetail(event:MethodReturnEvent):void
		{
			
			if(event && event.result)
			{
				var details:ClusterInfrastructureModel = event.result as ClusterInfrastructureModel;
				if(details && details.clusterInfrastructure)
				{
					_view.currentState = "showClusterInfraDetail";
					_view.clusterInfrstructureModel = details;
					
				}
				else
				{
					//_view.clusterInfrastructureDetailModel.errorMessage = 'Unable to convert';
					if (details.clusterInfrastructure == null)
					{
						_view.currentState = "errorFound";
					}
					else
					{
						_view.currentState = "dataNotFound";
					}
					
					
				}
				
			}
			else if (event && event.error)
			{
				_logger.warn("DeliveryInDoubt exception occurred.  Count: " + count.toString());
				if (event.error.toString().match("DeliveryInDoubt")) {
					// Re try to request data for not more than 2 times
					if (count < 2) {
						count ++;
						requestData();
						return;
					} else {
						_view.errorFoundLabel = event.error.message;
						_view.currentState = "errorFound";
						
						return;
					}
			}
			
		}
		
		
	}
}
}