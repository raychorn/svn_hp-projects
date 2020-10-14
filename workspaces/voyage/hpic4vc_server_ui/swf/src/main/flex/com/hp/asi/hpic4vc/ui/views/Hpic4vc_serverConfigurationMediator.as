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

import mx.binding.utils.ChangeWatcher;
import mx.logging.ILogger;
import mx.logging.Log;

/**
 * The mediator for Hpic4vc_serverCredentials
 */
public class Hpic4vc_serverConfigurationMediator extends Hpic4vc_BaseMediator
{
   private var _view:Hpic4vc_serverConfiguration;
   private var _proxyServer:Hpic4vc_server_providerProxy;
   private var _proxy:Hpic4vc_providerProxy;
   private var serviceGuid:String;
   private var count:int = 0;
   [Bindable]
   private var appModel:ApplicationDataModel = ApplicationDataModel.getInstance();
   
   
   private static var _logger:ILogger = Log.getLogger("Hpic4vc_serverConfigurationMediator");
   
   [View]
   /**
	* The mediator's view.
	*/
   public function get view():Hpic4vc_serverConfiguration {
	   return _view;
   }
   
   /** @private */
   public function set view(value:Hpic4vc_serverConfiguration):void {
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
		   _view.isAuthorized = appModel.isAuthorized;	   
		   if(appModel.isAuthorized)
		   {
			   _proxyServer.getServerConfiguration(serviceGuid, onGettingConfigurationPage);
		   }
		   
	   }
	   else
	   {
		   
	   _proxy.getUserInfo(serviceGuid, onGettingUserInfo);
	   }
   }

   private function onGettingConfigurationPage(event:MethodReturnEvent):void {
	   if (_view != null) {
		   _logger.warn("Received HPIC4VC server configuration data in onGettingConfigurationPage()");  
		   if (event == null) {
			   _view.noRecordsFoundLabel = Helper.getString("noRecordsFound");
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
			   _view.noRecordsFoundLabel = Helper.getString("noRecordsFound");
			   return;
		   } else if ((event.result as TableModel).errorMessage != null) {
			   _view.errorFoundLabel = (event.result as TableModel).errorMessage;
			   return;
		   } else if ((event.result as TableModel).informationMessage != null) {
			   _view.noRecordsFoundLabel = (event.result as TableModel).informationMessage;
			   return;
		   } else {
			   _view.noRecordsFoundLabel = "";
			   _view.errorFoundLabel = "";
		   }
		   
		   var tableModel:TableModel = event.result as TableModel;
		   var wrapper:DataGridWrapper = Helper.createDataGrid(tableModel);
		   
		   if (wrapper != null) {
			   _view.columns = wrapper.columns;
			   _view.dataProvider = wrapper.list;
		   }
	   }
	   else {
		   _logger.warn("View is null.  Returning from onGettingConfigurationPage() for Server config data.");
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
   private function onGettingUserInfo(event:MethodReturnEvent):void {
	   Helper.updateUserAuthorization(event);
	   /*if (_view != null) {
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
		   _logger.warn("View is null.  Returning from onGettingVCCredentialsPage() for server config data.");
		   _view.isAuthorized = "false";
		   return;
	   }*/
   }
   
   private function handlerAuthorization(event:Event):void
   {
	   _view.isAuthorized = appModel.isAuthorized;	
	   if(appModel.isAuthorized)
	   {
	   _proxyServer.getServerConfiguration(serviceGuid, onGettingConfigurationPage);
	   }
   }
}
}