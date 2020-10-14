package com.hp.asi.hpic4vc.server.provider;

import com.hp.asi.hpic4vc.provider.adapter.DataAdapter;
import com.hp.asi.hpic4vc.provider.adapter.DeleteAdapter;
import com.hp.asi.hpic4vc.provider.adapter.DeleteSoftwareComponentAdapter;
import com.hp.asi.hpic4vc.provider.adapter.PostAdapter;
import com.hp.asi.hpic4vc.provider.api.ProviderExecutor;
import com.hp.asi.hpic4vc.provider.impl.ProviderExecutorImpl;
import com.hp.asi.hpic4vc.provider.model.LabelValueListModel;
import com.hp.asi.hpic4vc.provider.model.StringModel;
import com.hp.asi.hpic4vc.provider.model.TableModel;
import com.hp.asi.hpic4vc.server.provider.adapter.ApplyRecomActionHostAdapter;
import com.hp.asi.hpic4vc.server.provider.adapter.ClusterDetailAdapter;
import com.hp.asi.hpic4vc.server.provider.adapter.ClusterInfrastructureDetailAdapter;
import com.hp.asi.hpic4vc.server.provider.adapter.ClusterInfrastructureSummaryAdapter;
import com.hp.asi.hpic4vc.server.provider.adapter.ClusterPropertiesAdapter;
import com.hp.asi.hpic4vc.server.provider.adapter.ClusterSummaryPortletAdapter;
import com.hp.asi.hpic4vc.server.provider.adapter.CreateTaskForServerProvisioningJobAdapter;
import com.hp.asi.hpic4vc.server.provider.adapter.DeleteFirmwareClusterUpdateAdapter;
import com.hp.asi.hpic4vc.server.provider.adapter.DeleteSmartComponentUpdateJobAdapter;
import com.hp.asi.hpic4vc.server.provider.adapter.FileUploadAdapter;
import com.hp.asi.hpic4vc.server.provider.adapter.GetAuthAdapter;
import com.hp.asi.hpic4vc.server.provider.adapter.GetBuildPlanAdapter;
import com.hp.asi.hpic4vc.server.provider.adapter.GetDeploymentConfigurationAdapter;
import com.hp.asi.hpic4vc.server.provider.adapter.GetFirmwareJobsAdapter;
import com.hp.asi.hpic4vc.server.provider.adapter.GetObjReferenceAdapter;
import com.hp.asi.hpic4vc.server.provider.adapter.GetPowerControlStatusAdapter;
import com.hp.asi.hpic4vc.server.provider.adapter.GetRediscoverNodeAdapter;
import com.hp.asi.hpic4vc.server.provider.adapter.PutRediscoverNodeAdatapter;
import com.hp.asi.hpic4vc.server.provider.adapter.GetServerNodesAdapter;
import com.hp.asi.hpic4vc.server.provider.adapter.GetSmartComponentUpdateStatusAdapter;
import com.hp.asi.hpic4vc.server.provider.adapter.HostConfClusterMismatchDataAdapter;
import com.hp.asi.hpic4vc.server.provider.adapter.HostConfClusterSummaryDataAdapter;
import com.hp.asi.hpic4vc.server.provider.adapter.HostConfigNetworkRefreshDataAdapter;
import com.hp.asi.hpic4vc.server.provider.adapter.HostConfigurationDataAdapter;
import com.hp.asi.hpic4vc.server.provider.adapter.HostConfigurationSwitchTypeDataAdapter;
import com.hp.asi.hpic4vc.server.provider.adapter.HostDetailAdapter;
import com.hp.asi.hpic4vc.server.provider.adapter.HostNetworkMismatchDataAdapter;
import com.hp.asi.hpic4vc.server.provider.adapter.HostNetworkMismatchSummaryDataAdapter;
import com.hp.asi.hpic4vc.server.provider.adapter.HostPropertiesAdapter;
import com.hp.asi.hpic4vc.server.provider.adapter.HostSummaryAdapter;
import com.hp.asi.hpic4vc.server.provider.adapter.HostSummaryPortletAdapter;
import com.hp.asi.hpic4vc.server.provider.adapter.InfrastructureDetailAdapter;
import com.hp.asi.hpic4vc.server.provider.adapter.InfrastructureSummaryAdapter;
import com.hp.asi.hpic4vc.server.provider.adapter.NetworkDetailAdapter;
import com.hp.asi.hpic4vc.server.provider.adapter.NetworkDiagramAdapter;
import com.hp.asi.hpic4vc.server.provider.adapter.NetworkSummaryAdapter;
import com.hp.asi.hpic4vc.server.provider.adapter.ServerCommStatusAdapter;
import com.hp.asi.hpic4vc.server.provider.adapter.ServerConfigurationAdapter;
import com.hp.asi.hpic4vc.server.provider.adapter.ServerCredentialsAdapter;
import com.hp.asi.hpic4vc.server.provider.adapter.SetPreferenceHostAdapter;
import com.hp.asi.hpic4vc.server.provider.adapter.SetPreferenceHostLevelAdapter;
import com.hp.asi.hpic4vc.server.provider.adapter.SubmitDeploymentBuildPlansAdapter;
import com.hp.asi.hpic4vc.server.provider.adapter.ToggleUIDPutAdapter;
import com.hp.asi.hpic4vc.server.provider.adapter.UpdateClusterFirmwareAdapter;
import com.hp.asi.hpic4vc.server.provider.adapter.UpdateClusterPropertiesAdapter;
import com.hp.asi.hpic4vc.server.provider.adapter.UpdateConfigurationAdapter;
import com.hp.asi.hpic4vc.server.provider.adapter.UpdateCredentialsAdapter;
import com.hp.asi.hpic4vc.server.provider.adapter.UpdateDeploymentConfigurationAdapter;
import com.hp.asi.hpic4vc.server.provider.adapter.UpdateHostPropertiesPasswordAdapter;
import com.hp.asi.hpic4vc.server.provider.adapter.UpdateNetworkAdapter;
import com.hp.asi.hpic4vc.server.provider.adapter.UpdatePowerControlAdapter;
import com.hp.asi.hpic4vc.server.provider.adapter.UpdateSmartComponentFirwareHost;
import com.hp.asi.hpic4vc.server.provider.adapter.UpdateSwitchTypeAdapter;
import com.hp.asi.hpic4vc.server.provider.adapter.UpdateVDSMigrateManagementAdapter;
import com.hp.asi.hpic4vc.server.provider.adapter.UpdateVDSversionsAdapter;
import com.hp.asi.hpic4vc.server.provider.adapter.UploadedSoftwareComponentsAdapter;
import com.hp.asi.hpic4vc.server.provider.adapter.VDSMigrateManagementDataAdapter;
import com.hp.asi.hpic4vc.server.provider.adapter.VDSVersionsDataAdapter;
import com.hp.asi.hpic4vc.server.provider.data.ClusterDetailResult;
import com.hp.asi.hpic4vc.server.provider.data.ClusterInfrastructureDetailResult;
import com.hp.asi.hpic4vc.server.provider.data.ClusterInfrastructureSummaryResult;
import com.hp.asi.hpic4vc.server.provider.data.ClusterPropertiesResult;
import com.hp.asi.hpic4vc.server.provider.data.ClusterSummaryResult;
import com.hp.asi.hpic4vc.server.provider.data.FirmwareJobsForClusterResult;
import com.hp.asi.hpic4vc.server.provider.data.HostConfigClusterMismatchResult;
import com.hp.asi.hpic4vc.server.provider.data.HostConfigClusterMismatchSummaryResult;
import com.hp.asi.hpic4vc.server.provider.data.HostConfigNetworkMismatchResult;
import com.hp.asi.hpic4vc.server.provider.data.HostConfigNetworkMismatchSummaryResult;
import com.hp.asi.hpic4vc.server.provider.data.HostConfigurationResult;
import com.hp.asi.hpic4vc.server.provider.data.HostDetailResult;
import com.hp.asi.hpic4vc.server.provider.data.HostPropertiesResult;
import com.hp.asi.hpic4vc.server.provider.data.HostSummaryResult;
import com.hp.asi.hpic4vc.server.provider.data.InfrastructureDetailResult;
import com.hp.asi.hpic4vc.server.provider.data.InfrastructureSummaryResult;
import com.hp.asi.hpic4vc.server.provider.data.ManageSmartComponentsResult;
import com.hp.asi.hpic4vc.server.provider.data.NetworkSummaryResult;
import com.hp.asi.hpic4vc.server.provider.data.ObjRefNameResult;
import com.hp.asi.hpic4vc.server.provider.data.PowerControlStatusResult;
import com.hp.asi.hpic4vc.server.provider.data.RediscoverNodeResult;
import com.hp.asi.hpic4vc.server.provider.data.ServerCommStatusResult;
import com.hp.asi.hpic4vc.server.provider.data.ServerConfigurationResult;
import com.hp.asi.hpic4vc.server.provider.data.ServerCredentialsResult;
import com.hp.asi.hpic4vc.server.provider.data.SmartComponentUpdateResult;
import com.hp.asi.hpic4vc.server.provider.data.SwitchTypeData;
import com.hp.asi.hpic4vc.server.provider.data.VDSMigrateManagementResult;
import com.hp.asi.hpic4vc.server.provider.data.VDSversionResult;
import com.hp.asi.hpic4vc.server.provider.data.Deployment.BuildPlanResult;
import com.hp.asi.hpic4vc.server.provider.data.Deployment.ServerNodesResult;
import com.hp.asi.hpic4vc.server.provider.data.network.NetworkDetailResult;
import com.hp.asi.hpic4vc.server.provider.model.AssociatedDevices;
import com.hp.asi.hpic4vc.server.provider.model.BuildPlanModel;
import com.hp.asi.hpic4vc.server.provider.model.ClusterDetailModel;
import com.hp.asi.hpic4vc.server.provider.model.ClusterInfraSummaryModel;
import com.hp.asi.hpic4vc.server.provider.model.ClusterInfrastructureModel;
import com.hp.asi.hpic4vc.server.provider.model.DeploymentConfigurationModel;
import com.hp.asi.hpic4vc.server.provider.model.DeploymentDetailModel;
import com.hp.asi.hpic4vc.server.provider.model.FirmwareListOfJobsForClusterModel;
import com.hp.asi.hpic4vc.server.provider.model.HostConfigClusterMismatchModel;
import com.hp.asi.hpic4vc.server.provider.model.HostConfigClusterSummaryModel;
import com.hp.asi.hpic4vc.server.provider.model.HostConfigMismatchModel;
import com.hp.asi.hpic4vc.server.provider.model.HostConfigModel;
import com.hp.asi.hpic4vc.server.provider.model.HostConfigSummaryMismatchModel;
import com.hp.asi.hpic4vc.server.provider.model.HostDetailModel;
import com.hp.asi.hpic4vc.server.provider.model.InfrastructureDetailModel;
import com.hp.asi.hpic4vc.server.provider.model.NetworkDetailModel;
import com.hp.asi.hpic4vc.server.provider.model.NetworkDiagramDetailModel;
import com.hp.asi.hpic4vc.server.provider.model.SmartComponentUpdateModel;
import com.hp.asi.hpic4vc.server.provider.model.SwitchTypeModel;
import com.hp.asi.hpic4vc.server.provider.model.VDSMigrateManagementModel;
import com.hp.asi.hpic4vc.server.provider.model.VDSVersionsModel;

