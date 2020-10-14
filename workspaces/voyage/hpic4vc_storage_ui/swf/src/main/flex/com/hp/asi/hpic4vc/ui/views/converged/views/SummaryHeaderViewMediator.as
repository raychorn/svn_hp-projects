package com.hp.asi.hpic4vc.ui.views.converged.views {
	
	import assets.components.Hpic4vc_BaseMediator;
	
	import com.hp.asi.hpic4vc.ui.Hpic4vc_storage_providerProxy;
	import com.hp.asi.hpic4vc.ui.model.DataGridWrapper;
	import com.hp.asi.hpic4vc.ui.model.FooterModel;
	import com.hp.asi.hpic4vc.ui.model.HeaderModel;
	import com.hp.asi.hpic4vc.ui.model.HealthModel;
	import com.hp.asi.hpic4vc.ui.model.HierarchialData;
	import com.hp.asi.hpic4vc.ui.model.HierarchialDataModel;
	import com.hp.asi.hpic4vc.ui.model.LaunchToolModel;
	import com.hp.asi.hpic4vc.ui.model.LinkModel;
	import com.hp.asi.hpic4vc.ui.utils.Helper;
	import com.hp.asi.hpic4vc.ui.views.converged.model.StorageSystemOverview;
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
	 * The mediator for the ArraySummaryView view.
	 */
	public class SummaryHeaderViewMediator extends Hpic4vc_BaseMediator {
		
		private var _proxyStorage:Hpic4vc_storage_providerProxy;
		private var _view:SummaryHeaderView;
		private var serviceGuid:String;

		private var countHeader:int = 0;
		private var countFooter:int = 0;
		private var countOverview:int = 0;
		private static var _logger:ILogger = Log.getLogger('SummaryHeaderViewMediator');
		
		[View]
		/**
		 * The mediator's view.
		 */
		public function get view():SummaryHeaderView {
			return _view;
		}
		
		/** @private */
		public function set view(value:SummaryHeaderView):void {
			_view = value;
			_proxyStorage = new Hpic4vc_storage_providerProxy();
		}
		
		override protected function clearData():void {
			_view.storageSystemOverview = null;
			_view = null;
		}
		
		private function hideManagementConsoleLink():void{
			if(_view){
				if (_view.managementConsoleLink){
					_view.managementConsoleLink.visible = false;
				}
			}
		}
		
		private function showManagementConsoleLink():void{
			if(_view){
				if (_view.managementConsoleLink){
					_view.managementConsoleLink.visible = true;
				}
			}
		}
		
		private function showLoading():void{
			if(_view){
				_view.showLoading = true;
				_view.showError = false;
			}
		}
		
		private function showError():void{
			if(_view){
				_view.showLoading = false;
				_view.showError = true;
			}
		}
		
		private function clearScreen():void {
			if(_view){
				_view.currentState = "DataUnavailable";
				showLoading();
				hideManagementConsoleLink();
				_view.mgmtConsoleLink = null;
			}
		}
		
		override protected function requestData():void {
			if (_view) {
				clearScreen();
				serviceGuid = getServerGuid();
				_proxyStorage.getStorageSystemOverview(serviceGuid, _contextObject.uid, onGettingStorageSystemOverview);
				_proxyStorage.getStorageSystemFooter(serviceGuid, _contextObject.uid, onGettingStorageSystemFooter);
				if(_view.mainHeader)
					_view.mainHeader.visible = false;
				_proxyStorage.getStorageSystemHeader(serviceGuid, _contextObject.uid, onGettingStorageSystemHeader);
			}
		}
		
		private function onGettingStorageSystemOverview(event:MethodReturnEvent):void {
			if (_view != null && _contextObject != null) {
				_logger.info("SummaryHeaderViewMediator.onGettingStorageSystemOverview: Storage system overview data retrieved.");
				_view.storageSystemOverviewErrorInfoText = "";
				if (event == null){
					_logger.debug("Event is null in onGettingStorageSystemOverview()");
					_view.storageSystemOverviewErrorInfoText = Helper.getString("noRecordsFound");
					_view.currentState = "DataUnavailable";
					showError();
					return;
				} else if (event.error != null){
					if (event.error.toString().match("DeliveryInDoubt")) {
						_logger.warn("DeliveryInDoubt exception occurred.  Count: " + countHeader);
						// Re try to request data for not more than 2 times
						if (countOverview < 2) {
							countOverview ++;
							_proxyStorage.getStorageSystemOverview(serviceGuid, _contextObject.uid, onGettingStorageSystemOverview);
							return;
						} else {
							_view.storageSystemOverviewErrorInfoText = event.error.message;
							_view.currentState = "DataUnavailable";
							showError();
							return;
						}
					} else {
						_view.storageSystemOverviewErrorInfoText = event.error.message;
						_view.currentState = "DataUnavailable";
						showError();
						return;
					}
				} else if(event.result == null){
					_logger.error("Event.result is null in onGettingStorageSystemOverview()");
					_view.storageSystemOverviewErrorInfoText = "No record found.";
					_view.currentState = "DataUnavailable";
					showError();
					return;
				} else if ((event.result as StorageSystemOverview).errorMessage != null) {
					_view.storageSystemOverviewErrorInfoText = (event.result as StorageSystemOverview).errorMessage;
					_view.currentState = "DataUnavailable";
					showError();
					return;
				} else if ((event.result as StorageSystemOverview).informationMessage != null) {
					_view.storageSystemOverviewErrorInfoText = (event.result as StorageSystemOverview).informationMessage;
					_view.currentState = "DataUnavailable";
					showError();
					return;
				}
				
				// This is the happy path
				_view.storageSystemOverview = event.result as StorageSystemOverview;
				
				if (_view.storageSystemOverview.summaryDetails != null &&
					_view.storageSystemOverview.summaryDetails.lvList != null &&
					_view.storageSystemOverview.summaryDetails.lvList.length > 0){
					_view.storageDetailList = _view.storageSystemOverview.summaryDetails.lvList;
					_view.currentState = "ShowLeftAndMiddle";
				} 
				if (_view.storageSystemOverview.storageType != null){
					_view.setStorageImage(_view.storageSystemOverview.storageType);
				} 
				if (_view.storageSystemOverview.storageSystemOverview != null &&
					_view.storageSystemOverview.storageSystemOverview.fieldData != null &&
					_view.storageSystemOverview.storageSystemOverview.pieChartData != null) {
					_view.summaryPortlet = _view.storageSystemOverview.storageSystemOverview;
					_view.currentState = "ShowAll";
				}
			} else {
				_logger.warn("View and/or ContextObject is null.");
				_view.storageSystemOverviewErrorInfoText = "View and/or ContextObject is null.";
				return;
			}
		}
		
		private function onGettingStorageSystemHeader(event:MethodReturnEvent):void {
			if (_view != null && _contextObject != null) {
				_logger.debug("Received HPIC4VC data in onGettingStorageSystemHeader()");
				
				if (event == null) {
					_view.noHeaderInfoFound = Helper.getString("noRecordsFound");
					return;
				} else if (event.error != null) {
					if (event.error.toString().match("DeliveryInDoubt")) {
						_logger.warn("DeliveryInDoubt exception occurred.  Count: " + countHeader);
						// Re try to request data for not more than 2 times
						if (countHeader < 2) {
							countHeader ++;
							_proxyStorage.getStorageSystemHeader(serviceGuid, _contextObject.uid, onGettingStorageSystemHeader);
							return;
						} else {
							_view.headerErrorMessage = event.error.message;
							return;
						}
					} else {
						_view.headerErrorMessage = event.error.message;
						return;
					}
				} else if (event.result == null) {
					_view.noHeaderInfoFound = Helper.getString("noRecordsFound");
					return;
				} else if ((event.result as HeaderModel).errorMessage != null) {
					_view.headerErrorMessage = (event.result as HeaderModel).errorMessage;
					return;
				} else if ((event.result as HeaderModel).informationMessage != null) {
					_view.headerErrorMessage = (event.result as HeaderModel).informationMessage;
					return;
				} else {
					_view.noHeaderInfoFound = "";
					_view.headerErrorMessage = "";
				}
				
				var headerInfo:HeaderModel = event.result as HeaderModel;
				
				createHeader(headerInfo);
			}
			else {
				_logger.warn("View and/or ContextObject is null.  Returning from onGettingStorageSystemHeader()");
				return;
			}
		}
		
		private function createHeader(headerInfo:HeaderModel):void {
			_view.mainHeader.visible = true;
			if (_view != null && _contextObject != null) {
				// Checks for null and errors
				if (headerInfo == null) {
					_view.noHeaderInfoFound = Helper.getString("noInfoFound");
					return;
				} else if (headerInfo.errorMessage != null) {
					_view.noHeaderInfoFound = headerInfo.errorMessage;
					_logger.error(headerInfo.errorMessage);
					return;
				}
				
				// Set the objReference Id
				_view.objReferenceId = serviceGuid;
				
				// Get Health status
				var healthStatus:HealthModel = headerInfo.health as HealthModel;
				_view.healthConsolidatedStatus = healthStatus.consolidatedStatus as String;	   
				_view.warningCount = healthStatus.warnCount as String;
				_view.errorCount = healthStatus.errorCount as String;
				_view.okCount = healthStatus.okCount as String;
				_view.informationCount = healthStatus.infoCount as String;
				
				// To get the I18Ned label for the VMware entity type.
				var contextObjectType:String = _contextObject.type;
				_view.objReferenceName = headerInfo.objReferenceType
					+ " \"" 
					+ headerInfo.objReferenceName
					+ "\"";
				
				_view.configurations = headerInfo.configurations as ArrayCollection;
				
				_view.helpUrl = headerInfo.helpUrl as String;
				
				_view.refreshList = headerInfo.refresh as ArrayCollection;
				
				_view.showRefreshHover = headerInfo.showRefreshHover as Boolean;
			} else {
				_logger.warn("View and/or ContextObject is null.  Returning from createHeader()");
				return;
			}
		}
		
		private function onGettingStorageSystemFooter(event:MethodReturnEvent):void {
			if (_view != null && _contextObject != null) {
				_view.errorFoundLabel = "";
				_logger.debug("Received HPIC4VC data in onGettingFooter()");
				if (event == null) {
					_view.errorFoundLabel = Helper.getString("noRecordsFound");
					return;
				} else if (event.error != null) {
					_logger.warn("DeliveryInDoubt exception occurred.  Count: " + countHeader);
					if (event.error.toString().match("DeliveryInDoubt")) {
						// Re try to request data for not more than 2 times
						if (countFooter < 2) {
							countFooter ++;
							_proxyStorage.getStorageSystemFooter(serviceGuid, _contextObject.uid, onGettingStorageSystemFooter);
							return;
						}
						else {
							_view.errorFoundLabel = event.error.message;
							return;
						}
					} else {
						_view.errorFoundLabel = event.error.message;
						return;
					}
				} else if ((event.result as FooterModel).errorMessage != null) {
					_view.errorFoundLabel = (event.result as FooterModel).errorMessage;
					return;
				} else if ((event.result as FooterModel).informationMessage != null) {
					_view.errorFoundLabel = (event.result as FooterModel).informationMessage;
					return;
				} 
				var footerInfo:FooterModel = event.result as FooterModel;
				var launchTools : ArrayCollection;
				launchTools = footerInfo.launchTools;
				var links:ArrayCollection = (launchTools[0] as LaunchToolModel).links;
				
				if (links.length > 0) {
					if ((links[0] as LinkModel).url != null)
						_view.mgmtConsoleLink = (links[0] as LinkModel).url;
					    showManagementConsoleLink();
				} else {
					hideManagementConsoleLink();
				}
			}
			else {
				_logger.warn("View and/or ContextObject is null.  Returning from onGettingResult()");
				return;
			}
		}
	}
}