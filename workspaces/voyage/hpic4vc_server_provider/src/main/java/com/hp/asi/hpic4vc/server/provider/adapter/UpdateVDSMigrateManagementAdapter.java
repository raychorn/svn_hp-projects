package com.hp.asi.hpic4vc.server.provider.adapter;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;

import com.hp.asi.hpic4vc.provider.adapter.PostAdapter;
import com.hp.asi.hpic4vc.provider.error.InitializationException;
import com.hp.asi.hpic4vc.provider.impl.SessionManagerImpl;
import com.hp.asi.hpic4vc.provider.model.StringModel;

public class UpdateVDSMigrateManagementAdapter extends PostAdapter<String, StringModel> {   
    private static final String SERVICE_NAME = "services/net/vdsMigrateManagement";
    private String   data;
    
    public UpdateVDSMigrateManagementAdapter () {
    	super();
    }

    public UpdateVDSMigrateManagementAdapter (String data) {
    	super();
    	this.data = data;
    }

    @Override
    public String getServiceName () {
        return SERVICE_NAME;
    }

    @Override
    public List<NameValuePair> getPostParams () {
    	return null;
    }

    public HttpEntityEnclosingRequestBase getHttpRequest(final String urlString) throws UnsupportedEncodingException {
        HttpEntityEnclosingRequestBase httpPut = new HttpPut(urlString);        
        StringEntity entity = new StringEntity(this.data, "UTF-8");
        entity.setContentType("application/json");
        httpPut.setEntity(entity);        
        return httpPut;
    }   

	@Override
	public StringModel formatData(String result) {
		StringModel model = new StringModel();
        model.data = result;
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
	    		    	
	        return '"'+"sessionID="+this.sessionInfo.getSessionId()+'"'
	        	  + "&filter="+'"'+"vmmUuid=" + this.sessionInfo.getServerGuid()+'"'
	              + "&locale="     + this.sessionInfo.getLocale();	                  
	    }
	@Override
	public StringModel getEmptyModel() {
		// TODO Auto-generated method stub
		return new StringModel();
	}

}
