package com.hp.asi.hpic4vc.server.provider.model;

import java.util.List;

import com.hp.asi.hpic4vc.provider.model.BaseModel;

public class FirmwareListOfJobsForClusterModel extends BaseModel{
	public List<FirmwareJobsForClusterModel> firmwareListOfJobsForClusterModel;
	public List<String> hosts;
	public UpdateFirmwareComponentMessageModel updateFirmwareComponentMessageModel;

	public List<String> getHosts() {
		return hosts;
	}

	public void setHosts(List<String> hosts) {
		this.hosts = hosts;
	}

	public List<FirmwareJobsForClusterModel> getFirmwareListOfJobsForClusterModel() {
		return firmwareListOfJobsForClusterModel;
	}

	public void setFirmwareListOfJobsForClusterModel(
			List<FirmwareJobsForClusterModel> firmwareListOfJobsForClusterModel) {
		this.firmwareListOfJobsForClusterModel = firmwareListOfJobsForClusterModel;
	}
	
	public UpdateFirmwareComponentMessageModel getUpdateFirmwareComponentMessageModel() {
		return updateFirmwareComponentMessageModel;
	}

	public void setUpdateFirmwareComponentMessageModel(
			UpdateFirmwareComponentMessageModel updateFirmwareComponentMessageModel) {
		this.updateFirmwareComponentMessageModel = updateFirmwareComponentMessageModel;
	}
    
}
