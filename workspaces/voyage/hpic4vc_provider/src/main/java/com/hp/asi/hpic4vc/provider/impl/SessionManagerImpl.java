package com.hp.asi.hpic4vc.provider.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.MessageContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.asi.hpic4vc.provider.api.SessionManager;
import com.hp.asi.hpic4vc.provider.error.InitializationException;
import com.hp.asi.hpic4vc.provider.utils.ssl.SslThumbprintVerifier;
import com.hp.asi.hpic4vc.provider.utils.ssl.ThumbprintTrustManager;
import com.vmware.vim25.Extension;
import com.vmware.vim25.ExtensionServerInfo;
import com.vmware.vim25.InvalidPropertyFaultMsg;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.ObjectContent;
import com.vmware.vim25.ObjectSpec;
import com.vmware.vim25.PropertyFilterSpec;
import com.vmware.vim25.RuntimeFaultFaultMsg;
import com.vmware.vim25.ServiceContent;
import com.vmware.vim25.VimPortType;
import com.vmware.vim25.VimService;
import com.vmware.vise.security.ClientSessionEndListener;
import com.vmware.vise.usersession.ServerInfo;
import com.vmware.vise.usersession.UserSession;
import com.vmware.vise.usersession.UserSessionService;

public class SessionManagerImpl implements SessionManager, ClientSessionEndListener {

    private static final SessionManager INSTANCE = new SessionManagerImpl();
    public static SessionManager getInstance() {
        return INSTANCE;
    }
    
    /** object for access to all of the methods defined in the vSphere API */
    private static VimPortType vimPort;
    
    private static final String COM_HP_IC4VC_NGC = "com.hp.ic4vc.ngc";
    private static final String COM_HP_IC4VC_NGC_ALL = "com.hp.ic4vc.ngc.all";
    private static final String COM_HP_IC4VC_NGC_SERVER = "com.hp.ic4vc.ngc.server";
    private static final String COM_HP_IC4VC_NGC_STORAGE = "com.hp.ic4vc.ngc.storage";
    private static final String SERVICE_INSTANCE = "ServiceInstance";
    private static final ManagedObjectReference SVC_INSTANCE_REF =
            new ManagedObjectReference();
    
    static {
        SVC_INSTANCE_REF.setType(SERVICE_INSTANCE);
        SVC_INSTANCE_REF.setValue(SERVICE_INSTANCE);
        (new Thread(new VimRunnable())).start();
        (new Thread(new SessionTimer())).start();
    }
    
    private static Map<String, Session> sessionMap =
            Collections.synchronizedMap(new HashMap<String, Session>());
    
    private UserSessionService userSessionService = null;
    private boolean            isDeveloperMode    = false;
    private String             baseURL            = null;
    private Log                log;
    
    SessionManagerImpl() {
        this.log                = LogFactory.getLog(this.getClass());
        determineIfDeveloperMode();
    }
    
    @Override
    public void sessionEnded (String clientId) {
        log.debug("Session for " + clientId + 
                  " was logged out.  Setting session Id to null.");
        this.baseURL        = null;
        this.clearSessions();
    }
    
    @Override
    public String getLocale () {
        String locale = null;
        UserSession userSession = this.getUserSession();
        if (null != userSession) {
            locale = userSession.locale;
        }
        log.debug("SessionManger.getLocale found " + locale);
        return locale;
    }
    
    @Override
    public String getServiceUrl(final String serverGuid) {
        ServerInfo serverInfo = this.getServerInfoObject(serverGuid);
        if (null != serverInfo) {
            return filterDefaultPort(serverInfo.serviceUrl);
        } else {
            return null;
        }   
    }
    
    String filterDefaultPort(final String url) {
    	return url.replaceFirst(":443", "");
    }
    
    @Override
    public String getSessionId(final String serverGuid) {
        ServerInfo serverInfo = this.getServerInfoObject(serverGuid);
        if (null != serverInfo) {
            return serverInfo.sessionCookie;
        } else {
            return null;
        }   
    }
    
    public String getExtensionServerThumbprint(String sessionId, String serverGuid) throws InitializationException{
    	
    	Extension extension = getExtension(sessionId, serverGuid);
    	String serverThumbprint = null;
    	if(null != extension){
    		List<ExtensionServerInfo> extensionServers = extension.getServer();
	    	if(extensionServers.size() > 0 && extensionServers.get(0) != null){
	    		serverThumbprint = extensionServers.get(0).getServerThumbprint();
	    	}
    	}    	
    	return serverThumbprint;
    }

