package com.hp.asi.hpic4vc.server.provider.adapter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import com.hp.asi.hpic4vc.provider.adapter.DataAdapter;
import com.hp.asi.hpic4vc.provider.error.InitializationException;
import com.hp.asi.hpic4vc.provider.impl.SessionManagerImpl;
import com.hp.asi.hpic4vc.server.provider.data.ClusterSummaryHostConfigOverview;
import com.hp.asi.hpic4vc.server.provider.data.HostConfigClusterMismatchSummaryResult;
import com.hp.asi.hpic4vc.server.provider.data.SummaryClusterMembersData;
import com.hp.asi.hpic4vc.server.provider.model.HostConfigClusterSummaryModel;


public class HostConfClusterSummaryDataAdapter extends DataAdapter<HostConfigClusterMismatchSummaryResult, HostConfigClusterSummaryModel>{
    
    private static final String SERVICE_NAME = "services/net/clusterMismatchSummary";
    private static final int DEFAULT_TIMEOUT_SECONDS = 300;
	 private static final int FIVE_MINUTES_IN_SECONDS = 300;
    public HostConfClusterSummaryDataAdapter() {
        super(HostConfigClusterMismatchSummaryResult.class);
    }
    
    @Override
    public String getServiceName () {
        return SERVICE_NAME;
    }

	@Override
	public HostConfigClusterSummaryModel formatData(HostConfigClusterMismatchSummaryResult rawData) {
		// TODO Auto-generated method stub
		HostConfigClusterSummaryModel model = new HostConfigClusterSummaryModel();		
		 List<ClusterSummaryHostConfigOverview> clusterSummaryHostConfigOverview= rawData.getClusterConfigOverview();
		 List<SummaryClusterMembersData> summaryClusterMembersData = clusterSummaryHostConfigOverview.get(0).getClusterMembers();
		 String refHostName=clusterSummaryHostConfigOverview.get(0).getRefHost();
		 String clustMismatch=clusterSummaryHostConfigOverview.get(0).getConfigStatus();		 
		
		 model.configStatus=clustMismatch;
		 model.refHostName=refHostName;
		 for(SummaryClusterMembersData hostconfMismatch : summaryClusterMembersData){	 						 
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
public HostConfigClusterSummaryModel getEmptyModel() {
	return null;
}
public int getTimeoutSeconds() {
    if (this.sessionInfo.isDeveloperMode()) {
        return FIVE_MINUTES_IN_SECONDS;
    }
    return DEFAULT_TIMEOUT_SECONDS;
}
}
