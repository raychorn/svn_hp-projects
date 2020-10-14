package assets.components
{
	import com.hp.asi.hpic4vc.ui.Hpic4vc_providerProxy;
	import com.hp.asi.hpic4vc.ui.model.DataGridWrapper;
	import com.hp.asi.hpic4vc.ui.model.TableModel;
	import com.hp.asi.hpic4vc.ui.utils.Helper;
	import com.hp.asi.hpic4vc.ui.views.Hpic4vc_manage_uiView;
	import com.vmware.core.model.IResourceReference;
	import com.vmware.flexutil.events.MethodReturnEvent;
	
	import flash.events.EventDispatcher;
	
	import mx.logging.ILogger;
	import mx.logging.Log;
	
	public class Hpic4vc_Overview_SoftwareFirmwareMediator extends Hpic4vc_BaseMediator
	{
		private var _view:Hpic4vc_Overview_SoftwareFirmware;
		private var _proxy:Hpic4vc_providerProxy;
		private var count:int = 0;
		
		private static var _logger:ILogger = Log.getLogger("Hpic4vc_Overview_SoftwareFirmwareMediator");
		
		[View]
		/**
		 * The mediator's view.
		 */
		public function get view():Hpic4vc_Overview_SoftwareFirmware {
			return _view;
		}
		
		/** @private */
		public function set view(value:Hpic4vc_Overview_SoftwareFirmware):void {
			_view = value;
			_proxy = new Hpic4vc_providerProxy();
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
		//	if (manageView != null)
		//		_view.hpic4vc_manage_uiView = manageView;
			requestData();
		}
		
		override protected function clearData():void {
			_view = null;
		}
		
		override protected function requestData():void {
			if (_contextObject != null) {
				_logger.debug("Requesting HPIC4VC data for software/firmware summary.");
				_proxy.getSoftwareFirmwareSummary(_contextObject.uid, onGettingSoftwareFirmwareSummary, _contextObject);
			//	getMoreLinkURL();
			} else {
				_logger.warn("ContextObject is null, hence not requesting software/firmware summary data.");
				return;
			}
		}
		//This is in many places, need to move it to the Base Mediator.
	/*	private function getMoreLinkURL():void {
			if (_contextObject.type == "HostSystem") {
				_view.hpic4vc_Overview_swfw_id = "com.hp.asi.hpic4vc.ui.host.manage";
			} else if (_contextObject.type == "VirtualMachine") {
				_view.hpic4vc_Overview_swfw_id = "com.hp.asi.hpic4vc.ui.vm.manage";
			} else if (_contextObject.type == "Datastore") {
				_view.hpic4vc_Overview_swfw_id = "com.hp.asi.hpic4vc.ui.datastore.manage";
			} else if (_contextObject.type == "ClusterComputeResource") {
				_view.hpic4vc_Overview_swfw_id = "com.hp.asi.hpic4vc.ui.cluster.manage";
			}
		}
		
		
		public function setTabClicked(swfwClicked:String):void {
			tab_Clicked = swfwClicked;
		}
		
		public function getManageView():Hpic4vc_manage_uiView {
			return manageView;
		}
		*/
		private function onGettingSoftwareFirmwareSummary(event:MethodReturnEvent):void {
			if (_view != null) {
				_logger.warn("Received HPIC4VC data in onGettingResult()");_view.noInfoFoundLabel = "";
				_view.errorFoundLabel = "";
				
				if (event == null) {
					_view.errorFoundLabel = Helper.getString("errorOccurred");
					return;
				} else if (event.error != null) {
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
					_view.noInfoFoundLabel = (event.result as TableModel).informationMessage;
					return;
				} 
				
				var sfTableModel:TableModel = event.result as TableModel;
				
				var sfWrapper:DataGridWrapper = Helper.createDataGrid(sfTableModel);
				
				if (sfWrapper != null) {
					_view.columns = sfWrapper.columns;
					_view.dataProvider = sfWrapper.list;
				}
			}
			else {
				_logger.warn("View is null.  Returning from onGettingResult() for software/firmware summary data");
				return;
			}
		}
    }
}