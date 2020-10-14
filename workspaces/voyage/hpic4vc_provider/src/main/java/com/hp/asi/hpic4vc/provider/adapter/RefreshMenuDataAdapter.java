package com.hp.asi.hpic4vc.provider.adapter;

import com.hp.asi.hpic4vc.provider.error.InitializationException;
import com.hp.asi.hpic4vc.provider.locale.I18NProvider;
import com.hp.asi.hpic4vc.provider.model.LinkModel;
import com.hp.asi.hpic4vc.provider.model.MenuModel;

public class RefreshMenuDataAdapter extends DataAdapter<String, MenuModel>  {

    private static final String SERVICE_NAME = "config/flex/monitor/host/show_refresh_cache";
    private boolean showRefreshCache = false;
    
    public RefreshMenuDataAdapter () {
        super(String.class);
    }

    @Override
    public String getServiceName () {
        return SERVICE_NAME;
    }

    @Override
    public MenuModel formatData (String rawData) {
        MenuModel refreshMenu  = new MenuModel();
        
        refreshMenu.addMenu(createReloadMenu());
        
        if (showRefreshCache) {
            refreshMenu.addMenu(createRefreshCacheMenu());
        }

        return refreshMenu;
    }

    @Override
    public MenuModel getEmptyModel () {
        return new MenuModel();
    }
    
    @Override
    public final MenuModel call() {        
        try {
            this.urlString = getWsUrl();
            log.debug("Service Url is " + urlString);
        } catch (InitializationException e) {
            this.showRefreshCache = false;
            return formatData(null);
        }
       
        try {
            String jsonData = httpGet(urlString);
            log.debug("Data for " + this.getServiceName() + " is " + jsonData);
            if (null == jsonData) {
                this.showRefreshCache = false;
            } else if (jsonData.contains("true")) {
                this.showRefreshCache = true;
            }
            
        } catch (Exception e) {
            log.debug("Caught exception: " + e.getMessage(), e);
           this.showRefreshCache = false;
           
        }
        return formatData(null);
    }

    
    private LinkModel createReloadMenu() {
        LinkModel lmReloadPage        = new LinkModel();
        lmReloadPage.displayName      = this.i18nProvider.getInternationalString
                (locale, I18NProvider.ReloadPage_String);
        lmReloadPage.url              = null;
        lmReloadPage.dialogTitle      = "Reload Page";
        lmReloadPage.dialogText       = null;
        return lmReloadPage;
    }
    
    private LinkModel createRefreshCacheMenu() {
        LinkModel lmRefreshCache      = new LinkModel();
        lmRefreshCache.displayName      = this.i18nProvider.getInternationalString
                (locale, I18NProvider.RefreshCache_String);
        lmRefreshCache.url              = null;
        lmRefreshCache.dialogTitle      = "Refresh Cache";
        lmRefreshCache.dialogText       = null;
        return lmRefreshCache;
    }
}