/**
 * Implementation of the Hpic4vc_server_provider interface
 */
public class Hpic4vc_server_providerImpl implements Hpic4vc_server_provider {

	private ProviderExecutor executorService;

	public Hpic4vc_server_providerImpl() {
		this.executorService = new ProviderExecutorImpl();
	}

	public String echo(String message) {
		return message;
	}

	public LabelValueListModel getHostSummaryData(final String objReferenceId) {
		DataAdapter<HostSummaryResult, LabelValueListModel> callable = new HostSummaryAdapter();
		return this.executorService.execute(callable, objReferenceId);
	}

	public LabelValueListModel getHostSummaryPortletData(
			final String objReferenceId) {
		DataAdapter<HostSummaryResult, LabelValueListModel> callable = new HostSummaryPortletAdapter();
		return this.executorService.execute(callable, objReferenceId);
	}

	public HostDetailModel getHostDetailData(final String objReferenceId) {
		DataAdapter<HostDetailResult, HostDetailModel> callable = new HostDetailAdapter();
		return this.executorService.execute(callable, objReferenceId);
	}

	public LabelValueListModel getInfrastructureSummaryData(
			final String objReferenceId) {
		DataAdapter<InfrastructureSummaryResult, LabelValueListModel> callable = new InfrastructureSummaryAdapter();
		return this.executorService.execute(callable, objReferenceId);
	}

