/* Copyright 2012 Hewlett-Packard Development Company, L.P. */

package assets.components {
	
	import com.hp.asi.hpic4vc.ui.Hpic4vc_storage_providerProxy;
	import com.hp.asi.hpic4vc.ui.model.LabelValueListModel;
	import com.hp.asi.hpic4vc.ui.model.LabelValueModel;
	import com.hp.asi.hpic4vc.ui.model.PieChartModel;
	import com.hp.asi.hpic4vc.ui.model.SummaryPortletModel;
	import com.hp.asi.hpic4vc.ui.utils.Helper;
	import com.vmware.core.model.IResourceReference;
	import com.vmware.flexutil.events.MethodReturnEvent;
	
	import flash.events.EventDispatcher;
	
	import mx.collections.ArrayCollection;
	import mx.controls.Label;
	import mx.logging.ILogger;
	import mx.logging.Log;
	
	/**
	 * The mediator for the Hpic4vc_summaryView view.
	 */
	public class Hpic4vc_SummaryPortlet_StorageMediator extends Hpic4vc_BaseMediator {
		
		private var _view:Hpic4vc_SummaryPortlet_Storage;
		private var _proxyStorage:Hpic4vc_storage_providerProxy;
		private var count:int = 0;
		
		private static var _logger:ILogger = Log.getLogger("Hpic4vc_SummaryPortlet_StorageMediator");
		
		[View]
		/**
		 * The mediator's view.
		 */
		public function get view():Hpic4vc_SummaryPortlet_Storage {
			return _view;
		}
		
		/** @private */
		public function set view(value:Hpic4vc_SummaryPortlet_Storage):void {
			_view = value;
			_proxyStorage = new Hpic4vc_storage_providerProxy();
		}

		/**public function set contextObject(value:Object):void {
			_contextObject = IResourceReference(value);
			
			if (_contextObject == null) {
				//TODO: Should I clear any data??
				return;
			}
			requestData();
		}**/
		
		override protected function clearData():void {
			_view = null;
		}
		
		override protected function requestData():void {
			if (_contextObject != null) {
				_logger.debug("Requesting HPIC4VC Storage Summary Portlet data.");
				_proxyStorage.getSummaryPortletData(_contextObject.uid, onGettingSummaryPortletResult);
			} else {
				_logger.warn("ContextObject is null, hence not requesting data.");
				return;
			}
		}
		
		
		private function onGettingSummaryPortletResult(event:MethodReturnEvent):void {
			if (_view != null) {
				_logger.debug("Received HPIC4VC Storage Summary Portlet data in onGettingResult()");
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
				} else if ((event.result as SummaryPortletModel).errorMessage != null) {
					_view.errorFoundLabel = (event.result as SummaryPortletModel).errorMessage;
					return;
				} else if ((event.result as SummaryPortletModel).informationMessage != null) {
					_view.noInfoFoundLabel = (event.result as SummaryPortletModel).informationMessage;
					return;
				}
				
				var summaryData:SummaryPortletModel = event.result as SummaryPortletModel;
				
				createSummaryPortlet(summaryData);
			}
			else {
				_logger.warn("View is null.  Returning from onGettingResult() for Storage Summary Portlet data.");
				return;
			}
		}
		
		private function createSummaryPortlet(summaryData:SummaryPortletModel):void {
			if (summaryData == null) {
				_view.errorFoundLabel = Helper.getString("noInfoFound");
				return;
			} else if (summaryData.errorMessage != null) {
				_view.errorFoundLabel = summaryData.errorMessage;
				_logger.error(summaryData.errorMessage);
				return; 
			} else if (summaryData.fieldData == null) {
				_view.noInfoFoundLabel = Helper.getString("noInfoFound");
				return;
			} else {
				_view.noInfoFoundLabel = "";
			}
			
			var labelValueListData:LabelValueListModel = summaryData.fieldData as LabelValueListModel;
			if (null == labelValueListData || null == labelValueListData.lvList as ArrayCollection) {
				_view.noInfoFoundLabel = Helper.getString("noInfoFound");
				return;
			}
			
			if (null != summaryData.pieChartData) {
				_view.pieChartData = summaryData.pieChartData as PieChartModel;
			}
			_view.lvList = labelValueListData.lvList as ArrayCollection;
		}
	}
}