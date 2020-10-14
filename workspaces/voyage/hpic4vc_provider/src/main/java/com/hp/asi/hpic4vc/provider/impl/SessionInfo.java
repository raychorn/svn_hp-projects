package com.hp.asi.hpic4vc.provider.impl;

import java.util.List;

import com.hp.asi.hpic4vc.provider.api.SessionManager;
import com.hp.asi.hpic4vc.provider.error.InitializationException;
import com.vmware.vim25.ObjectContent;
import com.vmware.vise.usersession.ServerInfo;
import com.vmware.vise.usersession.UserSession;

public class SessionInfo {
    private static final String DEFAULT_MOREF   = "Folder:group-d1";
    static final SessionManager SESSION_MANAGER = SessionManagerImpl.getInstance();

    private String         moref          = null;
    private String         serverGuid     = null;
    private String         locale         = null;
    private String         sessionId      = null;
    private String         serviceUrl     = null;
    private String         vmId           = null;
    private VObjectType    vObjectType    = null;
    private String         objReferenceId = null;


    
    public SessionInfo(final String objReferenceId) {
        this.objReferenceId = objReferenceId;
        if (null != objReferenceId && objReferenceId.indexOf(':') >= 0) {
            parseObjReferenceId(objReferenceId);            
        } else if (null != objReferenceId) {
            serverGuid  = objReferenceId;
            moref       = DEFAULT_MOREF;
        } else {
            findServerGuidUsingUserSession();
        }
        
        this.sessionId  = SESSION_MANAGER.getSessionId(serverGuid);
        this.locale     = SESSION_MANAGER.getLocale();
        this.serviceUrl = SESSION_MANAGER.getServiceUrl(serverGuid);
    }
    
    public SessionInfo(final String locale, VObjectType objectType) {
        this.locale        = locale;
        this.vObjectType   = objectType;
    }
    
    public String getLocale() {
        return this.locale;
    }
    
    public String getMoref() {
        return this.moref;
    }
    
    public String getServerGuid() {
        return this.serverGuid;
    }
    
    public String getSessionId() {
        return this.sessionId;
    }
    
    public String getVmId() {
        return this.vmId;
    }
    
    public VObjectType getVobjectType() {
        return this.vObjectType;
    } 
    
    public void setVobjectType(VObjectType vObjectType) {
        this.vObjectType = vObjectType;
    }
    
    public String getObjReferenceId() {
        return this.objReferenceId;
    }
    
    public List<ObjectContent> getVMProperties(String moRef, String sessionId, String serverGuid)  throws InitializationException {
        List<ObjectContent> objectContents = SESSION_MANAGER.getVMObjectContents(moRef, sessionId, serverGuid);
        return objectContents;
    }
    public boolean isDeveloperMode(){
    	
		return SESSION_MANAGER.isDeveloperMode();    	
    	
    }
    
    public String getQueryParameters () {
        return getBaseParameters() + "&moref=" + moref;
    }
    
    public String getBaseParameters () {
        return "?&serverGuid=" + serverGuid
              + "&sessionId="  + sessionId
              + "&locale="     + locale
              + "&serviceUrl=" + serviceUrl;
    }
    
    public String getUimServerThumbprint () throws InitializationException {
        
    	String thumbprint = SESSION_MANAGER.getExtensionServerThumbprint(sessionId, serverGuid);
    	if(null == thumbprint){
    		throw new InitializationException();
    	}
        return thumbprint;
    }
    
    public enum VObjectType {        
        HOST            ("HostSystem"),
        VM              ("VirtualMachine"),
        VM_TEMPLATE     ("VirtualMachine"),
        DATASTORE       ("Datastore"),
        DATACENTER		("Datacenter"),
        CLUSTER         ("ClusterComputeResource");
        
        private final String moref;
        VObjectType(final String str) {
            this.moref = str;
        }
        
        public String getMoref() {
            return this.moref;
        }
        
        public static VObjectType getVObjectTypeFromMoref(final String moref) {
            for (VObjectType type : VObjectType.values()) {
                if (moref.contains(type.moref)) { 
                    return type;
                }
            }
            return null;
        }
    }
    
    private void parseObjReferenceId(final String objReferenceId) {
        int parameterSeparator =  objReferenceId.indexOf(':');
        String morefStr = objReferenceId.substring(parameterSeparator + 1);
        vObjectType     = VObjectType.getVObjectTypeFromMoref(morefStr);
        serverGuid      = objReferenceId.substring(0, parameterSeparator);
        if (morefStr.contains(":")) {
            parameterSeparator = morefStr.indexOf(':');
            vmId  = morefStr.substring(parameterSeparator + 1);
            moref = vObjectType.getMoref() + ":" + vmId;
        } else {
            moref = vObjectType.getMoref();
        }       
    }
    
    /**
     * If the serverGuid couldn't be obtained from normal objRefenceId parsing,
     * grab it out of the UserSession.  This is NOT preferred since it defaults
     * to using the first ServerInfo object it finds, rather than looking for the
     * correct ServerInfo object.  However, without an objReferneceId, we do not
     * know which ServerInfo object to use.
     */
    private void findServerGuidUsingUserSession() {
        UserSession session = SESSION_MANAGER.getUserSession();
        if (null != session && session.serversInfo.length > 0) {
            ServerInfo serverItem = session.serversInfo[0];
            if (null != serverItem) {
                this.serverGuid = serverItem.serviceGuid;
            }
        }
    }
}
