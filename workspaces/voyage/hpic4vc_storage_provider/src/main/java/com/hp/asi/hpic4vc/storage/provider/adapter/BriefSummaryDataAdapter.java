package com.hp.asi.hpic4vc.storage.provider.adapter;

import com.hp.asi.hpic4vc.provider.adapter.DataAdapter;
import com.hp.asi.hpic4vc.provider.locale.I18NProvider;
import com.hp.asi.hpic4vc.provider.model.BarChartGroupModel;
import com.hp.asi.hpic4vc.provider.model.BarChartModel;
import com.hp.asi.hpic4vc.provider.model.StorageOverviewModel;
import com.hp.asi.hpic4vc.storage.provider.locale.I18NStorageProvider;
import com.hp.asi.ui.hpicsm.ws.data.ArraySummaryWSImpl;
import com.hp.asi.ui.hpicsm.ws.data.BriefSummaryResults;

/**
 * This summary contains a list of arrays and the number of volumes on
 * each array.  The result is placed in the overview tab in Velocity.
 * @author thorntji
 *
 */
public class BriefSummaryDataAdapter extends DataAdapter<BriefSummaryResults, StorageOverviewModel> {
    private static final String SERVICE_NAME = "services/swd/summary";

    public BriefSummaryDataAdapter () {
        super(BriefSummaryResults.class);
    }

    @Override
    public StorageOverviewModel formatData (BriefSummaryResults rawData) {
        StorageOverviewModel model = new StorageOverviewModel();
        if (rawData.hasError() || null != rawData.getErrorMessage()) {
            log.info("BriefSummaryResults had an error message.  " +
                    "Returning a StorageOverviewModel with the error message");
            model.errorMessage = rawData.getErrorMessage();
            return model;
        }
        
        ArraySummaryWSImpl[] results = rawData.getResults();
        if (null == results) {
            model.errorMessage = I18NStorageProvider.getInstance().getInternationalString
                    (locale, I18NStorageProvider.SO_NullResult);
            log.info("BriefSummaryResults.getResults() is null.  " +
            		"Returning a StorageOverviewModel with an error message set.");
            return model;
        }
        
        if (0 == results.length ) {
            model.informationMessage = I18NStorageProvider.getInstance().getInternationalString
                    (locale, I18NStorageProvider.SO_NoResults);
            log.info("BriefSummaryResults.getResults() has size zero.  " +
                    "Returning a StorageOverviewModel with an error message set.");
            return model;
        }
        
        for (ArraySummaryWSImpl arraySummaryWSImpl : rawData.getResults()) {
            if (null != arraySummaryWSImpl) {
                BarChartGroupModel arraySummary = 
                        makeArraySummaryModel(arraySummaryWSImpl);
                model.arraySummaries.add(arraySummary);
            }
        }
        
        return model;
    }

    @Override
    public String getServiceName () {
        return SERVICE_NAME;
    }

    @Override
    public StorageOverviewModel getEmptyModel () {
        return new StorageOverviewModel();
    }
    
    private BarChartGroupModel makeArraySummaryModel (final ArraySummaryWSImpl arraySummaryWSImpl) {
        BarChartGroupModel arraySummary = new BarChartGroupModel();
        arraySummary.groupTitle    = arraySummaryWSImpl.getFormattedArrayName();
        arraySummary.barChartData.add(makeBarChartData(arraySummaryWSImpl));
        return arraySummary;
    }
    
    private BarChartModel makeBarChartData(final ArraySummaryWSImpl arraySummaryWSImpl) {
        BarChartModel barChartData = new BarChartModel();
        barChartData.info       = arraySummaryWSImpl.getFormattedRelevantVolumeCount();
        barChartData.usedSpace  = arraySummaryWSImpl.getUsedCapacity();
        barChartData.freeSpace  = arraySummaryWSImpl.getFreeCapacity();
        barChartData.hoverData  = arraySummaryWSImpl.getFormattedTotalCapacity() + '\n' +
                                  arraySummaryWSImpl.getFormattedUsedCapacity()  + '\n' +
                                  arraySummaryWSImpl.getFormattedCapacityPercentSavings();
        checkForNoData(barChartData);
        return barChartData;
    }

    private void checkForNoData (BarChartModel barChartData) {
        if (barChartData.usedSpace <= 01 && barChartData.freeSpace <= 0l) {
            barChartData.errorMessage = I18NProvider.getInstance().getInternationalString
                    (locale, I18NProvider.Communication_Failure);
        }
        
    }
}
