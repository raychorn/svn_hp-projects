package com.hp.asi.hpic4vc.provider.adapter;

import java.util.ArrayList;
import java.util.List;

import com.hp.asi.hpic4vc.provider.data.PageResult;
import com.hp.asi.hpic4vc.provider.data.TabResult;
import com.hp.asi.hpic4vc.provider.locale.I18NProvider;
import com.hp.asi.hpic4vc.provider.locale.TabDisplayNameEnum;
import com.hp.asi.hpic4vc.provider.model.HostDataModel;
import com.hp.asi.hpic4vc.provider.model.PageModel;
import com.hp.asi.hpic4vc.provider.model.TabModel;

public class PageDataAdapter extends DataAdapter<PageResult, PageModel>{
    private final HostDataModel hostData;
    private final PageType pageType;
    
    public PageDataAdapter (final HostDataModel hostDataModel, PageType pageType) {
        super(PageResult.class);
        this.hostData = hostDataModel;
        this.pageType = pageType;
    }

    @Override
    public PageModel getEmptyModel () {
        return new PageModel();
    }    
    
    @Override
    public String getServiceName () {    	
        String serviceName = "config/flex/";
        
        if(null != pageType){
        	serviceName+= pageType.toString() + "/";
        }
        if (null != sessionInfo.getVobjectType()) {
            switch (sessionInfo.getVobjectType()) {
                case HOST:
                    serviceName+= "host"; 
                    break;
                case VM:
                    serviceName+= "vm";
                    break;
                case DATASTORE:
                	serviceName+= "datastore";
                    break;
                case CLUSTER:
                	serviceName+= "cluster";
                    break;
                default:
                    log.info("Invalid VMware Object type retrieved: " + 
                              sessionInfo.getVobjectType().toString());
                    break;
            }
        }      
        return serviceName;
    }
    
    @Override
    public PageModel formatData (PageResult rawData) {
        PageModel pageModel = new PageModel();

        if (rawData.getPages() == null) {
            pageModel.informationMessage = this.i18nProvider.getInternationalString
                    (locale, I18NProvider.Info_NoRecords);
            log.debug("The data had no records.  " +
            		"Returning a page model with an error message set.");
            return pageModel;
        }

        pageModel = createPageModel(rawData);                
        return pageModel;
    }
    
    protected PageModel createPageModel (PageResult rawData) {
        PageModel pageModel          = new PageModel();
        pageModel.show_refresh_cache = rawData.getShow_refresh_cache();
        pageModel.tabs               = createTabList(rawData.getPages());
        pageModel.portlets           = createTabList(rawData.getBoxes());
        
        return pageModel;
    }
    
    protected List<TabModel> createTabList (List<TabResult> tabResults) {
        List<TabModel> tabList = new ArrayList<TabModel>();
        
        if (null == tabResults) {
            return tabList;
        }
        
        for (TabResult result : tabResults) {
            TabModel currentTab = createTab(result);
            if (null != currentTab) {
                tabList.add(currentTab);
            }
        }
        return tabList;
    }
    
    protected TabModel createTab(final TabResult result) {
        if (!shouldDisplay(result)) {
            return null;
        }
        
        TabDisplayNameEnum displayNameEnum = TabDisplayNameEnum.
                getTabDisplayNameEnum(result.getDisplay_name_key());

        TabModel model         = new TabModel();
        model.displayNameKey   = result.getDisplay_name_key();
        model.displayNameValue = displayNameEnum.getDisplayNameValue(locale);
        model.order            = result.getOrder();
        model.column           = result.getColumn();
        model.component        = result.getComponent();
        model.helpUrl          = createHelpUrl(result.getHelp_topic());
        model.subTabs          = createTabList(result.getSub_pages());
        
        return model;
    }
    
    private boolean shouldDisplay(final TabResult result) {
        boolean shouldDisplay = true;
        
        if (null == result) {
            shouldDisplay = false;
        }
        
        if (result.isBladeOnly() && null != hostData && !hostData.isBladeServer) {
            shouldDisplay = false;
        }
        
        return shouldDisplay;
    }
    
    private String createHelpUrl(final String topic) {
    	if (null != topic && !topic.equals("")) {
    		return HelpMenuMaker.makeHelpTopicUrl(sessionInfo, topic);
    	}
    	return HelpMenuMaker.makeComponentHelpUrl(sessionInfo);
    }

}
