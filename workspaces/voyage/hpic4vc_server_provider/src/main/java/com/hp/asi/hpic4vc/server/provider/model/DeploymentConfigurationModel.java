package com.hp.asi.hpic4vc.server.provider.model;

import java.util.ArrayList;
import java.util.List;
import com.hp.asi.hpic4vc.provider.model.BaseModel;
import com.hp.asi.hpic4vc.server.provider.data.ServerCredentialsData;

public class DeploymentConfigurationModel extends BaseModel{
	
	public List<ServerCredentialsData> deploymentConfigData;

	public DeploymentConfigurationModel() {
		super();
		this.deploymentConfigData = new ArrayList<ServerCredentialsData>();
	}

}
