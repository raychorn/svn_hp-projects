package com.hp.asi.hpic4vc.storage.provider.adapter;

import java.util.ArrayList;
import java.util.List;

import com.hp.asi.hpic4vc.provider.adapter.PageDataAdapter;
import com.hp.asi.hpic4vc.provider.adapter.PageType;
import com.hp.asi.hpic4vc.provider.data.PageResult;
import com.hp.asi.hpic4vc.provider.data.TabResult;
import com.hp.asi.hpic4vc.provider.model.PageModel;
import com.hp.asi.hpic4vc.provider.model.TabModel;

public class StoragePageDataAdapter extends PageDataAdapter {
    
    private static final String STORAGE_TAB_KEY = "Storage";
    
    public StoragePageDataAdapter () {
        super(null, PageType.manage);
    }
    
    @Override
    protected PageModel createPageModel (PageResult rawData) {
        List<TabModel> tabModels = new ArrayList<TabModel>();
        TabResult page           = getStoragePage(rawData);
        
        if (page == null) {
            return new PageModel();
        } else {
            tabModels.add(createTab(page));
        }
        
        PageModel pageModel           = new PageModel();
        pageModel.tabs                = tabModels;
        pageModel.show_refresh_cache  = rawData.getShow_refresh_cache();
        
        return pageModel;
    }

    private TabResult getStoragePage (PageResult rawData) {        
        List<TabResult> pages = rawData.getPages();
        
        for (TabResult page : pages) {
            if (page.getDisplay_name_key().equalsIgnoreCase(STORAGE_TAB_KEY)) {
                return page;
            }
        }
        
        return null;
    }
    
}
