package com.hp.asi.hpic4vc.ui.views.converged.views {
	
	import assets.components.Hpic4vc_BaseMediator;
	
	import com.hp.asi.hpic4vc.ui.Hpic4vc_providerProxy;
	import com.hp.asi.hpic4vc.ui.Hpic4vc_storage_providerProxy;
	import com.hp.asi.hpic4vc.ui.model.ConfigurationListModel;
	import com.hp.asi.hpic4vc.ui.model.ConfigurationModel;
	import com.hp.asi.hpic4vc.ui.model.LinkModel;
	import com.hp.asi.hpic4vc.ui.model.StringModel;
	import com.vmware.data.query.events.DataByModelRequest;
	import com.vmware.flexutil.events.MethodReturnEvent;
	
	import mx.collections.ArrayCollection;
	import mx.logging.ILogger;
	import mx.logging.Log;
	
	// Declares the data request events sent from the class.
	[Event(name="{com.vmware.data.query.events.DataByModelRequest.REQUEST_ID}",
   type="com.vmware.data.query.events.DataByModelRequest")]
	/**
	 * The mediator for the HPSummaryView view.
	 */
	public class HPSummaryViewMediator extends Hpic4vc_BaseMediator {
		
		private var _proxy:Hpic4vc_providerProxy;
		private var _storageProxy:Hpic4vc_storage_providerProxy;
		private var _view:HPSummaryView;
		private var serviceGuid:String;
		private var count:int = 0;
		
		private static var _logger:ILogger = Log.getLogger('HPSummaryViewMediator');
		
		[View]
		/**
		 * The mediator's view.
		 */
		public function get view():HPSummaryView {
			return _view;
		}
		
		/** @private */
		public function set view(value:HPSummaryView):void {
			_view  = value;
			_proxy = new Hpic4vc_providerProxy();
			_storageProxy = new Hpic4vc_storage_providerProxy();
		}
		
		override protected function clearData():void {
			_view = null;
		}
		
		override protected function requestData():void {
			serviceGuid = getServerGuid();
			_proxy.getConfigurationLinks(serviceGuid, onData);
			_storageProxy.getDAMHelp(serviceGuid, onGettingOnlineHelp);
		}
		
		private function onData(event:MethodReturnEvent):void {
			_logger.info("Array summary data retrieved.");
			if (_view != null) {
				_logger.warn("Received HPIC4VC Storage Admin Portal data in onGettingConfigurationLinks()");
				if (event == null || event.result == null) {
					_logger.debug("The ConfigurationListModel object returned is null");
					return;
				}
				else if (event.error != null) {
					if (event.error.toString().match("DeliveryInDoubt")) {
						_logger.warn("DeliveryInDoubt exception occurred.  Count: " + count.toString());
						// Re try to request data for not more than 2 times
						if (count < 2) {
							count ++;
							requestData();
							return;
						} else {
							//_view.errorFoundLabel = event.error.message;
							return;
						}
					} else {
					//	_view.errorFoundLabel = event.error.message;
						return;
					}
				} 
				else if ((event.result as ConfigurationListModel).errorMessage != null) {
				//	_view.errorFoundLabel = (event.result as ConfigurationListModel).errorMessage;
					return;
				} 
				
				var result:ConfigurationListModel = event.result as ConfigurationListModel;
				var configLinks:ArrayCollection = result.configurationLinks;
				for(var index:int = 0; index < configLinks.length; index++) {
					var lm:LinkModel = (configLinks.getItemAt(index) as ConfigurationModel).link as LinkModel;
					if(lm.displayName == "Configure storage")
						_view.storageAdministratorPortalURL = lm.url;
					
				}
			}
			else {
				_logger.warn("View is null.  Returning from onGettingConfigurationLinks() for Storage Admin Portal data.");
				return;
			}
		}
		
		private function onGettingOnlineHelp(event:MethodReturnEvent):void {
			if (_view != null) {
				_logger.warn("Received HPIC4VC Storage Admin Portal data in onGettingConfigurationLinks()");
				if (event == null || event.result == null) {
					_logger.debug("The StringModel object returned is null");
					return;
				}
				else if (event.error != null) {
					if (event.error.toString().match("DeliveryInDoubt")) {
						_logger.warn("DeliveryInDoubt exception occurred.  Count: " + count.toString());
						// Re try to request data for not more than 2 times
						if (count < 2) {
							count ++;
							requestData();
							return;
						} else {
							//_view.errorFoundLabel = event.error.message;
							return;
						}
					} else {
						//	_view.errorFoundLabel = event.error.message;
						return;
					}
				} 
				var result:StringModel = event.result as StringModel;
				_view.helpUrl = result.data;
			}
			else {
				_logger.warn("View is null.  Returning from onGettingOnlineHelp.");
				return;
			}
		}
	}
	
}