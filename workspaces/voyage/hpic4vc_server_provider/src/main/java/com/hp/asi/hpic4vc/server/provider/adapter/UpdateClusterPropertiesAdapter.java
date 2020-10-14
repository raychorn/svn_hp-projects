package com.hp.asi.hpic4vc.server.provider.adapter;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.hp.asi.hpic4vc.provider.adapter.PostAdapter;
import com.hp.asi.hpic4vc.provider.error.CommunicationException;
import com.hp.asi.hpic4vc.provider.error.NullDataException;
import com.hp.asi.hpic4vc.provider.locale.I18NProvider;
import com.hp.asi.hpic4vc.provider.model.StringModel;

public class UpdateClusterPropertiesAdapter extends PostAdapter<String, StringModel> {
	

	private static final String SERVICE_NAME = "settings/clusterproperties";
	public  Boolean RESPONSE_ERROR = false;    
	private String   data;
	private String errorMessage;
	
	
	 public UpdateClusterPropertiesAdapter () {
	   	super();
	 }

	 public UpdateClusterPropertiesAdapter (String data) {
	    super();
	    this.data = data;
	}
	 
	@Override
	public String getServiceName () {
	    return SERVICE_NAME;
	}
	
    @Override
    public StringModel getEmptyModel () {
        return new StringModel();
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
	
    @Override
    public StringModel formatData (final String result) {
        StringModel model = new StringModel();
        if(RESPONSE_ERROR){
        	 model.errorMessage = result;
        }else {
        	 model.data = result;
        }
       
        return model;
    }
    
    @Override
	protected void checkHttpResponseForErrors(final HttpResponse response)
	            throws CommunicationException, NullDataException {
    	
    	String errorMsg   = i18nProvider.getInternationalString
                 (locale, I18NProvider.Error_NoWsResponse);
    	 
    	String httpGetMsg = "HttpResponse for " + this.getServiceName();
    	 
    	if (null == response) {
            log.error(httpGetMsg + " had a null response.  Throwing a NullDataException.");
            throw new NullDataException(errorMsg);
        }
        
        if (null == response.getStatusLine()) {
            log.error(httpGetMsg + " had a null statusLine in the response.  " +
                    "Throwing a NullDataException.");
            throw new NullDataException(errorMsg);
        }
        
        if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
            log.error(httpGetMsg + " had status code "
                    + response.getStatusLine().getStatusCode()
                    + " Throwing a CommunicationException.");
            RESPONSE_ERROR =true;
        }
        
        if (null == response.getEntity()) {
            log.info(httpGetMsg + " had a null entity, throwing a NullDataException.");
            throw new NullDataException(errorMsg);
        }
		 
		 
		  
	}

}
