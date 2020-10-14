/* Copyright 2012 Hewlett-Packard Development Company, L.P. */

package assets.components {
	
	import com.hp.asi.hpic4vc.ui.Hpic4vc_server_providerProxy;
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
	public class Hpic4vc_SummaryPortlet_ServerMediator extends Hpic4vc_BaseMediator {
		
		private var _view:Hpic4vc_SummaryPortlet_Server;
		private var _proxyServer:Hpic4vc_server_providerProxy;
		private var count:int = 0;
		
		private static var _logger:ILogger = Log.getLogger("Hpic4vc_SummaryPortlet_ServerMediator");
		
		[View]
		/**
		 * The mediator's view.
		 */
		public function get view():Hpic4vc_SummaryPortlet_Server {
			return _view;
		}
		
		/** @private */
		public function set view(value:Hpic4vc_SummaryPortlet_Server):void {
			_view = value;
			_proxyServer = new Hpic4vc_server_providerProxy();
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
				_logger.debug("Requesting HPIC4VC data.");
				_proxyServer.getHostSummaryPortletData(_contextObject.uid, onGettingSummaryPortletResult);
			} else {
				_logger.warn("ContextObject is null, hence not requesting data.");
				return;
			}
		}
		
		private function onGettingSummaryPortletResult(event:MethodReturnEvent):void {
			if (_view != null) {
				_logger.debug("Received HPIC4VC data in onGettingResult()");
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
				} else if ((event.result as LabelValueListModel).errorMessage != null) {
					_view.errorFoundLabel = (event.result as LabelValueListModel).errorMessage;
					return;
				} else if ((event.result as LabelValueListModel).informationMessage != null) {
					_view.noInfoFoundLabel = (event.result as LabelValueListModel).informationMessage;
					return;
				}
				
				var summaryData:LabelValueListModel = event.result as LabelValueListModel;
				
				createSummaryPortlet(summaryData);
			}
			else {
				_logger.warn("View is null.  Returning from onGettingResult()");
				return;
			}
		}
		
		private function createSummaryPortlet(summaryData:LabelValueListModel):void {
			if (summaryData == null) {
				_view.errorFoundLabel = Helper.getString("errorOccurred");
				return;
			} else if (summaryData.errorMessage != null) {
				_view.errorFoundLabel = summaryData.errorMessage;
				_logger.error(summaryData.errorMessage);
				return; 			
			} else {
				_view.errorFoundLabel = "";
			}
			
			var labelValueListData:LabelValueListModel = summaryData as LabelValueListModel;
			if (null == labelValueListData || null == labelValueListData.lvList as ArrayCollection) {
				_view.noInfoFoundLabel = Helper.getString("noInfoFound");
				return;
			}
						
			_view.lvList = labelValueListData.lvList as ArrayCollection;
		}
	}
}