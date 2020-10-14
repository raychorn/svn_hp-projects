package com.hp.asi.hpic4vc.server.provider.adapter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import com.hp.asi.hpic4vc.provider.adapter.DataAdapter;
import com.hp.asi.hpic4vc.provider.error.InitializationException;
import com.hp.asi.hpic4vc.provider.impl.SessionManagerImpl;
import com.hp.asi.hpic4vc.server.provider.data.ClusterConfigDetailsData;
import com.hp.asi.hpic4vc.server.provider.data.ClusterMembersData;
import com.hp.asi.hpic4vc.server.provider.data.HostConfigClusterMismatchResult;
import com.hp.asi.hpic4vc.server.provider.model.HostConfigClusterMismatchModel;


public class HostConfClusterMismatchDataAdapter extends DataAdapter<HostConfigClusterMismatchResult, HostConfigClusterMismatchModel>{
    
    private static final String SERVICE_NAME = "services/net/clusterMismatch";
    private static final int DEFAULT_TIMEOUT_SECONDS = 300;
	 private static final int FIVE_MINUTES_IN_SECONDS = 300;
    public HostConfClusterMismatchDataAdapter() {
        super(HostConfigClusterMismatchResult.class);
    }
    
    @Override
    public String getServiceName () {
        return SERVICE_NAME;
    }

	@Override
	public HostConfigClusterMismatchModel formatData(HostConfigClusterMismatchResult rawData) {
		// TODO Auto-generated method stub
		HostConfigClusterMismatchModel model = new HostConfigClusterMismatchModel();		
		 List<ClusterConfigDetailsData> clusterConfigDetailsData= rawData.getClusterConfigDetails();
		 List<ClusterMembersData> clusterMembersData = clusterConfigDetailsData.get(0).getClusterMembers();
		 String refHostName=clusterConfigDetailsData.get(0).getRefHost();
		 String clustMismatch=clusterConfigDetailsData.get(0).getConfigStatus();
		 String clusterUuid = clusterConfigDetailsData.get(0).getClusterUuid();
		 model.clusterUuid =clusterUuid; 
		 model.clustMismatch=clustMismatch;
		 model.refHostName=refHostName;
		 for(ClusterMembersData hostconfMismatch : clusterMembersData){	 						 
			 model.mismatch.add(hostconfMismatch);			
			}
				
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
		 log.info("clusterMismatch service: " + url);
        return url;       
    }	
	
	 public String getQueryParameters () {
	        return getBaseParameters() + "&moref=" + this.sessionInfo.getMoref();
	    }
	    
	 public String getBaseParameters () {	    	
	    	
		 String morefStr= this.sessionInfo.getMoref();
		 String clusterMoID=null;
		 if (morefStr.contains(":")) {
	            int parameterSeparator = morefStr.indexOf(':');
	            clusterMoID  = morefStr.substring(parameterSeparator + 1);	           
	        }
		 
		 return '"'+"vmmUuid=" + this.sessionInfo.getServerGuid()+'"'
				 + "&filter="+'"'+"clusterMoid="+clusterMoID+'"'
        		 + "&filter="+'"'+"sessionID="+this.sessionInfo.getSessionId()+'"';      		
	        
	    }

@Override
public HostConfigClusterMismatchModel getEmptyModel() {
	return null;
}
public int getTimeoutSeconds() {
    if (this.sessionInfo.isDeveloperMode()) {
        return FIVE_MINUTES_IN_SECONDS;
    }
    return DEFAULT_TIMEOUT_SECONDS;
}
}
