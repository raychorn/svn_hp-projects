package com.hp.asi.hpic4vc.server.provider.data;

import java.util.List;

public class FirmwareJobsForClusterResult extends ResultBase {
	public FirmwareJobsForClusterResult(List<FirmwareJobsForCluster> firmwareJobsForCluster) {
		super();
		this.firmwareJobsForCluster = firmwareJobsForCluster;
	}

	private List<FirmwareJobsForCluster> firmwareJobsForCluster;

	public List<FirmwareJobsForCluster> getFirmwareJobsForCluster() {
		return firmwareJobsForCluster;
	}

	public void setFirmwareJobsForCluster(List<FirmwareJobsForCluster> firmwareJobsForCluster) {
		this.firmwareJobsForCluster = firmwareJobsForCluster;
	}

}
