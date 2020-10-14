/* Copyright 2012 Hewlett-Packard Development Company, L.P.  */
package com.hp.asi.hpic4vc.ui
{
	import assets.components.ModelLocator;
	
	import com.vmware.flexutil.proxies.BaseProxy;
	
	import flash.utils.ByteArray;
	

	
	public class Hpic4vc_server_providerProxy extends BaseProxy
	{
		
		// channelUri uses the Web-ContextPath defined in MANIFEST.MF
		// A secure AMF channel is required because vSphere Web Client uses https
		private var model:ModelLocator = ModelLocator.getInstance();
		
		private static const CHANNEL_URI:String =
			"/" + Hpic4vc_ui.contextPath + "/messagebroker/";

		public function Hpic4vc_server_providerProxy(destination:String = "Hpic4vc_server_provider",
											         contextPath:String = "hpic4vc_ui") {
			//trace(model.getChannel());
			super(destination, CHANNEL_URI+model.getChannel());
		}
		
		public function getHostSummary(objReferenceId:String, callback:Function,  context:Object=null):void {
			//model.myTrace += "calling getHostSummaryData, \n"
			callService("getHostSummaryData", [objReferenceId], callback, context);
		}

		public function getHostDetail(objReferenceId:String, callback:Function,  context:Object=null):void {
			callService("getHostDetailData", [objReferenceId], callback, context);
		}

		public function getInfrastructureSummary(objReferenceId:String, callback:Function,  context:Object=null):void {
			//model.myTrace += "calling getInfrastructureSummaryData, \n"
			callService("getInfrastructureSummaryData", [objReferenceId], callback, context);
		}

		public function getInfrastructureDetail(objReferenceId:String, callback:Function,  context:Object=null):void {
			callService("getInfrastructureDetailData", [objReferenceId], callback, context);
		}
		
		public function getNetworkSummary(objReferenceId:String, callback:Function,  context:Object=null):void {
			//model.myTrace += "calling getNetworkSummaryData, \n"
			callService("getNetworkSummaryData", [objReferenceId], callback, context);
		}
		
		public function getNetworkDetail(objReferenceId:String, callback:Function,  context:Object=null):void {
			callService("getNetworkDetailData", [objReferenceId], callback, context);
		}

        public function getServerCredentials(objReferenceId:String, callback:Function,  context:Object=null):void {
            callService("getServerCredentials", [objReferenceId], callback, context);
        }

        public function updateServerCredentials(objReferenceId:String, data:String, callback:Function,  context:Object=null):void {
            callService("updateServerCredentials", [objReferenceId, data], callback, context);
        }

        public function getServerConfiguration(objReferenceId:String, callback:Function,  context:Object=null):void {
            callService("getServerConfiguration", [objReferenceId], callback, context);
        }

        public function updateServerConfiguration(objReferenceId:String, data:String, callback:Function,  context:Object=null):void {
            callService("updateServerConfiguration", [objReferenceId, data], callback, context);
        }

        public function getCommStatus(objReferenceId:String, callback:Function,  context:Object=null):void {
            callService("getCommStatus", [objReferenceId], callback, context);
        }
		
		public function getHostSummaryPortletData(objReferenceId:String, callback:Function,  context:Object=null):void {
			callService("getHostSummaryPortletData", [objReferenceId], callback, context);
		}
		
		public function getHostProperties(objReferenceId:String, callback:Function,  context:Object=null):void{
			callService("getHostProperties", [objReferenceId], callback, context);
		}
		
		public function updateHostPropertiesPassword(objReferenceId:String, data:String, callback:Function,  context:Object=null):void{
			callService("updateHostPropertiesPassword", [objReferenceId,data], callback, context);
		}
		
		
		public function getClusterSummaryPortletData(objReferenceId:String, callback:Function,  context:Object=null):void {
			callService("getClusterSummaryPortletData", [objReferenceId], callback, context);
		}
		
		public function getClusterDetailPortletData(objReferenceId:String, callback:Function,  context:Object=null):void {
			callService("getClusterDetailPortletData", [objReferenceId], callback, context);
		}
		
		public function getClusterInfrastructureSummary(objReferenceId:String, callback:Function,  context:Object=null):void {
			callService("getClusterInfrastructureSummaryData", [objReferenceId], callback, context);
		}
		
		public function getClusterInfrastructureDetail(objReferenceId:String, callback:Function, context:Object=null):void {
			callService("getClusterInfrastructureDetailData", [objReferenceId], callback, context);
		}
		
		public function getClusterProperties(objReferenceId:String, callback:Function,  context:Object=null):void {
			callService("getClusterProperties", [objReferenceId], callback, context);
		}
		
		public function updateClusterProperties(objReferenceId:String, data:String, callback:Function,  context:Object=null):void{
			callService("updateClusterProperties", [objReferenceId,data], callback, context);
		}

        public function getHostConfiguration(objReferenceId:String, callback:Function,  context:Object=null):void {
			callService("getHostConfiguration", [objReferenceId], callback, context);
		}
        
		public function uploadSmartComponents(objReferenceId:String, data:String,uploadedBinary:ByteArray,callback:Function,  context:Object=null):void {
			callService("uploadSmartComponents", [objReferenceId,data,uploadedBinary], callback, context);
		}		
		
		public function updateSwitchType(objReferenceId:String, data:String, callback:Function,  context:Object=null):void {
			callService("updateSwitchType", [objReferenceId, data], callback, context);
		}
		public function updateNetwork(objReferenceId:String, data:String, callback:Function,  context:Object=null):void {
			callService("updateNetwork", [objReferenceId, data], callback, context);
		}
		public function getNetworkDiagramData(objReferenceId:String, callback:Function,  context:Object=null):void {
			callService("getNetworkDiagramData", [objReferenceId], callback, context);
		}
		
		public function getDeploymentConfiguration(objReferenceId:String, callback:Function,  context:Object=null):void {
			callService("getDeploymentConfiguration", [objReferenceId], callback, context);
		}
		
		public function updateDeploymentConfiguration(objReferenceId:String,data:String, callback:Function,  context:Object=null):void {
			callService("updateDeploymentConfiguration", [objReferenceId, data], callback, context);
		}
		
		public function getUploadedSoftwareComponents(objReferenceId:String, callback:Function,  context:Object=null):void {
			callService("getUploadedSoftwareComponents", [objReferenceId], callback, context);
		}
		public function deleteSoftwareComponent(objReferenceId:String, data:String, callback:Function,  context:Object=null):void{
			callService("deleteSoftwareComponent", [objReferenceId,data], callback, context);
		}
		public function getSwitchTypeHostConfiguration(objReferenceId:String, callback:Function,  context:Object=null):void {
			callService("getSwitchTypeHostConfiguration", [objReferenceId], callback, context);
		}
		
		public function getAuth(objReferenceId:String,userName:String,password:String,host:String, callback:Function,  context:Object=null):void {
			callService("getAuth", [objReferenceId, userName,password,host], callback, context);
		}
		
		public function getServers(objReferenceId:String,auth:String, host:String,callback:Function, context:Object=null):void{
			callService("getServers", [objReferenceId, auth,host], callback, context);
		}
		
		public function getBuildPlans(objReferenceId:String,auth:String, host:String,callback:Function, context:Object=null):void{
			callService("getBuildPlans", [objReferenceId, auth,host],callback, context);
		}
		
		public function getSmartComponentUpdateStatusMessages(objReferenceId:String,callback:Function, context:Object=null):void{
			callService("getSmartComponentUpdateStatusMessages", [objReferenceId], callback, context);
		}
		
		public function updateToggleUUID(objReferenceId:String,callback:Function, context:Object=null):void{
			callService("updateToggleUUID", [objReferenceId], callback, context);
		}
		
		public function submitBuildPlans(objReferenceId:String,selectedServersXML:String,buildUri:String,auth:String, host:String, callback:Function, context:Object=null):void{
			
			callService("submitBuildPlans", [objReferenceId,selectedServersXML,buildUri,auth,host], callback, context);
		}
		
		public function getRefreshedNetwork(objReferenceId:String, callback:Function,  context:Object=null):void {
			callService("getRefreshedNetwork", [objReferenceId], callback, context);
		}
		public function getNetworkMismatch(objReferenceId:String, callback:Function,  context:Object=null):void 			
		{
			callService("getNetworkMismatch", [objReferenceId], callback, context);
		}		
		
		public function updateFirmwareComponents(objReferenceId:String, packageURL:String,options:Array,callback:Function,context:Object=null):void {
			callService("updateFirmwareComponents", [objReferenceId,packageURL,options], callback, context);
		}
		
		public function deleteSelectedComponentJob(objReferenceId:String, data:String, callback:Function,  context:Object=null):void{
			callService("deleteSelectedComponentJob", [objReferenceId,data], callback, context);
		}
				
		public function createTaskForServerProvisioningJob(objReferenceId:String, data:String, callback:Function , context:Object=null):void{
			callService("createTaskForServerProvisioningJob",[objReferenceId,data],callback , context);
		}
		public function applyHostMismatchActions(objReferenceId:String, data:String, callback:Function,  context:Object=null):void {
			callService("applyHostMismatchActions", [objReferenceId, data], callback, context);
		}
		public function getClusterMismatch(objReferenceId:String, callback:Function,  context:Object=null):void 			
		{
			callService("getClusterMismatch", [objReferenceId], callback, context);
		}		
		public function setPreferenceHost(objReferenceId:String, data:String,clusterUuid:String, callback:Function,  context:Object=null):void {
			callService("setPreferenceHost", [objReferenceId, data,clusterUuid],callback, context);
		}		
		public function getNetworkMismatchHost(objReferenceId:String,host:String,callback:Function, context:Object=null):void{
			callService("getNetworkMismatchHost", [objReferenceId, host],callback, context);
		}
		
		public function getFirmwareJobsForCluster(objReferenceId:String, callback:Function,  context:Object=null):void {
			callService("getFirmwareJobsForCluster", [objReferenceId], callback, context);
		}
		
		public function updateClusterFirmwareComponents(objReferenceId:String, packageURL:String,options:Array,hosts:Array,callback:Function,context:Object=null):void {
			callService("updateClusterFirmwareComponents", [objReferenceId,packageURL,options,hosts], callback, context);
		}
		
		public function deleteClusterFirmwareJob(objReferenceId:String,data:String,hosts:Array,callback:Function,context:Object=null):void{
			callService("deleteClusterFirmwareJob", [objReferenceId,data,hosts], callback, context);
		}
		
		public function getPowerControlStatus(objReferenceId:String,callback:Function, context:Object=null):void{
			callService("getPowerControlStatus", [objReferenceId], callback, context);
		}
		
		public function updatePowerControl(objReferenceId:String,callback:Function, context:Object=null):void{
			callService("updatePowerControl", [objReferenceId], callback, context);
		}
		public function getNetworkMismatchSummary(objReferenceId:String, callback:Function,  context:Object=null):void 			
		{
			callService("getNetworkMismatchSummary", [objReferenceId], callback, context);
		}	
		public function getClusterSummaryMismatch(objReferenceId:String, callback:Function,  context:Object=null):void 			
		{
			callService("getClusterSummaryMismatch", [objReferenceId], callback, context);
		}	
		public function rediscoverNode(objReferenceId:String,callback:Function, context:Object=null):void{
			callService("putRediscoverNode", [objReferenceId], callback, context);
		}
		
		public function getObjReferenceName(objReferenceId:String, callback:Function, context:Object=null):void {
			callService("getObjReferenceName", [objReferenceId,objReferenceId], callback, context);
		}
		public function setreferenceHostLevel(objReferenceId:String, data:String, callback:Function,  context:Object=null):void {
			callService("setreferenceHostLevel", [objReferenceId, data],callback, context);
		}	
		public function getVDSVersion(objReferenceId:String, callback:Function,  context:Object=null):void {
			callService("getVDSVersion", [objReferenceId], callback, context);
		}
		public function updateVDSversions(objReferenceId:String, data:String, callback:Function,  context:Object=null):void {
			callService("updateVDSversions", [objReferenceId, data], callback, context);
		}
		public function getMigrateManagement(objReferenceId:String, callback:Function,  context:Object=null):void {
			callService("getMigrateManagement", [objReferenceId], callback, context);
		}
		public function updateVDSMigrateManagement(objReferenceId:String, data:String, callback:Function,  context:Object=null):void {
			callService("updateVDSMigrateManagement", [objReferenceId, data], callback, context);
		}
	}
}