/* Copyright 2012 Hewlett-Packard Development Company, L.P. */

package com.hp.asi.hpic4vc.ui.views {

import assets.components.Hpic4vc_BaseMediator;

import com.hp.asi.hpic4vc.ui.Hpic4vc_providerProxy;
import com.hp.asi.hpic4vc.ui.model.DataGridWrapper;
import com.hp.asi.hpic4vc.ui.model.TableModel;
import com.hp.asi.hpic4vc.ui.model.UserInfoModel;
import com.hp.asi.hpic4vc.ui.utils.Helper;
import com.vmware.flexutil.events.MethodReturnEvent;

import flash.utils.setTimeout;

import mx.controls.advancedDataGridClasses.AdvancedDataGridColumn;
import mx.logging.ILogger;
import mx.logging.Log;

/**
 * The mediator for Hpic4vc_hpOneViewCredentials
 */
public class Hpic4vc_hpOneViewCredentialsMediator extends Hpic4vc_BaseMediator
{
   private var _view:Hpic4vc_hpOneViewCredentials;
   private var _proxy:Hpic4vc_providerProxy;
   private var serviceGuid:String;
   private var count:int = 0;
   
   private static var _logger:ILogger = Log.getLogger("Hpic4vc_hpOneViewCredentialsMediator");
   
   [View]
   /**
	* The mediator's view.
	*/
   public function get view():Hpic4vc_hpOneViewCredentials {
	   return _view;
   }
   
   /** @private */
   public function set view(value:Hpic4vc_hpOneViewCredentials):void {
	   _view = value;
	   _proxy = new Hpic4vc_providerProxy();
	   _view._proxy = _proxy;
	   requestData();
   }
   
   override protected function requestData():void {
	   serviceGuid = getServerGuid();
	   _view.serviceGuid = serviceGuid;
	   _proxy.getHPOneViewCredentials(serviceGuid, onGettingHPOneViewCredentialsPage);
	   _proxy.getUserInfo(serviceGuid, onGettingUserInfo);
   }

   private function onGettingHPOneViewCredentialsPage(event:MethodReturnEvent):void {
	   if (_view.isAuthorized != "") {  
		   _view.errorFoundLabel = "";
	   		if (_view != null) {
			   _logger.debug("Received HPIC4VC hpOneView Credentials data in onGettingHPOneViewCredentialsPage()");
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
			   } 
			   _view.tableModel = event.result as TableModel;
			   if ( (_view.tableModel) && (_view.tableModel.rowRawData) ) {
				   _logger.debug("Received HPIC4VC hpOneView Credentials data in onGettingHPOneViewCredentialsPage() --> rows="+_view.tableModel.rowRawData.length);
			   }
			   _logger.debug("Received HPIC4VC hpOneView Credentials data in onGettingHPOneViewCredentialsPage() --> ("+event.result+')');
		   }
		   else {
			   _logger.warn("View is null.  Returning from onGettingHPOneViewCredentialsPage() for hpOneView credentials data.");
			   return;
		   }
	   } else {
		   setTimeout(onGettingHPOneViewCredentialsPage, 500, event);
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
		   _logger.warn("View is null.  Returning from onGettingHPOneViewCredentialsPage() for hpOneView credentials data.");
		   _view.isAuthorized = "false";
		   return;
	   }
	}
}
}