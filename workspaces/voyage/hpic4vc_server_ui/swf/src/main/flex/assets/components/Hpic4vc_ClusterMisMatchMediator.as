package assets.components
{

	import assets.components.Hpic4vc_BaseMediator;
	import assets.components.Hpic4vc_ClusterMismatch;
	
	import com.hp.asi.hpic4vc.ui.Hpic4vc_server_providerProxy;
	import com.hp.asi.hpic4vc.ui.model.HostConfigClusterMismatchModel;	
	import com.vmware.core.model.IResourceReference;
	import com.vmware.flexutil.events.MethodReturnEvent;
	
	import mx.logging.ILogger;
	import mx.logging.Log;

	public class Hpic4vc_ClusterMisMatchMediator extends Hpic4vc_BaseMediator
	{
		private var _view:Hpic4vc_ClusterMismatch;
		private var _proxyServer:Hpic4vc_server_providerProxy;		
		private var count:int = 0;
		private static var _logger:ILogger = Log.getLogger("Hpic4vc_ClusterMisMatchMediator");
		[Embed(source="/assets/images/success.png")]
		[Bindable]
		public var successCls:Class;
		[Embed(source="/assets/images/statusRed.png")]
		[Bindable]
		public var statusRedCls:Class;
		[Embed(source="/assets/images/help_gray.png")]
		[Bindable]
		public var help_grayCls:Class;			
		[Embed(source="/assets/images/info.png")]
		[Bindable]
		public var errorCls:Class;
		[View]
		/**
		 * The mediator's view.
		 */
		public function get view():Hpic4vc_ClusterMismatch {
			return _view;
		}
		
		/** @private */
		public function set view(value:Hpic4vc_ClusterMismatch):void {
			_view = value;
			_proxyServer = new Hpic4vc_server_providerProxy();
			_contextObject = _view._contextObject;
			if (_contextObject != null && IResourceReference(_view._contextObject) != null) {  
				if (_contextObject.uid != IResourceReference(_view._contextObject).uid) {
					clearData();
					return;
				}
			}			
			requestData();
			
		}
		/*override public function set contextObject(value:Object):void {
			if (_view == null){
				_logger.warn('Cluster mismatch view is null');
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
		}*/
		
		override protected function clearData():void {
			_view = null;
		}
		
		override protected function requestData():void {			
			if (_contextObject != null) {
				_logger.debug("Requesting HPIC4VC data.");
			//_proxyServer.getNetworkMismatch(serviceGuid, onGettingHostConfiguration, null);			
			_proxyServer.getClusterMismatch(_contextObject.uid, onGettingHostConfiguration, _contextObject);
			}else {
				_logger.warn("ContextObject is null, hence not requesting data.");
				return;
			}
			
		}	
		
		
		private function onGettingHostConfiguration(event:MethodReturnEvent):void 
		{
			_logger.warn("Received HPIC4VC Host Properties data in onGettingHostConfiguration()");
			if (_view)
			{			
				if (event.result && ( event.result is HostConfigClusterMismatchModel))
				{					
					_view.clusterMismatch = event.result as HostConfigClusterMismatchModel;		
					_view.currentState = "ClusterConfMismatch";
					if(_view.clusterMismatch.clustMismatch == "UNKNOWN"){						
						_view.loader1.source=help_grayCls;					
						_view.loader1.toolTip="UNKNOWN";
					}				
					else if(_view.clusterMismatch.clustMismatch == "MISMATCH"){						
						_view.loader1.source=statusRedCls;
						_view.loader1.toolTip="MISMATCH";
					}					
					else if(_view.clusterMismatch.clustMismatch == "OK"){						
						_view.loader1.source=successCls;
						_view.loader1.toolTip="OK";
					}
					else if(_view.clusterMismatch.clustMismatch == "NA"){						
						_view.loader1.source=errorCls;
						_view.loader1.toolTip="NA";
					}					
				}
				else if (event && event.error)
				{
					if (event.error.toString().match("DeliveryInDoubt")) {
						if (count < 2) {
							count ++;
							requestData();
							return;
						}
					}
					else{
						_view.currentState = "errorFound";
					}
				}
			}
				
			else {
				_logger.warn("View is null.  Returning from onGettingHostConfiguration()");
				return;
			}
			
		}
		
		
	}
}