    Extension getExtension(String sessionId, String serverGuid) throws InitializationException {    	
    	String key = this.getSessionKey(sessionId, serverGuid);
    	Session session = sessionMap.get(key);
    	
    	Extension extension = null;
    	if (session != null) {
    	    extension = session.getExtension();
    	}
    	
    	if(null != extension) {
    		return extension;
    	}
    	
    	this.initHttpsClient(serverGuid);
        List<String> hpExtensions = new ArrayList<String>();
		Collections.addAll(hpExtensions,						   
						   COM_HP_IC4VC_NGC_ALL, 
						   COM_HP_IC4VC_NGC_SERVER, 
						   COM_HP_IC4VC_NGC_STORAGE);
		
		ServiceContent serviceContent = getServiceContent(sessionId, serverGuid);
		ManagedObjectReference mor    = serviceContent.getExtensionManager();
		try {
			for(String ext: hpExtensions){
				extension = getVimPort().findExtension(mor, ext);
				if(null != extension){
				    sessionMap.get(key).setExtension(extension);
					break;
				}
			}
			
		} catch (RuntimeFaultFaultMsg e) {
			
			log.error("RuntimeFaultMsg caught.  Could not get extension info for plugin "
					+ COM_HP_IC4VC_NGC + "  Throwing an InitializationException.", e);
            throw new InitializationException("Error retrieving extension URL from vimPort.");
		}
		return extension;
	}

	@Override
    public String getWSURLHostname (final String sessionId,
                                    final String serverGuid)
                                            throws InitializationException {
         if (null != this.baseURL) {
             return this.baseURL;
         }
         
         
         String fullURL = null;
         
         if(!this.isDeveloperMode) {
             Extension extension = getExtension(sessionId, serverGuid);
             
             fullURL   = extension.getClient().get(0).getUrl();
             
             int startOfHostname = fullURL.indexOf("//");
             if (startOfHostname == -1){
                 startOfHostname = 0;
             } else {
                 startOfHostname+=2;
             }
             
             int endOfBaseURL = fullURL.indexOf("/", startOfHostname);
             if (endOfBaseURL == -1){
                 this.baseURL = fullURL + "/";
             } else {
                 this.baseURL = fullURL.substring(0,endOfBaseURL + 1);
             }
         }
         else {
             this.baseURL = "http://localhost:8080/";
         }
         log.debug("SessionInfo baseURL is " + baseURL);
         
         return this.baseURL;
     }
    
    protected void setUserSessionService(final UserSessionService sessionService) {
        this.userSessionService = sessionService;
    }
    
    synchronized VimPortType getVimPort () {
        if (null == vimPort) {
            log.debug("VimPort is null.  Using lazy initialization.");
            synchronized(SessionManagerImpl.class) {
                VimService vimService = new VimService();
                vimPort = vimService.getVimPort();
                log.debug("Found the vimPort.");
            }
        }
        return vimPort;
    }
    
    private String getSessionKey(final String sessionId, final String serverGuid) {
        return sessionId + "_" + serverGuid;
    }
    
    private void updateSession(ServerInfo serverInfo) {
        if (serverInfo != null) {
            String key = this.getSessionKey(serverInfo.sessionCookie, serverInfo.serviceGuid);
            Session sess = sessionMap.get(key);
            if (sess == null) {
                sess = new Session(serverInfo.sessionCookie, serverInfo.serviceGuid);
            }
            sess.setServerInfo(serverInfo);
            sessionMap.put(key, sess);
        }
    }
    
    private void clearSessions() {
        sessionMap.clear();
    }
    
    private void determineIfDeveloperMode () {
        String ic4vcMode = System.getProperty("ic4vc.mode");
        if(null != ic4vcMode && ic4vcMode.equalsIgnoreCase("Developer")) {
            log.debug("SessionInfo detected developer mode.");
            isDeveloperMode = true;
        }        
    }
    
