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
public class Hpic4vc_uiMediator extends Hpic4vc_BaseMediator {
   private var _view:Hpic4vc_uiView;
   private var _proxy:Hpic4vc_providerProxy;
   private var _loader:Loader;
   //private var _header:Hpic4vc_Header;
   private var countTabs:int = 0;
   private var countHeader:int = 0;
   private var countFooter:int = 0;
   
   private static var _logger:ILogger = Log.getLogger("Hpic4vc_uiMediator");
   
   [View]
   /**
	* The mediator's view.
	*/
   public function get view():Hpic4vc_uiView {
	   return _view;
   }
   
   /** @private */
   public function set view(value:Hpic4vc_uiView):void {
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
	   _view.canvas.removeAllChildren();
	   _view.currentState = "DataUnavailable";
	   _logger.debug("Requesting HPIC4VC data.");
	   
	   if (_contextObject != null) {
		   _view.header.visible = false;
		   _view.tabBar.visible = false;
		   _proxy.getHeader(_contextObject.uid, onGettingHeader, _contextObject);
		   _view._contextObject = _contextObject;
		//   _proxy.getPages(_contextObject.uid, onGettingPages, _contextObject);
		   _proxy.getMonitorTabPages(_contextObject.uid, onGettingPages, _contextObject);
		   _proxy.getFooter(_contextObject.uid, onGettingFooter, _contextObject);
		   
		   if (tab_Clicked != null) {
			   _view.tabInfo = tab_Clicked;
			   tab_Clicked = null;
		   }
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
					   _proxy.getMonitorTabPages(_contextObject.uid, onGettingPages, _contextObject);
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
		   
		   // Get the tabModels for displaying the corresponding tabs on default or clicking of More links from Overview page.
		   for (var i:int = 0; i < tabs.length; i++) 
		   {
			   var item:TabModel = page.tabs.getItemAt(i) as TabModel;
			   if (item.displayNameKey == "Getting_Started") {
				   item.order = i.toString();
				   _view.gettingStartTabModel = item;
			   } else if (item.displayNameKey == "Tasks") {
				   item.order = i.toString();
				   _view.tasksTabModel = item;
			   } else if (item.displayNameKey == "Newsfeed") {
				   item.order = i.toString();
				   _view.newsfeedTabModel = item;
			   } else if (item.displayNameKey == "Health") {
				   item.order = i.toString();
				   _view.healthTabModel = item;
			   }
		   }
		   if(_view != null)
			   uiView = _view;
	   }
	   else {
		   _logger.warn("View and/or ContextObject is null.  Returning from onGettingResult()");
		   return;
	   }
   }

   private function onGettingHeader(event:MethodReturnEvent):void {
	   if (_view != null && _contextObject != null) {
		   _logger.debug("Received HPIC4VC data in onGettingHeader()");
	   
		   if (event == null) {
			   _view.noHeaderInfoFound = Helper.getString("noRecordsFound");
			   return;
		   } else if (event.error != null) {
			   if (event.error.toString().match("DeliveryInDoubt")) {
				   _logger.warn("DeliveryInDoubt exception occurred.  Count: " + countHeader);
				   // Re try to request data for not more than 2 times
				   if (countHeader < 2) {
					   countHeader ++;
					   _proxy.getHeader(_contextObject.uid, onGettingHeader, _contextObject);
					   return;
				   } else {
					   _view.headerErrorMessage = event.error.message;
					   _view.currentState = "DataAvailable";
					   return;
				   }
			   } else {
				   _view.headerErrorMessage = event.error.message;
				   _view.currentState = "DataAvailable";
				   return;
			   }
		   } else if (event.result == null) {
			   _view.noHeaderInfoFound = Helper.getString("noRecordsFound");
			   _view.currentState = "DataAvailable";
			   return;
		   } else if ((event.result as HeaderModel).errorMessage != null) {
			   _view.headerErrorMessage = (event.result as HeaderModel).errorMessage;
			   _view.currentState = "DataAvailable";
			   return;
		   } else {
			   _view.noHeaderInfoFound = "";
			   _view.headerErrorMessage = "";
			   _view.currentState = "DataAvailable";
		   }
		   
		   var headerInfo:HeaderModel = event.result as HeaderModel;
		   
		   createHeader(headerInfo);
	   }
	   else {
		   _logger.warn("View and/or ContextObject is null.  Returning from onGettingResult()");
		   return;
	   }
   }
   
