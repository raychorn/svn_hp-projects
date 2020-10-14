package com.hp.asi.hpic4vc.server.provider.model;

import com.hp.asi.hpic4vc.provider.model.BaseModel;

public class JobsModel extends BaseModel {
	
	private String jobDescription;
	private String status;
	private String statusDescriptions;
	private String errorDescription;
	private String percentComplete;
	private String noJobs;
	
	public String getJobDescription() {
		return jobDescription;
	}
	public void setJobDescription(String jobDescription) {
		this.jobDescription = jobDescription;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getStatusDescriptions() {
		return statusDescriptions;
	}
	public void setStatusDescriptions(String statusDescriptions) {
		this.statusDescriptions = statusDescriptions;
	}
	public String getErrorDescription() {
		return errorDescription;
	}
	public void setErrorDescription(String errorDescription) {
		this.errorDescription = errorDescription;
	}
	public String getPercentComplete() {
		return percentComplete;
	}
	public void setPercentComplete(String percentComplete) {
		this.percentComplete = percentComplete ;
	}
	public String getNoJobs() {
		return noJobs;
	}
	public void setNoJobs(String noJobs) {
		this.noJobs = noJobs;
	}

}
