/* Copyright 2012 Hewlett-Packard Development Company, L.P.  */
package com.hp.asi.hpic4vc.ui
{
	
	import assets.components.Hpic4vc_TestWizard;
	
	import com.hp.asi.hpic4vc.ui.model.LinkModel;
	import com.hp.asi.hpic4vc.ui.model.MenuModel;
	import com.hp.asi.hpic4vc.ui.views.Hpic4vc_uiMediator;
	import com.vmware.actionsfw.ActionContext;
	import com.vmware.actionsfw.events.ActionInvocationEvent;
	import com.vmware.core.model.IResourceReference;
	import com.vmware.flexutil.events.MethodReturnEvent;
	import com.vmware.ui.IContextObjectHolder;
	
	import flash.events.EventDispatcher;
	import flash.net.URLRequest;
	import flash.net.navigateToURL;
	import flash.utils.setTimeout;
	
	import mx.collections.ArrayCollection;
	import mx.controls.Alert;
	import mx.core.FlexGlobals;
	import mx.core.UIComponent;
	import mx.events.MenuEvent;
	import mx.logging.ILogger;
	import mx.logging.Log;
	import mx.managers.PopUpManager;
	
	import spark.components.TitleWindow;
	
	
	/**
	 * This actionscript class handles the provisioning tasks in the context menus for various entity types.
	 *  
	 * The requestHandler's event is the 'uid' specified in the context 
	 * menu's "uid" of the action spec extension (see plugin.xml).
	 * 
	 * @author nidadavo
	 * 
	 */	
	public class Hpic4vc_actionCommand
	{
		private var _proxyStorage:Hpic4vc_storage_providerProxy = new Hpic4vc_storage_providerProxy();
		private var eInfo:String;
		private var _contextObject:IResourceReference;
		
		private static var _logger:ILogger = Log.getLogger("Hpic4vc_actionCommand");
				
		/* The requestHandler's event is the 'uid' specified in the context 
		menu's "uid" of the action spec extension (see plugin.xml).*/
		[RequestHandler("com.hp.asi.hpic4vc.ui.vm.cloneVM")]
				
		public function onCloneVmInvocation(event:ActionInvocationEvent):void {
			
			_contextObject = getIResourceReference(event);
			_proxyStorage.getProvisioningTasks(_contextObject.uid, onGettingProvisioningTaskResult, _contextObject);
			eInfo = event.type;
		}
		
		[RequestHandler("com.hp.asi.hpic4vc.ui.host.createDatastore")]
		
		public function onCreateDatastoreInvocation(event:ActionInvocationEvent):void {
			
			_contextObject = getIResourceReference(event);
			_proxyStorage.getProvisioningTasks(_contextObject.uid, onGettingProvisioningTaskResult, _contextObject);
			eInfo = event.type;
		}		
		
		[RequestHandler("com.hp.asi.hpic4vc.ui.vm.createVMfromTemplate")]
		
		public function onCreateVMfromTemplateForTemplateInvocation(event:ActionInvocationEvent):void {
			
			_contextObject = getIResourceReference(event);
			_proxyStorage.getProvisioningTasks(_contextObject.uid, onGettingProvisioningTaskResult, _contextObject);
			eInfo = event.type;
		}	
		
		[RequestHandler("com.hp.asi.hpic4vc.ui.cluster.createDatastore")]
		
		public function onCreateDatastoreInvocationForCluster(event:ActionInvocationEvent):void {
			
			_contextObject = getIResourceReference(event);
			_proxyStorage.getProvisioningTasks(_contextObject.uid, onGettingProvisioningTaskResult, _contextObject);
			eInfo = event.type;
		}		
		
		[RequestHandler("com.hp.asi.hpic4vc.ui.cluster.createVMfromTemplate")]
		
		public function onCreateVMfromTemplateForCluster(event:ActionInvocationEvent):void {
			
			_contextObject = getIResourceReference(event);
			_proxyStorage.getProvisioningTasks(_contextObject.uid, onGettingProvisioningTaskResult, _contextObject);
			eInfo = event.type;
		}	
		
		[RequestHandler("com.hp.asi.hpic4vc.ui.host.createVMfromTemplate")]
		
		public function onCreateVMfromTemplateInvocation(event:ActionInvocationEvent):void {
			
			_contextObject = getIResourceReference(event);
			_proxyStorage.getProvisioningTasks(_contextObject.uid, onGettingProvisioningTaskResult, _contextObject);
			eInfo = event.type;
		}
		
		
		[RequestHandler("com.hp.asi.hpic4vc.ui.datastore.deleteDatastore")]
		
		public function onDeleteDatastoreInvocation(event:ActionInvocationEvent):void {
			
			_contextObject = getIResourceReference(event);
			_proxyStorage.getProvisioningTasks(_contextObject.uid, onGettingProvisioningTaskResult, _contextObject);
			eInfo = event.type;
		}
		
		[RequestHandler("com.hp.asi.hpic4vc.ui.datastore.expandDatastore")]
		
		public function onExpandDatastoreInvocation(event:ActionInvocationEvent):void {
			
			_contextObject = getIResourceReference(event);
			_proxyStorage.getProvisioningTasks(_contextObject.uid, onGettingProvisioningTaskResult, _contextObject);
			eInfo = event.type;
		}
		
		
		
		
		[RequestHandler("com.hp.asi.hpic4vc.ui.host.testWizard")]
		
		public function testWizardInvocation(event:ActionInvocationEvent):void {
			_contextObject = getIResourceReference(event);
			var testWizard:Hpic4vc_TestWizard;
			testWizard = PopUpManager.createPopUp(FlexGlobals.topLevelApplication.parentDocument, Hpic4vc_TestWizard, true) as Hpic4vc_TestWizard;
		//	Alert.show("Testing flex Wizard");
		}
		
		private function getIResourceReference(event:ActionInvocationEvent):IResourceReference {
			var actionContext:ActionContext = event.context;
			if (actionContext == null || (actionContext.targetObjects.length <= 0)
			|| (!(actionContext.targetObjects[0] is IResourceReference))) {
				return null;
			}
			return (actionContext.targetObjects[0] as IResourceReference);
		}
		
		private function onGettingProvisioningTaskResult(event:MethodReturnEvent):void {
			if (_contextObject != null) {
				_logger.debug("Received HPIC4VC data in onGettingProvisioningTaskResult()");
				
				if(event == null || event.result == null) {
					_logger.debug("The MenuModel object returned is null");
					return;
				}
			
				else if(event.error != null) {
					Alert.show(event.error.message);
					return;
				}
			
				else if((event.result as MenuModel).errorMessage != null) {
					Alert.show((event.result as MenuModel).errorMessage);
					return;
				}
					
				var menuItems:ArrayCollection = (event.result as MenuModel).menuItems;
				var url:String;
				var i:int;
				var space:RegExp = /\s/g;
				for( var index:int = 0; index < menuItems.length; index++ ) {
					var lm:LinkModel = menuItems.getItemAt(index) as LinkModel;
					var toCompare:String;
					i = eInfo.lastIndexOf(".");
					
					/* Compare the display name from the returned MenuModel to the context menu's display name.
					The context menu's display name is retrieved from the "uid" of the action spec extension (see plugin.xml).
					Note: The display name in the Link Model has to match the display name in the "uid" tag of the action spec extension 
					for this check.*/
					if((eInfo.substring(i+1)).toLowerCase() == ((lm.displayName).replace(space,"")).toLowerCase())
						url = lm.url;
				}
				if(url != null)
					navigateToURL(new URLRequest(url), "_blank");
				//Todo display a dialog box
				
			}
			else {
				_logger.warn("ContextObject is null.  Returning from onGettingProvisioningTaskResult()");
				return;
			}
		}
		
	}
}