package com.hp.asi.hpic4vc.server.provider.adapter;

import java.util.ArrayList;
import java.util.List;

import com.hp.asi.hpic4vc.provider.adapter.DataAdapter;
import com.hp.asi.hpic4vc.provider.model.TableModel;
import com.hp.asi.hpic4vc.server.provider.data.ServerCredentialsData;
import com.hp.asi.hpic4vc.server.provider.data.ServerCredentialsResult;

public class ServerCredentialsAdapter extends DataAdapter<ServerCredentialsResult, TableModel>
{
	private static final String SERVICE_NAME = "settings/password";
	
	public ServerCredentialsAdapter()
	{
		super(ServerCredentialsResult.class);
    }

	@Override
	public String getServiceName()
	{
		return SERVICE_NAME;
	}

	@Override
	public TableModel formatData(ServerCredentialsResult rawData)
	{
        log.debug("Server Credentials beginning" );
        
        TableModel model = new TableModel();
		
        // Set the column names before
        model.columnNames.add("Host");
		model.columnNames.add("Username");
		model.columnNames.add("Type");
		model.columnNames.add("Unique ID");
		model.columnNames.add("Password");
		
		if (rawData.hasError()) {
            log.debug("ServerCredentialsResult had an error message.  " +
                    "Returning a TableModel with the error message");
            model.errorMessage = rawData.getErrorMessage();
            return model;
        }

		// Set tooltips
		model.columnToolTips.add("Host");
		model.columnToolTips.add("Username");
		model.columnToolTips.add("Type");
		model.columnToolTips.add("Unique ID");
		model.columnToolTips.add("Password");
		
		// Set Column widths
		model.columnWidth.add("200");
		model.columnWidth.add("200");
		model.columnWidth.add("200");
		model.columnWidth.add("0");
		model.columnWidth.add("0");
		
		// Set Right click menu
		model.columnRightClickMenu.add(null);
		model.columnRightClickMenu.add(null);
		model.columnRightClickMenu.add(null);
		model.columnRightClickMenu.add(null);
		model.columnRightClickMenu.add(null);
		
		// Set if column is visible
		model.isColumnVisible.add(true);
		model.isColumnVisible.add(true);
		model.isColumnVisible.add(true);
		model.isColumnVisible.add(false);
		model.isColumnVisible.add(false);
		
		// Set if column is numeric
		model.isColumnNumeric.add(false);
		model.isColumnNumeric.add(false);
		model.isColumnNumeric.add(false);
		model.isColumnNumeric.add(false);
		model.isColumnNumeric.add(false);
		
		// Set if column has icons
		model.hasIcons.add(false);
		model.hasIcons.add(false);
		model.hasIcons.add(false);
		model.hasIcons.add(false);
		model.hasIcons.add(false);
		
		ServerCredentialsData[] pwdbList = rawData.getPwdb();
		if(pwdbList.length == 0){
			return model;
		}
		
		for(ServerCredentialsData pw:pwdbList){
			List<String> row = new ArrayList<String>();
			row.add(pw.getHost());
			row.add(pw.getUsername());
			if(pw.getType().equals("ProLiant Server")){
				row.add("VMware Host");
			}else{
				row.add(pw.getType());
			}
			row.add(pw.getId());
			row.add(pw.getPassword());
			
			// Set row formatted and raw data
			model.rowFormattedData.add(row);
			model.rowRawData.add(row);
			
			// Set row icons
			List<String> rowIcons = new ArrayList<String>();
			rowIcons.add(null);
			rowIcons.add(null);
			rowIcons.add(null);
			rowIcons.add(null);
			rowIcons.add(null);
			model.rowIcons.add(rowIcons);
			
			// Set row ids
			List<String> rowIds = new ArrayList<String>();
			rowIds.add(null);
			rowIds.add(null);
			rowIds.add(null);
			rowIds.add(null);
			rowIds.add(null);
			model.rowIds.add(rowIds);
		}
		
		return model;
	}

	@Override
	public TableModel getEmptyModel()
	{
		return new TableModel();
	}

}
