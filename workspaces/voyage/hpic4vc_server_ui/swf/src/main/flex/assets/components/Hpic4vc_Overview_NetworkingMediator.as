package assets.components
{
	import com.hp.asi.hpic4vc.ui.Hpic4vc_server_providerProxy;
	import com.hp.asi.hpic4vc.ui.model.HostConfigSummaryMismatchModel;
	import com.hp.asi.hpic4vc.ui.model.TableModel;
	import com.hp.asi.hpic4vc.ui.utils.Helper;
	import com.hp.asi.hpic4vc.ui.views.Hpic4vc_manage_uiView;
	import com.vmware.core.model.IResourceReference;
	import com.vmware.flexutil.events.MethodReturnEvent;
	
	import flash.events.EventDispatcher;
	
	import mx.logging.ILogger;
	import mx.logging.Log;
	
	public class Hpic4vc_Overview_NetworkingMediator extends Hpic4vc_BaseMediator
	{
		private var _view:Hpic4vc_Overview_Networking;
		private var _proxyServer:Hpic4vc_server_providerProxy;
		private var count:int = 0;
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
		[Embed(source="/assets/images/warning.png")]
		[Bindable]
		public var warningCls:Class;
		[Bindable]
		public var hostConfigMismatchData:HostConfigSummaryMismatchModel = new HostConfigSummaryMismatchModel();	
		private static var _logger:ILogger = Log.getLogger("Hpic4vc_Overview_NetworkingMediator");
		
		[View]
		/**
		 * The mediator's view.
		 */
		public function get view():Hpic4vc_Overview_Networking {
			return _view;
		}
		
		/** @private */
		public function set view(value:Hpic4vc_Overview_Networking):void {
			_view = value;
			_proxyServer = new Hpic4vc_server_providerProxy();
		}
		
		override public function set contextObject(value:Object):void {
			if (_view == null){
				_logger.warn('Overview_Networking view is null');
				return;
			}
			_contextObject = _view._contextObject;
			if (_contextObject != null && IResourceReference(value) != null) {  
				if (_contextObject.uid != IResourceReference(value).uid) {
					clearData();
					return;
				}
			}
			requestDataMismatch();
			requestData();
		}
		
		override protected function clearData():void {
			_view = null;
		}
		
		override protected function requestData():void {
			if (_contextObject != null) {
				_logger.debug("Requesting HPIC4VC data.");				
				_proxyServer.getNetworkSummary(_contextObject.uid, onGettingNetworkSummary, _contextObject);
				//_proxyServer.getNetworkMismatchSummary(_contextObject.uid, onGettingNetworkSummaryHost, _contextObject);
			} else {
				_logger.warn("ContextObject is null, hence not requesting data.");
				return;
			}
		}
		
		private function requestDataMismatch():void {
			if (_contextObject != null) {
				_logger.debug("Requesting HPIC4VC data.");				
				//_proxyServer.getRefreshedNetwork(_contextObject.uid, onGettingHostConfiguration, null);
				_proxyServer.getNetworkMismatchSummary(_contextObject.uid, onGettingNetworkSummaryHost, _contextObject);
			} else {
				_logger.warn("ContextObject is null, hence not requesting data.");
				return;
			}
		}
		/*private function onGettingHostConfiguration(event:MethodReturnEvent):void 
		{				
			// refreshing network on summary portlet
		}*/
		private function onGettingNetworkSummaryHost(event:MethodReturnEvent):void 
		{
			_logger.warn("Received HPIC4VC Host Properties data in onGettingNetworkSummaryHost()");
			if (_view)
			{
			if (event != null && event.result && _view !=null)
			{
				
				if (event.result && ( event.result is HostConfigSummaryMismatchModel))			{
					hostConfigMismatchData = event.result as HostConfigSummaryMismatchModel;					
					_view.currentState = "showInfraPortlet";	
					if(hostConfigMismatchData.errorMessage!=null){
						_view.lbdata.percentWidth=100;
						_view.lbdata.text="Host Network Configuration: "+hostConfigMismatchData.errorMessage;
					}else{
						_view.lbdata.text="Host Network Configuration";
					}
					if(hostConfigMismatchData.configStatus == "UNKNOWN"){						
						_view.loader1.source=help_grayCls;
						_view.loader1.toolTip="UNKNOWN";
					}					
						
					else if(hostConfigMismatchData.configStatus == "MISMATCH"){						
						_view.loader1.source=statusRedCls;
						_view.loader1.toolTip="MISMATCH";
					}					
					else if(hostConfigMismatchData.configStatus == "OK"){						
						_view.loader1.source=successCls;
						_view.loader1.toolTip="OK";
					}
					else if(hostConfigMismatchData.configStatus == "NA"){						
						_view.loader1.source=errorCls;
						_view.loader1.toolTip="NA";
					}				
					else if(hostConfigMismatchData.configStatus == "HOSTCONFIG_INPROGRESS"){
						_view.loader1.source=warningCls;
						_view.loader1.toolTip="Host Configuration is in progress";		
						
					}
					
				}
			}
			else if (event && event.error)
			{
				if (event.error.toString().match("DeliveryInDoubt")) {
					if (count < 2) {
						count ++;
						requestDataMismatch();
						return;
					}
				}
			}
			else if (event && event.error)
			{
				_logger.warn(event.error.message);
				_view.lbdata.text="Host Network Configuration: Unable to get the Host Network Summary details for this Host";
				//_view.statePanelID.visible=false;
			}
		}
			else {
				_logger.warn("View is null.  Returning from onGettingNetworkSummaryHost()");
				return;
			}
		}
		
	/*	private function getMoreLinkURL():void {
			if (_contextObject.type == "HostSystem") {
				_view.hpic4vc_Overview_networking_id = "com.hp.asi.hpic4vc.ui.host.manage";
			} else if (_contextObject.type == "VirtualMachine") {
				_view.hpic4vc_Overview_networking_id = "com.hp.asi.hpic4vc.ui.vm.manage";
			} else if (_contextObject.type == "Datastore") {
				_view.hpic4vc_Overview_networking_id = "com.hp.asi.hpic4vc.ui.datastore.manage";
			} else if (_contextObject.type == "ClusterComputeResource") {
				_view.hpic4vc_Overview_networking_id = "com.hp.asi.hpic4vc.ui.cluster.manage";
			}
		}
		
		
		public function setTabClicked(networkingClicked:String):void {
			tab_Clicked = networkingClicked;
		}
		
		public function getManageView():Hpic4vc_manage_uiView {
			return manageView;
		}
		*/
		private function onGettingNetworkSummary(event:MethodReturnEvent):void {
			if (_view != null) {
				_logger.warn("Received HPIC4VC data in onGettingNetworkSummary()");
				_view.dataNotFound = "";
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
				} else if ((event.result as TableModel).errorMessage != null) {
					_view.errorFoundLabel = (event.result as TableModel).errorMessage;
					return;
				} else if ((event.result as TableModel).informationMessage != null) {
					_view.dataNotFound = (event.result as TableModel).informationMessage;
					return;
				} 
	
				var netSummary:TableModel = event.result as TableModel;
				if (netSummary)
					_view.summaryPortletData = netSummary;
			}
			else {
				_logger.warn("View is null.  Returning from onGettingResult()");
				return;
			}
		}
	}
}