	public InfrastructureDetailModel getInfrastructureDetailData(
			final String objReferenceId) {
		DataAdapter<InfrastructureDetailResult, InfrastructureDetailModel> callable = new InfrastructureDetailAdapter();
		return this.executorService.execute(callable, objReferenceId);
	}

	public TableModel getNetworkSummaryData(final String objReferenceId) {
		DataAdapter<NetworkSummaryResult, TableModel> callable = new NetworkSummaryAdapter();
		return this.executorService.execute(callable, objReferenceId);
	}

	public NetworkDetailModel getNetworkDetailData(final String objReferenceId) {
		DataAdapter<NetworkDetailResult, NetworkDetailModel> callable = new NetworkDetailAdapter();
		return this.executorService.execute(callable, objReferenceId);
	}

	public TableModel getServerCredentials(final String objReferenceId) {
		DataAdapter<ServerCredentialsResult, TableModel> callable = new ServerCredentialsAdapter();
		return this.executorService.execute(callable, objReferenceId);
	}

	public TableModel getServerConfiguration(final String objReferenceId) {
		DataAdapter<ServerConfigurationResult, TableModel> callable = new ServerConfigurationAdapter();
		return this.executorService.execute(callable, objReferenceId);
	}

	public TableModel getCommStatus(final String objReferenceId) {
		DataAdapter<ServerCommStatusResult, TableModel> callable = new ServerCommStatusAdapter();
		return this.executorService.execute(callable, objReferenceId);
	}

