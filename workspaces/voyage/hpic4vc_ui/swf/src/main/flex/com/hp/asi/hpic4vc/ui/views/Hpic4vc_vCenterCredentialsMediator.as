/* Copyright 2012 Hewlett-Packard Development Company, L.P. */

package com.hp.asi.hpic4vc.ui.views {

import assets.components.Hpic4vc_BaseMediator;

import com.hp.asi.hpic4vc.ui.Hpic4vc_providerProxy;
import com.hp.asi.hpic4vc.ui.model.ApplicationDataModel;
import com.hp.asi.hpic4vc.ui.model.TableModel;
import com.hp.asi.hpic4vc.ui.utils.Helper;
import com.vmware.flexutil.events.MethodReturnEvent;

import flash.events.Event;
import flash.utils.setTimeout;

import mx.binding.utils.ChangeWatcher;
import mx.logging.ILogger;
import mx.logging.Log;

/**
 * The mediator for Hpic4vc_vCenterCredentials
 */
public class Hpic4vc_vCenterCredentialsMediator extends Hpic4vc_BaseMediator
{
   private var _view:Hpic4vc_vCenterCredentials;
   private var _proxy:Hpic4vc_providerProxy;
   private var serviceGuid:String;
   private var count:int = 0;
   
   [Bindable]
   private var appModel:ApplicationDataModel = ApplicationDataModel.getInstance();
   
   private static var _logger:ILogger = Log.getLogger("Hpic4vc_vCenterCredentialsMediator");
   
   [View]
   /**
	* The mediator's view.
	*/
   public function get view():Hpic4vc_vCenterCredentials {
	   return _view;
   }
   
   /** @private */
   public function set view(value:Hpic4vc_vCenterCredentials):void {
	   _view = value;
	   _proxy = new Hpic4vc_providerProxy();
	   _view._proxy = _proxy;
	   ChangeWatcher.watch(appModel,["isAuthorized"],handlerAuthorization);
	   requestData();
   }
   
   override protected function requestData():void {
	   serviceGuid = getServerGuid();
	   _view.serviceGuid = serviceGuid;
	   
	   if (ApplicationDataModel.isUserValidated())
	   {
		   _view.isAuthorized = appModel.isAuthorized;
		   if(appModel.isAuthorized)
		   {
		   _proxy.getVCCredentials(serviceGuid, onGettingVCCredentialsPage);   
		   }
	   }
	   else
	   {
		   _proxy.getUserInfo(serviceGuid, onGettingUserInfo);
	   }
   }

   private function onGettingVCCredentialsPage(event:MethodReturnEvent):void {
	  
		   _view.errorFoundLabel = "";
	   		if (_view != null) {
			   _logger.debug("Received HPIC4VC vCenter Credentials data in onGettingVCCredentialsPage()");
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
		   }
		   else {
			   _logger.warn("View is null.  Returning from onGettingVCCredentialsPage() for vCenter credentials data.");
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
   
   private function handlerAuthorization(event:Event):void
   {
	   _view.isAuthorized = appModel.isAuthorized;
	   if(appModel.isAuthorized)
	   {
		   _proxy.getVCCredentials(serviceGuid, onGettingVCCredentialsPage);   
	   }
   }
}
}