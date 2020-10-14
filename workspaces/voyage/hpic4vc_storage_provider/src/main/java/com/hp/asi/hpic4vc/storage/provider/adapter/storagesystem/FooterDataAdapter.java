package com.hp.asi.hpic4vc.storage.provider.adapter.storagesystem;

import java.net.URI;

import com.hp.asi.hpic4vc.provider.adapter.LaunchLinksDataAdapter;
import com.hp.asi.hpic4vc.provider.dam.util.ModelObjectUriResolver;
import com.hp.asi.hpic4vc.provider.error.InitializationException;
import com.hp.asi.hpic4vc.provider.impl.SessionManagerImpl;


public class FooterDataAdapter extends LaunchLinksDataAdapter {
    private static final ModelObjectUriResolver RESOURCE_RESOLVER = new ModelObjectUriResolver();
    private static final String ARRAY_PARAMETER = "?&arrayUID=";
    private static final String SERVICE_NAME = "services/swd/launchtools_storage_system";
    
    protected String storageSystemUid;
    
    public FooterDataAdapter (final String storageSystemUid) {
        super();
        URI uri = URI.create(storageSystemUid);
        try {
            this.storageSystemUid = RESOURCE_RESOLVER.getObjectSpecificId(uri);
        } catch (IllegalArgumentException e) {
            log.info("Setting storageSystemUid to " + storageSystemUid, e);
            this.storageSystemUid = storageSystemUid;
        }
    }
    
    @Override
    public String getServiceName () {
        return SERVICE_NAME;
    }

    @Override
    protected String getWsUrl () throws InitializationException {
        String baseUrl = SessionManagerImpl.getInstance().getWSURLHostname
            (sessionInfo.getSessionId(), sessionInfo.getServerGuid());
        
        StringBuilder url = new StringBuilder();
        url.append(baseUrl);
        url.append(SERVICE_NAME);
        url.append(ARRAY_PARAMETER);
        url.append(this.storageSystemUid);
        url.append("&sessionId=");
        url.append(sessionInfo.getSessionId());
        url.append("&locale=");
        url.append(sessionInfo.getLocale());
        
        return url.toString();        
    }

}
