package com.hp.asi.hpic4vc.provider.adapter;

import java.util.Locale;
import java.util.concurrent.Callable;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

import com.hp.asi.hpic4vc.provider.data.ConfigurationData;
import com.hp.asi.hpic4vc.provider.error.CommunicationException;
import com.hp.asi.hpic4vc.provider.error.InitializationException;
import com.hp.asi.hpic4vc.provider.error.NullDataException;
import com.hp.asi.hpic4vc.provider.impl.SessionInfo;
import com.hp.asi.hpic4vc.provider.impl.SessionManagerImpl;
import com.hp.asi.hpic4vc.provider.locale.I18NProvider;
import com.hp.asi.hpic4vc.provider.locale.LocaleHelper;
import com.hp.asi.hpic4vc.provider.model.BaseModel;
import com.hp.asi.hpic4vc.provider.utils.ssl.SslThumbprintVerifier;
import com.hp.asi.hpic4vc.provider.utils.ssl.ThumbprintTrustManager;

/**
* @param <S>  The type of object to return to the hpic4vc_ui.
* */
public abstract class BaseAdapter<T, S extends BaseModel> implements Callable<S> {
    private static final int DEFAULT_TIMEOUT_SECONDS = 30;
    private static final int FIVE_MINUTES_IN_SECONDS = 300;
    protected HttpClient    	httpClient;
    protected Locale        	locale;
    protected SessionInfo   	sessionInfo;
    protected Log           	log;
    protected String        	urlString;
    protected I18NProvider  	i18nProvider;
    protected ConfigurationData configData;
    
    public BaseAdapter() {
        this.httpClient   = null;
        this.locale       = null;
        this.sessionInfo  = null;
        this.log          = LogFactory.getLog(this.getClass());
        this.urlString    = null;
        this.i18nProvider = I18NProvider.getInstance();
        this.configData	  = ConfigurationData.getInstance();
    }
    
    public final void initialize (final SessionInfo sessionInfo) {
        this.sessionInfo = sessionInfo;
        this.locale      = LocaleHelper.getLocale(sessionInfo.getLocale());
        this.httpClient  = getNewHttpClient();
        if (null != locale) {
            log.debug("BaseAdapter initialzed with locale " + locale.getDisplayName());
        } else {
            log.debug("Locale object could not be located for " + sessionInfo.getLocale());
        }
    }
    
    /**
     * Gets an empty object model extending from BaseModel.  This is so
     * that, in case of errors, the object can be created and the error
     * message can be set from the abstract DataAdapter class.
     * 
     * @return    An object extending from BaseModel.
     */
    public abstract S getEmptyModel();
    
    /**
     * Returns the part of the url that is required for making a web
     * service call for data or to post data.  For example, to get HBA
     * data the servie name is "hbas".
     * 
     * @return  The web service name for retrieving or posting data.
     */
    public abstract String getServiceName();
    
    /**
     * Determines how long to wait before timing out when making the web service call.
     * @return  A time in seconds to wait before timing out.
     */
    public int getTimeoutSeconds() {
        if (this.sessionInfo.isDeveloperMode()) {
            return FIVE_MINUTES_IN_SECONDS;
        }
        return DEFAULT_TIMEOUT_SECONDS;
    }
    
    /**
     * Formats the non-null data object into an object extending from BaseModel
     * for the hpic4vc_ui to read.
     * 
     * @param rawData
     *            The data received from the web service and mapped to a data
     *            object. This will be always be non-null; null data is handled
     *            by the base class.
     * 
     * @return Returns an object extending from BaseModel. If an error occurs,
     *         the object's errorMessage will be set. This method should not
     *         return null.
     */
    public abstract S formatData (final T rawData);
    
    /**
     * Pulled this out in case a child class would like to override how
     * the URL is created for the web service call to get JSON data.
     * @return
     * @throws InitializationException
     */
    protected String getWsUrl () throws InitializationException {
        String hostname = SessionManagerImpl.getInstance().getWSURLHostname
                (this.sessionInfo.getSessionId(),
                 this.sessionInfo.getServerGuid());
        
        if (hostname == null) {
            return null;
        }
        
        return hostname + getServiceName() + sessionInfo.getQueryParameters();  
    }
    
    /**
     * Checks the HTTP response for null response, null status, bad status,
     * and null entity.  In case of any issues, throws an exception for use
     * by the calling class.
     * 
     * @param response
     *               The response sent from the HTTP request.
     * @throws CommunicationException
     *               Thrown if 
     * @throws NullDataException
     */
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
            throw new CommunicationException(errorMsg);
        }
        
        if (null == response.getEntity()) {
            log.info(httpGetMsg + " had a null entity, throwing a NullDataException.");
            throw new NullDataException(errorMsg);
        }

    } 
    
    /**
     * Checks if the JSON string returned contains 404 Page Not Found.
     * @param jsonString
     * @return True if the page is an error page, false otherwise.
     */
    protected boolean isErrorPage(final String jsonString, final String urlString) {
        if (jsonString.startsWith("<html><head><title>")) {
            log.info("Bad Url: " + urlString + ".  Returning isErrorPage=true.");
            return true;
        }
        return false;
    }
    
    HttpClient getNewHttpClient() {      
        
        if(!isSSLRequired()) {
            return new DefaultHttpClient();
        }
        
        String thumbprint = null;
        try {
            thumbprint = sessionInfo.getUimServerThumbprint();
        } catch (InitializationException e) {
            String errorMessage = 
                    "Https connection not available for service " 
                    + this.getServiceName();            
            log.info(errorMessage);            
            return new DefaultHttpClient();
        }
        
        try {
                SSLContext context = SSLContext.getInstance("SSL");
                SslThumbprintVerifier verifier = new SslThumbprintVerifier();                           
    
                verifier.setThumbprint(thumbprint);
                ThumbprintTrustManager trustManager = new ThumbprintTrustManager(null, verifier);
                context.init(null, new TrustManager[] { trustManager }, null);

                SSLSocketFactory sf = new SSLSocketFactory(context);
                sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
                HttpParams params = new BasicHttpParams();
                SchemeRegistry registry = new SchemeRegistry();             
                registry.register(new Scheme("https", sf, 443));
                ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);
                return new DefaultHttpClient(ccm, params);
            } catch (Exception e) {
                return new DefaultHttpClient();
            } 
        } 
    
    
    protected boolean isSSLRequired (){
        
        try {
            String baseURL = SessionManagerImpl
                    .getInstance().getWSURLHostname(
                            sessionInfo.getSessionId(), 
                            sessionInfo.getServerGuid());
            
            if (baseURL.toLowerCase().startsWith("https")) {
                return true;
            }
        } catch (InitializationException e) {
            return false;
        }
        return false;   
        
    }  
}
