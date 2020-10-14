package com.hp.asi.hpic4vc.provider.api;

import java.util.List;

import com.hp.asi.hpic4vc.provider.error.InitializationException;
import com.vmware.vim25.ObjectContent;
import com.vmware.vise.usersession.UserSession;

public interface SessionManager {
    
    /**
     * Gets the locale as a String for the session.
     * @return  String representing the locale (ex: en_US).
     */
    public String getLocale();

    /**
     * Looks for the URL location of the vCenter service where the product is 
     * installed.  Attempts to find the registered service by matching the 
     * serverGuid.  If the serverGuid is null, it returns the first service
     * URL it finds (since we are currently supporting one service per client).  
     * 
     * @param serverGuid  The serverGuid of the vCenter to locate. 
     * @return            The URL of the matching vCenter service running.  
     *                    If serverGuid is null, returns the first service
     *                    URL provided.
     */
    public String getServiceUrl(final String serverGuid);
    
    /**
     * Attempts to find the sessionId associated with the provided serverGuid.  
     * If the serverGuid is null, it locates the first sessionId found.
     * 
     * @param serverGuid
     * @return
     */
public String getSessionId(final String serverGuid);
    
    /**
     * Gets the base URL in order to call the web services.
     * 
     * @param sessionId
     * @param serverGuid
     * @return
     * @throws InitializationException  Thrown if sessionId is null or if unable
     *                                  to determine the location where the web 
     *                                  services are running.
     */
    public String getWSURLHostname(final String sessionId, 
                                   final String serverGuid) 
                                           throws InitializationException;

	public String getExtensionServerThumbprint(String sessionId, String serverGuid) throws InitializationException;

	public boolean isDeveloperMode();
	
	/**
	 * Gets the VM object contents
	 * @param moRef
	 * @param sessionId
	 * @param serverGuid
	 * @return
	 * @throws InitializationException
	 */
	public List<ObjectContent> getVMObjectContents(String moRef, String sessionId, String serverGuid) throws InitializationException;
	
	/**
	 * 
	 * @return
	 */
	public UserSession getUserSession();
	
}
