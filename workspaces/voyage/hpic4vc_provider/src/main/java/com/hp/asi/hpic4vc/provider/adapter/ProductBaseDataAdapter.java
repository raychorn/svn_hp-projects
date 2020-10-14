package com.hp.asi.hpic4vc.provider.adapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.hp.asi.hpic4vc.provider.data.ProductHelpPageData;
import com.hp.asi.hpic4vc.provider.error.InitializationException;
import com.hp.asi.hpic4vc.provider.impl.SessionManagerImpl;
import com.hp.asi.hpic4vc.provider.model.BaseModel;
import com.hp.asi.hpic4vc.provider.model.LinkModel;

public abstract class ProductBaseDataAdapter<S  extends BaseModel> extends DataAdapter<ProductHelpPageData[], S> {
    
    private static String baseUrl = null;

    public ProductBaseDataAdapter () {
        super(ProductHelpPageData[].class);
    }
    
    List<ProductHelpPageData> getSortedDataByOrder(final ProductHelpPageData[] pages) {
    	List<ProductHelpPageData> data = new ArrayList<ProductHelpPageData>();
    	data.addAll(Arrays.asList(pages));
    	Collections.sort(data);
    	return data;
    }
    
    LinkModel createLinkModel(final ProductHelpPageData data, final boolean isHelpUrl) throws InitializationException {
        if (null == data) {
            return null;
        }
        
        LinkModel link = new LinkModel();
        link.url = createUrl(data, isHelpUrl);
        link.displayName = this.i18nProvider.getInternationalString
                    (locale, data.getTitle());
        if (null == link.displayName) {
            log.debug("ProductBaseAdapter could not find the" +
                    " internationalized display name for " + data.getTitle() + 
                    ".  Not including this link.");
            return null;
        }
        return link;
    }

    String createUrl(final ProductHelpPageData data, final boolean isHelpUrl) throws InitializationException {
    	if (isHelpUrl) {
    		return HelpMenuMaker.makeHelpTopicUrl(sessionInfo, data.getUrl());
    	}
    	
    	if (isWebUrl(data.getUrl())) {
    		return createWebUrl(data.getWeb_root(), data.getUrl());
    	}
    	return data.getUrl();
    }
    
    private boolean isWebUrl(final String url) {
        if (null !=url && !url.contains("hpic4vc.ui.insightManagement")) {
            return true;
        } else {
            return false;
        }
    }
    
    private String createWebUrl(final String webRoot, final String url) 
            throws InitializationException {
        if (null != webRoot) {
        	if (url.contains("ui_mgmtjsp")) {
                return url + "?sessionId=" + sessionInfo.getSessionId();
            }
            return url;
        } else {
            return this.getBaseUrl() + url;
        }
    }

    String getBaseUrl() throws InitializationException {
        if (null != baseUrl) {
            return baseUrl;
        }
        
        baseUrl = SessionManagerImpl.getInstance().
                getWSURLHostname(this.sessionInfo.getSessionId(),
                                 this.sessionInfo.getServerGuid());

        return baseUrl;
        
    }
}
