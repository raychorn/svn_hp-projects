package com.hp.asi.hpic4vc.server.provider.adapter;

import com.hp.asi.hpic4vc.provider.adapter.DataAdapter;
import com.hp.asi.hpic4vc.provider.model.LabelValueListModel;
import com.hp.asi.hpic4vc.server.provider.data.HostSummaryResult;

public class HostSummaryAdapter extends DataAdapter<HostSummaryResult, LabelValueListModel>
{
	private static final String SERVICE_NAME = "services/host/hostsummary";
	
	public HostSummaryAdapter()
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
        log.debug("HostSummaryAdapter beginning" );
        
        LabelValueListModel model = new LabelValueListModel();
		
		if (rawData.hasError()) {
            log.debug("HostSummaryResults had an error message.  " +
                    "Returning a HostSummaryModel with the error message");
            model.errorMessage = rawData.getErrorMessage();
            return model;
        }
		
		if(rawData.getVmware_name() == null) //Maybe not the best check
		{
			model.errorMessage = rawData.getErrorMessage();
			return model;
		}
		
		model.addLabelValuePair("Server Name", rawData.getIlo_server_name());
		model.addLabelValuePair("VMWare Host Name", rawData.getVmware_name());
		model.addLabelValuePair("Product Name", rawData.getProduct_name());
		model.addLabelValuePair("iLO Address", rawData.getIlo_address());
		model.addLabelValuePair("iLO License Type", rawData.getIlo_license_type());
		model.addLabelValuePair("iLO Firmware Version", rawData.getIlo_firmware_version());
		model.addLabelValuePair("UID Status", rawData.getUid_status());
		if (rawData.getStatus() != null){
			model.addLabelValuePair("Host Power Status", rawData.getPower_status());
		}
		model.addLabelValuePair("Total CPU", rawData.getTotal_cpu());
		model.addLabelValuePair("Total Memory", rawData.getTotal_memory());
		model.addLabelValuePair("Total NICs", Integer.toString(rawData.getTotal_nics()));
		model.addLabelValuePair("Total Smart Array", rawData.getTotal_storage());
		model.addLabelValuePair("System ROM", rawData.getRom());
		model.addLabelValuePair("Backup ROM", rawData.getBackup_rom());
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
        log.debug("HostSummaryAdapter the end" );
		log.debug(model.toString());
		return model;
	}

	@Override
	public LabelValueListModel getEmptyModel()
	{
		return new LabelValueListModel();
	}

}
