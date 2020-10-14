/* Copyright 2012 Hewlett-Packard Development Company, L.P. */

package {
import assets.components.Hpic4vc_BaseComponent;
import assets.components.Hpic4vc_Cluster;
import assets.components.Hpic4vc_ClusterInfrastructureDetailMediator;
import assets.components.Hpic4vc_ClusterInfrastructure_Detail;
import assets.components.Hpic4vc_ClusterMediator;
import assets.components.Hpic4vc_ClusterMisMatchMediator;
import assets.components.Hpic4vc_ClusterMismatch;
import assets.components.Hpic4vc_Cluster_GettingStarted;
import assets.components.Hpic4vc_DataGrid;
import assets.components.Hpic4vc_Datastore_GettingStarted;
import assets.components.Hpic4vc_FirmwareStatusMessages;
import assets.components.Hpic4vc_FirmwareStatusMessagesMediator;
import assets.components.Hpic4vc_Footer;
import assets.components.Hpic4vc_Header;
import assets.components.Hpic4vc_Health;
import assets.components.Hpic4vc_HealthMediator;
import assets.components.Hpic4vc_Manage_BaseComponent;
import assets.components.Hpic4vc_Newsfeed;
import assets.components.Hpic4vc_NewsfeedMediator;
import assets.components.Hpic4vc_Overview;
import assets.components.Hpic4vc_OverviewMediator;
import assets.components.Hpic4vc_Overview_BaseComponent;
import assets.components.Hpic4vc_Overview_ClusterInfrastructure;
import assets.components.Hpic4vc_Overview_ClusterInfrastructureMediator;
import assets.components.Hpic4vc_Overview_ClusterMediator;
import assets.components.Hpic4vc_Overview_ClusterNetworking;
import assets.components.Hpic4vc_Overview_ClusterNetworkingMediator;
import assets.components.Hpic4vc_Overview_ClusterSummary;
import assets.components.Hpic4vc_Overview_Host;
import assets.components.Hpic4vc_Overview_HostMediator;
import assets.components.Hpic4vc_Overview_Infrastructure;
import assets.components.Hpic4vc_Overview_InfrastructureMediator;
import assets.components.Hpic4vc_Overview_Networking;
import assets.components.Hpic4vc_Overview_NetworkingMediator;
import assets.components.Hpic4vc_Overview_Newsfeed;
import assets.components.Hpic4vc_Overview_NewsfeedMediator;
import assets.components.Hpic4vc_Overview_SoftwareFirmware;
import assets.components.Hpic4vc_Overview_SoftwareFirmwareMediator;
import assets.components.Hpic4vc_Overview_Storage;
import assets.components.Hpic4vc_Overview_StorageMediator;
import assets.components.Hpic4vc_Server_ClusterProperties;
import assets.components.Hpic4vc_Server_CommStatus;
import assets.components.Hpic4vc_Server_CommStatusMediator;
import assets.components.Hpic4vc_Server_Host;
import assets.components.Hpic4vc_Server_HostMediator;
import assets.components.Hpic4vc_Server_Infrastructure;
import assets.components.Hpic4vc_Server_InfrastructureMediator;
import assets.components.Hpic4vc_Server_NetworkDiagram;
import assets.components.Hpic4vc_Server_NetworkDiagramMediator;
import assets.components.Hpic4vc_Server_Networking;
import assets.components.Hpic4vc_Server_NetworkingDetail;
import assets.components.Hpic4vc_Server_NetworkingDetailMediator;
import assets.components.Hpic4vc_Server_NetworkingMediator;
import assets.components.Hpic4vc_SmartComponentUpdate_StatusMessages;
import assets.components.Hpic4vc_SoftwareFirmware;
import assets.components.Hpic4vc_SoftwareFirmwareMediator;
import assets.components.Hpic4vc_SoftwareFirmware_Detail;
import assets.components.Hpic4vc_SoftwareFirmware_Detail_Mediator;
import assets.components.Hpic4vc_SoftwareFirmware_StatusMesgMediator;
import assets.components.Hpic4vc_Storage;
import assets.components.Hpic4vc_StorageMediator;
import assets.components.Hpic4vc_Storage_HBAs;
import assets.components.Hpic4vc_Storage_HBAsMediator;
import assets.components.Hpic4vc_Storage_Paths;
import assets.components.Hpic4vc_Storage_PathsMediator;
import assets.components.Hpic4vc_Storage_Replications;
import assets.components.Hpic4vc_Storage_ReplicationsMediator;
import assets.components.Hpic4vc_Storage_StorageVolumes;
import assets.components.Hpic4vc_Storage_StorageVolumesMediator;
import assets.components.Hpic4vc_Storage_Summary;
import assets.components.Hpic4vc_Storage_SummaryMediator;
import assets.components.Hpic4vc_Storage_VMsToVolumes;
import assets.components.Hpic4vc_Storage_VMsToVolumesMediator;
import assets.components.Hpic4vc_Storage_VirtualDisks;
import assets.components.Hpic4vc_Storage_VirtualDisksMediator;
import assets.components.Hpic4vc_SummaryPortlet_Server;
import assets.components.Hpic4vc_SummaryPortlet_ServerMediator;
import assets.components.Hpic4vc_SummaryPortlet_Storage;
import assets.components.Hpic4vc_SummaryPortlet_StorageMediator;
import assets.components.Hpic4vc_Tasks;
import assets.components.Hpic4vc_TasksMediator;
import assets.components.Hpic4vc_VM_GettingStarted;
import assets.components.Hpic4vc_manage_GettingStarted;

import com.hp.asi.hpic4vc.ui.Hpic4vc_actionCommand;
import com.hp.asi.hpic4vc.ui.Hpic4vc_server_actionCommand;
import com.hp.asi.hpic4vc.ui.model.BarChartGroupModel;
import com.hp.asi.hpic4vc.ui.model.BarChartModel;
import com.hp.asi.hpic4vc.ui.model.CertificateModel;
import com.hp.asi.hpic4vc.ui.model.ConfigurationListModel;
import com.hp.asi.hpic4vc.ui.model.ConfigurationModel;
import com.hp.asi.hpic4vc.ui.model.DataGridWrapper;
import com.hp.asi.hpic4vc.ui.model.DistributedVirtualSwitchModel;
import com.hp.asi.hpic4vc.ui.model.FirmwareModel;
import com.hp.asi.hpic4vc.ui.model.FooterModel;
import com.hp.asi.hpic4vc.ui.model.FullSummaryModel;
import com.hp.asi.hpic4vc.ui.model.HeaderModel;
import com.hp.asi.hpic4vc.ui.model.HealthModel;
import com.hp.asi.hpic4vc.ui.model.HostDetailModel;
import com.hp.asi.hpic4vc.ui.model.HostDetailViewModel;
import com.hp.asi.hpic4vc.ui.model.InfrastructureDetailModel;
import com.hp.asi.hpic4vc.ui.model.InfrastructureDetailViewModel;
import com.hp.asi.hpic4vc.ui.model.LabelValueListModel;
import com.hp.asi.hpic4vc.ui.model.LabelValueModel;
import com.hp.asi.hpic4vc.ui.model.LaunchToolModel;
import com.hp.asi.hpic4vc.ui.model.LinkModel;
import com.hp.asi.hpic4vc.ui.model.MenuModel;
import com.hp.asi.hpic4vc.ui.model.NetworkDetailModel;
import com.hp.asi.hpic4vc.ui.model.NetworkDetailViewModel;
import com.hp.asi.hpic4vc.ui.model.NewsFeedModel;
import com.hp.asi.hpic4vc.ui.model.NewsItem;
import com.hp.asi.hpic4vc.ui.model.PageModel;
import com.hp.asi.hpic4vc.ui.model.PieChartModel;
import com.hp.asi.hpic4vc.ui.model.PortletModel;
import com.hp.asi.hpic4vc.ui.model.RefreshCacheModel;
import com.hp.asi.hpic4vc.ui.model.StorageOverviewModel;
import com.hp.asi.hpic4vc.ui.model.StringModel;
import com.hp.asi.hpic4vc.ui.model.SummaryPortletModel;
import com.hp.asi.hpic4vc.ui.model.TableModel;
import com.hp.asi.hpic4vc.ui.model.TaskModel;
import com.hp.asi.hpic4vc.ui.model.VCFabricModel;
import com.hp.asi.hpic4vc.ui.model.VCNetworkModel;
import com.hp.asi.hpic4vc.ui.model.VirtualConnectModuleModel;
import com.hp.asi.hpic4vc.ui.model.VirtualSwitchModel;
import com.hp.asi.hpic4vc.ui.views.*;
import com.hp.asi.hpic4vc.ui.views.converged.*;
import com.hp.asi.hpic4vc.ui.views.converged.model.HPSummaryDetails;
import com.hp.asi.hpic4vc.ui.views.converged.views.ArrayColumnSetContainer;
import com.hp.asi.hpic4vc.ui.views.converged.views.HPSummaryView;
import com.hp.asi.hpic4vc.ui.views.converged.views.HPSummaryViewMediator;
import com.hp.asi.hpic4vc.ui.views.converged.views.StorageDetailsPortletView;
import com.hp.asi.hpic4vc.ui.views.converged.views.StorageDetailsPortletViewMediator;
import com.hp.asi.hpic4vc.ui.views.converged.views.SummaryHeaderView;
import com.hp.asi.hpic4vc.ui.views.converged.views.SummaryHeaderViewMediator;
import com.hp.asi.hpic4vc.ui.views.converged.views.SystemDetailsPortletView;
import com.hp.asi.hpic4vc.ui.views.converged.views.SystemDetailsPortletViewMediator;

import mx.core.FlexTextField;

/**
 * Include the classes instantiated dynamically, because they are otherwise
 * not included by the compiler.
 */
internal class ModuleClasses {
   Hpic4vc_uiView
   Hpic4vc_uiMediator
   
   Hpic4vc_summaryView
   Hpic4vc_summaryViewMediator
   
   Hpic4vc_SummaryPortlet_Server
   
   Hpic4vc_SummaryPortlet_Storage
   Hpic4vc_SummaryPortlet_StorageMediator
   
   Hpic4vc_SummaryPortlet_ServerMediator
   
   Hpic4vc_actionCommand
   
   Hpic4vc_server_actionCommand

   Hpic4vc_BaseComponent
   Hpic4vc_Overview_BaseComponent
   Hpic4vc_Manage_BaseComponent
   Hpic4vc_DataGrid
  
   Hpic4vc_Overview
   Hpic4vc_OverviewMediator
   
   Hpic4vc_Overview_Host
   Hpic4vc_Overview_HostMediator
   
   Hpic4vc_Overview_Infrastructure
   Hpic4vc_Overview_InfrastructureMediator
   
   Hpic4vc_Overview_Networking
   Hpic4vc_Overview_NetworkingMediator
   
   Hpic4vc_Server_NetworkingDetail
   Hpic4vc_Server_NetworkingDetailMediator
   
   Hpic4vc_Server_NetworkDiagram
   Hpic4vc_Server_NetworkDiagramMediator
   
   Hpic4vc_Overview_Newsfeed
   Hpic4vc_Overview_NewsfeedMediator
   
   Hpic4vc_Overview_SoftwareFirmware
   Hpic4vc_Overview_SoftwareFirmwareMediator
   
   Hpic4vc_Overview_Storage
   Hpic4vc_Overview_StorageMediator
   
   Hpic4vc_Newsfeed
   Hpic4vc_NewsfeedMediator
   
   Hpic4vc_Tasks
   Hpic4vc_TasksMediator
   
   Hpic4vc_Health
   Hpic4vc_HealthMediator
   
   Hpic4vc_SoftwareFirmware
   Hpic4vc_SoftwareFirmwareMediator
   
   Hpic4vc_SoftwareFirmware_Detail
   Hpic4vc_SoftwareFirmware_Detail_Mediator
   
   Hpic4vc_FirmwareStatusMessages
   Hpic4vc_FirmwareStatusMessagesMediator
   
   Hpic4vc_SmartComponentUpdate_StatusMessages
   Hpic4vc_SoftwareFirmware_StatusMesgMediator
   
   Hpic4vc_Storage
   Hpic4vc_StorageMediator
   
   Hpic4vc_Storage_Summary
   Hpic4vc_Storage_SummaryMediator
   
   Hpic4vc_Storage_StorageVolumes
   Hpic4vc_Storage_StorageVolumesMediator
   
   Hpic4vc_Storage_VirtualDisks
   Hpic4vc_Storage_VirtualDisksMediator
   
   Hpic4vc_Storage_HBAs
   Hpic4vc_Storage_HBAsMediator
   
   Hpic4vc_Storage_Paths
   Hpic4vc_Storage_PathsMediator
   
   Hpic4vc_Storage_Replications
   Hpic4vc_Storage_ReplicationsMediator
   
   Hpic4vc_Storage_VMsToVolumes
   Hpic4vc_Storage_VMsToVolumesMediator
  
   Hpic4vc_Server_Host
   Hpic4vc_Server_HostMediator
   
   Hpic4vc_Server_Infrastructure
   Hpic4vc_Server_InfrastructureMediator
   
   Hpic4vc_Server_Networking
   Hpic4vc_Server_NetworkingMediator

   Hpic4vc_Server_NetworkDiagram
   Hpic4vc_Server_NetworkDiagramMediator

   Hpic4vc_serverCredentials
   Hpic4vc_serverCredentialsMediator
   
   Hpic4vc_serverConfiguration
   Hpic4vc_serverConfigurationMediator

   Hpic4vc_Server_CommStatus
   Hpic4vc_Server_CommStatusMediator
   
   Hpic4vc_Header
   Hpic4vc_Footer
   
   Hpic4vc_gettingStarted
   Hpic4vc_gettingStartedMediator
   
   Hpic4vc_vCenterCredentials
   Hpic4vc_vCenterCredentialsMediator

   Hpic4vc_hpOneViewCredentials
   Hpic4vc_hpOneViewCredentialsMediator
   
   Hpic4vc_certificateManagement
   Hpic4vc_certificateManagementMediator
   
   Hpic4vc_storageAdministratorPortal
   Hpic4vc_storageAdministratorPortalMediator
   
   Hpic4vc_Overview_ClusterSummary
   Hpic4vc_Overview_ClusterMediator
   
   Hpic4vc_Cluster
   Hpic4vc_ClusterMediator
   
   Hpic4vc_Overview_ClusterInfrastructure
   Hpic4vc_Overview_ClusterInfrastructureMediator
   
   Hpic4vc_manage_GettingStarted
   Hpic4vc_VM_GettingStarted
   Hpic4vc_Datastore_GettingStarted
   Hpic4vc_Cluster_GettingStarted
   
   Hpic4vc_manage_uiView
   Hpic4vc_manage_uiMediator;
   
    
   Hpic4vc_ClusterInfrastructure_Detail;
   Hpic4vc_ClusterInfrastructureDetailMediator;
   
   redirectPage;
   redirectPageMediator;
   
   HPSummaryView;
   HPSummaryViewMediator;
   
   Hpic4vc_Server_ClusterProperties;
   
   SummaryHeaderView;
   SummaryHeaderViewMediator;
   
   SystemDetailsPortletView;
   SystemDetailsPortletViewMediator;
   
   StorageDetailsPortletView;
   StorageDetailsPortletViewMediator;
   
   ArrayColumnSetContainer; 

   Hpic4vc_HostConfigurator;
   Hpic4vc_HostConfiguratorMediator;
   
   Hpic4vc_deploymentConfiguration 
   Hpic4vc_deploymentConfigurationMediator 
      
   Hpic4vc_ClusterMismatch;
   Hpic4vc_ClusterMisMatchMediator;
   
   Hpic4vc_Overview_ClusterNetworking;
   Hpic4vc_Overview_ClusterNetworkingMediator;
   
}
}