	public TableModel getHostProperties(final String objReferenceId) {
		DataAdapter<HostPropertiesResult, TableModel> callable = new HostPropertiesAdapter();
		return this.executorService.execute(callable, objReferenceId);
	}

	public StringModel updateServerCredentials(final String objReferenceId,
			final String data) {
		PostAdapter<String, StringModel> callable = new UpdateCredentialsAdapter(
				data);
		return this.executorService.execute(callable, objReferenceId);
	}

	public StringModel updateServerConfiguration(final String objReferenceId,
			final String data) {
		UpdateConfigurationAdapter callable = new UpdateConfigurationAdapter(
				data);
		return this.executorService.execute(callable, objReferenceId);
	}

	public StringModel updateHostPropertiesPassword(
			final String objReferenceId, final String data) {
		PostAdapter<String, StringModel> callable = new UpdateHostPropertiesPasswordAdapter(
				data);
		return this.executorService.execute(callable, objReferenceId);
	}

	public LabelValueListModel getClusterSummaryPortletData(
			final String objReferenceId) {
		DataAdapter<ClusterSummaryResult, LabelValueListModel> callable = new ClusterSummaryPortletAdapter();
		return this.executorService.execute(callable, objReferenceId);
	}

	public ClusterInfraSummaryModel getClusterInfrastructureSummaryData(
			final String objReferenceId) {
		DataAdapter<ClusterInfrastructureSummaryResult, ClusterInfraSummaryModel> callable = new ClusterInfrastructureSummaryAdapter();
		return this.executorService.execute(callable, objReferenceId);
	}

	public ClusterDetailModel getClusterDetailPortletData(
			final String objReferenceId) {
		DataAdapter<ClusterDetailResult, ClusterDetailModel> callable = new ClusterDetailAdapter();
		return this.executorService.execute(callable, objReferenceId);
	}

