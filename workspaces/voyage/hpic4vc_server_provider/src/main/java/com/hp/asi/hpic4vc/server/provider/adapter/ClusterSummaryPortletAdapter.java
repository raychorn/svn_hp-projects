package com.hp.asi.hpic4vc.server.provider.adapter;

import com.hp.asi.hpic4vc.provider.adapter.DataAdapter;
import com.hp.asi.hpic4vc.provider.model.LabelValueListModel;
import com.hp.asi.hpic4vc.server.provider.data.ClusterSummaryResult;

public class ClusterSummaryPortletAdapter extends DataAdapter<ClusterSummaryResult,LabelValueListModel> {

    private static final String SERVICE_NAME = "orservices/host/swd/clustersummary";
	
	public ClusterSummaryPortletAdapter()
	{
		super(ClusterSummaryResult.class);
    }

	@Override
	public String getServiceName()
	{
		return SERVICE_NAME;
	}

	@Override
	public LabelValueListModel formatData(ClusterSummaryResult rawData)
	{
        log.debug("HostSummaryAdapter beginning" );
        
        LabelValueListModel model = new LabelValueListModel();
		
		if (rawData.hasError()) {
            log.debug("ClusterSummaryResults had an error message.  " +
                    "Returning a ClusterSummaryModel with the error message");
            model.errorMessage = rawData.getErrorMessage();
            return model;
        }
		model.addLabelValuePair("Cluster Name", rawData.getCluster_name());
		model.addLabelValuePair("Hosts", rawData.getHosts());
		model.addLabelValuePair("VMs",rawData.getVms());
		model.addLabelValuePair("Blades",rawData.getBlades());
		
        log.debug("ClusterSummaryAdapter the end" );
		log.debug(model.toString());
		return model;
	}

	@Override
	public LabelValueListModel getEmptyModel()
	{
		return new LabelValueListModel();
	}

}
