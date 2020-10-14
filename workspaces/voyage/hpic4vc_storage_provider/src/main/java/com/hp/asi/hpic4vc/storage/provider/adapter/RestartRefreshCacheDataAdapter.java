package com.hp.asi.hpic4vc.storage.provider.adapter;

import com.hp.asi.hpic4vc.provider.adapter.DataAdapter;
import com.hp.asi.hpic4vc.provider.data.RefreshResult;
import com.hp.asi.hpic4vc.provider.model.RefreshCacheModel;

public class RestartRefreshCacheDataAdapter extends
        DataAdapter<RefreshResult, RefreshCacheModel> {

    private static final String SERVICE_NAME = "services/swd/restartrefresh";

    public RestartRefreshCacheDataAdapter () {
        super(RefreshResult.class);
    }

    @Override
    public String getServiceName () {
        return SERVICE_NAME;
    }

    @Override
    public RefreshCacheModel formatData (RefreshResult rawData) {
        RefreshCacheModel rcmData = new RefreshCacheModel();
        rcmData.setEstimatedTimeLabel(rawData.getFormattedEstTimeLabel());
        rcmData.setRemainingTime(rawData.getFormattedRemainingTime());
        rcmData.setSummary(removeHtml(rawData.getFormattedSummary()));
        rcmData.setPopulating(false);
        if (rawData.isPrepopulating() || rawData.isRepopulating()) {
           rcmData.setPopulating(true);
        }
        return rcmData;
    }

    @Override
    public RefreshCacheModel getEmptyModel () {
        return new RefreshCacheModel();
    }

}
