package com.hp.asi.hpic4vc.storage.provider.adapter;

import com.hp.asi.hpic4vc.provider.adapter.DataAdapter;
import com.hp.asi.hpic4vc.provider.model.BaseModel;
import com.hp.asi.hpic4vc.provider.model.LabelValueListModel;
import com.hp.asi.hpic4vc.provider.model.PieChartModel;
import com.hp.asi.hpic4vc.provider.model.SummaryPortletModel;
import com.hp.asi.hpic4vc.storage.provider.locale.I18NStorageProvider;
import com.hp.asi.ui.hpicsm.ws.data.FullSummaryResult;
import com.hp.asi.ui.hpicsm.ws.data.FullSummaryWSImpl;
import com.hp.asi.ui.hpicsm.ws.data.StorageToHostSummaryWSImpl;

/**
 * 
 * @author thorntji
 *
 * @param <T>  The type of object returned from the web service call.
 * @param <S>  The type of object to return to the hpic4vc_ui.
 */
public abstract class AbstractSummaryDataAdapter<T, S extends BaseModel> extends DataAdapter<T, S> {

    public AbstractSummaryDataAdapter (Class<T> clazz) {
        super(clazz);
    }
    
    public S makeData(final FullSummaryResult rawData) {
        S model = getEmptyModel();
        
        if (rawData.hasError()) {
            log.debug("Full summary had an error message.  Returning a " +
                    model.getClass().getName() + 
                    " with an error message set.");
            model.errorMessage = rawData.getErrorMessage();
            return model;
        }
        if (null == rawData.getResult()) {
            model.errorMessage = I18NStorageProvider.getInstance().getInternationalString
                    (locale, I18NStorageProvider.Info_NotAvailable);
            log.debug("Full summary had a null result.  Returning a " +
                    model.getClass().getName() + 
                    " with an error message set.");
            return model;
        }
        
        return createModel(rawData.getResult());
    }
    
    public abstract S createModel (final FullSummaryWSImpl wsObject);

    SummaryPortletModel createHpToVobjectSummary (final StorageToHostSummaryWSImpl hpToVobject) {
        SummaryPortletModel portletModel = new SummaryPortletModel();
        portletModel.fieldData           = createHpToVobjectFieldData(hpToVobject);
        portletModel.pieChartData        = createPieChartData(hpToVobject.getStorageUsed(), 
                                                              hpToVobject.getTotalProvisioned());  
        return portletModel;
    }
    
    abstract LabelValueListModel createHpToVobjectFieldData (final StorageToHostSummaryWSImpl hpToVobject);

    PieChartModel createPieChartData (final long amtUsed, final long amtTotal) {
        PieChartModel pieChartData = new PieChartModel();        
        if (amtUsed < amtTotal) {
            pieChartData.percentUsed = ((int) ( ((double) amtUsed / 
                    (double) amtTotal) * 100.0));
            pieChartData.percentFree = 100 - pieChartData.percentUsed;
        } else {
            pieChartData.percentUsed = 100;
        }
        
        return pieChartData;
    }
    

    String removeColon (String label) {
        if (label.endsWith(":")) {
            return label.substring(0, label.length() - 1);
        } else {
            return label;
        }
    }



}
