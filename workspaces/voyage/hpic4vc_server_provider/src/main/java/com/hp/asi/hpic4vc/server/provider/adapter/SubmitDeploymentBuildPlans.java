package com.hp.asi.hpic4vc.server.provider.adapter;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;

import com.hp.asi.hpic4vc.provider.adapter.PostAdapter;
import com.hp.asi.hpic4vc.provider.error.InitializationException;
import com.hp.asi.hpic4vc.provider.impl.SessionManagerImpl;
import com.hp.asi.hpic4vc.provider.model.StringModel;

public class SubmitDeploymentBuildPlans extends PostAdapter<String, StringModel> {
	
	  private static final String SERVICE_NAME = "rest/os-deployment-build-plans";
	 
	  private String data;
	  
	  public SubmitDeploymentBuildPlans (String data) {
	    	super();
	    	this.data = data;
	    }

	  @Override
	    public String getServiceName () {
	        return SERVICE_NAME;
	    }
	  
	  
	  
	  @Override
	    public List<NameValuePair> getPostParams () {
	    	String kvPairs[] = data.split(":");
	        List<NameValuePair> params = new ArrayList<NameValuePair>();
	        for (String kvPair:kvPairs){
	        	String kv[] = kvPair.split("=");
	        	if (kv.length == 2){
	        		try {
	        			params.add(new BasicNameValuePair(kv[0], kv[1]));
	        		}
	        		catch (Exception e){
	        			log.debug(e);
	        		}
	        	}
	        }
	        return params;
	    }
	  
	  	public HttpEntityEnclosingRequestBase getHttpRequest(final String urlString) throws UnsupportedEncodingException {
	        HttpEntityEnclosingRequestBase httpPut = new HttpPut(urlString);
	        //List<NameValuePair> params = getPostParams();    
	        StringEntity entity = new StringEntity(this.data, "UTF-8");
	        entity.setContentType("application/json");
	        httpPut.setEntity(entity);
	        return httpPut;
	  	}
	  	
	    protected String getWsUrl () throws InitializationException {
			String url;
			String developerHostname = "http://localhost:8085/";
			String hostname = SessionManagerImpl.getInstance().getWSURLHostname
	                (this.sessionInfo.getSessionId(),
	                 this.sessionInfo.getServerGuid());
			if(hostname.contains("localhost")){
				hostname=developerHostname;
			}
			url= hostname + SERVICE_NAME + sessionInfo.getQueryParameters();
	        return url;
	       
	    }
	    
	    @Override
		public StringModel formatData(String result) {
			// TODO Auto-generated method stub
			StringModel model = new StringModel();
	        model.data = result;
	        return model;
		}

		@Override
		public StringModel getEmptyModel() {
			// TODO Auto-generated method stub
			 return new StringModel();
		}


}
