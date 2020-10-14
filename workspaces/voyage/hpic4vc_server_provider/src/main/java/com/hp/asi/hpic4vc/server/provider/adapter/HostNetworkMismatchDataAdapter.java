package com.hp.asi.hpic4vc.server.provider.adapter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import com.hp.asi.hpic4vc.provider.adapter.DataAdapter;
import com.hp.asi.hpic4vc.provider.error.InitializationException;
import com.hp.asi.hpic4vc.provider.impl.SessionManagerImpl;
import com.hp.asi.hpic4vc.server.provider.data.HostConfigDetailsMismatch;
import com.hp.asi.hpic4vc.server.provider.data.HostConfigMismatchDetailedView;
import com.hp.asi.hpic4vc.server.provider.data.HostConfigMismatchvSwitches;
import com.hp.asi.hpic4vc.server.provider.data.HostConfigNetworkMismatchResult;
import com.hp.asi.hpic4vc.server.provider.data.HostConfigurationMismatch;
import com.hp.asi.hpic4vc.server.provider.data.HostServerProfileData;
import com.hp.asi.hpic4vc.server.provider.data.ServerProfileConnections;
import com.hp.asi.hpic4vc.server.provider.model.HostConfigMismatchModel;

public class HostNetworkMismatchDataAdapter extends DataAdapter<HostConfigNetworkMismatchResult, HostConfigMismatchModel>{
    
    private static final String SERVICE_NAME = "services/net/networkMismatchHost";
    private String host;
    private static final int DEFAULT_TIMEOUT_SECONDS = 180;
    private static final int FIVE_MINUTES_IN_SECONDS = 300;
    public HostNetworkMismatchDataAdapter() {
        super(HostConfigNetworkMismatchResult.class);
    }
    public HostNetworkMismatchDataAdapter(String _host) {
		super(HostConfigNetworkMismatchResult.class);		
		this.host = _host;
	}
    @Override
    public String getServiceName () {
        return SERVICE_NAME;
    }

	@Override
	public HostConfigMismatchModel formatData(HostConfigNetworkMismatchResult rawData) {
		// TODO Auto-generated method stub
		
		 String vCProfileStatus = null;		
		 String serverStatus = null;		
		 String hostStatus =null;
		 String refHostMoid =null;
		 String refHostName=null;
		 HostConfigMismatchDetailedView hostConfigMismatchDetailedView = null;
		 HostServerProfileData hostServerProfileData=null;
		 List<ServerProfileConnections> serverProfileConnections = null;
		 HostConfigurationMismatch hostConfigurationMismatch=null;
		 HostConfigMismatchModel model = new HostConfigMismatchModel();		
		 List<HostConfigDetailsMismatch> hostConfigDetails= rawData.getHostConfigDetails();
		 String errorMessage=rawData.getMessage();
		 if(hostConfigDetails.size()!=0){
		 hostConfigurationMismatch = hostConfigDetails.get(0).getHostConfiguration();
		 hostStatus=hostConfigDetails.get(0).getConfigStatus();
		 refHostMoid=hostConfigDetails.get(0).getRefHostMoid();
		 refHostName=hostConfigDetails.get(0).getRefHostName();		
		 hostServerProfileData = hostConfigDetails.get(0).getServerProfile();
		 }
		 if(hostConfigurationMismatch!=null){
			 vCProfileStatus=hostConfigurationMismatch.getHostConfigStatus();
		 hostConfigMismatchDetailedView=hostConfigurationMismatch.getHostConfigDetailedView();
		 }
		  
		 if(hostConfigMismatchDetailedView!=null){
		 List<HostConfigMismatchvSwitches>mismatchData=hostConfigMismatchDetailedView.getvSwitches();
		 
		 for(HostConfigMismatchvSwitches hostconfMismatch : mismatchData){	 						 
			 model.mismatch.add(hostconfMismatch);			
			}
		 }
		 model.vCProfileStatus=vCProfileStatus;
		 model.hostStatus=hostStatus;
		 model.refHostMoid=refHostMoid;
		 model.refHostName=refHostName;
		 model.errorMessage=errorMessage;
		
		 if(hostServerProfileData!=null){
		 serverStatus=hostServerProfileData.getStatus();
		 serverProfileConnections=hostServerProfileData.getConnections();
		 }
		 if(serverProfileConnections!=null){
		 for(ServerProfileConnections hostconfMismatchprofile : serverProfileConnections){	 						 
			 model.connprofile.add(hostconfMismatchprofile);			
			}
		 }
		 model.serverStatus=serverStatus;	
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
public HostConfigMismatchModel getEmptyModel() {
	return null;
}
public int getTimeoutSeconds() {
    if (this.sessionInfo.isDeveloperMode()) {
        return FIVE_MINUTES_IN_SECONDS;
    }
    return DEFAULT_TIMEOUT_SECONDS;
}
}
