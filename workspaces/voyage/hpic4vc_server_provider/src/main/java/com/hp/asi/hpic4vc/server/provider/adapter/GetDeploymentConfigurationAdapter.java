package com.hp.asi.hpic4vc.server.provider.adapter;

import java.util.ArrayList;
import java.util.List;

import com.hp.asi.hpic4vc.provider.adapter.DataAdapter;
import com.hp.asi.hpic4vc.provider.model.TableModel;
import com.hp.asi.hpic4vc.server.provider.data.ServerCredentialsData;
import com.hp.asi.hpic4vc.server.provider.data.ServerCredentialsResult;
import com.hp.asi.hpic4vc.server.provider.model.DeploymentConfigurationModel;

public class GetDeploymentConfigurationAdapter extends  DataAdapter<ServerCredentialsResult, DeploymentConfigurationModel> 
{

	private static final String SERVICE_NAME = "settings/icsp";

	public GetDeploymentConfigurationAdapter() {
		super(ServerCredentialsResult.class);
		
	}
	
	@Override
	public String getServiceName()
	{
		return SERVICE_NAME;
	}
	
	@Override
	public DeploymentConfigurationModel formatData(ServerCredentialsResult rawData)
	{
        log.debug("Server Credentials beginning" );
        
        DeploymentConfigurationModel model = new DeploymentConfigurationModel();
		
		if (rawData.hasError()) {
            log.debug("ServerCredentialsResult had an error message.  " +
                    "Returning a DeploymentConfigurationModel with the error message");
            model.errorMessage = rawData.getErrorMessage();
            return model;
        }

		
		
		ServerCredentialsData[] pwdbList = rawData.getPwdb();
		if(pwdbList.length == 0){
			return model;
		}
		
		for(ServerCredentialsData pw:pwdbList){
			
			model.deploymentConfigData.add(pw);
		}
		
		return model;
	}

	@Override
	public DeploymentConfigurationModel getEmptyModel()
	{
		return new DeploymentConfigurationModel();
	}


}
