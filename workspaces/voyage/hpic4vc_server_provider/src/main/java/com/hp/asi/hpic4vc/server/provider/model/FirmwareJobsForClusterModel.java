package com.hp.asi.hpic4vc.server.provider.model;

import com.hp.asi.hpic4vc.provider.model.BaseModel;

public class FirmwareJobsForClusterModel extends BaseModel {
   public FirmwareJobsModel firmwareJobsModel;
   public String host;
   public FirmwareJobsModel getFirmwareJobsModel() {
		return firmwareJobsModel;
   }
   public void setFirmwareJobsModel(FirmwareJobsModel firmwareJobsModel) {
		this.firmwareJobsModel = firmwareJobsModel;
   }
   public String getHost() {
		return host;
   }
   public void setHost(String host) {
		this.host = host;
   }
}