/* Copyright 2012 Hewlett-Packard Development Company, L.P.  */
package com.hp.asi.hpic4vc.ui
{
	import assets.components.ModelLocator;
	
	import com.vmware.flexutil.proxies.BaseProxy;

	
	public class Hpic4vc_storage_providerProxy extends BaseProxy
	{
		
		private var model:ModelLocator = ModelLocator.getInstance();
		// channelUri uses the Web-ContextPath defined in MANIFEST.MF
		// A secure AMF channel is required because vSphere Web Client uses https
		private static const CHANNEL_URI:String =
			"/" + Hpic4vc_ui.contextPath + "/messagebroker/";

		public function Hpic4vc_storage_providerProxy(destination:String = "Hpic4vc_storage_provider",
											  contextPath:String = "hpic4vc_ui") {
			super(destination, CHANNEL_URI + model.getChannel());
		}
		
		public function getHbas(objReferenceId:String, callback:Function,  context:Object=null):void {
			callService("getHbas", [objReferenceId], callback, context);
		}
		
		public function getPaths(objReferenceId:String, callback:Function,  context:Object=null):void {
			callService("getPaths", [objReferenceId], callback, context);
		}
		
		public function getReplications(objReferenceId:String, callback:Function,  context:Object=null):void {
			callService("getReplications", [objReferenceId], callback, context);
		}
		
		public function getVirtualDisks(objReferenceId:String, callback:Function,  context:Object=null):void {
			callService("getVirtualDisks", [objReferenceId], callback, context);
		}
		
		public function getVmsToVolumes(objReferenceId:String, callback:Function,  context:Object=null):void {
			callService("getVmsToVolumes", [objReferenceId], callback, context);
		}
		
		public function getStorageVolumes(objReferenceId:String, callback:Function,  context:Object=null):void {
			callService("getStorageVolumes", [objReferenceId], callback, context);
		}
		
		public function getSummaryPortletData(objReferenceId:String, callback:Function,  context:Object=null):void {
			callService("getSummaryPortletData", [objReferenceId], callback, context);
		}
		
		public function getStorageOverview(objReferenceId:String, callback:Function, context:Object=null):void {
			callService("getStorageOverview", [objReferenceId], callback, context);
		}
		
		public function getFullSummary(objReferenceId:String, callback:Function, context:Object=null):void {
			callService("getFullSummary", [objReferenceId], callback, context);
		}
		
		public function getRefreshCacheStatus(objReferenceId:String, callback:Function,  context:Object=null):void {
			callService("getRefreshCacheStatus", [objReferenceId], callback, context);
		}
		
		public function startRefreshCache(objReferenceId:String, callback:Function,  context:Object=null):void {
			callService("startRefreshCache", [objReferenceId], callback, context);
		}
		
		public function restartRefreshCache(objReferenceId:String, callback:Function,  context:Object=null):void {
			callService("restartRefreshCache", [objReferenceId], callback, context);
		}
		
		public function cancelRefreshCache(objReferenceId:String, callback:Function,  context:Object=null):void {
			callService("cancelRefreshCache", [objReferenceId], callback, context);
		}
		
		public function getProvisioningTasks(objReferenceId:String, callback:Function,  context:Object=null):void {
			callService("getProvisioningTasks", [objReferenceId], callback, context);
		}

		public function getStoragePages(objReferenceId:String, callback:Function,  context:Object=null):void {
			callService("getStoragePages", [objReferenceId], callback, context);
		}
		
		public function getStorageSystemHeader(objReferenceId:String, arrayUid:String, callback:Function, context:Object=null):void {
			callService("getStorageSystemHeader", [objReferenceId, arrayUid], callback, context);
		}
		
		public function getDAMHelp(objReferenceId:String, callback:Function, context:Object=null):void {
			callService("getDAMHelp", [objReferenceId], callback, context);
		}
		
		public function getStorageSystemOverview(objReferenceId:String, arrayUid:String, callback:Function, context:Object=null):void {
			callService("getStorageSystemOverview", [objReferenceId, arrayUid], callback, context);
		}
		
		public function getStorageSystemDetails(objReferenceId:String, arrayUid:String, callback:Function, context:Object=null):void {
			callService("getStorageSystemDetails", [objReferenceId, arrayUid], callback, context);
		}
		
		public function getStorageSetDetails(objReferenceId:String, arrayUid:String, callback:Function, context:Object=null):void {
			callService("getStorageSetDetails", [objReferenceId, arrayUid], callback, context);
		}
		
		public function getStorageSystemFooter(objReferenceId:String, arrayUid:String, callback:Function, context:Object=null):void {
			callService("getStorageSystemFooter", [objReferenceId, arrayUid], callback, context);
		}
	}
}