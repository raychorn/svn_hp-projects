package com.hp.asi.hpic4vc.server.provider.adapter;

import com.hp.asi.hpic4vc.provider.adapter.DataAdapter;
import com.hp.asi.hpic4vc.provider.model.LabelValueListModel;
import com.hp.asi.hpic4vc.server.provider.data.HostSummaryResult;

public class HostSummaryPortletAdapter extends DataAdapter<HostSummaryResult, LabelValueListModel>
{
	private static final String SERVICE_NAME = "services/host/hostsummary";
	
	public HostSummaryPortletAdapter()
	{
		super(HostSummaryResult.class);
    }

	@Override
	public String getServiceName()
	{
		return SERVICE_NAME;
	}

	@Override
	public LabelValueListModel formatData(HostSummaryResult rawData)
	{
        log.debug("HostSummaryPortletAdapter beginning" );
        
        LabelValueListModel model = new LabelValueListModel();
		
		if (rawData.hasError()) {
            log.debug("HostSummaryResults had an error message.  " +
                    "Returning a HostSummaryModel with the error message");
            model.errorMessage = rawData.getErrorMessage();
            return model;
        }
		
		if(rawData.getVmware_name() == null) //Maybe not the best check
		{
			model.errorMessage = "No Data in Host Summary Result.";
			return model;
		}
		
		model.addLabelValuePair("Server Name", rawData.getIlo_server_name());	
		if(rawData.getBlade())
		{
			model.addLabelValuePair("Enclosure", rawData.getEnclosure());
			model.addLabelValuePair("Bay", rawData.getBay());
		}
		model.addLabelValuePair("iLO Address", rawData.getIlo_address());	
		
		String bundleStatus = "";
		if(rawData.getHp_bundle_status() != null && rawData.getHp_bundle_status())
		{
			bundleStatus = "Installed";
		}else if(rawData.getHp_bundle_status() != null && !rawData.getHp_bundle_status()){
			bundleStatus = "Not Installed";  	
		}else {
		   bundleStatus = "";
		}	
		model.addLabelValuePair("HP Insight Provider Bundle", bundleStatus);
		
        log.debug("HostSummaryPortletAdapter the end" );
		log.debug(model.toString());
		return model;
	}

	@Override
	public LabelValueListModel getEmptyModel()
	{
		return new LabelValueListModel();
	}

}