	public ClusterInfrastructureModel getClusterInfrastructureDetailData(
			final String objReferenceId) {
		DataAdapter<ClusterInfrastructureDetailResult, ClusterInfrastructureModel> callable = new ClusterInfrastructureDetailAdapter();
		return this.executorService.execute(callable, objReferenceId);
	}

	public TableModel getClusterProperties(final String objReferenceId) {

		DataAdapter<ClusterPropertiesResult, TableModel> callable = new ClusterPropertiesAdapter();
		return this.executorService.execute(callable, objReferenceId);
	}

	public StringModel updateClusterProperties(final String objReferenceId,
			final String data) {
		PostAdapter<String, StringModel> callable = new UpdateClusterPropertiesAdapter(
				data);
		return this.executorService.execute(callable, objReferenceId);
	}

	public HostConfigModel getHostConfiguration(final String objReferenceId) {
		DataAdapter<HostConfigurationResult, HostConfigModel> callable = new HostConfigurationDataAdapter();
		return this.executorService.execute(callable, objReferenceId);
	}

	public SwitchTypeModel getSwitchTypeHostConfiguration(
			final String objReferenceId) {
		DataAdapter<SwitchTypeData, SwitchTypeModel> callable = new HostConfigurationSwitchTypeDataAdapter();
		return this.executorService.execute(callable, objReferenceId);
	}

	public StringModel updateSwitchType(final String objReferenceId,
			final String data) {
		PostAdapter<String, StringModel> callable = new UpdateSwitchTypeAdapter(
				data);
		return this.executorService.execute(callable, objReferenceId);
	}

	public StringModel updateNetwork(final String objReferenceId,
			final String data) {
		PostAdapter<String, StringModel> callable = new UpdateNetworkAdapter(
				data);
		return this.executorService.execute(callable, objReferenceId);
	}

	public NetworkDiagramDetailModel getNetworkDiagramData(
			final String objReferenceId) {
		DataAdapter<NetworkDetailResult, NetworkDiagramDetailModel> callable = new NetworkDiagramAdapter();
		return this.executorService.execute(callable, objReferenceId);
	}

	public DeploymentConfigurationModel getDeploymentConfiguration(
			final String objReferenceId) {
		DataAdapter<ServerCredentialsResult, DeploymentConfigurationModel> callable = new GetDeploymentConfigurationAdapter();
		return this.executorService.execute(callable, objReferenceId);
	}

	public StringModel updateDeploymentConfiguration(
			final String objReferenceId, final String data) {
		PostAdapter<String, StringModel> callable = new UpdateDeploymentConfigurationAdapter(
				data);
		return this.executorService.execute(callable, objReferenceId);
	}

	public StringModel uploadSmartComponents(final String objReferenceId,
			String data, byte uploadedBinary[]) {
		PostAdapter<String, StringModel> callable = new FileUploadAdapter(data,
				uploadedBinary);
		return this.executorService.execute(callable, objReferenceId);
	}

	public TableModel getUploadedSoftwareComponents(final String objReferenceId) {

		DataAdapter<ManageSmartComponentsResult, TableModel> callable = new UploadedSoftwareComponentsAdapter();
		return this.executorService.execute(callable, objReferenceId);
	}

	public StringModel deleteSoftwareComponent(String objReferenceId,
			String data) {

		DeleteAdapter<String, StringModel> callable = new DeleteSoftwareComponentAdapter(
				data);
		return this.executorService.execute(callable, objReferenceId);
	}

	public StringModel getAuth(final String objReferenceId,
			final String userName, final String password, final String host) {
		PostAdapter<String, StringModel> callable = new GetAuthAdapter(
				userName, password, host);
		return this.executorService.execute(callable, objReferenceId);
	}

	public DeploymentDetailModel getServers(final String objReferenceId,
			final String auth, final String host) {
		DataAdapter<ServerNodesResult, DeploymentDetailModel> callable = new GetServerNodesAdapter(
				auth, host);
		return this.executorService.execute(callable, objReferenceId);
	}

