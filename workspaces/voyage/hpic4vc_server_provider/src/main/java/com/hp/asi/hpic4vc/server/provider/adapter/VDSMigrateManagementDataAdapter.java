package com.hp.asi.hpic4vc.server.provider.adapter;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.hp.asi.hpic4vc.provider.adapter.DataAdapter;
import com.hp.asi.hpic4vc.provider.error.InitializationException;
import com.hp.asi.hpic4vc.provider.impl.SessionManagerImpl;
import com.hp.asi.hpic4vc.server.provider.data.VDSMigrateManagementResult;
import com.hp.asi.hpic4vc.server.provider.model.VDSMigrateManagementModel;


public class VDSMigrateManagementDataAdapter extends DataAdapter<VDSMigrateManagementResult, VDSMigrateManagementModel>{
   
    private static final String SERVICE_NAME = "services/net/vdsMigrateManagement";
    
    public VDSMigrateManagementDataAdapter() {
        super(VDSMigrateManagementResult.class);
    }
    
    @Override
    public String getServiceName () {
        return SERVICE_NAME;
    }

	@Override
	public VDSMigrateManagementModel formatData(VDSMigrateManagementResult rawData) {		
		VDSMigrateManagementModel model = new VDSMigrateManagementModel();		
		model.type =rawData.getType();		
		model.settingName = rawData.getSettingName();
		model.selectedValue= rawData.getSelectedValue();
		model.supportedValues=rawData.getSupportedValues();		
		return model;
		
	}	
	protected String getWsUrl () throws InitializationException {
		String url = null;		
		String hostname = SessionManagerImpl.getInstance().getWSURLHostname
                (this.sessionInfo.getSessionId(),
                 this.sessionInfo.getServerGuid());			
		try {
			url= hostname + SERVICE_NAME + "?filter="+URLEncoder.encode(getQueryParameters(),"UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			 log.error("Encode url " + this.getQueryParameters()
	                    + " caught an UnsupportedEncodingException", e);
		}				 
        return url;
       
    }	
	
	 public String getQueryParameters () {
	        return getBaseParameters() + "&moref=" + this.sessionInfo.getMoref();
	    }
	    
	    public String getBaseParameters () {	    	
	    		    	
	        return '"'+"sessionID="+this.sessionInfo.getSessionId()+'"'
	        	  + "&filter="+'"'+"vmmUuid=" + this.sessionInfo.getServerGuid()+'"'	
	              + "&locale="     + this.sessionInfo.getLocale();
	              //+ "&serviceUrl=" + this.sessionInfo.getServiceUrl();	       
	    }
	
@Override
public VDSMigrateManagementModel getEmptyModel() {
	// TODO Auto-generated method stub
	return null;
}
}
