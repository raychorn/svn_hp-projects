package com.hp.asi.hpic4vc.ui.views
{
	import assets.components.Hpic4vc_BaseMediator;
	
	import com.hp.asi.hpic4vc.ui.Hpic4vc_providerProxy;
	import com.hp.asi.hpic4vc.ui.Hpic4vc_server_providerProxy;
	import com.hp.asi.hpic4vc.ui.model.ApplicationDataModel;
	import com.hp.asi.hpic4vc.ui.model.HostConfigModel;
	import com.hp.asi.hpic4vc.ui.model.SwitchTypeModel;
	import com.hp.asi.hpic4vc.ui.model.TableModel;
	import com.hp.asi.hpic4vc.ui.model.UserInfoModel;
	import com.hp.asi.hpic4vc.ui.model.VDSMigrateManagementModel;
	import com.hp.asi.hpic4vc.ui.model.VDSVersionsModel;
	import com.hp.asi.hpic4vc.ui.utils.Helper;
	import com.hp.asi.hpic4vc.ui.views.Hpic4vc_HostConfigurator;
	import com.vmware.core.model.IResourceReference;
	import com.vmware.flexutil.events.MethodReturnEvent;
	import com.vmware.usersession.ServerInfo;
	import com.vmware.vsphere.client.util.UserSessionManager;
	
	import flash.events.Event;
	
	import mx.binding.utils.ChangeWatcher;
	import mx.collections.ArrayCollection;
	import mx.logging.ILogger;
	import mx.logging.Log;
	import mx.utils.ObjectUtil;
	
	import spark.collections.Sort;
	import spark.collections.SortField;

	public class Hpic4vc_HostConfiguratorMediator extends Hpic4vc_BaseMediator
	{
		private var _view:Hpic4vc_HostConfigurator;
		private var _proxyServer:Hpic4vc_server_providerProxy;
		private var _proxy:Hpic4vc_providerProxy;
		private var serviceGuid:String;
		private var countUserCall:int = 0;
		private static var _logger:ILogger = Log.getLogger("Hpic4vc_HostConfiguratorMediator");
		private var count:int = 0;
		
		[Bindable]
		private var appModel:ApplicationDataModel = ApplicationDataModel.getInstance();
		[View]
		/**
		 * The mediator's view.
		 */
		public function get view():Hpic4vc_HostConfigurator {
			return _view;
		}
		
		/** @private */
		public function set view(value:Hpic4vc_HostConfigurator):void {
			_view = value;			
			_proxyServer = new Hpic4vc_server_providerProxy();
			_proxy = new Hpic4vc_providerProxy();
			ChangeWatcher.watch(appModel,["isAuthorized"],handlerAuthorization);
			requestData();
		}
		
		override protected function clearData():void {
			_view = null;
		}
		
		override protected function requestData():void {
			serviceGuid = (UserSessionManager.instance.userSession.serversInfo[0] as ServerInfo).serviceGuid;
			_view.serviceGuid = serviceGuid;
			if(ApplicationDataModel.isUserValidated())
			{
				_view.isAuthorized = appModel.isAuthorized;
				_proxyServer.getHostConfiguration(serviceGuid, onGettingHostConfiguration, null);
				_proxyServer.getSwitchTypeHostConfiguration(serviceGuid,onGettingSwitchTypeHostConfiguration,null);
				_proxyServer.getVDSVersion(serviceGuid,onGettingVDSversion,null);
			}
			else
			{
				_proxy.getUserInfo(serviceGuid, onGettingUserInfo);
			}
			
			//_proxyServer.getMigrateManagement(serviceGuid,onGettingVDSMigrateManagement,null);
			
		}
		override public function set contextObject(value:Object):void {
			if (_view == null){
				_logger.warn('Configuration_Host view is null');
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
		/*private function onGettingVDSMigrateManagement(event:MethodReturnEvent):void 
		{
			_logger.warn("Received HPIC4VC Host Properties data in onGettingHostConfiguration()");
			var i:int;
			if (event != null && event.result)
			{				
				if (event.result && ( event.result is VDSMigrateManagementModel))	{					
					_view.vdsMigrateManagement = event.result as VDSMigrateManagementModel;
				}
				for (i = 0; i < _view.vdsMigrateManagement.supportedValues.length; i++) 
				{					
					if(_view.vdsMigrateManagement.supportedValues.getItemAt(i) == _view.vdsMigrateManagement.selectedValue)
					{
						_view.listVdsMM.selectedIndex=i;
					}
					
				}
			}
			else if (event && event.error)
			{
				_view.currentState = "errorFound";
			}
		}*/
		
		private function onGettingVDSversion(event:MethodReturnEvent):void 
		{
			_logger.warn("Received HPIC4VC Host Properties data in onGettingHostConfiguration()");
			var i:int;
			if (_view != null) {
			if (event != null && event.result)
			{				
				if (event.result && ( event.result is VDSVersionsModel))	{					
					_view.vdsversions = event.result as VDSVersionsModel;
				}
				for (i = 0; i < _view.vdsversions.supportedValues.length; i++) 
				{					
					if(_view.vdsversions.supportedValues.getItemAt(i) == _view.vdsversions.selectedValue)
					{
						_view.listVds.selectedIndex=i;
					}
					
				}
			}
			else if (event && event.error)
			{
				if (event.error.toString().match("DeliveryInDoubt")) {
					if (count < 2) {
						count ++;
						_proxyServer.getVDSVersion(serviceGuid,onGettingVDSversion,null);
						return;
					}
				}
			}
			else if (event && event.error)
			{
				_view.currentState = "errorFound";
				_view.lodingID.visible=false;	
			}
			}
			else {
				_logger.warn("View is null.  Returning from onGettingVDSversion()");
				//_view.lodingID.visible=false;					
				return;
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
		
		
		private function onGettingHostConfiguration(event:MethodReturnEvent):void 
		{
			_logger.warn("Received HPIC4VC Host Properties data in onGettingHostConfiguration()");			
			if (_view != null) {
			if (event != null && event.result)	{			
				if (event.result && ( event.result is HostConfigModel))
				{				
					_view.hostConfigData = event.result as HostConfigModel;		
					//_view.hostConfigData.networks=null;
					if(_view.hostConfigData.networks!=null){
					getNetworkSort(_view.hostConfigData.networks)
					_view.orginalHostConfigData = ObjectUtil.clone(_view.hostConfigData) as HostConfigModel;
					getNetworkSort(_view.orginalHostConfigData.networks)
					}
					
					_view.lodingID.visible=false;					
					_view.addMgmtEvents();
					if(_view.hostConfigData.networks == null){
						_view.errorID.visible=true;
					}
					else if(_view.hostConfigData.networks.length == 0){
						_view.errorID.visible=true;
					}
					
					else{
						_view.errorID.visible=false;
					}
					/*if( _view.hostConfigData.networks == null || _view.hostConfigData.networks.length == 0){
						_proxyServer.getRefreshedNetwork(serviceGuid, onGettingHostConfiguration, null);
					}*/
				}
			}
			else if (event && event.error)
			{
				if (event.error.toString().match("DeliveryInDoubt")) {
					if (count < 2) {
						count ++;
						_proxyServer.getHostConfiguration(serviceGuid, onGettingHostConfiguration, null);
						return;
					}
				}
			}
			else if (event && event.error)
			{
				_view.currentState = "errorFound";
				_view.lodingID.visible=false;	
			}
			}
			else {
				_logger.warn("View is null.  Returning from onGettingHostConfiguration()");
				//_view.lodingID.visible=false;	
				return;
			}
		}
		
		private function getNetworkSort(network:ArrayCollection):void
		{
			var dataSortField:SortField = new SortField();
			dataSortField.name = "networkName";
			dataSortField.numeric = false;
			
			var dataSort:Sort = new Sort();
			dataSort.fields = [dataSortField];
					
			network.sort = dataSort;
			network.refresh();
		}
		
		private function onGettingSwitchTypeHostConfiguration(event:MethodReturnEvent):void 
		{
			_logger.warn("Received HPIC4VC Host Properties data in onGettingSwitchTypeHostConfiguration()");
			if (_view != null) {
			if (event != null && event.result)
			{
				if (event.result && ( event.result is SwitchTypeModel))
				{
					_view.switchTypeData = event.result as SwitchTypeModel;
					_view.isSwitchDataAvailable =true;
					_view.orginalSwithtypeData = ObjectUtil.clone(_view.switchTypeData) as SwitchTypeModel;
					if(_view.switchTypeData.switchdata.selectedValue=="VMWARE_VDS"){
						_view.grpVdsversion.visible=true;						
					}
					_view.addEvents();
				}
				
			}
			else if (event && event.error)
			{
				if (event.error.toString().match("DeliveryInDoubt")) {
					if (count < 2) {
						count ++;
						_proxyServer.getSwitchTypeHostConfiguration(serviceGuid,onGettingSwitchTypeHostConfiguration,null);
						return;
					}
				}
			}
			else if (event && event.error)
			{
				_view.currentState = "errorFound";
				_view.lodingID.visible=false;	
			}
			}
			else {
				_logger.warn("View is null.  Returning from onGettingSwitchTypeHostConfiguration()");
				//_view.lodingID.visible=false;	
				return;
			}
		}
		
		private function handlerAuthorization(event:Event):void
		{
			view.isAuthorized = appModel.isAuthorized;
			_proxyServer.getHostConfiguration(serviceGuid, onGettingHostConfiguration, null);
			_proxyServer.getSwitchTypeHostConfiguration(serviceGuid,onGettingSwitchTypeHostConfiguration,null);
			_proxyServer.getVDSVersion(serviceGuid,onGettingVDSversion,null);
		}
			
	}
}