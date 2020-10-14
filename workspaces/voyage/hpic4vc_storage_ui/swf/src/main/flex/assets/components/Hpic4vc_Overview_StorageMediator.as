package assets.components
{
	import com.hp.asi.hpic4vc.ui.Hpic4vc_storage_providerProxy;
	import com.hp.asi.hpic4vc.ui.model.StorageOverviewModel;
	import com.hp.asi.hpic4vc.ui.utils.Helper;
	import com.hp.asi.hpic4vc.ui.views.Hpic4vc_manage_uiView;
	import com.vmware.core.model.IResourceReference;
	import com.vmware.flexutil.events.MethodReturnEvent;
	
	import flash.events.EventDispatcher;
	
	import mx.collections.ArrayCollection;
	import mx.logging.ILogger;
	import mx.logging.Log;
	
	public class Hpic4vc_Overview_StorageMediator extends Hpic4vc_BaseMediator
	{
		private var _view:Hpic4vc_Overview_Storage;
		private var _proxyStorage:Hpic4vc_storage_providerProxy;
		private var count:int = 0;
		
		private static var _logger:ILogger = Log.getLogger("Hpic4vc_Overview_StorageMediator");
		
		[View]
		/**
		 * The mediator's view.
		 */
		public function get view():Hpic4vc_Overview_Storage {
			return _view;
		}
		
		/** @private */
		public function set view(value:Hpic4vc_Overview_Storage):void {
			_view = value;
			_proxyStorage = new Hpic4vc_storage_providerProxy();
		}
		
		override public function set contextObject(value:Object):void {
			if (_view == null){
				_logger.warn('Overview_Softwarefirmware view is null');
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
				_logger.debug("Requesting HPIC4VC data for storage overview.");
				_proxyStorage.getStorageOverview(_contextObject.uid, onGettingStorageOverviewResult, _contextObject);
			//	getMoreLinkURL();
			} else {
				_logger.warn("ContextObject is null, hence not requesting storage overview data.");
				return;
			}
		}
		
	/*	private function getMoreLinkURL():void {
			if (_contextObject.type == "HostSystem") {
				_view.hpic4vc_Overview_storage_id = "com.hp.asi.hpic4vc.ui.host.manage";
			} else if (_contextObject.type == "VirtualMachine") {
				_view.hpic4vc_Overview_storage_id = "com.hp.asi.hpic4vc.ui.vm.manage";
			} else if (_contextObject.type == "Datastore") {
				_view.hpic4vc_Overview_storage_id = "com.hp.asi.hpic4vc.ui.datastore.manage";
			} else if (_contextObject.type == "ClusterComputeResource") {
				_view.hpic4vc_Overview_storage_id = "com.hp.asi.hpic4vc.ui.cluster.manage";
			}
		}
		
		public function setTabClicked(storageClicked:String):void {
			tab_Clicked = storageClicked;
		}
		
		public function getManageView():Hpic4vc_manage_uiView
		{
			return manageView;
		}
		*/
		private function onGettingStorageOverviewResult(event:MethodReturnEvent):void {
			if (_view != null) {
				_logger.warn("Received HPIC4VC data in onGettingResult() for storage overview data.");
				_view.noInfoFoundLabel = "";
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
				} else if ((event.result as StorageOverviewModel).errorMessage != null) {
					_view.errorFoundLabel = (event.result as StorageOverviewModel).errorMessage;
					return;
				} else if ((event.result as StorageOverviewModel).informationMessage != null) {
					_view.noInfoFoundLabel = (event.result as StorageOverviewModel).informationMessage;
					return;
				} 
				
				var soModel:StorageOverviewModel = event.result as StorageOverviewModel;
				
				_view.storageOverviewData = soModel.arraySummaries as ArrayCollection;
			}
			else {
				_logger.warn("View is null.  Returning from onGettingResult() for storage overview data.");
				return;
			}
		}
    }
}