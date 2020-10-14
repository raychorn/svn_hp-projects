package com.hp.asi.hpic4vc.ui.views.converged.views {
	
	import assets.components.Hpic4vc_BaseMediator;
	
	import com.hp.asi.hpic4vc.ui.Hpic4vc_storage_providerProxy;
	import com.hp.asi.hpic4vc.ui.model.DataGridWrapper;
	import com.hp.asi.hpic4vc.ui.model.HierarchialData;
	import com.hp.asi.hpic4vc.ui.model.HierarchialDataModel;
	import com.hp.asi.hpic4vc.ui.utils.Helper;
	import com.vmware.data.query.events.DataByModelRequest;
	import com.vmware.flexutil.events.MethodReturnEvent;
	
	import mx.collections.ArrayCollection;
	import mx.controls.Alert;
	import mx.controls.advancedDataGridClasses.AdvancedDataGridColumn;
	import mx.logging.ILogger;
	import mx.logging.Log;
	
	// Declares the data request events sent from the class.
	[Event(name="{com.vmware.data.query.events.DataByModelRequest.REQUEST_ID}",
   type="com.vmware.data.query.events.DataByModelRequest")]
	/**
	 * The mediator for the ArrayDetailsPortletView view.
	 */
	public class StorageDetailsPortletViewMediator extends Hpic4vc_BaseMediator {
		
		private var _proxyStorage:Hpic4vc_storage_providerProxy;
		private var _view:StorageDetailsPortletView;
		private var serviceGuid:String;
		private var countHeader:int = 0;
		
		private static var _logger:ILogger = Log.getLogger('StorageDetailsPortletViewMediator');
		
		[View]
		/**
		 * The mediator's view.
		 */
		public function get view():StorageDetailsPortletView {
			return _view;
		}
		
		/** @private */
		public function set view(value:StorageDetailsPortletView):void {
			_view = value;
			_proxyStorage = new Hpic4vc_storage_providerProxy();
		}
		
		override protected function clearData():void {
			_view = null;
		}
		
		override protected function requestData():void {
			clearGrid();
			serviceGuid = getServerGuid();
			if (_proxyStorage != null && _contextObject != null) {
				_proxyStorage.getStorageSetDetails(serviceGuid, _contextObject.uid, onStorageSetDetailData);
			}
		}
		
		private function clearGrid():void {
			if(_view){
				_view.errorMessage = "";
				_view.noInfoFound="";
				_view.dataGrid.reset();
			}
		}
		
		private function onStorageSetDetailData(event:MethodReturnEvent):void {
			if (_view != null && _contextObject != null) {
				_logger.debug("Received Storage Set details data in onStorageSetDetailData()");

				if (event == null) {
					_view.noInfoFound = Helper.getString("noRecordsFound");
					return;
				} else if (event.error != null) {
					if (event.error.toString().match("DeliveryInDoubt")) {
						_logger.warn("DeliveryInDoubt exception occurred.  Count: " + countHeader);
						// Re try to request data for not more than 2 times
						if (countHeader < 2) {
							countHeader ++;
							_proxyStorage.getStorageSetDetails(serviceGuid, _contextObject.uid, onStorageSetDetailData);
							return;
						} else {
							_view.errorMessage = event.error.message;
							return;
						}
					} else {
						_view.errorMessage = event.error.message;
						return;
					}
				} else if (event.result == null) {
					_view.noInfoFound = Helper.getString("noRecordsFound");
					return;
				} else if ((event.result as HierarchialDataModel).errorMessage != null) {
					_view.errorMessage = (event.result as HierarchialDataModel).errorMessage;
					return;
				}  else if ((event.result as HierarchialDataModel).informationMessage != null) {
					_view.noInfoFound = (event.result as HierarchialDataModel).informationMessage;
					return;
				} else {
					_view.errorMessage = "";
					_view.noInfoFound = "";
				}
				_view.storageSummaryDP = (event.result as HierarchialDataModel).rowFormattedData;
				_view.rowCount = 10;
			}
			else {
				_logger.warn("View and/or ContextObject is null.  Returning from onStorageSetDetailData()");
				return;
			}
		}
	}
}