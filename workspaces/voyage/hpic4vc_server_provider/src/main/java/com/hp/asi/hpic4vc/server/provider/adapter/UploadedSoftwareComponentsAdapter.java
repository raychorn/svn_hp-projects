package com.hp.asi.hpic4vc.server.provider.adapter;

import java.util.ArrayList;
import java.util.List;

import com.hp.asi.hpic4vc.provider.adapter.DataAdapter;
import com.hp.asi.hpic4vc.provider.model.TableModel;
import com.hp.asi.hpic4vc.server.provider.data.Component;
import com.hp.asi.hpic4vc.server.provider.data.ManageSmartComponentsResult;

public class UploadedSoftwareComponentsAdapter extends DataAdapter<ManageSmartComponentsResult,TableModel>{

	private static final String SERVICE_NAME = "services/host/smart_components";
	
	public UploadedSoftwareComponentsAdapter(){
		super(ManageSmartComponentsResult.class);
	}

	@Override
	public TableModel getEmptyModel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getServiceName() {
		// TODO Auto-generated method stub
		return SERVICE_NAME;
	}

	
	@Override
	public TableModel formatData(ManageSmartComponentsResult rawData)
	{
        log.debug("UploadedSoftwareComponentsAdapter beginning" );
        
        TableModel model = new TableModel();
		
		if (rawData.hasError()) {
            log.debug("UploadedSoftwareComponentsAdapter had an error message.  " +
                    "Returning a ManageSoftwareComponentsModel with the error message");
            model.errorMessage = rawData.getErrorMessage();
            return model;
        }
		
		model.columnNames.add("Version");
		model.columnNames.add("Name");
		model.columnNames.add("Filename");
		model.columnNames.add("Delete");
		
		
		List<Component> componentsList = rawData.getComponents();
		
		if(componentsList.size() == 0){
			return model;
		}
		
		for(Component comp:componentsList){
			List<String> row = new ArrayList<String>();
			row.add(comp.getVersion());
			row.add(comp.getName());
			row.add(comp.getFilename());
			row.add("delete");
			model.rowFormattedData.add(row);
		}
		
		
        log.debug("UploadedSoftwareComponentsAdapter the end" );
		log.debug(model.toString());
		return model;
	}
	

}
