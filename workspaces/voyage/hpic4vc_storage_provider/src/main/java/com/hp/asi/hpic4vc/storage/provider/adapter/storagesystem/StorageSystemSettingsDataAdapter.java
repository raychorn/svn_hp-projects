package com.hp.asi.hpic4vc.storage.provider.adapter.storagesystem;

import com.hp.asi.hpic4vc.provider.adapter.SettingsDataAdapter;

public class StorageSystemSettingsDataAdapter extends SettingsDataAdapter {
    
    @Override
    public String getServiceName () {
        return "config/pages/storage_system_overview/settings";
    }

}
