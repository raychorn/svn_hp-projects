package com.hp.asi.hpic4vc.server.provider.adapter;

import java.util.ArrayList;
import java.util.List;

import com.hp.asi.hpic4vc.provider.adapter.DataAdapter;
import com.hp.asi.hpic4vc.server.provider.data.NetworkSummaryResult;
import com.hp.asi.hpic4vc.server.provider.data.Nic;
import com.hp.asi.hpic4vc.provider.model.TableModel;


public class NetworkSummaryAdapter extends DataAdapter<NetworkSummaryResult, TableModel>
{
	private static final String SERVICE_NAME = "services/host/networksummary";
	private static final String LINK_SPEED_GB="Gb";
	public NetworkSummaryAdapter()
	{
		super(NetworkSummaryResult.class);
    }

	@Override
	public String getServiceName()
	{
		return SERVICE_NAME;
	}

		
	@Override
	public TableModel formatData(NetworkSummaryResult rawData)
	{
        log.debug("NetworkSummaryAdapter beginning" );
        
		TableModel model = new TableModel();
		
		if (rawData.hasError()) 
		{
            log.debug("NetworkSummaryResults had an error message.  " +
                    "Returning a TableModel with the error message");
            model.errorMessage = rawData.getErrorMessage();
            return model;
        }
		
		if(rawData.getNics() == null) //Maybe not the best check
		{
			model.errorMessage = "No Data in Network Summary Result.";
			return model;
		}
		
		List<Nic> nicList = rawData.getNics();
		if(nicList.isEmpty())
		{
			return model;
		}
		
		model.columnNames.add("VM NIC");
		model.columnNames.add("Physical NIC");
		model.columnNames.add("Link Speed");
		model.columnNames.add("vSwitch");
		
		for (Nic nic : nicList)
		{
			List<String> row = new ArrayList<String>();
			row.add(nic.getVmnic());
			row.add(nic.getPhysical_nic());
			if(Float.toString(nic.getSpeedGb()).equals("0.0"))
				row.add("Not Linked");
			else
				row.add(Float.toString(nic.getSpeedGb())+" "+LINK_SPEED_GB);
			row.add(nic.getVswitch());
			
			model.rowFormattedData.add(row);
		}
		
        log.debug("NetworkSummaryAdapter the end" );
		log.debug(model.toString());
		return model;
	}

	@Override
	public TableModel getEmptyModel()
	{
		return new TableModel();
	}

}
