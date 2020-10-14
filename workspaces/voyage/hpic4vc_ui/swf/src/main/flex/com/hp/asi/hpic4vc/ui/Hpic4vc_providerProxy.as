/* Copyright 2012 Hewlett-Packard Development Company, L.P.  */
package com.hp.asi.hpic4vc.ui
{
	import assets.components.ModelLocator;
	
	import com.vmware.flexutil.proxies.BaseProxy;
	
	import mx.controls.Alert;

	
	public class Hpic4vc_providerProxy extends BaseProxy
	{
		
		private var model:ModelLocator = ModelLocator.getInstance();
		
		// channelUri uses the Web-ContextPath defined in MANIFEST.MF
		// A secure AMF channel is required because vSphere Web Client uses https
		private static const CHANNEL_URI:String =
			"/" + Hpic4vc_ui.contextPath + "/messagebroker/";

		public function Hpic4vc_providerProxy(destination:String = "Hpic4vc_provider",
											  contextPath:String = "hpic4vc_ui") {
			super(destination, CHANNEL_URI + model.getChannel());
		}
		
		public function getHeader(objReferenceId:String, callback:Function, context:Object=null):void {
			callService("getHeader", [objReferenceId], callback, context);
		}
		
		public function getFooter(objReferenceId:String, callback:Function, context:Object=null):void {
			callService("getFooter", [objReferenceId], callback, context);
		}
		
		public function getSoftwareFirmwareSummary(objReferenceId:String, callback:Function,  context:Object=null):void {
			//model.myTrace += "calling getSoftwareFirmwareSummary, \n"
			callService("getSoftwareFirmwareSummary", [objReferenceId], callback, context);
		}
		
		public function getSoftwareFirmwareDetail(objReferenceId:String, callback:Function,  context:Object=null):void {
			//model.myTrace += "calling getSoftwareFirmwareDetials, \n"
			callService("getSoftwareFirmwareDetail", [objReferenceId], callback, context);
		}
		
		public function getNewsFeed(objReferenceId:String, callback:Function,  context:Object=null):void {
			callService("getNewsFeed", [objReferenceId], callback, context);
		}
		
		public function getHealthDetails(objReferenceId:String, callback:Function,  context:Object=null):void {
			callService("getHealthDetails", [objReferenceId], callback, context);
		}
		
		public function getNewsDetails(objReferenceId:String, callback:Function,  context:Object=null):void {
			callService("getNewsDetails", [objReferenceId], callback, context);
		}
		
		public function getTaskDetails(objReferenceId:String, callback:Function,  context:Object=null):void {
			callService("getTaskDetails", [objReferenceId], callback, context);
		}
		
		public function getPages(objReferenceId:String, callback:Function,  context:Object=null):void {
			callService("getPages", [objReferenceId], callback, context);
		}
		
		public function getMonitorTabPages(objReferenceId:String, callback:Function,  context:Object=null):void {
			callService("getMonitorTabPages", [objReferenceId], callback, context);
		}
		
		public function getManageTabPages(objReferenceId:String, callback:Function,  context:Object=null):void {
			callService("getManageTabPages", [objReferenceId], callback, context);
		}
		
		public function getConfigurationLinks(objReferenceId:String, callback:Function,  context:Object=null):void {
			callService("getConfigurationLinks", [objReferenceId], callback, context);
		}
		
		public function getProductHelpPages(objReferenceId:String, callback:Function,  context:Object=null):void {
			callService("getProductHelpPages", [objReferenceId], callback, context);
		}
		
		public function getVCCredentials(objReferenceId:String, callback:Function,  context:Object=null):void {
			callService("getVCCredentials", [objReferenceId], callback, context);
		}
		
		public function addVCCredential(objReferenceId:String, host:String, username:String, password:String, callback:Function):void {
			callService("addVCCredential", [objReferenceId, host, username, password], callback);
		}
		
		public function editVCCredential(objReferenceId:String, host:String, username:String, password:String, id:String, callback:Function):void {
			callService("editVCCredential", [objReferenceId, host, username, password, id], callback);
		}
		
		public function deleteVCCredential(objReferenceId:String, host:String, username:String, id:String, callback:Function):void {
			callService("deleteVCCredential", [objReferenceId, host, username, id], callback);
		}
		
		public function getHPOneViewCredentials(objReferenceId:String, callback:Function,  context:Object=null):void {
			callService("getHPOneViewCredentials", [objReferenceId], callback, context);
		}
		
		public function addHPOneViewCredential(objReferenceId:String, host:String, username:String, password:String, callback:Function):void {
			callService("addHPOneViewCredential", [objReferenceId, host, username, password], callback);
		}
		
		public function editHPOneViewCredential(objReferenceId:String, host:String, username:String, password:String, id:String, callback:Function):void {
			callService("editHPOneViewCredential", [objReferenceId, host, username, password, id], callback);
		}
		
		public function deleteHPOneViewCredential(objReferenceId:String, host:String, username:String, id:String, callback:Function):void {
			callService("deleteHPOneViewCredential", [objReferenceId, host, username, id], callback);
		}
		
		public function generateSelfSignedCertificate(objReferenceId:String, args:Array, callback:Function):void {
			callService("generateSelfSignedCertificate", [objReferenceId, args], callback);
		}
		
		public function getGenerateSelfSignedHelp(objReferenceId:String, callback:Function):void {
			callService("getGenerateSelfSignedHelp", [objReferenceId], callback);
		}
		
		public function generateSignedRequest(objReferenceId:String, args:Array, callback:Function):void {
			callService("generateSignedRequest", [objReferenceId, args], callback);
		}
		
		public function getGenerateSignedRequestHelp(objReferenceId:String, callback:Function):void {
			callService("getGenerateSignedRequestHelp", [objReferenceId], callback);
		}
		
		public function installSignedCertificate(objReferenceId:String, certificate:String, callback:Function):void {
			callService("installSignedCertificate", [objReferenceId, certificate], callback);
		}
		
		public function getInstalledCertificate(objReferenceId:String, callback:Function):void {
			callService("getInstalledCertificate", [objReferenceId], callback);
		}
		
		public function getInstallCertificateHelp(objReferenceId:String, callback:Function):void {
			callService("getInstallCertificateHelp", [objReferenceId], callback);
		}
		
		public function getProductAbout(objReferenceId:String, callback:Function, context:Object=null):void {
			callService("getProductAbout", [objReferenceId], callback);
		}
		
		public function getSummaryPortlets(objReferenceId:String, callback:Function,  context:Object=null):void {
			//model.myTrace += "calling getSummaryPortlets \n"
			callService("getSummaryPortlets", [objReferenceId], callback, context);
		}
		
		public function getPluginHostName(objReferenceId:String, callback:Function):void {
			callService("getPluginHostName", [objReferenceId], callback);
		}
		
		public function getUserInfo(objReferenceId:String, callback:Function):void {
			callService("getUserInfo", [objReferenceId], callback);
		}
		
		public function getTaskSummary(objReferenceId:String, callback:Function):void {
			callService("getTaskSummary", [objReferenceId], callback);
		}
		
		public function getICServerProvisioningHelp(objReferenceId:String, callback:Function):void {
			callService("getICServerProvisioningHelp", [objReferenceId], callback);
		}
		public function getNetworkPreferenceHelp(objReferenceId:String, callback:Function):void {
			callService("getNetworkPreferenceHelp", [objReferenceId], callback);
		}
	}
}