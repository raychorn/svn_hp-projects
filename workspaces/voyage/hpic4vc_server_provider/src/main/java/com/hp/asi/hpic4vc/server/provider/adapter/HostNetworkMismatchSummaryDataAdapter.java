package com.hp.asi.hpic4vc.server.provider.adapter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import com.hp.asi.hpic4vc.provider.adapter.DataAdapter;
import com.hp.asi.hpic4vc.provider.error.InitializationException;
import com.hp.asi.hpic4vc.provider.impl.SessionManagerImpl;
import com.hp.asi.hpic4vc.server.provider.data.HostConfigNetworkMismatchSummaryResult;
import com.hp.asi.hpic4vc.server.provider.data.NetworkSummaryHostConfigOverview;
import com.hp.asi.hpic4vc.server.provider.model.HostConfigSummaryMismatchModel;

public class HostNetworkMismatchSummaryDataAdapter extends DataAdapter<HostConfigNetworkMismatchSummaryResult, HostConfigSummaryMismatchModel>{
    
    private static final String SERVICE_NAME = "services/net/networkMismatchSummaryHost";
    private String host;
    private static final int DEFAULT_TIMEOUT_SECONDS = 180;
    private static final int FIVE_MINUTES_IN_SECONDS = 300;
    public HostNetworkMismatchSummaryDataAdapter() {
        super(HostConfigNetworkMismatchSummaryResult.class);
    }
   
    @Override
    public String getServiceName () {
        return SERVICE_NAME;
    }

	@Override
	public HostConfigSummaryMismatchModel formatData(HostConfigNetworkMismatchSummaryResult rawData) {
		// TODO Auto-generated method stub
		HostConfigSummaryMismatchModel model = new HostConfigSummaryMismatchModel();		
		List<NetworkSummaryHostConfigOverview> networkSummaryHostConfigOverview= rawData.getHostConfigOverview();
		String errorMessage= rawData.getMessage();
		if(networkSummaryHostConfigOverview.size()!=0){
			String hostConfigurationMismatch = networkSummaryHostConfigOverview.get(0).getStatus();	
			model.configStatus=hostConfigurationMismatch;
			 for(NetworkSummaryHostConfigOverview hostconfMismatch : networkSummaryHostConfigOverview){	 						 
				 model.mismatch.add(hostconfMismatch);			
				}		
		}		
		model.errorMessage=errorMessage;
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
	    	
		 String morefStr= this.sessionInfo.getMoref();
		 //String vmId=null;
		 if(morefStr!=null){
		 if (morefStr.contains(":") && morefStr.contains("HostSystem")) {
	            int parameterSeparator = morefStr.indexOf(':');
	            host  = morefStr.substring(parameterSeparator + 1);	           
	        }
		 }
	        return '"'+"hostMoid=" + host+'"'
	        		 + "&filter="+'"'+"sessionID="+this.sessionInfo.getSessionId()+'"'
	        		 + "&filter="+'"'+"vmmUuid=" + this.sessionInfo.getServerGuid()+'"';   
	    }

@Override
public HostConfigSummaryMismatchModel getEmptyModel() {
	return null;
}
public int getTimeoutSeconds() {
    if (this.sessionInfo.isDeveloperMode()) {
        return FIVE_MINUTES_IN_SECONDS;
    }
    return DEFAULT_TIMEOUT_SECONDS;
}
}
