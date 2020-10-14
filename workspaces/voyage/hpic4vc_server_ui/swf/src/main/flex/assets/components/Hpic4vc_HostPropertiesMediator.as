package assets.components
{
	import com.hp.asi.hpic4vc.ui.Hpic4vc_providerProxy;
	import com.hp.asi.hpic4vc.ui.Hpic4vc_server_providerProxy;
	import com.hp.asi.hpic4vc.ui.model.TableModel;
	import com.hp.asi.hpic4vc.ui.model.UserInfoModel;
	import com.hp.asi.hpic4vc.ui.utils.Helper;
	import com.vmware.flexutil.events.MethodReturnEvent;
	
	import flash.events.EventDispatcher;
	import flash.utils.setTimeout;
	
	import mx.controls.Alert;
	import mx.logging.ILogger;
	import mx.logging.Log;

	public class Hpic4vc_HostPropertiesMediator extends Hpic4vc_BaseMediator
	{
		private var _view:Hpic4vc_HostProperties;
		private var _proxyServer:Hpic4vc_server_providerProxy;
		private var _proxy:Hpic4vc_providerProxy;
		
		private static var _logger:ILogger = Log.getLogger("Hpic4vc_HostPropertiesMediator");
		
		[View]
		/**
		 * The mediator's view.
		 */
		public function get view():Hpic4vc_HostProperties {
			return _view;
		}
		
		/** @private */
		public function set view(value:Hpic4vc_HostProperties):void {
			_view = value;
			_proxyServer = new Hpic4vc_server_providerProxy();
			_proxy = new Hpic4vc_providerProxy();
		}
		
		override protected function clearData():void {
			_view = null;
		}
		
		override protected function requestData():void {
			_proxyServer.getHostProperties(_contextObject.uid, onGettingHostProperties, _contextObject);
			_proxy.getUserInfo(_contextObject.uid, onGettingUserInfo);
		}
		
		private function onGettingHostProperties(event:MethodReturnEvent):void {
			_logger.warn("Received HPIC4VC Host Properties data in onGettingHostProperties()");
			if (event != null && event.result) {
				var detail:TableModel = event.result as TableModel;
				if (detail)
					_view.hostProperties =  Helper.createDataGrid( detail );
			}
			else if (event && event.error)
			{
				_view.errorFound = event.error.message;
				_view.currentState = "errorOcurred";
			}
		}
		
		/**
		 * VMware Administrator role has a role id -1.  Only if the user 
		 has this Administrator role then he is authorized to perform
		 the operation. 
		 * @param event MethodReturnEvent
		 * @return A Boolean value to indicate if the user is authorized.
		 * 
		 */
		private function onGettingUserInfo(event:MethodReturnEvent):void {
			if (_view != null) {
				_logger.warn("Received HPIC4VC UserInfo data in onGettingUserInfo()");
				if (event == null) {
					_logger.warn(Helper.getString("noRecordsFound"));
					_view.isAuthorized = "false";
					return;
				} else if (event.error != null) {
					_logger.warn(event.error.message);
					_view.isAuthorized = "false";
					return;
				} else if ((event.result as UserInfoModel).errorMessage != null) {
					_logger.warn((event.result as UserInfoModel).errorMessage);
					_view.isAuthorized = "false";
					return;
				} else if ((event.result as UserInfoModel).informationMessage != null) {
					_logger.warn((event.result as UserInfoModel).informationMessage);
					_view.isAuthorized = "false";
					return;
				} 
				
				var userInfo:UserInfoModel = event.result as UserInfoModel;
				
				if (userInfo.roleId == "-1") {
					_view.isAuthorized = "true";
				} else {
					_view.isAuthorized = "false";
				}
				_logger.info("Return value in onGettingUserInfo(): " + _view.isAuthorized);
			}
			else {
				_logger.warn("View is null.  Returning from onGettingVCCredentialsPage() for Host Properties data.");
				_view.isAuthorized = "false";
				return;
			}
		}
	}
}