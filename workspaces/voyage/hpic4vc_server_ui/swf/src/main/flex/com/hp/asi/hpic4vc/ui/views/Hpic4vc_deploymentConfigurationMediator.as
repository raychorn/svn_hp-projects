package com.hp.asi.hpic4vc.ui.views
{
	import assets.components.Hpic4vc_BaseMediator;
	
	import com.hp.asi.hpic4vc.ui.Hpic4vc_providerProxy;
	import com.hp.asi.hpic4vc.ui.Hpic4vc_server_providerProxy;
	import com.hp.asi.hpic4vc.ui.model.ApplicationDataModel;
	import com.hp.asi.hpic4vc.ui.model.DeploymentConfigurationModel;
	import com.hp.asi.hpic4vc.ui.model.UserInfoModel;
	import com.hp.asi.hpic4vc.ui.utils.Helper;
	import com.vmware.flexutil.events.MethodReturnEvent;
	import com.vmware.usersession.ServerInfo;
	import com.vmware.vsphere.client.util.UserSessionManager;
	
	import mx.logging.ILogger;
	import mx.logging.Log;

	public class Hpic4vc_deploymentConfigurationMediator  extends Hpic4vc_BaseMediator
	{
		
		private var _view:Hpic4vc_deploymentConfiguration;
		private var _proxyServer:Hpic4vc_server_providerProxy;
		private var _proxy:Hpic4vc_providerProxy;
		private var serviceGuid:String;
		private var count:int = 0;
		[Bindable]
		private var appModel:ApplicationDataModel = ApplicationDataModel.getInstance();
		
		private static var _logger:ILogger = Log.getLogger("Hpic4vc_deploymentConfigurationMediator");
		
		[View]
		/**
		 * The mediator's view.
		 */
		public function get view():Hpic4vc_deploymentConfiguration {
			return _view;
		}
		
		/** @private */
		public function set view(value:Hpic4vc_deploymentConfiguration):void {
			_view = value;
			_proxyServer = new Hpic4vc_server_providerProxy();
			_proxy = new Hpic4vc_providerProxy();
			_view._proxy = _proxyServer;
			requestData();
		}
		
		override protected function requestData():void
		{
			serviceGuid = (UserSessionManager.instance.userSession.serversInfo[0] as ServerInfo).serviceGuid;
			_view.serviceGuid = serviceGuid;
			if(ApplicationDataModel.isUserValidated())
			{
				_proxyServer.getDeploymentConfiguration(serviceGuid, onGettingCredentialsPage);
			}
			else
			{
			_proxy.getUserInfo(serviceGuid, onGettingUserInfo);
			}
			   

		}
		
		private function getCredentialInfo():void
		{
			_proxyServer.getDeploymentConfiguration(serviceGuid, onGettingCredentialsPage);
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
			getCredentialInfo();
		}
		
		private function onGettingCredentialsPage(event:MethodReturnEvent):void
		{
			if ( _view && appModel.isAuthorized)
			{
				if (event && event.result)
				{
					_view.isDeploymentConfigurationSetup = false;
					_view.currentState = "showDeploymentConfiguration";
					
					if (event.result is DeploymentConfigurationModel)
					{
						var model:DeploymentConfigurationModel = new DeploymentConfigurationModel();
						model = event.result as DeploymentConfigurationModel;
						if(model.deploymentConfigData && model.deploymentConfigData.length > 0)
						{
							
						_view.isDeploymentConfigurationSetup = true;
						_view.host = model.deploymentConfigData.getItemAt(0).host;
						_view.username = model.deploymentConfigData.getItemAt(0).username;
						_view.deploymentConfigId = model.deploymentConfigData.getItemAt(0).id;
						}
					}
				}
				else if(event && event.error)
				{
					_view.isDeploymentConfigurationSetup = false;
					_logger.warn("DeliveryInDoubt exception occurred.  Count: " + count.toString());
					if (event.error.toString().match("DeliveryInDoubt")) 
					{
						// Re try to request data for not more than 2 times
						if (count < 2) 
						{
							count ++;
							requestData();
							return;
						} else
						{
							_view.errorFoundLabel = event.error.message;
							_view.currentState = "errorFound";
							
							return;
						}
				    }
				}
			}
		}
	
	}
}