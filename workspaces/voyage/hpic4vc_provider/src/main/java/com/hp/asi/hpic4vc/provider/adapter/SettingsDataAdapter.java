package com.hp.asi.hpic4vc.provider.adapter;

import com.hp.asi.hpic4vc.provider.data.SettingsData;
import com.hp.asi.hpic4vc.provider.locale.I18NProvider;
import com.hp.asi.hpic4vc.provider.model.LinkModel;
import com.hp.asi.hpic4vc.provider.model.MenuModel;

public class SettingsDataAdapter extends DataAdapter<SettingsData[], MenuModel> {
	
	
    public SettingsDataAdapter () {
        super(SettingsData[].class);
    }

    @Override
    public MenuModel formatData (SettingsData[] rawData) {
        MenuModel configList = new MenuModel();

        for (SettingsData config : rawData) {
            LinkModel lm = createLinkModel(config);
            if (null != lm) {
                configList.addMenu(lm);
            }
        }

        return configList;
    }

    @Override
    public String getServiceName () {
        String serviceName = "";
        if (null != sessionInfo.getVobjectType()) {
            switch (sessionInfo.getVobjectType()) {
                case HOST:
                    serviceName = "config/pages/host_overview/settings"; 
                    break;
                case VM:
                    serviceName = "config/pages/vm_overview/settings";
                    break;
                case DATASTORE:
                    serviceName = "config/pages/datastore_overview/settings";
                    break;
                case CLUSTER:
                    serviceName = "config/pages/cluster_overview/settings";
                    break;
                default:
                        break;
            }
        } 
        return serviceName;
    }
    
    @Override
    public MenuModel getEmptyModel () {
        return new MenuModel();
    }
    
    private LinkModel createLinkModel(final SettingsData config) {
        DisplayName nameEnum = DisplayName.getDisplayName(config.getDisplay_name());
        if (null == nameEnum) {
            return null;
        }
        LinkModel lm   = new LinkModel();        
        lm.displayName = this.i18nProvider.getInternationalString(locale, nameEnum.I18NDisplayName);
        
        switch (nameEnum) {
            case SAP:
                lm.url = config.getURL() + "?sessionId=" + sessionInfo.getSessionId();
                break;
            default:
                lm.url = config.getURL();
                break;
        }
        
        return lm;
    }
    
	
    enum DisplayName {
        Version       ("Version Information",  I18NProvider.Settings_Version),
        Cluster       ("Cluster Properties",   I18NProvider.Settings_Cluster_Properties),
        Host          ("Host Properties",      I18NProvider.Settings_Host_Properties),
        Communication ("Communication Status", I18NProvider.Settings_Communication_Status),
        SAP           ("Storage Administrator Portal for VCenter", 
                       I18NProvider.Settings_Storage_Admin_Portal),
        VASA          ("VASA Provider Registration", 
                       I18NProvider.Settings_VASA_Provider_URL);
        
        final String displayName;
        final String I18NDisplayName;
        
        DisplayName (final String name, final String I18nName) {
            this.displayName     = name;
            this.I18NDisplayName = I18nName;
        }
        
        static DisplayName getDisplayName(final String str) {
            for (DisplayName nameEnum : DisplayName.values()) {
                if (nameEnum.displayName.equalsIgnoreCase(str)) {
                    return nameEnum;
                }
            }
            return null;
        }
        
    }
}
