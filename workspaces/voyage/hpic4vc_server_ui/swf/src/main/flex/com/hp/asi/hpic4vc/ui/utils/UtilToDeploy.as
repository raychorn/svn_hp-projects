package com.hp.asi.hpic4vc.ui.utils
{
	import com.hp.asi.hpic4vc.ui.model.DeploymentDetailModel;
	import com.hp.asi.hpic4vc.ui.utils.networkDiagram.Images;
	import com.hp.asi.hpic4vc.ui.vo.DeploymentVO;
	import com.hp.asi.hpic4vc.ui.vo.NicsVO;
	import com.hp.asi.hpic4vc.ui.vo.ServerNodesData;
	
	import mx.collections.ArrayCollection;
	import mx.controls.Alert;

	
	public class UtilToDeploy
	{
		
		[Bindable]
		public static var targetServer:String;
		
		public function UtilToDeploy()
		{
		}
		
		
		
		
		public static function validateHostManagementData(deploymentDetailModel:DeploymentDetailModel):Boolean
		{
			var validData:Boolean = false;
			var infoTitle:String ;
			
			
			if (missingStaticIPAddress(deploymentDetailModel))
			{
				infoTitle = Helper.getString('MISSING_INFO') + " (" + targetServer + ")";
				Alert.show(Helper.getString('STATIC_IP_MISSING_INFO'),infoTitle);
			}
			else if (missingNetMask(deploymentDetailModel))
			{
				infoTitle = Helper.getString('MISSING_INFO') + " (" + targetServer + ")";
				Alert.show(Helper.getString('NETMASK_MISSING_INFO'),infoTitle);
			}
			else if(missingHostNameData(deploymentDetailModel))
			{
				infoTitle = Helper.getString('MISSING_INFO') + " (" + targetServer + ")";
				Alert.show(Helper.getString('HOSTNAME_MISSING_INFO'),infoTitle);
			}
			else if(missingGatewayAddress(deploymentDetailModel))
			{
				infoTitle = Helper.getString('MISSING_INFO') + " (" + targetServer + ")";
				Alert.show(Helper.getString('GATEWAY_MISSING_INFO'),infoTitle);
			}
			else if(missingESXiCredentialsData(deploymentDetailModel))
			{
				infoTitle = Helper.getString('MISSING_INFO') + " (" + targetServer + ")";
				Alert.show(Helper.getString('ESXI_CREDENETIALS_INFO'),infoTitle);
			}
				
			else
			{
				validData= true;
			}
			
			
			return validData;	
			
		}
		
		private static function missingStaticIPAddress(deploymentDetailModel:DeploymentDetailModel):Boolean
		{
			var rtn :Boolean = false;
			var deployVO:DeploymentVO;
			var nicsInfo:NicsVO  ;
			
			for ( var i:int ; i<deploymentDetailModel.selectedServers.length; i++)
			{
				deployVO = deploymentDetailModel.selectedServers.getItemAt(i) as DeploymentVO;
				
				if (deployVO.personalityData.nics)
				{
					nicsInfo = deployVO.personalityData.nics.getItemAt(0) as NicsVO;
					
					if(!nicsInfo.dhcp)
					{
						if(nicsInfo.ip4Address == null )
						{
							targetServer = deployVO.personalityData.displayName;
							rtn = true;
							break;
						}
						else if (nicsInfo.ip4Address == "" )
						{
							targetServer = deployVO.personalityData.displayName;
							rtn = true;
							break;
						}
						
					}
					
				}
			}
			
			return rtn;
		}
		
		
		private static function missingGatewayAddress(deploymentDetailModel:DeploymentDetailModel):Boolean
		{
			var rtn :Boolean = false;
			var deployVO:DeploymentVO;
			var nicsInfo:NicsVO  ;
			
			for ( var i:int ; i<deploymentDetailModel.selectedServers.length; i++)
			{
				deployVO = deploymentDetailModel.selectedServers.getItemAt(i) as DeploymentVO;
				
				if (deployVO.personalityData.nics)
				{
					nicsInfo = deployVO.personalityData.nics.getItemAt(0) as NicsVO;
					
					if(!nicsInfo.dhcp)
					{
						if(nicsInfo.gateway == null )
						{
							targetServer = deployVO.personalityData.displayName;
							rtn = true;
							break;
						}
						else if (nicsInfo.gateway == "" )
						{
							targetServer = deployVO.personalityData.displayName;
							rtn = true;
							break;
						}
						
					}
					
				}
			}
			
			return rtn;
		}
		
		
		private static function missingNetMask(deploymentDetailModel:DeploymentDetailModel):Boolean
		{
			var rtn :Boolean = false;
			var deployVO:DeploymentVO;
			var nicsInfo:NicsVO  ;
			
			for ( var i:int ; i<deploymentDetailModel.selectedServers.length; i++)
			{
				deployVO = deploymentDetailModel.selectedServers.getItemAt(i) as DeploymentVO;
				
				if (deployVO.personalityData.nics)
				{
					nicsInfo = deployVO.personalityData.nics.getItemAt(0) as NicsVO;
					
					if(!nicsInfo.dhcp)
					{
						if(nicsInfo.mask == null )
						{
							targetServer = deployVO.personalityData.displayName;
							rtn = true;
							break;
						}
						else if (nicsInfo.mask == "" )
						{
							targetServer = deployVO.personalityData.displayName;
							rtn = true;
							break;
						}
						
					}
					
				}
			}
			
			return rtn;
		}
		
		private static function missingUHCPData(deploymentDetailModel:DeploymentDetailModel):Boolean
		{
			var rtn :Boolean = false;
			var deployVO:DeploymentVO;
			var nicsInfo:NicsVO  ;
			
			for ( var i:int ; i<deploymentDetailModel.selectedServers.length; i++)
			{
				deployVO = deploymentDetailModel.selectedServers.getItemAt(i) as DeploymentVO;
				
				if (deployVO.personalityData.nics)
				{
					nicsInfo = deployVO.personalityData.nics.getItemAt(0) as NicsVO;
					
					if(!nicsInfo.dhcp)
					{
						if(nicsInfo.ip4Address == null || nicsInfo.mask == null)
						{
							targetServer = deployVO.personalityData.displayName;
							rtn = true;
						}
						else if (nicsInfo.ip4Address == "" || nicsInfo.mask == "")
						{
							targetServer = deployVO.personalityData.displayName;
							rtn = true;
						}
						
					}
					
				}
			}
			
			return rtn;
		}
		
		private static function missingHostNameData(deploymentDetailModel:DeploymentDetailModel):Boolean
		{
			var rtn :Boolean = false;
			var deployVO:DeploymentVO;
			var nicsInfo:NicsVO  ;
			
			for ( var i:int ; i<deploymentDetailModel.selectedServers.length; i++)
			{
				deployVO = deploymentDetailModel.selectedServers.getItemAt(i) as DeploymentVO;
				if (deployVO.personalityData.nics)
				{
					nicsInfo = deployVO.personalityData.nics.getItemAt(0) as NicsVO;
					
					if (deployVO.personalityData.hostName== null && !nicsInfo.dhcp)
					{
						targetServer = deployVO.personalityData.displayName;
						rtn = true;
						break;
					}
					else if (deployVO.personalityData.hostName == ""  && !nicsInfo.dhcp)
					{
						targetServer = deployVO.personalityData.displayName;
						rtn = true;
						break;
					}
				}
				
			}
			
			return rtn;
		}
		
		private static function missingESXiCredentialsData(deploymentDetailModel:DeploymentDetailModel):Boolean
		{
			var rtn :Boolean = false;
			var deployVO:DeploymentVO;
			
			
			for ( var i:int ; i<deploymentDetailModel.selectedServers.length; i++)
			{
				deployVO = deploymentDetailModel.selectedServers.getItemAt(i) as DeploymentVO;
				if (deployVO.hostData)
				{
					
					
					if (deployVO.hostData.username== null || deployVO.hostData.password == null)
					{
						targetServer = deployVO.personalityData.displayName;
						rtn = true;
						break;
					}
					else if (deployVO.hostData.username == "" ||   deployVO.hostData.password == "")
					{
						targetServer = deployVO.personalityData.displayName;
						rtn = true;
						break;
					}
				}
				
			}
			
			return rtn;
		}
		
		private static function missingDomainData(deploymentDetailModel:DeploymentDetailModel):Boolean
		{
			var rtn :Boolean = false;
			var deployVO:DeploymentVO;
			var nicsInfo:NicsVO  ;
			
			for ( var i:int ; i<deploymentDetailModel.selectedServers.length; i++)
			{
				deployVO = deploymentDetailModel.selectedServers.getItemAt(i) as DeploymentVO;
				if (deployVO.personalityData.nics)
				{
					nicsInfo = deployVO.personalityData.nics.getItemAt(0) as NicsVO;
					
					if (deployVO.personalityData.domainName== null && !nicsInfo.dhcp)
					{
						targetServer = deployVO.personalityData.displayName;
						rtn = true;
						break;
					}
					else if (deployVO.personalityData.domainName == "" && !nicsInfo.dhcp )
					{
						targetServer = deployVO.personalityData.displayName;
						rtn = true;
						break;
					}
				}
				
				
			}
			
			return rtn;
		}
		
		public static function isNIC0AndDHCPSelected(deploymentDetailModel:DeploymentDetailModel):Boolean
		{
			var rtn:Boolean = false;
			var deployVO:DeploymentVO;
			var nicsInfo:NicsVO  ;
			for ( var i:int ; i<deploymentDetailModel.selectedServers.length; i++)
			{
				deployVO = deploymentDetailModel.selectedServers.getItemAt(i) as DeploymentVO;
				if(deployVO.personalityData)
				{
					nicsInfo = deployVO.personalityData.nics.getItemAt(0) as NicsVO;
				}
				
				if (deployVO.useNIC0 && nicsInfo.dhcp)
				{
					rtn = true;
					
				}
				
			}
			
			return rtn;
			
		}
		
		public static function getStatusImage(serverNodesData:ServerNodesData):Object{
			
			
			var serverStatusImage:Object;
			
			if(serverNodesData.state == ServerProvisioningStates.MAINTENANCE && serverNodesData.opswLifecycle == ServerProvisioningLifeCycleStates.UNPROVISIONED)
			{
				serverStatusImage = Images.STATUS_GREEN;
			}
			else if (serverNodesData.state == ServerProvisioningStates.MAINTENANCE && serverNodesData.opswLifecycle == ServerProvisioningLifeCycleStates.PROVISIONING)
			{
				serverStatusImage = Images.STATUS_WARNING;
			}
			else if (serverNodesData.state == ServerProvisioningStates.UNREACHABLE && serverNodesData.opswLifecycle == ServerProvisioningLifeCycleStates.PROVISIONING)
			{
				serverStatusImage = Images.STATUS_WARNING;
			}
			else if (serverNodesData.state == ServerProvisioningStates.MAINTENANCE && serverNodesData.opswLifecycle == ServerProvisioningLifeCycleStates.PROVISION_FAILED)
			{
				serverStatusImage = Images.STATUS_WARNING;
			}
			else if (serverNodesData.state == ServerProvisioningStates.UNREACHABLE && serverNodesData.opswLifecycle == ServerProvisioningLifeCycleStates.UNPROVISIONED)
			{
				serverStatusImage = Images.STATUS_RED;
			}
			else if (serverNodesData.state == ServerProvisioningStates.UNREACHABLE && serverNodesData.opswLifecycle == ServerProvisioningLifeCycleStates.PROVISION_FAILED)
			{
				serverStatusImage = Images.STATUS_RED;
			}

			else
			{
				serverStatusImage = Images.STATUS_UNKNOWN;
			}
			
			return serverStatusImage;
		}
		
		
		public static function getServerStatus(serverNodesData:ServerNodesData):String{
			
			var serverStatus:String;
			
			if(serverNodesData.state == ServerProvisioningStates.MAINTENANCE && serverNodesData.opswLifecycle == ServerProvisioningLifeCycleStates.UNPROVISIONED)
			{
				serverStatus = "Unprovisioned server is in maintennace mode";
			}
			else if (serverNodesData.state == ServerProvisioningStates.MAINTENANCE && serverNodesData.opswLifecycle == ServerProvisioningLifeCycleStates.PROVISIONING)
			{
				serverStatus = "Provisioning in progress";
			}
			else if (serverNodesData.state == ServerProvisioningStates.UNREACHABLE && serverNodesData.opswLifecycle == ServerProvisioningLifeCycleStates.PROVISIONING)
			{
				serverStatus = "Server is booting during provisioning";
			}
			else if (serverNodesData.state == ServerProvisioningStates.MAINTENANCE && serverNodesData.opswLifecycle == ServerProvisioningLifeCycleStates.PROVISION_FAILED)
			{
				serverStatus = "Provisioning failed";
			}
			else if (serverNodesData.state == ServerProvisioningStates.UNREACHABLE && serverNodesData.opswLifecycle == ServerProvisioningLifeCycleStates.UNPROVISIONED)
			{
				serverStatus = "Unprovisioned server is unreachable";
			}
			else if (serverNodesData.state == ServerProvisioningStates.UNREACHABLE && serverNodesData.opswLifecycle == ServerProvisioningLifeCycleStates.PROVISION_FAILED)
			{
				serverStatus = "Provisioning failed and server is unreachable";
			}
			else
			{
				serverStatus = "Unknown";
			}
			
			return serverStatus;
		}
		
	}
}