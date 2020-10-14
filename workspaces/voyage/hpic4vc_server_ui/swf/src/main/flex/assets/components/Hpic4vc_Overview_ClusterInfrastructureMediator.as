package assets.components
{
	import com.hp.asi.hpic4vc.ui.Hpic4vc_server_providerProxy;
	import com.hp.asi.hpic4vc.ui.model.ClusterInfraSummaryModel;
	import com.hp.asi.hpic4vc.ui.model.LabelValueListModel;
	import com.hp.asi.hpic4vc.ui.utils.Helper;
	import com.vmware.core.model.IResourceReference;
	import com.vmware.flexutil.events.MethodReturnEvent;
	import com.vmware.ui.IContextObjectHolder;
	
	import mx.logging.ILogger;
	import mx.logging.Log;

	public class Hpic4vc_Overview_ClusterInfrastructureMediator extends Hpic4vc_BaseMediator
	{
		
			private var _view:Hpic4vc_Overview_ClusterInfrastructure;
			private var _proxyServer:Hpic4vc_server_providerProxy;
			private var count:int = 0;
			
			
			private static var _logger:ILogger = Log.getLogger("Hpic4vc_Overview_ClusterInfrastructureMediator");
			
			[View]
			/**
			 * The mediator's view.
			 */
			public function get view():Hpic4vc_Overview_ClusterInfrastructure {
				return _view;
			}
			
			/** @private */
			public function set view(value:Hpic4vc_Overview_ClusterInfrastructure):void {
				_view = value;
				_proxyServer = new Hpic4vc_server_providerProxy();
			}
			
			override public function set contextObject(value:Object):void {
				if (_view == null){
					_logger.warn('Cluster_Overview_Infrastructure view is null');
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
					_logger.debug("Requesting HPIC4VC data.");
					_proxyServer.getClusterInfrastructureSummary(_contextObject.uid, handler, _contextObject);
				} else {
					_logger.warn("ContextObject is null, hence not requesting data.");
					return;
				}
			}
			
			private function onGettingClusterInfrastructureSummary(event:MethodReturnEvent):void
			{
				if (_view)
				{
					_logger.warn("Received HPIC4VC data in onGettingInfrastructureSummary()");
					if (event && event.result)
					{
						//_view.summaryPortletData = LabelValueListModel(event.result);
						
					}
				}
			}
			
			
			
			private function handler(event:MethodReturnEvent):void
				{
					if (_view != null) {
						_logger.warn("Received HPIC4VC data in onGettingClusterSummary()");
						_view.dataNotFound = "";
						_view.errorFoundLabel = "";
						
						if (event == null) {
							_view.errorFoundLabel = Helper.getString("errorOccurred");
							_view.currentState = "errorLoadingPortlet";
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
									_view.currentState = "errorLoadingPortlet";
									return;
								}
							} else {
								_view.errorFoundLabel = event.error.message;
								_view.currentState = "errorLoadingPortlet";
								return;
							}
						} else if (event.result == null) {
							_view.errorFoundLabel = Helper.getString("errorOccurred");
							_view.currentState = "errorLoadingPortlet";
							return;
						} else if ((event.result as ClusterInfraSummaryModel).errorMessage != null) {
							_view.errorFoundLabel = (event.result as ClusterInfraSummaryModel).errorMessage;
							_view.currentState = "errorLoadingPortlet";
							return;
						} else if ((event.result as ClusterInfraSummaryModel).informationMessage != null) {
							_view.dataNotFound = (event.result as ClusterInfraSummaryModel).informationMessage;
							_view.currentState = "dataNotFound";
							return;
						} 
						
						var clusterInfraSummary:ClusterInfraSummaryModel = event.result as ClusterInfraSummaryModel;
						if (clusterInfraSummary)
						{
							_view.clusterInfraSummaryModel = clusterInfraSummary;
							_view.currentState = "showSummaryPortlet";
						}
							
					}
					else {
						_logger.warn("View is null.  Returning from onGettingResult()");
						return;
					}
				
			}
			
			
		
	}
}