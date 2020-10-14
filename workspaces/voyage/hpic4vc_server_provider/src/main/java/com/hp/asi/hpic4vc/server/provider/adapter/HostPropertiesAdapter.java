package com.hp.asi.hpic4vc.server.provider.adapter;

import java.util.ArrayList;
import java.util.List;

import com.hp.asi.hpic4vc.provider.adapter.DataAdapter;
import com.hp.asi.hpic4vc.server.provider.data.HostPropertiesDetails;
import com.hp.asi.hpic4vc.server.provider.data.HostPropertiesResult;
import com.hp.asi.hpic4vc.provider.model.TableModel;

public class HostPropertiesAdapter extends DataAdapter<HostPropertiesResult,TableModel> {
	
	private static final String SERVICE_NAME = "settings/hostproperties";
	private static final String PROLIANT_SERVER = "ProLiant Server";
	private static final String VMWARE_HOST = "VMware Host";
	public HostPropertiesAdapter() {
		super(HostPropertiesResult.class);
	}

	@Override
	public String getServiceName()
	{
		return SERVICE_NAME;
	}

	@Override
	public TableModel formatData(HostPropertiesResult rawData) {
        log.debug("HostPropertiesAdapter beginning" );
        
		TableModel model = new TableModel();
		
		if (rawData.hasError()) 
		{
            log.debug("HostPropertiesResults had an error message.  " +
                    "Returning a TableModel with the error message");
            model.errorMessage = rawData.getErrorMessage();
            return model;
        }
		
		if(rawData.getHostprop() == null) //Maybe not the best check
		{
			model.errorMessage = "Not Available";
			return model;
		}
		
		List<HostPropertiesDetails>hostPropertiesDetailsList = rawData.getHostprop();
		if(hostPropertiesDetailsList.isEmpty())
		{
			return model;
		}
		
		model.columnNames.add("Host");
		model.columnNames.add("User Name");		
		model.columnNames.add("Type");
		
		for (HostPropertiesDetails hostPropertiesDetails : hostPropertiesDetailsList)
		{
			List<String> row = new ArrayList<String>();
			row.add(hostPropertiesDetails.getHost());
			row.add(hostPropertiesDetails.getUsername());
			if(hostPropertiesDetails.getType().equals(PROLIANT_SERVER)){
				row.add(VMWARE_HOST);
			}else {
				row.add(hostPropertiesDetails.getType());	
			}
			
			
			model.rowFormattedData.add(row);
		}
		
        log.debug("HostProperties Adapter the end" );
		log.debug(model.toString());
		
		
		
		return model;
		
	}
	
	@Override
	public TableModel getEmptyModel()
	{
		return new TableModel();
	}



}
