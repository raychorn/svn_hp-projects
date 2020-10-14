package com.hp.asi.hpic4vc.provider.impl;

import java.util.Date;

import com.vmware.vim25.Extension;
import com.vmware.vim25.ServiceContent;
import com.vmware.vise.usersession.ServerInfo;

public class Session {
    
    private ServiceContent serviceContent;
    private final String sessionId;
    private final String serverGuid;
    private ServerInfo serverInfo;
    private Extension extension;
    private Date lastAccess;
    
    public Session(String sessionId, String serverGuid) {
        this.sessionId = sessionId;
        this.serverGuid = serverGuid;
        this.lastAccess = new Date();
    }
    
    private synchronized void updateAccess() {
        this.lastAccess = new Date();
    }
    
    ServerInfo getServerInfo() {
        this.updateAccess();
        return this.serverInfo;
    }
    
    void setServerInfo(ServerInfo si) {
        this.updateAccess();
        this.serverInfo = si;
    }
    
    ServiceContent getServiceContent() {
        this.updateAccess();
        return this.serviceContent;
    }
    
    void setServiceContent(ServiceContent sc) {
        this.updateAccess();
        this.serviceContent = sc;
    }
    
    Extension getExtension() {
        this.updateAccess();
        return this.extension;
    }
    
    void setExtension(Extension extension) {
        this.updateAccess();
        this.extension = extension;
    }
    
    String getSessionId() {
        this.updateAccess();
        return this.sessionId;
    }
    
    String getServerGuid() {
        this.updateAccess();
        return this.serverGuid;
    }
    
    synchronized Date getLastAccess() {
        return this.lastAccess;
    }
}
