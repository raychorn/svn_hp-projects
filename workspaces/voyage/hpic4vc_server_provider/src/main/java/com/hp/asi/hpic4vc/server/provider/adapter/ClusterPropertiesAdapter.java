package com.hp.asi.hpic4vc.server.provider.adapter;

import java.util.ArrayList;
import java.util.List;

import com.hp.asi.hpic4vc.provider.adapter.DataAdapter;
import com.hp.asi.hpic4vc.provider.model.TableModel;
import com.hp.asi.hpic4vc.server.provider.data.ClusterProperties;
import com.hp.asi.hpic4vc.server.provider.data.ClusterPropertiesResult;


public class ClusterPropertiesAdapter extends
		DataAdapter<ClusterPropertiesResult, TableModel> {

	private static final String SERVICE_NAME = "settings/clusterproperties";

	public ClusterPropertiesAdapter() {
		super(ClusterPropertiesResult.class);
	}

	@Override
	public TableModel formatData(ClusterPropertiesResult rawData) {

		log.debug("Cluster Properties beginning");

		TableModel model = new TableModel();

		if (rawData.hasError()) {
			log.debug("ClusterPropertiesResult had an error message.  "
					+ "Returning a TableModel with the error message");
			model.errorMessage = rawData.getErrorMessage();
			return model;
		}

		model.columnNames.add("UserName");
		model.columnNames.add("Password");
		model.columnNames.add("Type");
		
        List<ClusterProperties> clusterPropertiesList = rawData.getClusterprop();
		
		for(ClusterProperties clusterProperties : clusterPropertiesList){
			
			List<String> row = new ArrayList<String>();
			row.add(clusterProperties.getUsername());
			row.add(clusterProperties.getPassword());
			row.add(clusterProperties.getType());
			model.rowFormattedData.add(row);
		}
		
		log.debug("ClusterProperties Adapter the end" );
		log.debug(model.toString());

		return model;

	}

	@Override
	public TableModel getEmptyModel() {
		return new TableModel();
	}

	@Override
	public String getServiceName() {
		return SERVICE_NAME;
	}

}
