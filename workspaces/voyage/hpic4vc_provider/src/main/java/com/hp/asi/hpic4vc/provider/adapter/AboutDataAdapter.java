package com.hp.asi.hpic4vc.provider.adapter;

import com.hp.asi.hpic4vc.provider.data.AboutData;
import com.hp.asi.hpic4vc.provider.locale.I18NProvider;
import com.hp.asi.hpic4vc.provider.model.LinkModel;
import com.hp.asi.hpic4vc.provider.model.MenuModel;

public class AboutDataAdapter extends DataAdapter<AboutData, MenuModel> {

    public AboutDataAdapter () {
        super(AboutData.class);    
    }

    @Override
    public MenuModel formatData (AboutData rawData) {
        MenuModel aboutMenu = new MenuModel();
        LinkModel lm = new LinkModel();
        AboutData about = rawData;
        lm.displayName  = this.i18nProvider.getInternationalString
                (locale, I18NProvider.About_String);
        lm.url = about.getVersion();
        aboutMenu.addMenu(lm);

        return aboutMenu;
    }

    @Override
    public String getServiceName () {
        String serviceName = "config/about"; 
        return serviceName;
    }

    @Override
    public MenuModel getEmptyModel () {
        return new MenuModel();
    }

}
