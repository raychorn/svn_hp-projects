package assets.components
{
	import com.hp.asi.hpic4vc.ui.Hpic4vc_server_providerProxy;
	import com.hp.asi.hpic4vc.ui.model.LabelValueListModel;
	import com.hp.asi.hpic4vc.ui.utils.Helper;
	import com.hp.asi.hpic4vc.ui.views.Hpic4vc_manage_uiView;
	import com.vmware.core.model.IResourceReference;
	import com.vmware.flexutil.events.MethodReturnEvent;
	
	import flash.events.EventDispatcher;
	
	import mx.logging.ILogger;
	import mx.logging.Log;
	
	public class Hpic4vc_Overview_InfrastructureMediator extends Hpic4vc_BaseMediator
	{
		private var _view:Hpic4vc_Overview_Infrastructure;
		private var _proxyServer:Hpic4vc_server_providerProxy;
		private var count:int = 0;
		
		private static var _logger:ILogger = Log.getLogger("Hpic4vc_Overview_InfrastructureMediator");
		
		[View]
		/**
		 * The mediator's view.
		 */
		public function get view():Hpic4vc_Overview_Infrastructure {
			return _view;
		}
		
		/** @private */
		public function set view(value:Hpic4vc_Overview_Infrastructure):void {
			_view = value;
			_proxyServer = new Hpic4vc_server_providerProxy();
		}

		override public function set contextObject(value:Object):void {
            if (_view == null){
                _logger.warn('Overview_Infrastructure view is null');
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
				_proxyServer.getInfrastructureSummary(_contextObject.uid, onGettingInfrastructureSummary, _contextObject);
			//	getMoreLinkURL();
			} else {
				_logger.warn("ContextObject is null, hence not requesting data.");
				return;
			}
		}
		
	/*	private function getMoreLinkURL():void {
			if (_contextObject.type == "HostSystem") {
				_view.hpic4vc_Overview_infrastructure_id = "com.hp.asi.hpic4vc.ui.host.manage";
			} else if (_contextObject.type == "VirtualMachine") {
				_view.hpic4vc_Overview_infrastructure_id = "com.hp.asi.hpic4vc.ui.vm.manage";
			} else if (_contextObject.type == "Datastore") {
				_view.hpic4vc_Overview_infrastructure_id = "com.hp.asi.hpic4vc.ui.datastore.manage";
			} else if (_contextObject.type == "ClusterComputeResource") {
				_view.hpic4vc_Overview_infrastructure_id = "com.hp.asi.hpic4vc.ui.cluster.manage";
			}
		}
		
		
		public function setTabClicked(infrastructureClicked:String):void {
			tab_Clicked = infrastructureClicked;
		}
		
		public function getManageView():Hpic4vc_manage_uiView {
			return manageView;
		}*/
		
        private function onGettingInfrastructureSummary(event:MethodReturnEvent):void {
			if (_view != null) {
				_logger.warn("Received HPIC4VC data in onGettingInfrastructureSummary()");
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
				} else if ((event.result as LabelValueListModel).errorMessage != null) {
					_view.errorFoundLabel = (event.result as LabelValueListModel).errorMessage;
					_view.currentState = "errorLoadingPortlet";
					return;
				} else if ((event.result as LabelValueListModel).informationMessage != null) {
					_view.dataNotFound = (event.result as LabelValueListModel).informationMessage;
					_view.currentState = "dataNotFound";
					return;
				} 
				
	            var infraSummary:LabelValueListModel = event.result as LabelValueListModel;
	            if (infraSummary)
				{
	                _view.summaryPortletData = infraSummary;
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