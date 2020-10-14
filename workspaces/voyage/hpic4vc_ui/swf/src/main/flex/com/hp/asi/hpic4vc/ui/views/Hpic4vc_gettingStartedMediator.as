/* Copyright 2012 Hewlett-Packard Development Company, L.P. */

package com.hp.asi.hpic4vc.ui.views {

import assets.components.Hpic4vc_BaseMediator;

import com.hp.asi.hpic4vc.ui.Hpic4vc_providerProxy;
import com.hp.asi.hpic4vc.ui.model.ConfigurationListModel;
import com.hp.asi.hpic4vc.ui.model.MenuModel;
import com.vmware.flexutil.events.MethodReturnEvent;

import mx.logging.ILogger;
import mx.logging.Log;

/**
 * The mediator for Hpic4vc_gettingStarted
 */
public class Hpic4vc_gettingStartedMediator extends Hpic4vc_BaseMediator
{
   private var _view:Hpic4vc_gettingStarted;
   private var _proxy:Hpic4vc_providerProxy;
   private var userName:String;
   private var serviceGuid:String;
   private var count:int = 0;
   
   private static var _logger:ILogger = Log.getLogger("Hpic4vc_gettingStartedMediator");
   
   [View]
   /**
	* The mediator's view.
	*/
   public function get view():Hpic4vc_gettingStarted {
	   return _view;
   }
   
   /** @private */
   public function set view(value:Hpic4vc_gettingStarted):void {
	   _view = value;
	   _proxy = new Hpic4vc_providerProxy();
	   requestData();
   }
   
   override protected function clearData():void {
	   _view = null;
   }
   
   override protected function requestData():void {
	   serviceGuid = getServerGuid();
	   _proxy.getProductHelpPages(serviceGuid, onGettingProductHelpPages);
	   _proxy.getConfigurationLinks(serviceGuid, onGettingConfigurationLinks);
   }

   private function onGettingProductHelpPages(event:MethodReturnEvent):void {
	   if (_view != null) {
		   _logger.warn("Received HPIC4VC data in onGettingProductHelpPages()");
		   if (event == null || event.result == null) {
			   _logger.debug("The Product helplinks list object returned is null");
			   return;
		   }
		   else if (event.error != null) {
			   if (event.error.toString().match("DeliveryInDoubt")) {
				   _logger.warn("DeliveryInDoubt exception occurred.  Count: " + count.toString());
				   // Re try to request data for not more than 2 times
				   if (count < 2) {
					   count ++;
					   requestData();
					   return;
				   } else {
					   _view.errorMessageProductLinks = event.error.message;
					   return;
				   }
			   } else {
				   _view.errorMessageProductLinks = event.error.message;
				   return;
			   }
		   } 
		   else if ((event.result as MenuModel).errorMessage != null) {
			   _view.errorMessageProductLinks  = (event.result as MenuModel).errorMessage;
			   return;
		   } 
		   
		   var result:MenuModel = event.result as MenuModel;
		   if (_view)
			   _view.helpLinksList = result.menuItems;
	   }
	   else {
		   _logger.warn("View is null.  Returning from onGettingProductHelpPages().");
		   return;
	   }
   }
   
   private function onGettingConfigurationLinks(event:MethodReturnEvent):void {
	   if (_view != null) {
		   _logger.warn("Received HPIC4VC data in onGettingConfigurationLinks()");
		   if (event == null || event.result == null) {
			   _logger.debug("The ConfigurationListModel object returned is null");
		   }
		   else if (event.error != null) {
			   if (event.error.toString().match("DeliveryInDoubt")) {
				   _logger.warn("DeliveryInDoubt exception occurred.  Count: " + count.toString());
				   // Re try to request data for not more than 2 times
				   if (count < 2) {
					   count ++;
					   requestData();
					   return;
				   } else {
					   _view.errorMessageConfigLinks = event.error.message;
					   return;
				   }
			   } else {
				   _view.errorMessageConfigLinks = event.error.message;
				   return;
			   }
		   } 
		   else if ((event.result as ConfigurationListModel).errorMessage != null) {
			   _view.errorMessageConfigLinks = (event.result as ConfigurationListModel).errorMessage;
		   } 
		   
		   var result:ConfigurationListModel = event.result as ConfigurationListModel;
		   if (null != result) {
		       if (_view )
			       _view.configurationLinksList = result.configurationLinks;
		   }
	   }
	   else {
		   _logger.warn("View is null.  Returning from onGettingConfigurationLinks().");
		   return;
	   }
   }
  
}
}