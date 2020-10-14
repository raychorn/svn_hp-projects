package com.hp.asi.hpic4vc.ui
{
	import assets.components.Hpic4vc_Deployment_Wizard;
	
	import com.hp.asi.hpic4vc.ui.model.HeaderModel;
	import com.hp.asi.hpic4vc.ui.model.StringModel;
	import com.vmware.actionsfw.ActionContext;
	import com.vmware.actionsfw.events.ActionInvocationEvent;
	import com.vmware.core.model.IResourceReference;
	import com.vmware.flexutil.events.MethodReturnEvent;
	import com.vmware.usersession.ServerInfo;
	import com.vmware.vsphere.client.util.UserSessionManager;
	
	import mx.core.FlexGlobals;
	import mx.logging.ILogger;
	import mx.logging.Log;
	import mx.managers.PopUpManager;

	public class Hpic4vc_server_actionCommand
	{
		
		
		private var _proxyStorage:Hpic4vc_server_providerProxy = new Hpic4vc_server_providerProxy();
		private var _proxy:Hpic4vc_providerProxy = new Hpic4vc_providerProxy();
		private var eInfo:String;
		private var _contextObject:IResourceReference;
		private var wizard:Hpic4vc_Deployment_Wizard = new Hpic4vc_Deployment_Wizard();
		
		
		private static var _logger:ILogger = Log.getLogger("Hpic4vc_server_actionCommand");
		
		
		[RequestHandler("com.hp.asi.hpic4vc.ui.cluster.HPDeployment")]
		
		public function onHpDeploymentInvocation(event:ActionInvocationEvent):void {
			_contextObject = getIResourceReference(event);
			/*var wizard:Hpic4vc_Deployment_Wizard ; 
			wizard = PopUpManager.createPopUp(FlexGlobals.topLevelApplication.parentDocument,Hpic4vc_Deployment_Wizard,true) as Hpic4vc_Deployment_Wizard;
			PopUpManager.centerPopUp(wizard);*/
			//getHelpUrl();
			getObjectRefernceName(_contextObject);
		}
		
		
		private function getIResourceReference(event:ActionInvocationEvent):IResourceReference {
			var actionContext:ActionContext = event.context;
			if (actionContext == null || (actionContext.targetObjects.length <= 0)
				|| (!(actionContext.targetObjects[0] is IResourceReference))) {
				return null;
			}
			return (actionContext.targetObjects[0] as IResourceReference);
		}
		
		private function getObjectRefernceName(obj:IResourceReference):void
		{
			_proxyStorage.getObjReferenceName(obj.uid,onGettingObjReferenceName,obj);
			
		}
		
		
		

		private function onGettingObjReferenceName(event:MethodReturnEvent):void
		{
			
			if(event && event.result)
			{
				if(event.result is StringModel)
				{
					var stringModel:StringModel = new StringModel();
					stringModel = event.result as StringModel;
					
					if (stringModel.errorMessage)
					{
						_proxy.getHeader(IResourceReference(_contextObject).uid,onGettingHeader,_contextObject);
						
					}
					else if (stringModel.data)
					{
						
						wizard = PopUpManager.createPopUp(FlexGlobals.topLevelApplication.parentDocument,Hpic4vc_Deployment_Wizard,true) as Hpic4vc_Deployment_Wizard;
						if (IResourceReference(_contextObject).type =="ClusterComputeResource")
						{
							wizard.vcenterFolderName = "Cluster : " + stringModel.data;
						}
						else
						{
							wizard.vcenterFolderName = "Datacenter :" +  stringModel.data;
						}
						
						
						
						wizard.vCenterFolderRef  = _contextObject.uid;
					
						PopUpManager.centerPopUp(wizard);
						
					}
					else if(stringModel.data == null)
					{
						_proxy.getHeader(IResourceReference(_contextObject).uid,onGettingHeader,_contextObject);
					}
					
				}
			}
			
		}
		
		private function onGettingHeader(event:MethodReturnEvent):void
		{
			var wizard:Hpic4vc_Deployment_Wizard ;
			wizard = PopUpManager.createPopUp(FlexGlobals.topLevelApplication.parentDocument,Hpic4vc_Deployment_Wizard,true) as Hpic4vc_Deployment_Wizard;
			
			
			
			
			if(event && event.result)
			{
				if(event.result is HeaderModel)
				{
					var headerModel:HeaderModel = new HeaderModel();
					headerModel = event.result as HeaderModel;
					
					if (headerModel.errorMessage)
					{
						
					}
					else if (headerModel.objReferenceName)
					{
						if (IResourceReference(_contextObject).type =="ClusterComputeResource")
						{
							wizard.vcenterFolderName = "Cluster : " + headerModel.objReferenceName;
						}
						else
						{
							wizard.vcenterFolderName = "Datacenter :" +  headerModel.objReferenceName;
						}
						
						
					
						
						wizard.vCenterFolderRef  = _contextObject.uid;
						PopUpManager.centerPopUp(wizard);
						
					}
					else
					{
						 
						
						wizard.vcenterFolderName = headerModel.objReferenceName;
						wizard.vCenterFolderRef  = _contextObject.uid;
						PopUpManager.centerPopUp(wizard);
					}
				}
			}
		}
		public function Hpic4vc_server_actionCommand()
		{
		}
	}
}