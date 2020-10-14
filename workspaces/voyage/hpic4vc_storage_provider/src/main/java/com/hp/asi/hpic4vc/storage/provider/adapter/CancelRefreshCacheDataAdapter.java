package com.hp.asi.hpic4vc.storage.provider.adapter;

import com.hp.asi.hpic4vc.provider.adapter.DataAdapter;
import com.hp.asi.hpic4vc.provider.data.RefreshResult;
import com.hp.asi.hpic4vc.provider.model.RefreshCacheModel;

public class CancelRefreshCacheDataAdapter extends
        DataAdapter<RefreshResult, RefreshCacheModel> {

    private static final String SERVICE_NAME = "services/swd/cancelrefresh";

    public CancelRefreshCacheDataAdapter () {
        super(RefreshResult.class);
    }

    @Override
    public String getServiceName () {
        return SERVICE_NAME;
    }

    @Override
    public RefreshCacheModel formatData (RefreshResult rawData) {
        return null;
    }

    @Override
    public RefreshCacheModel getEmptyModel () {
        return null;
    }

}
