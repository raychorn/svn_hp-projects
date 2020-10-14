package com.hp.asi.hpic4vc.server.provider.adapter;

import java.util.ArrayList;
import java.util.List;

import com.hp.asi.hpic4vc.provider.adapter.DataAdapter;
import com.hp.asi.hpic4vc.provider.model.TableModel;
import com.hp.asi.hpic4vc.server.provider.data.ServerConfigurationData;
import com.hp.asi.hpic4vc.server.provider.data.ServerConfigurationResult;
import com.hp.asi.hpic4vc.server.provider.data.ServerCredentialsData;

public class ServerConfigurationAdapter extends DataAdapter<ServerConfigurationResult, TableModel>
{
	private static final String SERVICE_NAME = "settings/config";
	
	public ServerConfigurationAdapter()
	{
		super(ServerConfigurationResult.class);
    }

	@Override
	public String getServiceName()
	{
		return SERVICE_NAME;
	}

	@Override
	public TableModel formatData(ServerConfigurationResult rawData)
	{
        log.debug("Server Credentials beginning" );
        
        TableModel model = new TableModel();
		
		if (rawData.hasError()) {
            log.debug("ServerConfigurationResult had an error message.  " +
                    "Returning a TableModel with the error message");
            model.errorMessage = rawData.getErrorMessage();
            return model;
        }

		model.columnNames.add("Parameter");
		model.columnNames.add("Value");

		ServerConfigurationData[] srvcfgList = rawData.getSrvcfg();
		if(srvcfgList.length == 0){
			return model;
		}
		
		for(ServerConfigurationData cfg:srvcfgList){
			List<String> row = new ArrayList<String>();
			row.add(cfg.getParam_name());
			row.add(cfg.getParam_value());
			model.rowFormattedData.add(row);
		}
		
		return model;
	}

	@Override
	public TableModel getEmptyModel()
	{
		return new TableModel();
	}

}
