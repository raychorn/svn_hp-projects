package com.hp.asi.hpic4vc.server.provider.model;

import java.util.ArrayList;
import java.util.List;

import com.hp.asi.hpic4vc.provider.model.BaseModel;

public class SmartComponentUpdateModel extends BaseModel {
	
	public List<QueueModel> queue;
	public List<JobsModel> jobs;
	public List<String> errors;
	public UpdateSoftwareComponentMessagModel updateSoftwareComponentMessageModel;
	public SmartComponentUpdateModel()
	{
		super();
		this.queue = new ArrayList<QueueModel>();
		this.jobs  = new ArrayList<JobsModel>();
		this.errors = new ArrayList<String>();
		this.updateSoftwareComponentMessageModel = new UpdateSoftwareComponentMessagModel();
	}

}