	public BuildPlanModel getBuildPlans(final String objReferenceId,
			final String auth, final String host) {
		DataAdapter<BuildPlanResult, BuildPlanModel> callable = new GetBuildPlanAdapter(
				auth, host);
		return this.executorService.execute(callable, objReferenceId);
	}

	public StringModel submitBuildPlans(final String objReferenceId,
			final String data, final String buildUri, final String auth,
			final String host) {
		PostAdapter<String, StringModel> callable = new SubmitDeploymentBuildPlansAdapter(
				data, buildUri, auth, host);
		return this.executorService.execute(callable, objReferenceId);
	}

	public SmartComponentUpdateModel getSmartComponentUpdateStatusMessages(
			String objReferenceId) {
		DataAdapter<SmartComponentUpdateResult, SmartComponentUpdateModel> callable = new GetSmartComponentUpdateStatusAdapter();
		return this.executorService.execute(callable, objReferenceId);
	}

	public StringModel updateToggleUUID(String objReferenceId) {
		PostAdapter<String, StringModel> callable = new ToggleUIDPutAdapter();
		return this.executorService.execute(callable, objReferenceId);
	}
    public HostConfigMismatchModel getNetworkMismatch(final String objReferenceId)
    {
     DataAdapter<HostConfigNetworkMismatchResult, HostConfigMismatchModel> callable = new HostNetworkMismatchDataAdapter();
     return this.executorService.execute(callable, objReferenceId);
    }
	public HostConfigModel getRefreshedNetwork(final String objReferenceId) {
		DataAdapter<HostConfigurationResult, HostConfigModel> callable = new HostConfigNetworkRefreshDataAdapter();
		return this.executorService.execute(callable, objReferenceId);
	}
	
	public SmartComponentUpdateModel deleteSelectedComponentJob(String objReferenceId, String data) {
	     PostAdapter<SmartComponentUpdateResult, SmartComponentUpdateModel> callable = new DeleteSmartComponentUpdateJobAdapter(data);
		 return this.executorService.execute(callable, objReferenceId);
	}
	
	 public SmartComponentUpdateModel updateFirmwareComponents(final String objReferenceId,final String data,final String[] args) {
	       PostAdapter<SmartComponentUpdateResult, SmartComponentUpdateModel> callable = new UpdateSmartComponentFirwareHost(data,args);
	   	   return this.executorService.execute(callable, objReferenceId);
	  }
	 
	 public StringModel createTaskForServerProvisioningJob(final String objReferenceId,  final String data){
			PostAdapter<String, StringModel> callable = new CreateTaskForServerProvisioningJobAdapter(data);
			return this.executorService.execute(callable, objReferenceId);
			
		}
	 public StringModel applyHostMismatchActions(final String objReferenceId,final String data) {
			PostAdapter<String, StringModel> callable = new ApplyRecomActionHostAdapter(data);
			return this.executorService.execute(callable, objReferenceId);
		}
	 public HostConfigClusterMismatchModel getClusterMismatch(final String objReferenceId)
	    {
	     DataAdapter<HostConfigClusterMismatchResult, HostConfigClusterMismatchModel> callable = new HostConfClusterMismatchDataAdapter();
	     return this.executorService.execute(callable, objReferenceId);
	    }
	 public StringModel setPreferenceHost(final String objReferenceId,final String data,final String clusterUuid) {
			PostAdapter<String, StringModel> callable = new SetPreferenceHostAdapter(data,clusterUuid);
			return this.executorService.execute(callable, objReferenceId);
		}
	 public HostConfigMismatchModel getNetworkMismatchHost(final String objReferenceId,final String host) {
			DataAdapter<HostConfigNetworkMismatchResult, HostConfigMismatchModel> callable = new HostNetworkMismatchDataAdapter(host);
			return this.executorService.execute(callable, objReferenceId);
		}
	 
	 public StringModel getPowerControlStatus(final String objReferenceId)
	 {
		 DataAdapter<PowerControlStatusResult, StringModel> callable = new GetPowerControlStatusAdapter();
			return this.executorService.execute(callable, objReferenceId);
		 
	 }
	 
