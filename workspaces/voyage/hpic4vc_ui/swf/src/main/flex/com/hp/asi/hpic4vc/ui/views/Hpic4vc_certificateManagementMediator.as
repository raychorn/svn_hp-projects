/* Copyright 2012 Hewlett-Packard Development Company, L.P. */

package com.hp.asi.hpic4vc.ui.views {

import assets.components.Hpic4vc_BaseMediator;

import com.hp.asi.hpic4vc.ui.Hpic4vc_providerProxy;
import com.hp.asi.hpic4vc.ui.model.ApplicationDataModel;
import com.hp.asi.hpic4vc.ui.model.UserInfoModel;
import com.hp.asi.hpic4vc.ui.utils.Helper;
import com.vmware.flexutil.events.MethodReturnEvent;

import flash.events.Event;

import mx.binding.utils.ChangeWatcher;
import mx.logging.ILogger;
import mx.logging.Log;

/**
 * The mediator for Hpic4vc_CertificateManagementMediator
 */
public class Hpic4vc_certificateManagementMediator extends Hpic4vc_BaseMediator
{
   private var _view:Hpic4vc_certificateManagement;
   private var _proxy:Hpic4vc_providerProxy;
   private var serviceGuid:String;
   
   private static var _logger:ILogger = Log.getLogger("Hpic4vc_certificateManagementMediator");
   
   [Bindable]
   private var appModel:ApplicationDataModel = ApplicationDataModel.getInstance();
   
   [View]
   /**
	* The mediator's view.
	*/
   public function get view():Hpic4vc_certificateManagement {
	   return _view;
   }
   
   /** @private */
   public function set view(value:Hpic4vc_certificateManagement):void {
	   _view = value;
	   _proxy = new Hpic4vc_providerProxy();
	   _view._proxy = _proxy;
	   ChangeWatcher.watch(appModel,["isAuthorized"],handlerAuthorization);
	   requestData();
   }
   
   override protected function clearData():void {
	   _view = null;
   }
   
   override protected function requestData():void {
	   serviceGuid = getServerGuid();
	   _view.serviceGuid = serviceGuid;
	   if(ApplicationDataModel.isUserValidated())
	   {
		_view.isAuthorized = appModel.isAuthorized		   
	   }
	   else
	   {
	   _proxy.getUserInfo(serviceGuid, onGettingUserInfo);
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
   }
}
}