package com.hp.asi.hpic4vc.provider.adapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;

import com.hp.asi.hpic4vc.provider.error.CommunicationException;
import com.hp.asi.hpic4vc.provider.error.InitializationException;
import com.hp.asi.hpic4vc.provider.error.NullDataException;
import com.hp.asi.hpic4vc.provider.locale.I18NProvider;
import com.hp.asi.hpic4vc.provider.model.BaseModel;

public abstract class CredentialsPostAdapter<T, S extends BaseModel> extends BaseAdapter<T, S> {
	/*
	 *  Note: This base class is very similar to PostAdapter however this is by-design since credential handling
	 *   will be done quite differently for the 7.3 release.
	 */
    
    public CredentialsPostAdapter() {
        super();
    }
    
    public abstract StringEntity getPostParams();
    
    public abstract S formatData(final String response);
    
    @Override
    public S call () {
        S result = getEmptyModel();
        try {
            this.urlString = getWsUrl();
            log.debug("Service Url is " + urlString);
        } catch (InitializationException e) {
            String errorMessage = this.i18nProvider.getInternationalString
                    (locale, I18NProvider.Error_CannotMakeUrl);
            result.errorMessage = errorMessage;
            log.error(errorMessage);
            log.debug("Initialization exception for service " + this.getServiceName(), e);
            return result;
        }
       
        try {
            String returnMessage = sendRequest(urlString);
            log.debug("Setting the return data for " + this.getServiceName() + 
            		" to " + returnMessage);
            if (null == returnMessage) {
                String errorMessage = this.i18nProvider.getInternationalString
                        (locale, I18NProvider.Error_NoWsResponse);
                result.errorMessage = errorMessage;
                log.error(errorMessage);
                log.debug("Null data for " + this.getServiceName());
                return result;
            }
            
            result = formatData(returnMessage);
            
        } catch (Exception e) {
            log.debug("Caught exception: " + e.getMessage(), e);
           result.errorMessage = e.getMessage();
        }
        log.debug("Returning result from the provider " + result.toString());
        return result;
    }

    String sendRequest (final String urlString) throws NullDataException, CommunicationException {
        try {
            HttpRequestBase httpRequest = getHttpRequest(urlString);
            HttpResponse response = httpClient.execute(httpRequest);
            checkHttpResponseForErrors(response);
            
            HttpEntity entity     = response.getEntity();
            String result         = getResponse(entity.getContent());
            log.info("Data returned from the UIM PUT/POST command for " + 
                          this.getServiceName() + " is " + result);
            return checkForUimErrors(result);
        } catch (IOException e) {
            String errorMessage = this.i18nProvider.getInternationalString
                    (locale, I18NProvider.Error_NoWsResponse);
            log.info("httpGet for " + this.getServiceName() + " caught an IOException.  " +
                    "Throwing a CommunicationException.", e);
            throw new CommunicationException(errorMessage);
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
    }
    
    /**
     * This method is used by default to perform a POST.  Overwrite this method
     * to perform a PUT or to change the format of the POST request.
     * @param urlString
     * @return
     * @throws UnsupportedEncodingException
     */
    public HttpRequestBase getHttpRequest(final String urlString) throws UnsupportedEncodingException {
        HttpEntityEnclosingRequestBase httpPost = new HttpPost(urlString);
		httpPost.setHeader("Content-type", "application/json");
		httpPost.setHeader("Accept", "application/json");
		StringEntity entity = getPostParams();            
        httpPost.setEntity(entity);
        return httpPost;
    }
    
    String getResponse(final InputStream inputStream) throws IOException {
    	BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
        StringBuffer sb   = new StringBuffer();
        String line;
        while ( (line = in.readLine()) != null) {
            sb.append(line);
        }                
        in.close();
        return sb.toString();
    }

    String checkForUimErrors(final String result) throws CommunicationException {
        
        if (isErrorPage(result, urlString)) {
            log.error("Page for " + this.getServiceName() + 
                      " is Page Not Found.  Returning a null string.");
            return null;
        }
        
        if (isPythonError(result))	{
            String errorMessage = this.i18nProvider.getInternationalString
                    (locale, I18NProvider.Error_PythonException);
            log.error("Recieved a Python exception for " + this.getServiceName() + 
                      " , throwing a CommunicationException.");
            throw new CommunicationException(errorMessage);
        }
        return result;
    }
    
    boolean isPythonError(final String result) {
    	if (result.contains("exception")) {
    		log.debug("Detected an error response from the Python post command.");
    		return true;
    	}
    	
    	return false;
    }
}