	 public StringModel updatePowerControl(final String objReferenceId) {
			PostAdapter<String, StringModel> callable = new UpdatePowerControlAdapter();
			return this.executorService.execute(callable, objReferenceId);
	 }
	 
	 public FirmwareListOfJobsForClusterModel getFirmwareJobsForCluster(final String objReferenceId) {
			DataAdapter<FirmwareJobsForClusterResult, FirmwareListOfJobsForClusterModel> callable = new GetFirmwareJobsAdapter();
			return this.executorService.execute(callable, objReferenceId);
	 }

	public FirmwareListOfJobsForClusterModel updateClusterFirmwareComponents(final String objReferenceId, final String data, final String[] args, final  String[] args1) {
		PostAdapter<FirmwareJobsForClusterResult, FirmwareListOfJobsForClusterModel> callable = new UpdateClusterFirmwareAdapter(data,args,args1);
	   	return this.executorService.execute(callable, objReferenceId);
	}

	public FirmwareListOfJobsForClusterModel deleteClusterFirmwareJob(final String objReferenceId,final String data,final String[] args) {
		 PostAdapter<FirmwareJobsForClusterResult, FirmwareListOfJobsForClusterModel> callable = new DeleteFirmwareClusterUpdateAdapter(data,args);
		 return this.executorService.execute(callable, objReferenceId);
	}
	public HostConfigSummaryMismatchModel getNetworkMismatchSummary(final String objReferenceId)
	{
	     DataAdapter<HostConfigNetworkMismatchSummaryResult, HostConfigSummaryMismatchModel> callable = new HostNetworkMismatchSummaryDataAdapter();
	     return this.executorService.execute(callable, objReferenceId);
	}
	public AssociatedDevices getRediscoverNode(final String objReferenceId){
		DataAdapter<RediscoverNodeResult, AssociatedDevices> callable = new GetRediscoverNodeAdapter();
		return this.executorService.execute(callable, objReferenceId);
	}
	 
	public StringModel putRediscoverNode(final String objReferenceId){
		PostAdapter<String, StringModel> callable = new PutRediscoverNodeAdatapter();
		return this.executorService.execute(callable, objReferenceId);
	}
	 public HostConfigClusterSummaryModel getClusterSummaryMismatch(final String objReferenceId)
	    {
	     DataAdapter<HostConfigClusterMismatchSummaryResult, HostConfigClusterSummaryModel> callable = new HostConfClusterSummaryDataAdapter();
	     return this.executorService.execute(callable, objReferenceId);
	    }
	 
		public StringModel getObjReferenceName(final String objReferenceId, final String uuidData)
		{
			 DataAdapter<ObjRefNameResult, StringModel> callable = new GetObjReferenceAdapter(uuidData);
		     return this.executorService.execute(callable, objReferenceId);
		}
	 public StringModel setreferenceHostLevel(final String objReferenceId,final String data) {
				PostAdapter<String, StringModel> callable = new SetPreferenceHostLevelAdapter(data);
				return this.executorService.execute(callable, objReferenceId);
			}
	 public VDSVersionsModel getVDSVersion(final String objReferenceId) {
			DataAdapter<VDSversionResult, VDSVersionsModel> callable = new VDSVersionsDataAdapter();
			return this.executorService.execute(callable, objReferenceId);
		}
	 public StringModel updateVDSversions(final String objReferenceId,final String data) {
			PostAdapter<String, StringModel> callable = new UpdateVDSversionsAdapter(data);
			return this.executorService.execute(callable, objReferenceId);
		}
	 public VDSMigrateManagementModel getMigrateManagement(final String objReferenceId) {
			DataAdapter<VDSMigrateManagementResult, VDSMigrateManagementModel> callable = new VDSMigrateManagementDataAdapter();
			return this.executorService.execute(callable, objReferenceId);
		}
	 public StringModel updateVDSMigrateManagement(final String objReferenceId,final String data) {
			PostAdapter<String, StringModel> callable = new UpdateVDSMigrateManagementAdapter(data);
			return this.executorService.execute(callable, objReferenceId);
		}
}
