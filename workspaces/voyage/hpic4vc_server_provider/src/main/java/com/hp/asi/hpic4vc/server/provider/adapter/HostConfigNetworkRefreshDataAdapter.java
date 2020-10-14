package com.hp.asi.hpic4vc.server.provider.adapter;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import com.hp.asi.hpic4vc.provider.adapter.DataAdapter;
import com.hp.asi.hpic4vc.provider.error.InitializationException;
import com.hp.asi.hpic4vc.provider.impl.SessionManagerImpl;
import com.hp.asi.hpic4vc.server.provider.data.HostConfigurationData;
import com.hp.asi.hpic4vc.server.provider.data.HostConfigurationResult;
import com.hp.asi.hpic4vc.server.provider.model.HostConfigModel;

public class HostConfigNetworkRefreshDataAdapter extends
		DataAdapter<HostConfigurationResult, HostConfigModel> {
	
	 private static final String SERVICE_NAME = "services/net/refreshNetwork";
	 private static final int DEFAULT_TIMEOUT_SECONDS = 60;
	 private static final int FIVE_MINUTES_IN_SECONDS = 300;
	public HostConfigNetworkRefreshDataAdapter() {
		super(HostConfigurationResult.class);
	}

	@Override
	public String getServiceName() {
		return SERVICE_NAME;
	}

	@Override
	public HostConfigModel formatData(HostConfigurationResult rawData) {
		// TODO Auto-generated method stub
		List<HostConfigurationData> networklist = rawData.getNetworks();

		HostConfigModel model = new HostConfigModel();
		for (HostConfigurationData hostconf : networklist) {
			if (!model.networks.contains(hostconf.getNetworkName())) {
				model.networks.add(hostconf);
			}

		}
		return model;
	}
	protected String getWsUrl () throws InitializationException {
		String url = null;		
		String hostname = SessionManagerImpl.getInstance().getWSURLHostname
                (this.sessionInfo.getSessionId(),
                 this.sessionInfo.getServerGuid());			
		try {
			url= hostname + SERVICE_NAME + "?filter="+ URLEncoder.encode(getQueryParameters(),"UTF-8");
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
	    	
	        return '"'+"action=refresh"+'"'
	        	  + "&filter="+'"'+"sessionID="+this.sessionInfo.getSessionId()+'"'
	        	  + "&filter="+'"'+"vmmUuid=" + this.sessionInfo.getServerGuid()+'"'	        	 
	              + "&locale="     + this.sessionInfo.getLocale();
	              //+ "&serviceUrl=" + this.sessionInfo.getServiceUrl();	       
	    }
	@Override
	public HostConfigModel getEmptyModel() {
		// TODO Auto-generated method stub
		return null;
	}
	public int getTimeoutSeconds() {
	    if (this.sessionInfo.isDeveloperMode()) {
	        return FIVE_MINUTES_IN_SECONDS;
	    }
	    return DEFAULT_TIMEOUT_SECONDS;
	}
}
