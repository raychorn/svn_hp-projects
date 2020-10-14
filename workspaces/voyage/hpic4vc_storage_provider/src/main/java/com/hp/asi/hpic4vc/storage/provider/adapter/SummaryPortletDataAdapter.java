package com.hp.asi.hpic4vc.storage.provider.adapter;

import java.util.Collection;

import com.hp.asi.hpic4vc.provider.model.LabelValueListModel;
import com.hp.asi.hpic4vc.provider.model.SummaryPortletModel;
import com.hp.asi.hpic4vc.storage.provider.locale.I18NStorageProvider;
import com.hp.asi.ui.hpicsm.ws.data.FullSummaryResult;
import com.hp.asi.ui.hpicsm.ws.data.FullSummaryWSImpl;
import com.hp.asi.ui.hpicsm.ws.data.StorageToHostSummaryWSImpl;

/**
 * Used the StorageToHostSummaryWSImpl nested in the FullSummaryResult to
 * construct the summary portlet in the summary page of the Voyage UI.
 */
public class SummaryPortletDataAdapter extends AbstractSummaryDataAdapter<FullSummaryResult, SummaryPortletModel> {
    private static final String SERVICE_NAME = "services/swd/fullsummary";
    
    public SummaryPortletDataAdapter() {
        super(FullSummaryResult.class);
    }
    
    @Override
    public String getServiceName () {
        return SERVICE_NAME;
    }

    @Override
    public SummaryPortletModel getEmptyModel () {
        return new SummaryPortletModel();
    }
    
	@Override
	public SummaryPortletModel formatData(FullSummaryResult rawData) {
		return super.makeData(rawData);
	}

    @Override
    public SummaryPortletModel createModel (final FullSummaryWSImpl wsObject) {
        if (null == wsObject.getStorageToHost()) {
            SummaryPortletModel portletModel = new SummaryPortletModel();
            log.info("FullSummaryResult.getResult().getStorageToHost is null.  " +
                    "Returning a SummaryPortletModel with an error message set.");
            portletModel.errorMessage = I18NStorageProvider.getInstance().getInternationalString
                    (locale, I18NStorageProvider.Portlet_NoResult);
            return portletModel;
        }
        return createHpToVobjectSummary(wsObject.getStorageToHost()); 
    }
    
    @Override
    LabelValueListModel createHpToVobjectFieldData (final StorageToHostSummaryWSImpl hpToVobject) {
        LabelValueListModel fieldData   = new LabelValueListModel();
        Collection<String> labels = hpToVobject.getStorageToHostLabels();
        int i = 0;
        for (String str : labels) {
            String label;
            String data;

            switch (i) {
                case 0:  label = I18NStorageProvider.getInstance().getInternationalString
                                   (locale, I18NStorageProvider.Portlet_VolumesLabel);
                         data  = createVolumeData(hpToVobject);
                         break;
                case 1:  label = str;
                         data  = hpToVobject.getFormattedTotalProvisioned();
                         break;
                case 2:  label = str;
                         data  = hpToVobject.getFormattedStorageUsed();
                         break;
                case 3:  label = str;
                         data  = hpToVobject.getFormattedStorageSavings();
                         break;
                case 4:  continue;
                default: label = "";
                         data  = null;
            }
            label = removeColon(label.trim());
            fieldData.addLabelValuePair(label, data);
            i++;
        }        
        return fieldData;
    }
    
    private String createVolumeData(final StorageToHostSummaryWSImpl hpToVobject) {
        StringBuilder sb = new StringBuilder();
        sb.append(hpToVobject.getFormattedVolumeCount());
        sb.append(" ");
        sb.append(I18NStorageProvider.getInstance().getInternationalString
                (locale, I18NStorageProvider.Portlet_Volumes));
        sb.append("\n");
        sb.append(hpToVobject.getFormattedThinProvisionCount());
        sb.append(" ");
        sb.append(I18NStorageProvider.getInstance().getInternationalString
                (locale, I18NStorageProvider.Portlet_Thinprovisioned));
        sb.append("\n");
        sb.append(hpToVobject.getFormattedOverProvisionCount());
        sb.append(" ");
        sb.append(I18NStorageProvider.getInstance().getInternationalString
                (locale, I18NStorageProvider.Portlet_Overprovisioned));
        return sb.toString();
    }
}
