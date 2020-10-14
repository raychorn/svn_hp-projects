package com.hp.asi.hpic4vc.provider.adapter;

import java.util.Collection;

import com.hp.asi.hpic4vc.provider.data.ProductHelpPageData;
import com.hp.asi.hpic4vc.provider.error.InitializationException;
import com.hp.asi.hpic4vc.provider.locale.I18NProvider;
import com.hp.asi.hpic4vc.provider.model.ConfigurationListModel;
import com.hp.asi.hpic4vc.provider.model.LinkModel;

public class ProductConfigurationDataAdapter extends ProductBaseDataAdapter <ConfigurationListModel>{
    private static final String SERVICE_NAME = "config/configurationPages";

    @Override
    public String getServiceName () {
        return SERVICE_NAME;
    }
    
    @Override
    protected String getWsUrl () throws InitializationException {
        return getBaseUrl() + SERVICE_NAME;
    }

    @Override
    public ConfigurationListModel formatData (ProductHelpPageData[] rawData) {
        ConfigurationListModel model = new ConfigurationListModel();
        LinkModel link               = null;
        LinkModel helpLink           = null;
        String helpString            = this.i18nProvider.getInternationalString
                (locale, I18NProvider.Help_String);
        Collection<ProductHelpPageData> pageData = getSortedDataByOrder(rawData);
        try {
            for (ProductHelpPageData data : pageData) {
                link                 = createLinkModel(data, false);
                helpLink             = new LinkModel();
                helpLink.displayName = helpString;
                helpLink.url         = HelpMenuMaker.makeHelpTopicUrl(sessionInfo, data.getHelpUrl());
                model.addLinkWithHelpLink(link,  helpLink);
            }
        } catch (InitializationException e) {
            log.debug(e.getMessage(), e);
            model.errorMessage = e.getMessage();
            return model;
        }

        return model;
    }

    @Override
    public ConfigurationListModel getEmptyModel () {
       return new ConfigurationListModel();
    }

}
