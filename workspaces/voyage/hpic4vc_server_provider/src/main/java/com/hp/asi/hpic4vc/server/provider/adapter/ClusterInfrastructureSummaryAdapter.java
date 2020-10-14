package com.hp.asi.hpic4vc.server.provider.adapter;

import com.hp.asi.hpic4vc.provider.adapter.DataAdapter;
import com.hp.asi.hpic4vc.server.provider.data.ClusterInfrastructureSummaryResult;
import com.hp.asi.hpic4vc.server.provider.data.OasClusterInfraSummary;
import com.hp.asi.hpic4vc.server.provider.model.ClusterInfraSummaryModel;


public class ClusterInfrastructureSummaryAdapter extends DataAdapter<ClusterInfrastructureSummaryResult, ClusterInfraSummaryModel>
{
	
private static final String SERVICE_NAME =  "orservices/host/swd/clusterinfrasummary";
	

	

	
	public ClusterInfrastructureSummaryAdapter()
	{
		super(ClusterInfrastructureSummaryResult.class);
    }

	@Override
	public String getServiceName()
	{
		return SERVICE_NAME;
	}

	@Override
	public ClusterInfraSummaryModel formatData(ClusterInfrastructureSummaryResult rawData)
	{
        log.debug("ClusterInfrastructureSummaryAdapter beginning" );
        
        ClusterInfraSummaryModel model = new ClusterInfraSummaryModel();
		
		if (rawData.hasError()) 
		{
            log.debug("ClusterInfrastructureSummaryResults had an error message.  " +
                    "Returning a InfrastructureSummaryModel with the error message");
            model.errorMessage = rawData.getErrorMessage();
            return model;
        }
		
		if(rawData.getOas()== null) 
		{
			model.errorMessage = "Not Available";
		return model;
		}
		
		for (OasClusterInfraSummary clusterInfraSummaryOA : rawData.getOas())
		{
			if (null == clusterInfraSummaryOA.getOa())
			{
				model.errorMessage = "Not Available";
				
			}
			else
			{
//				model.addLabelValuePair("Enclosure", clusterInfraSummaryOA.getOa().getEnclosure_info().getEnclosureName());
//				model.addLabelValuePair("Rack", clusterInfraSummaryOA.getOa().getEnclosure_info().getRackName());
//				model.addLabelValuePair("Power Supplies",   Integer.toString(clusterInfraSummaryOA.getOa().getEnclosure_info().getPowerSuppliesPresent()) + " " + "of" + " " + 
//						Integer.toString(clusterInfraSummaryOA.getOa().getEnclosure_info().getPowerSupplyBays() ));
//				model.addLabelValuePair("Fans", Integer.toString(clusterInfraSummaryOA.getOa().getEnclosure_info().getFansPresent()) + " " + "of" + " " +
//						Integer.toString(clusterInfraSummaryOA.getOa().getEnclosure_info().getFanBays()) );


			}
			if (null == clusterInfraSummaryOA.getOa().getThermal())	
			{
				model.errorMessage = "Not Available";
				
			}
			if (null == clusterInfraSummaryOA.getOa().getPower())	
			{
				model.errorMessage = "Not Available";
				
			}
			else
			{
				//model.addLabelValuePair("Power Consumed", clusterInfraSummaryOA.getOa().getPower().getPowerConsumed() + " " + "W");
				model.getOas().add(clusterInfraSummaryOA);
				
			}
			
			
			
			
		}
		
        log.debug("ClusterInfrastructureSummaryAdapter the end" );
		log.debug(model.toString());
		return model;
	}

	@Override
	public ClusterInfraSummaryModel getEmptyModel()
	{
		return new ClusterInfraSummaryModel();
	}


}
