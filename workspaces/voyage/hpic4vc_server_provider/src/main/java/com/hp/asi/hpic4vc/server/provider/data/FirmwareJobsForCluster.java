package com.hp.asi.hpic4vc.server.provider.data;

import org.codehaus.jackson.annotate.JsonProperty;

import com.hp.asi.hpic4vc.server.provider.data.FirmwareJobs;

public class FirmwareJobsForCluster {
	
	@JsonProperty("FirmwareJobs")
	private FirmwareJobs firmwareJobs;
	@JsonProperty("Host")
   	private String Host;

 	public FirmwareJobs getFirmwareJobs(){
		return this.firmwareJobs;
	}
	public void setFirmwareJobs(FirmwareJobs firmwareJobs){
		this.firmwareJobs = firmwareJobs;
	}
	public String getHost() {
		return Host;
	}
	public void setHost(String host) {
		Host = host;
	}
   
}