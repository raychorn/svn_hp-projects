/* Copyright 2012 Hewlett-Packard Development Company, L.P. */

package com.hp.asi.hpic4vc.ui.views {

//import assets.components.Hpic4vc_Header;

import assets.components.Hpic4vc_BaseMediator;

import com.hp.asi.hpic4vc.ui.Hpic4vc_providerProxy;
import com.hp.asi.hpic4vc.ui.model.FooterModel;
import com.hp.asi.hpic4vc.ui.model.HeaderModel;
import com.hp.asi.hpic4vc.ui.model.HealthModel;
import com.hp.asi.hpic4vc.ui.model.PageModel;
import com.hp.asi.hpic4vc.ui.model.TabModel;
import com.hp.asi.hpic4vc.ui.utils.Helper;
import com.vmware.core.model.IResourceReference;
import com.vmware.flexutil.events.MethodReturnEvent;

import flash.display.Loader;
import flash.events.EventDispatcher;
import flash.utils.setTimeout;

import mx.collections.ArrayCollection;
import mx.logging.ILogger;
import mx.logging.Log;

/**
 * The mediator for Hpic4vc_uiView
 */
public class Hpic4vc_manage_uiMediator extends Hpic4vc_BaseMediator {
   private var _view:Hpic4vc_manage_uiView;
   private var _proxy:Hpic4vc_providerProxy;
   private var _loader:Loader;
   //private var _header:Hpic4vc_Header;
   private var countTabs:int = 0;
   private var countHeader:int = 0;
   private var countFooter:int = 0;
   
   private static var _logger:ILogger = Log.getLogger("Hpic4vc_manage_uiMediator");
   
   [View]
   /**
	* The mediator's view.
	*/
   public function get view():Hpic4vc_manage_uiView {
	   return _view;
   }
   
   /** @private */
   public function set view(value:Hpic4vc_manage_uiView):void {
	   _view = value;
	   _proxy = new Hpic4vc_providerProxy();
   }
      
   override protected function refreshPageContent():void{
	   //Intentionally left blank so the refresh button does not refresh the entire
	   //UI.  Instead the refresh will be handled by the individual tabs themselves.
   }
   
   override protected function clearData():void {
	   _view = null;
   }
   
   override protected function requestData():void {
	//   _view.tabs = null;
	   _view.canvas.removeAllChildren();
	   _view.currentState = "DataUnavailable";
	   _logger.debug("Requesting HPIC4VC data.");
	   
	   if (_contextObject != null) {
		   
		   Helper._proxy = _proxy;
		   Helper._view = _view;
		   Helper._contextObject = _contextObject;
		   _view.header.visible = false;
		   _view.tabBar.visible = false;
		   _view.footer.visible = false;
		   _proxy.getHeader(_contextObject.uid, onGettingHeader, _contextObject);
		   _view._contextObject = _contextObject;
		//   _view.objReferenceId = _contextObject.uid;
		   
		   _proxy.getManageTabPages(_contextObject.uid, onGettingPages, _contextObject);
		   _proxy.getFooter(_contextObject.uid, onGettingFooter, _contextObject);
		   
		//   _view.tabInfo = null;
		   
		   if (tab_Clicked != null) {
		   	_view.tabInfo = tab_Clicked;
			tab_Clicked = null;
		   }
		//   else
		//   {
		//	   _view.tabInfo = null;
		//   }
	   } else {
		   _logger.warn("ContextObject is null, hence not requesting data.");
		   return;
	   }
   }
   
   private function onGettingPages(event:MethodReturnEvent):void {
	   if (_view != null && _contextObject != null) {
		   _view.tabBar.visible = true;
		   _logger.debug("Received HPIC4VC data in onGettingPages()");
		   
		   if (event == null) {
			   _view.noTabsFound = Helper.getString("noRecordsFound");
			   return;
		   } else if (event.error != null) {
			   if (event.error.toString().match("DeliveryInDoubt")) {
				   _logger.warn("DeliveryInDoubt exception occurred.  Count: " + countTabs);
				   // Re try to request data for not more than 2 times
				   if (countTabs < 2) {
					   countTabs ++;
					   _proxy.getManageTabPages(_contextObject.uid, onGettingPages, _contextObject);
					   return;
				   } else {
					   _view.tabsErrorMessage = event.error.message;
					   _view.currentState = "ErrorFound";
					   return;
				   }
			   } else {
				   _view.tabsErrorMessage = event.error.message;
				   _view.currentState = "ErrorFound";
				   return;
			   }
		   } else if (event.result == null) {
			   _view.noTabsFound = Helper.getString("noRecordsFound");
			   _view.currentState = "ErrorFound";
			   return;
		   } else if ((event.result as PageModel).errorMessage != null) {
			   _view.tabsErrorMessage = (event.result as PageModel).errorMessage;
			   _view.currentState = "ErrorFound";
			   return;
		   } else {
			   _view.noTabsFound = "";
			   _view.tabsErrorMessage = "";
			   _view.currentState = "DataAvailable";
		   }
		   
		   var page:PageModel = event.result as PageModel;
		   var tabs:ArrayCollection = page.tabs;
		   
		   // Sort the tabs as per the order specified.
		   Helper.sortArrayCollection(tabs, "order", true);
		   
		   _view.tabs = tabs;
		   _view.gettingStartTabModel =null;
		   _view.swfwTabModel =null;
		   _view.hostTabModel=null;
		   _view.networkingTabModel=null;
		   _view.infrastructureTabModel= null;
		   _view.clusterTabModel=null;
		   _view.storageTabModel=null;
		   
		   // Get the tabModels for displaying the corresponding tabs on default or clicking of More links from Overview page.
		   for (var i:int = 0; i < tabs.length; i++) 
		   {
			   var item:TabModel = page.tabs.getItemAt(i) as TabModel;
			   if (item.displayNameKey == "Getting_Started") {
				   item.order = i.toString();
				   _view.gettingStartTabModel = item;
			   } else if (item.displayNameKey == "Overview") {
				   item.order = i.toString();
				   _view.overviewTabModel = item;
			   } else if (item.displayNameKey == "Software_Firmware") {
				   item.order = i.toString();
				   _view.swfwTabModel = item;
			   } else if (item.displayNameKey == "Host") {
				   item.order = i.toString();
				   _view.hostTabModel = item;
			   } else if (item.displayNameKey == "Networking") {
				   item.order = i.toString();
				   _view.networkingTabModel = item;
			   } else if (item.displayNameKey == "Infrastructure") {
				   item.order = i.toString();
				   _view.infrastructureTabModel = item;
			   } else if (item.displayNameKey == "Storage") {
				   item.order = i.toString();
				   _view.storageTabModel = item;
			   } else if (item.displayNameKey == "Cluster") {
				   item.order = i.toString();
				   _view.clusterTabModel = item;
			   }
		   }
		   if(_view != null)
			   manageView = _view;
	   }
	   else {
		   _logger.warn("View and/or ContextObject is null.  Returning from onGettingResult()");
		   return;
	   }
   }
   
   private function onGettingHeader(event:MethodReturnEvent):void {
	   Helper.onGettingHeaderDetails(event);
   }
   
   private function onGettingFooter(event:MethodReturnEvent):void {
	   Helper.onGettingFooterDetails(event);
   }
}
}