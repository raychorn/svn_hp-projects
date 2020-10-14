package assets.components
{
	import com.hp.asi.hpic4vc.ui.Hpic4vc_providerProxy;
	import com.hp.asi.hpic4vc.ui.Hpic4vc_server_providerProxy;
	import com.hp.asi.hpic4vc.ui.model.ApplicationDataModel;
	import com.hp.asi.hpic4vc.ui.model.DataGridWrapper;
	import com.hp.asi.hpic4vc.ui.model.HostConfigMismatchModel;
	import com.hp.asi.hpic4vc.ui.model.HostDetailModel;
	import com.hp.asi.hpic4vc.ui.model.NetworkDetailModel;
	import com.hp.asi.hpic4vc.ui.model.NetworkDetailViewModel;
	import com.hp.asi.hpic4vc.ui.model.TableModel;
	import com.hp.asi.hpic4vc.ui.model.UserInfoModel;
	import com.hp.asi.hpic4vc.ui.utils.Helper;
	import com.vmware.flexutil.events.MethodReturnEvent;
	
	import flash.events.Event;
	import flash.events.EventDispatcher;
	import flash.utils.setTimeout;
	
	import mx.binding.utils.ChangeWatcher;
	import mx.controls.Alert;
	import mx.logging.ILogger;
	import mx.logging.Log;
	
	public class Hpic4vc_Server_NetworkingDetailMediator extends Hpic4vc_BaseMediator
	{
		private var _view:Hpic4vc_Server_NetworkingDetail;
		private var _proxyServer:Hpic4vc_server_providerProxy;
		private var count:int = 0;
		private var countCall:int = 0;
		private var countUserCall:int = 0;
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
		private var _proxy:Hpic4vc_providerProxy;
		private static var _logger:ILogger = Log.getLogger("Hpic4vc_Server_NetworkingDetailMediator");
		[Bindable]
		public var isAuthorized:Boolean;
		
		[Bindable]
		private var appModel:ApplicationDataModel = ApplicationDataModel.getInstance();
		[View]
		/**
		 * The mediator's view.
		 */
		public function get view():Hpic4vc_Server_NetworkingDetail {
			return _view;
		}
		
		/** @private */
		public function set view(value:Hpic4vc_Server_NetworkingDetail):void {
			_view = value;
			_proxyServer = new Hpic4vc_server_providerProxy();			
			_proxy = new Hpic4vc_providerProxy();
			ChangeWatcher.watch(appModel,["isAuthorized"],handlerAuthorization);
		}
		
		override protected function clearData():void {
			_view = null;
		}
		
		override protected function requestData():void {
			if (_contextObject != null) {
				_logger.debug("Requesting HPIC4VC data.");
				_view._contextObject=_contextObject;				
				_proxyServer.getNetworkDetail(_contextObject.uid, onGettingNetworkDetail, _contextObject);	
				getNetworkMismatchRequest();
			} else {
				_logger.warn("ContextObject is null, hence not requesting data.");
				return;
			}
		}
		
		private function onGettingNetworkDetail(event:MethodReturnEvent):void {
			if (_view != null) {
				_logger.warn("Received HPIC4VC data in onGettingResult()");
				if (event == null){
					_view.errorFound = Helper.getString("errorOccurred");
					return;
				} 
				else if (event.error != null) {
					_logger.warn("DeliveryInDoubt exception occurred.  Count: " + count.toString());
					if (event.error.toString().match("DeliveryInDoubt")) {
						// Re try to request data for not more than 2 times
						if (count < 2) {
							count ++;
							requestData();
							return;
						} else {
							_view.errorFound = event.error.message;
							return;
						}
					} else {
						_view.errorFound = event.error.message;
						return;
					}
				} 
				else if (event.result == null) {
					_view.errorFound= event.error.message;
				} 
				
				var detail:NetworkDetailModel = event.result as NetworkDetailModel;
				if (detail)
					_view.netDetail = NetworkDetailViewModel.makeNetworkDetailViewModel( detail );
				else
					_view.netDetail.errorMessage = new String('Unable to convert');
			}
			else {
				_logger.warn("View is null.  Returning from onGettingResult()");
				return;
			}
		}
		
		//for Network Mistchmatch
		
		private function getNetworkMismatchRequest():void
		{			
			if(ApplicationDataModel.isUserValidated())
				{
					isAuthorized = appModel.isAuthorized;
					_proxyServer.getNetworkMismatch(_contextObject.uid, onGettingHostConfiguration, _contextObject);
				}
				else
				{
				_proxy.getUserInfo(_contextObject.uid, onGettingUserInfo);
					
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
		private function onGettingUserInfo(event:MethodReturnEvent):void 
		{
			Helper.updateUserAuthorization(event);	
		}
		
		private function handlerAuthorization(event:Event):void
		{
			isAuthorized = appModel.isAuthorized;
			_proxyServer.getNetworkMismatch(_contextObject.uid, onGettingHostConfiguration, _contextObject);
		}
		
		private function onGettingHostConfiguration(event:MethodReturnEvent):void 
		{
			if (_view != null) {
			_logger.warn("Received HPIC4VC Host Properties data in onGettingHostConfiguration()");
			if (event != null && event.result)
			{			
				if (event.result && ( event.result is HostConfigMismatchModel))
				{					
					var dataValue:String=_contextObject.uid;
					var data_array:Array=dataValue.split(":");	
					_view.networkMismatch = event.result as HostConfigMismatchModel;					
					_view.currentState = "showDetail";
					/*if(_view.networkMismatch.mismatch == null){
						_view.errorMessageId.text="Host configuration does not have hostConfigDetailedView for this Host";
					}*/
					var errorMessage:String=_view.networkMismatch.errorMessage;
					if(errorMessage!=null){
						_view.errorMessageId.text=errorMessage;
					}
					
					if(_view.networkMismatch.hostStatus == "UNKNOWN"){						
						_view.loader1.source=help_grayCls;
						_view.lbdata.text="Unknown";				
						if(_view.networkMismatch.refHostMoid == "NONE"){
						_view.errorMessageId.text= Helper.getString('ERROR_MESSAGE_REFERENCEHOST_NOTFOUND');
						}
						else if(_view.networkMismatch.vCProfileStatus == "MISMATCH"){
							_view.errorMessageId.text= Helper.getString('ERROR_MESSAGE_REFERENCEHOST_VIRTUAL_NOTMATCH');
						}
					}					
						
					else if(_view.networkMismatch.hostStatus == "MISMATCH"){						
						_view.loader1.source=statusRedCls;
						_view.lbdata.text="Mismatch";
						_view.btnActions.enabled=true;
					}					
					else if(_view.networkMismatch.hostStatus == "OK"){						
						_view.loader1.source=successCls;
						_view.lbdata.text="OK";
					}
					else if(_view.networkMismatch.hostStatus == "NA"){						
						_view.loader1.source=errorCls;
						_view.lbdata.text="NA";
						_view.btnPreference.enabled=false;
						_view.errorMessageId.text= Helper.getString('ERROR_MESSAGE_REFERENCEHOST_VIRTUAL_CONNECT_NOTFOUND');
					}	
					else if(_view.networkMismatch.hostStatus == "HOSTCONFIG_INPROGRESS"){
						_view.loader1.source=warningCls;
						_view.networkMismatch.refHostName=null;
						_view.lbdata.text= Helper.getString('HOST_CONFIHURATION_INPROGRESS');	
						_view.btnPreference.enabled=false;
					}
					
					if(_view.networkMismatch.refHostMoid == data_array[2]){						
						_view.btnPreference.enabled=false;						
						_view.refHostName.text="Reference Host : "+_view.networkMismatch.refHostName;
					}
					else{
						//_view.btnPreference.enabled=true;
						_view.refHostName.text="Reference Host :"+_view.networkMismatch.refHostName;
					}
					if(_view.networkMismatch.serverStatus == "UNKNOWN"){
						_view.loader1server.source=help_grayCls;
						_view.lbdataServer.text="Unknown";		
						_view.btnActions.enabled=false;
					}
					else if(_view.networkMismatch.serverStatus == "MISMATCH"){
						_view.loader1server.source=statusRedCls;
						_view.lbdataServer.text="Mismatch";
						_view.btnActions.enabled=false;
					}
					else if(_view.networkMismatch.serverStatus == "OK"){
						_view.loader1server.source=successCls;
						_view.lbdataServer.text="OK";
					}
					else if(_view.networkMismatch.serverStatus == "NA"){
						_view.loader1server.source=errorCls;
						_view.lbdataServer.text="NA";
						_view.btnActions.enabled=false;
					}
					if(_view.networkMismatch.vCProfileStatus == "UNKNOWN"){
						_view.loader1vc.source=help_grayCls;
						_view.lbdatavc.text="Unknown";		
					}
					else if(_view.networkMismatch.vCProfileStatus == "MISMATCH"){
						_view.loader1vc.source=statusRedCls;
						_view.lbdatavc.text="Mismatch";
					}
					else if(_view.networkMismatch.vCProfileStatus == "OK"){
						_view.loader1vc.source=successCls;
						_view.lbdatavc.text="OK";
					}
					else if(_view.networkMismatch.vCProfileStatus == "NA"){
						_view.loader1vc.source=errorCls;
						_view.lbdatavc.text="NA";
					}
					if(!isAuthorized){
						_view.btnActions.enabled=false;
						_view.btnPreference.enabled=false;
						_view.errorMessageId.text=Helper.getString('noPermission');
					}
					if(_view.errorMessageId.text == Helper.getString('loading')){
						_view.errorMessageId.visible=false;
					}
					
				}
			}
			else if (event.error.toString().match("DeliveryInDoubt")) {
				// Re try to request data for not more than 2 times
				if (countCall < 2) {
					countCall ++;
					getNetworkMismatchRequest();
					return;
				}			
			}
			else if (event && event.error)
			{
				_logger.warn(event.error.message);
				_view.errorMessageId.text= Helper.getString('HOST_CONFIHURATION_NOTFOUND');
				//_view.statePanelID.visible=false;
			}
		}
			
	}
}
}