    private void initHttpsClient(String serverGuid) {
        
        ServerInfo serverInfo = this.getServerInfoObject(serverGuid);
        if (serverInfo == null) {
            return;
        } else {
            try {
                String serviceUrl = serverInfo.serviceUrl;
                int port = 443;
                int portIndex = serviceUrl.indexOf(":", 6);
                if (portIndex != -1) {
                    int endIndex = serviceUrl.indexOf("/sdk");
                    String portString = serviceUrl.substring(portIndex + 1, endIndex);
                    try {
                        port = Integer.parseInt(portString);
                    } catch (NumberFormatException e) {
                        // Use default port
                    }
                }
                
                List<String> thumbprints = new ArrayList<String>();
                if (serverInfo.thumbprint != null && !serverInfo.thumbprint.isEmpty()) {
                    thumbprints.add(serverInfo.thumbprint);
                }
                
                SSLContext context = SSLContext.getInstance("SSL");
    
                SslThumbprintVerifier verifier = new SslThumbprintVerifier();
                verifier.setThumbprints(thumbprints);
    
                ThumbprintTrustManager trustManager = new ThumbprintTrustManager(
                        null, verifier);
                context.init(null, new TrustManager[] { trustManager }, null);
                javax.net.ssl.SSLSessionContext sslsc1 = context
                        .getServerSessionContext();
                sslsc1.setSessionTimeout(0);
                HostnameVerifier hostnameVerifier = trustManager.new HostnameVerifier();
                HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
                SSLSocketFactory ssf = context.getSocketFactory();
                ssf.createSocket(serverInfo.name, port);
                HttpsURLConnection.setDefaultSSLSocketFactory(ssf);
            } catch (Exception exception) {
                log.error("Error when initializing https client", exception);
            }
        }
    }
    
    @Override
    public synchronized UserSession getUserSession() {
        UserSession session = null;
        
        if (null == this.userSessionService) {
            log.warn("The UserSessionService is null.  Cannot get a UserSession.");
            return null;            
        }

        log.debug("Retrieving the user session from the user session service.");
        try {
            session = userSessionService.getUserSession();
        } catch (Exception e) {
            this.clearSessions();
            log.debug("userSessionService.getUserSession threw an exception", e);
            return null;
        } catch (Throwable t) {
            this.clearSessions();
            log.debug("userSessionService.getUserSession threw a throwable", t);
        }
        
        if (null == session) {
            this.clearSessions();
            log.debug("User Session is STILL null.");
        } else {
            log.debug("userSessionService.getUserSession() successfully retrieved a user session.");
        }

        return session;
    }
    
    /**
     * Find the relevant ServerInfo Object for the given serverGuid and the
     * current session.
     */
    private synchronized ServerInfo getServerInfoObject(String serverGuid) {
       UserSession userSession = this.getUserSession();
       if (null == userSession) {
           log.warn("UserSession is null.  Won't be able to communicate with vCenter.");
           return null;
       }

       if (null == userSession.serversInfo || userSession.serversInfo.length < 1 ) {
           log.warn("UserSession.serversInfo is null or empty.  " +
           		"Won't be able to communicate with vCenter.");
       }
       
       if (null == serverGuid) {
           log.warn("ServerGuid is null.  Returing first ServerInfo from UserSession.");
           ServerInfo si = userSession.serversInfo[0];
           this.updateSession(si);
           return si;
       }
       
       for (ServerInfo sinfo : userSession.serversInfo) {
          if (sinfo.serviceGuid.equalsIgnoreCase(serverGuid)) {
              this.updateSession(sinfo);
              return sinfo;
          }
       }
       log.warn("Couldn't find ServerInfo in userSession.serversInfo.  Won't" +
               " be able to communicate with vCenter.");
       return null;
    }

    
    private ServiceContent getServiceContent(final String sessionId,
                                             final String serverGuid)
                                                     throws InitializationException {
        
        if (null == sessionId) {
            log.warn("SessionId is null.  Cannot get ServiceContext.");
            throw new InitializationException("SessionId is Null, cannot create ServiceContent.");
        }
        
        String key = this.getSessionKey(sessionId, serverGuid);
        Session session = sessionMap.get(key);
        ServiceContent sc = null;
        if (session != null) {
            sc = session.getServiceContent();
        }
        
        if (sc != null) {
            return sc;
        }
        
        String serviceUrl = getServiceUrl(serverGuid);
        if (null == serviceUrl) {
            log.warn("ServiceUrl is null.  Cannot get ServiceContext.");
            throw new InitializationException("Service URL is Null, cannot create ServiceContent.");
        }
        
        setRequestContext(sessionId, serviceUrl);
        try {
           sc = vimPort.retrieveServiceContent(SVC_INSTANCE_REF);
           sessionMap.get(key).setServiceContent(sc);
        } catch (RuntimeFaultFaultMsg e) {
            log.error("Couldn't get the serviceConent from the vimPort - " +
                    "throwing an initialization exception.", e);
            throw new InitializationException("Error retrieving ServiceContent from vimPort.");
        }

        return sc;
     }
    
