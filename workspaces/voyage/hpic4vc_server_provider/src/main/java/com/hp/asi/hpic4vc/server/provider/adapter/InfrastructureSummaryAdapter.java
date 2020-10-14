package com.hp.asi.hpic4vc.server.provider.adapter;

import com.hp.asi.hpic4vc.provider.adapter.DataAdapter;
import com.hp.asi.hpic4vc.provider.model.LabelValueListModel;
import com.hp.asi.hpic4vc.server.provider.data.InfrastructureSummaryResult;

public class InfrastructureSummaryAdapter extends DataAdapter<InfrastructureSummaryResult, LabelValueListModel>
{
	private static final String SERVICE_NAME = "services/host/hostinfrasummary";
	
	public InfrastructureSummaryAdapter()
	{
		super(InfrastructureSummaryResult.class);
    }

	@Override
	public String getServiceName()
	{
		return SERVICE_NAME;
	}

	@Override
	public LabelValueListModel formatData(InfrastructureSummaryResult rawData)
	{
        log.debug("InfrastructureSummaryAdapter beginning" );
        
        LabelValueListModel model = new LabelValueListModel();
		
		if (rawData.hasError()) 
		{
            log.debug("InfrastructureSummaryResults had an error message.  " +
                    "Returning a InfrastructureSummaryModel with the error message");
            model.errorMessage = rawData.getErrorMessage();
            return model;
        }
		
		if(rawData.getOa() == null) 
		{
			model.errorMessage = "Not Available";
			return model;
		}
		
		if(rawData.getOa().getEnclosure_info() == null) 
		{
			model.errorMessage = "Missing Data in Infrastructure Summary Result.";
			return model;
		}
		
		model.addLabelValuePair("Enclosure", rawData.getOa().getEnclosure_info().getEnclosureName());
		model.addLabelValuePair("Rack", rawData.getOa().getEnclosure_info().getRackName());
		
		//model.addLabelValuePair("PowerSupplyBays", rawData.getOa().getEnclosure_info().getPowerSupplyBays());
		//model.addLabelValuePair("PowerSuppliesPresent", rawData.getOa().getEnclosure_info().getPowerSuppliesPresent());

		model.addLabelValuePair("Power Supplies",   Integer.toString(rawData.getOa().getEnclosure_info().getPowerSuppliesPresent()) + " " + "of" + " " + 
													Integer.toString(rawData.getOa().getEnclosure_info().getPowerSupplyBays() ));
		
		//model.addLabelValuePair("FanBays", rawData.getOa().getEnclosure_info().getFanBays());
		//model.addLabelValuePair("FansPresent", rawData.getOa().getEnclosure_info().getFansPresent());

		model.addLabelValuePair("Fans", Integer.toString(rawData.getOa().getEnclosure_info().getFansPresent()) + " " + "of" + " " +
										Integer.toString(rawData.getOa().getEnclosure_info().getFanBays()) );
		
		model.addLabelValuePair("Power Consumed", rawData.getOa().getPower().getPowerConsumed() + " " + "W");
		
        log.debug("InfrastructureSummaryAdapter the end" );
		log.debug(model.toString());
		return model;
	}

	@Override
	public LabelValueListModel getEmptyModel()
	{
		return new LabelValueListModel();
	}

}
