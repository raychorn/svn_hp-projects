/* Copyright 2012 Hewlett-Packard Development Company, L.P. */

package com.hp.asi.hpic4vc.ui.views {

import assets.components.Hpic4vc_BaseMediator;

import com.hp.asi.hpic4vc.ui.Hpic4vc_providerProxy;
import com.hp.asi.hpic4vc.ui.model.PortletModel;
import com.hp.asi.hpic4vc.ui.utils.Helper;
import com.vmware.core.model.IResourceReference;
import com.vmware.flexutil.events.MethodReturnEvent;

import flash.events.EventDispatcher;

import mx.collections.ArrayCollection;
import mx.logging.ILogger;
import mx.logging.Log;

/**
 * The mediator for the Hpic4vc_summaryView view.
 */
public class Hpic4vc_summaryViewMediator extends Hpic4vc_BaseMediator {

   private var _view:Hpic4vc_summaryView;
   private var _proxy:Hpic4vc_providerProxy;
   private var count:int = 0;
   
   private var baseMediator:Hpic4vc_BaseMediator = new Hpic4vc_BaseMediator();
   
   private static var _logger:ILogger = Log.getLogger("Hpic4vc_summaryViewMediator");
   
   [View]
   /**
    * The mediator's view.
    */
   public function get view():Hpic4vc_summaryView {
      return _view;
   }

   /** @private */
   public function set view(value:Hpic4vc_summaryView):void {
      _view = value;
	  _proxy = new Hpic4vc_providerProxy();
   }
   
   override protected function clearData():void {
	   _view = null;
   }
   
   override protected function requestData():void {
	   _logger.debug("Requesting HPIC4VC data.");
	   if (_contextObject != null) {
		   _view.contextObject = _contextObject;
		   _proxy.getSummaryPortlets(_contextObject.uid, onGettingSummaryPortlets);
		//   getMoreLinkURL();
		   //Setting the tab clicked to navigate to the overview tab under Manage
		//   baseMediator.setTabClickedInfo("overviewTab");
		   _view.hpic4vc_manage_uiView_id = baseMediator.getManageMoreLinkURLs(_contextObject);
	   } else {
		   _logger.warn("ContextObject is null, hence not requesting data.");
		   return;
	   }
   }
   
 /*  private function getMoreLinkURL():void {
		if (_contextObject.type == "HostSystem") {
			_view.hpic4vc_uiView_id = "com.hp.asi.hpic4vc.ui.host.monitor";
		} else if (_contextObject.type == "VirtualMachine") {
			_view.hpic4vc_uiView_id = "com.hp.asi.hpic4vc.ui.vm.monitor";
		} else if (_contextObject.type == "Datastore") {
			_view.hpic4vc_uiView_id = "com.hp.asi.hpic4vc.ui.datastore.monitor";
		} else if (_contextObject.type == "ClusterComputeResource") {
			_view.hpic4vc_uiView_id = "com.hp.asi.hpic4vc.ui.cluster.monitor";
		}
   }*/
   
   private function onGettingSummaryPortlets(event:MethodReturnEvent):void {
	   if (_view != null) {
		   _logger.debug("Received HPIC4VC data in onGettingSummaryPortlets()");
	   
		   if (event == null) {
			   _view.noInfoFoundLabel = Helper.getString("noPortletsFound");
			   return;
		   } else if (event.error != null) {
			   _logger.warn("DeliveryInDoubt exception occurred.  Count: " + count);
			   if (event.error.toString().match("DeliveryInDoubt")) {
				   // Re try to request data for not more than 2 times
				   if (count < 2) {
					   count ++;
					   _proxy.getSummaryPortlets(_contextObject.uid, onGettingSummaryPortlets);
					   return;
				   } else {
					   _view.errorMessage = event.error.message;
					   return;
				   }
			   } else {
				   _view.errorMessage = event.error.message;
				   return;
			   }
		   } else if (event.result == null) {
			   _view.noInfoFoundLabel = Helper.getString("noPortletsFound");
			   return;
		   } else if ((event.result as PortletModel).errorMessage != null) {
			   _view.errorMessage = (event.result as PortletModel).errorMessage;
			   return;
		   } else {
			   _view.noInfoFoundLabel = "";
			   _view.errorMessage = "";
		   }
		   
		   var summaryPage:PortletModel = event.result as PortletModel;
		   if (summaryPage != null) {
			   _view.summaryPortletList = summaryPage.portlets as ArrayCollection;
		   }
	   }
	   else {
		   _logger.warn("View is null.  Returning from onGettingResult()");
		   return;
	   }
   }
}
}