   private function onGettingFooter(event:MethodReturnEvent):void {
	   if (_view != null && _contextObject != null) {
		   _view.footer.visible = true;
		   _logger.debug("Received HPIC4VC data in onGettingFooter()");
		   if (event == null) {
			   _view.noFooterInfoFound = Helper.getString("noRecordsFound");
			   return;
		   } else if (event.error != null) {
			   _logger.warn("DeliveryInDoubt exception occurred.  Count: " + countHeader);
			   if (event.error.toString().match("DeliveryInDoubt")) {
				   // Re try to request data for not more than 2 times
				   if (countFooter < 2) {
					   countFooter ++;
					   _proxy.getFooter(_contextObject.uid, onGettingFooter, _contextObject);
					   return;
				   } else {
					   _view.footerErrorMessage = event.error.message;
					   return;
				   }
			   } else {
				   _view.footerErrorMessage = event.error.message;
				   return;
			   }
		   } else if (event.result == null) {
			   _view.noFooterInfoFound = Helper.getString("noRecordsFound");
			   return;
		   } else if ((event.result as FooterModel).errorMessage != null) {
			   _view.footerErrorMessage = (event.result as FooterModel).errorMessage;
			   return;
		   } else {
			   _view.noFooterInfoFound = "";
			   _view.footerErrorMessage = "";
			   _view.currentState = "DataAvailable";
		   }
		   var footerInfo:FooterModel = event.result as FooterModel;
		   
		   _view.footerData = footerInfo;
	   }
	   else {
		   _logger.warn("View and/or ContextObject is null.  Returning from onGettingResult()");
		   return;
	   }
   }
   
   private function createHeader(headerInfo:HeaderModel):void {
	   if (_view != null && _contextObject != null) {
		   _view.header.visible = true;
		   // Checks for null and errors
		   if (headerInfo == null) {
			   _view.noHeaderInfoFound = Helper.getString("noInfoFound");
			   return;
		   } else if (headerInfo.errorMessage != null) {
			   _view.noHeaderInfoFound = headerInfo.errorMessage;
			   _logger.error(headerInfo.errorMessage);
			   return;
		   }
		   
		   // Set the objReference Id
		   _view.objReferenceId = _contextObject.uid;
		   
		   // Get Health status
		   var healthStatus:HealthModel = headerInfo.health as HealthModel;
		   _view.healthConsolidatedStatus = healthStatus.consolidatedStatus as String;	   
		   _view.warningCount = healthStatus.warnCount as String;
		   _view.errorCount = healthStatus.errorCount as String;
		   _view.okCount = healthStatus.okCount as String;
		   _view.informationCount = healthStatus.infoCount as String;
		   
		   // To get the I18Ned label for the VMware entity type.
		   var contextObjectType:String = _contextObject.type;
		   _view.objReferenceName = Helper.getString(contextObjectType) 
			   					 + " \"" 
								 + headerInfo.objReferenceName
								 + "\"";
		   
		   _view.productInfo = headerInfo.productInfo;
		   _view.enclosureInfo = headerInfo.enclosureInfo;
		   
		   // Limit the display of Tasks count to 10.  If not, the tooltip would be too large!! 
		   if (headerInfo.tasks.length > 10) {
			   var tasksArrayCollection:ArrayCollection = new ArrayCollection();
			   for (var i:int = 0; i < 10; i++) 
			   {
				   tasksArrayCollection.addItem(headerInfo.tasks.getItemAt(i));
			   }
			   _view.tasksHoverValue = tasksArrayCollection;
		   } else{
		 	   _view.tasksHoverValue = headerInfo.tasks as ArrayCollection;
		   }
	
		   _view.actionsCollection = headerInfo.actions as ArrayCollection;
		   
			actionsMenu = headerInfo.actions as ArrayCollection;
		   
		   _view.configurations = headerInfo.configurations as ArrayCollection;
		   
		   _view.helpUrl = headerInfo.helpUrl as String;
		   
		   _view.refreshList = headerInfo.refresh as ArrayCollection;
		   
		   _view.showRefreshHover = headerInfo.showRefreshHover as Boolean;
	   } else {
		   _logger.warn("View and/or ContextObject is null.  Returning from createHeader()");
		   return;
	   }
   }
}
}