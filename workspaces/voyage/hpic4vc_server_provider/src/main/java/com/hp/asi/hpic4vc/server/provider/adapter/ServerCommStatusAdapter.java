package com.hp.asi.hpic4vc.server.provider.adapter;

import java.util.ArrayList;
import java.util.List;

import com.hp.asi.hpic4vc.provider.adapter.DataAdapter;
import com.hp.asi.hpic4vc.provider.model.TableModel;
import com.hp.asi.hpic4vc.server.provider.data.ServerCommStatusResult;

public class ServerCommStatusAdapter extends DataAdapter<ServerCommStatusResult, TableModel>
{
	private static final String SERVICE_NAME = "services/host/hostcomm";
	
	public ServerCommStatusAdapter()
	{
		super(ServerCommStatusResult.class);
    }

	@Override
	public String getServiceName()
	{
		return SERVICE_NAME;
	}

	@Override
	public TableModel formatData(ServerCommStatusResult rawData)
	{
        log.debug("Server Credentials beginning" );
        
        TableModel model = new TableModel();
		
		if (rawData.hasError()) {
            log.debug("ServerConfigurationResult had an error message.  " +
                    "Returning a TableModel with the error message");
            model.errorMessage = rawData.getErrorMessage();
            return model;
        }

		model.columnNames.add("Management Component");
		model.columnNames.add("Description");
		model.columnNames.add("Status");
		model.columnNames.add("Last Update");
		
		 
    	List<String> server = new ArrayList<String>();
		server.add("Server");
		server.add("HP SNMP Agents (ESX), HP CIM Providers (ESXi), or HP AMS (Gen8)");
		if(rawData.getServer() != null)
		{
			server.add(rawData.getServer().status);
			server.add(rawData.getServer().last_update);
		}
		else
		{
			server.add("Unavailable");
			server.add("");
		}
		
		model.rowFormattedData.add(server);
	     
			
	    
	    List<String> ilo = new ArrayList<String>();
		ilo.add("iLO");
		ilo.add("HP iLO Management Processor");
		if(rawData.getIlo() != null)
		{
			ilo.add(rawData.getIlo().status);
			ilo.add(rawData.getIlo().last_update);
		}
		else{
			ilo.add("Unavailable");
			ilo.add("");
		}
		model.rowFormattedData.add(ilo);   
	    
		
		// Only show OA and VC for a blade system 
		if(rawData.isBlade()){

			List<String> oa = new ArrayList<String>();
			oa.add("OA");
			oa.add("HP Blade System Onboard Administratorr");
			if(rawData.getOa() != null)
			{
				oa.add(rawData.getOa().status);
				oa.add(rawData.getOa().last_update);
			}
			else
			{
				oa.add("Unavailable");
				oa.add("");

			}
			model.rowFormattedData.add(oa);
		


		   List<String> vcm = new ArrayList<String>();
			vcm.add("Virtual Connect");
			vcm.add("HP Virtual Connect Manager");
			if(rawData.getVcm() != null)
			{
				vcm.add(rawData.getVcm().status);
				vcm.add(rawData.getVcm().last_update);
			}
			else
			{
				vcm.add("Unavailable");
				vcm.add("");
			}
			model.rowFormattedData.add(vcm);
			
			
		}
       
		
	  

		
		return model;
	}

	@Override
	public TableModel getEmptyModel()
	{
		return new TableModel();
	}

}
