package com.hp.asi.hpic4vc.provider.adapter;

import java.util.Collection;

import com.hp.asi.hpic4vc.provider.data.ProductHelpPageData;
import com.hp.asi.hpic4vc.provider.error.InitializationException;
import com.hp.asi.hpic4vc.provider.model.LinkModel;
import com.hp.asi.hpic4vc.provider.model.MenuModel;

public class ProductHelpDataAdapter extends ProductBaseDataAdapter <MenuModel> {
    
    private static final String SERVICE_NAME = "config/productPages";
    
    @Override
    public String getServiceName () {
        return SERVICE_NAME;
    }
    
    @Override
    protected String getWsUrl () throws InitializationException {
        return getBaseUrl() + SERVICE_NAME;
    }
    

    @Override
    public MenuModel formatData (ProductHelpPageData[] rawData) {
        MenuModel model  = new MenuModel();
        LinkModel aLink  = null;
        
        Collection<ProductHelpPageData> pageData = getSortedDataByOrder(rawData);
        try {
            for (ProductHelpPageData data : pageData) {
                aLink = createLinkModel(data, true);
                if (null != aLink) {
                    model.addMenu(aLink);
                }
            }
        } catch (InitializationException e) {
            log.debug(e.getMessage(), e);
            model.errorMessage = e.getMessage();
            return model;
        }

        return model;
    }

    @Override
    public MenuModel getEmptyModel () {
        return new MenuModel();
    }
}
