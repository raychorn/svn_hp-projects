package com.hp.asi.hpic4vc.server.provider.model;

import java.util.ArrayList;
import java.util.List;

import com.hp.asi.hpic4vc.provider.model.BaseModel;

public class FirmwareJobsModel extends BaseModel {
	
	public List<QueueClusterModel> queue;
	public List<JobsModel> jobs;
	public List<String> errors;
	public UpdateSoftwareComponentMessagModel updateSoftwareComponentMessageModel;
	public FirmwareJobsModel()
	{
		super();
		this.queue = new ArrayList<QueueClusterModel>();
		this.jobs  = new ArrayList<JobsModel>();
		this.updateSoftwareComponentMessageModel = new UpdateSoftwareComponentMessagModel();
		this.errors = new ArrayList<String>();
	}

}