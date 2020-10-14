package com.hp.asi.hpic4vc.storage.provider.impl;

import com.hp.asi.hpic4vc.storage.provider.api.Hpic4vc_storage_provider;

/**
 * Implementation of the Hpic4vc_storage_provider interface
 */
public class Hpic4vc_storage_providerImpl implements Hpic4vc_storage_provider {

    @Override
    public String echo (String message) {
        return message;
    }

}
