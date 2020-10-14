/* Copyright 2012 Hewlett-Packard Development Company, L.P. */

package com.hp.asi.hpic4vc.ui.views {

import assets.components.Hpic4vc_BaseMediator;

import com.hp.asi.hpic4vc.ui.Hpic4vc_providerProxy;
import com.hp.asi.hpic4vc.ui.Hpic4vc_server_providerProxy;
import com.hp.asi.hpic4vc.ui.model.ApplicationDataModel;
import com.hp.asi.hpic4vc.ui.model.DataGridWrapper;
import com.hp.asi.hpic4vc.ui.model.TableModel;
import com.hp.asi.hpic4vc.ui.model.UserInfoModel;
import com.hp.asi.hpic4vc.ui.utils.Helper;
import com.vmware.flexutil.events.MethodReturnEvent;
import com.vmware.usersession.ServerInfo;
import com.vmware.vsphere.client.util.UserSessionManager;

import flash.events.Event;
import flash.events.EventDispatcher;
import flash.utils.setTimeout;

import mx.binding.utils.ChangeWatcher;
import mx.collections.ArrayCollection;
import mx.controls.advancedDataGridClasses.AdvancedDataGridColumn;
import mx.logging.ILogger;
import mx.logging.Log;

/**
 * The mediator for Hpic4vc_serverCredentials
 */
public class Hpic4vc_serverCredentialsMediator extends Hpic4vc_BaseMediator
{
   private var _view:Hpic4vc_serverCredentials;
   private var _proxyServer:Hpic4vc_server_providerProxy;
   private var _proxy:Hpic4vc_providerProxy;
   private var serviceGuid:String;
   private var count:int = 0;
   
   [Bindable]
   private var appModel:ApplicationDataModel = ApplicationDataModel.getInstance();
   
   
   private static var _logger:ILogger = Log.getLogger("Hpic4vc_serverCredentialsMediator");
   
   [View]
   /**
	* The mediator's view.
	*/
   public function get view():Hpic4vc_serverCredentials {
	   return _view;
   }
   
   /** @private */
   public function set view(value:Hpic4vc_serverCredentials):void {
	   _view = value;
	   _proxyServer = new Hpic4vc_server_providerProxy();
	   _proxy = new Hpic4vc_providerProxy();
       _view._proxy = _proxyServer;
	   requestData();
	   ChangeWatcher.watch(appModel,["isAuthorized"],handlerAuthorization);
   }
   
   override protected function clearData():void {
	   _view = null;
   }
   
   override protected function requestData():void {
	   serviceGuid = (UserSessionManager.instance.userSession.serversInfo[0] as ServerInfo).serviceGuid;
       _view.serviceGuid = serviceGuid;
	    
	   if(ApplicationDataModel.isUserValidated())
	   {
		   _proxyServer.getServerCredentials(serviceGuid, onGettingCredentialsPage);	   
	   }
	   else
	   {
	  		 _proxy.getUserInfo(serviceGuid, onGettingUserInfo);
		   
	   }
   }
   
   private function handlerAuthorization(event:Event):void
   {
	   if(appModel.isAuthorized)
	   {
		   _proxyServer.getServerCredentials(serviceGuid, onGettingCredentialsPage);
	   }
	   else
	   {
		   
	   }
	   
   }
   
 
   
   
   
   private function updateData(data:String):void {
       serviceGuid = (UserSessionManager.instance.userSession.serversInfo[0] as ServerInfo).serviceGuid;
       _proxyServer.updateServerCredentials(serviceGuid, data, onGettingCredentialsPage);
   }

   private function onGettingCredentialsPage(event:MethodReturnEvent):void {   
		   if (_view != null) {
			   _logger.debug("Received HPIC4VC server credentials data in onGettingCredentialsPage()");  
			   if (event == null) {
				   _view.errorFoundLabel = Helper.getString("noRecordsFound");
				   return;
			   } else if (event.error != null) {
				   if (event.error.toString().match("DeliveryInDoubt")) {
					   _logger.warn("DeliveryInDoubt exception occurred.  Count: " + count.toString());
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
				   _view.errorFoundLabel = Helper.getString("noRecordsFound");
				   return;
			   } 
			   
			  _view.tableModel = event.result as TableModel;
			  _view.currentState =  "Authorized";
			  _view.dataAvailable();
		   }
		   else {
			   _logger.warn("View is null.  Returning from onGettingCredentialsPage() for Server credentials data.");
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
}

} //End of package