    public List<ObjectContent> getVMObjectContents(String moRef, String sessionId, String serverGuid) 
                                                    throws InitializationException {
        ServiceContent service = null;
        service = getServiceContent(sessionId, serverGuid);
        
        if (service == null) {
            return null;
        }
        int i = moRef.lastIndexOf(":");
        ManagedObjectReference vmMor = new ManagedObjectReference();
        vmMor.setType("VirtualMachine");
        vmMor.setValue(moRef.substring(i+1));
           
        /*Currently adding property spec only for the VObject- VirtualMachine as it is required only for VirtualMachine to get its config property. 
         The config property is used to check if the vm is template or not. In future, the property spec for any VObject can be added as per the
         requirement. 
         Note: VM is template is checked in the ActionDataAdapter class.*/ 
        
        com.vmware.vim25.PropertySpec propertySpec = new com.vmware.vim25.PropertySpec();
        propertySpec.setAll(Boolean.FALSE);
        propertySpec.setType("VirtualMachine");
        propertySpec.getPathSet().add("config");
        
        ObjectSpec objectSpec = new ObjectSpec();
        objectSpec.setObj(vmMor);
        objectSpec.setSkip(Boolean.FALSE);
   
        PropertyFilterSpec propertyFilterSpec = new PropertyFilterSpec();
        propertyFilterSpec.getPropSet().add(propertySpec);
        propertyFilterSpec.getObjectSet().add(objectSpec);
    
        List<PropertyFilterSpec> propertyFilterSpecs = new ArrayList<PropertyFilterSpec>();
        propertyFilterSpecs.add(propertyFilterSpec);
    
        List<ObjectContent> objectContents = null;
        try {
            objectContents = vimPort.retrieveProperties(
                         service.getPropertyCollector(), propertyFilterSpecs);
        } catch (RuntimeFaultFaultMsg e) {
            log.error("Couldn't get the objectConents from the vimPort - " +
                    "throwing an initialization exception.", e);
            throw new InitializationException("Error retrieving ObjectContent from vimPort.");
        } catch (InvalidPropertyFaultMsg e) {
            log.error("Couldn't get the objectConents from the vimPort - " +
                    "throwing an initialization exception.", e);
            throw new InitializationException("Error retrieving ObjectContent from vimPort.");
        }
        return objectContents;
        
    }
    
    private void setRequestContext(final String sessionId, final String serviceUrl) {
        List<String> values = new ArrayList<String>();
        values.add("vmware_soap_session=" + sessionId);
        Map<String, List<String>> reqHeadrs = new HashMap<String, List<String>>();
        reqHeadrs.put("Cookie", values);
        Map<String, Object> reqContext = ((BindingProvider)vimPort).getRequestContext();
        reqContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, serviceUrl);
        reqContext.put(BindingProvider.SESSION_MAINTAIN_PROPERTY, true);
        reqContext.put(MessageContext.HTTP_REQUEST_HEADERS, reqHeadrs);
    }
    
    private static class SessionTimer implements Runnable {
        
        private Log log = LogFactory.getLog(SessionTimer.class);
        
        private static final long DEFAULT_CHECK_INTERVAL = 3600000L;
        private static final long DEFAULT_SESSION_TIMEOUT = 1800000L;

        @Override
        public void run() {
            while (true) {
                if (!sessionMap.isEmpty()) {
                    List<String> expiredSessions = new ArrayList<String>();
                    for (Entry<String, Session> entry : sessionMap.entrySet()) {
                        log.debug("Current sessionMap content: " + sessionMap);
                        Date currentTime = new Date();
                        Date lastAccess = entry.getValue().getLastAccess();
                        Long idle = currentTime.getTime() - lastAccess.getTime();
                        if (idle >= DEFAULT_SESSION_TIMEOUT) {
                            expiredSessions.add(entry.getKey());
                        }
                    }
                    
                    if (!expiredSessions.isEmpty()) {
                        log.debug("Deleting expired sessions: " + expiredSessions);
                        sessionMap.keySet().removeAll(expiredSessions);
                    }
                }
                
                try {
                    Thread.sleep(DEFAULT_CHECK_INTERVAL);
                } catch (InterruptedException e) {}
            }
        }
    }
    
    private static class VimRunnable implements Runnable {
        private Log log;
        
        protected VimRunnable() {
            this.log = LogFactory.getLog(this.getClass());
        }
        
        @Override
        public void run () {
            log.debug("Getting vimPort");
            VimService vimService = new VimService();
            vimPort = vimService.getVimPort();
            log.debug("Finished getting the vimPort");
        }
        
    }


	@Override
	public boolean isDeveloperMode() {
		
		return this.isDeveloperMode;
	}
}
