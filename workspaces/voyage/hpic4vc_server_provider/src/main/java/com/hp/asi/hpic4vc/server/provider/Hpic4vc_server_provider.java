package com.hp.asi.hpic4vc.server.provider;

import com.hp.asi.hpic4vc.provider.model.LabelValueListModel;
import com.hp.asi.hpic4vc.provider.model.StringModel;
import com.hp.asi.hpic4vc.provider.model.TableModel;
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
 * Interface used to test a java service call from the Flex UI.
 * 
 * It must be declared as osgi:service with the same name in
 * main/resources/META-INF/spring/bundle-context-osgi.xml
 */
public interface Hpic4vc_server_provider {

	public String echo(String message);

	public LabelValueListModel getHostSummaryData(final String objReferenceId);

	public LabelValueListModel getHostSummaryPortletData(
			final String objReferenceId);

	public LabelValueListModel getClusterSummaryPortletData(
			final String objReferenceId);

	public HostDetailModel getHostDetailData(final String objReferenceId);

	public LabelValueListModel getInfrastructureSummaryData(
			final String objReferenceId);

	public InfrastructureDetailModel getInfrastructureDetailData(
			final String objReferenceId);

	public TableModel getNetworkSummaryData(final String objReferenceId);

	public NetworkDetailModel getNetworkDetailData(final String objReferenceId);

	public TableModel getServerCredentials(final String objReferenceId);

	public TableModel getServerConfiguration(final String objReferenceId);

	public TableModel getCommStatus(final String objReferenceId);

	public TableModel getHostProperties(final String objReferenceId);

	public StringModel updateServerCredentials(final String objReferenceId,
			final String data);

	public StringModel updateServerConfiguration(final String objReferenceId,
			final String data);

	public StringModel updateHostPropertiesPassword(
			final String objReferenceId, final String data);

	public ClusterInfraSummaryModel getClusterInfrastructureSummaryData(
			final String objReferenceId);

	public ClusterDetailModel getClusterDetailPortletData(
			final String objReferenceId);

	public ClusterInfrastructureModel getClusterInfrastructureDetailData(
			final String objReferenceId);

	public TableModel getClusterProperties(final String objReferenceId);

	public StringModel updateClusterProperties(final String objReferenceId,
			final String data);

	public StringModel uploadSmartComponents(final String objReferenceId,
			final String data, final byte uploadedBinary[]);

	public HostConfigModel getHostConfiguration(final String objReferenceId);

	public SwitchTypeModel getSwitchTypeHostConfiguration(
			final String objReferenceId);

	public StringModel updateSwitchType(final String objReferenceId,
			final String data);

	public StringModel updateNetwork(final String objReferenceId,
			final String data);

	public TableModel getUploadedSoftwareComponents(final String objReferenceId);

	public StringModel deleteSoftwareComponent(final String objReferenceId,
			final String data);

	// for network Diagram
	public NetworkDiagramDetailModel getNetworkDiagramData(
			final String objReferenceId);

	// Server provisioning- deployment
	public DeploymentConfigurationModel getDeploymentConfiguration(
			final String objReferenceId);

	public StringModel updateDeploymentConfiguration(
			final String objReferenceId, final String data);

	public StringModel getAuth(final String objReferenceId,
			final String userName, final String password, final String host);

	// public StringModel getAuth(DeploymentUserDataModel userData );
	public DeploymentDetailModel getServers(final String objReferenceId,
			final String auth, final String host);

	public BuildPlanModel getBuildPlans(final String objReferenceId,
			final String auth, final String host);

	public SmartComponentUpdateModel getSmartComponentUpdateStatusMessages(
			final String objReferenceId);

	public StringModel updateToggleUUID(final String objReferenceId);

	public HostConfigModel getRefreshedNetwork(final String objReferenceId);

	public StringModel submitBuildPlans(final String objReferenceId,
			final String data, final String buildUri, final String auth,
			final String host);

	public SmartComponentUpdateModel updateFirmwareComponents(
			final String objReferenceId, final String data, final String[] args);

	public SmartComponentUpdateModel deleteSelectedComponentJob(
			final String objReferenceId, final String data);

	public StringModel createTaskForServerProvisioningJob(
			final String objReferenceId, final String data);

	public HostConfigMismatchModel getNetworkMismatch(
			final String objReferenceId);

	public StringModel applyHostMismatchActions(final String objReferenceId,
			final String data);

	public HostConfigClusterMismatchModel getClusterMismatch(
			final String objReferenceId);

	public StringModel setPreferenceHost(final String objReferenceId,
			final String data, final String clusterUuid);

	public HostConfigMismatchModel getNetworkMismatchHost(
			final String objReferenceId, final String host);

	public StringModel getPowerControlStatus(final String objReferenceId);

	public StringModel updatePowerControl(final String objReferenceId);

	public FirmwareListOfJobsForClusterModel getFirmwareJobsForCluster(final String objReferenceId);
	public FirmwareListOfJobsForClusterModel updateClusterFirmwareComponents(final String objReferenceId,final String data,final String[] args ,final String[] args1);
	public FirmwareListOfJobsForClusterModel deleteClusterFirmwareJob(final String objReferenceId,final String data,final String[] args);

	public HostConfigSummaryMismatchModel getNetworkMismatchSummary(final String objReferenceId);
    public HostConfigClusterSummaryModel getClusterSummaryMismatch(final String objReferenceId);
	
	public AssociatedDevices getRediscoverNode(final String objReferenceId);
	public StringModel putRediscoverNode(final String objReferenceId);
	
	public StringModel getObjReferenceName(final String objReferenceId, final String uuidData);
	public StringModel setreferenceHostLevel(final String objReferenceId,
			final String data);
	public VDSVersionsModel getVDSVersion(final String objReferenceId);
	public StringModel updateVDSversions(final String objReferenceId,final String data);
	public VDSMigrateManagementModel getMigrateManagement(final String objReferenceId);
	public StringModel updateVDSMigrateManagement(final String objReferenceId,final String data);

}
