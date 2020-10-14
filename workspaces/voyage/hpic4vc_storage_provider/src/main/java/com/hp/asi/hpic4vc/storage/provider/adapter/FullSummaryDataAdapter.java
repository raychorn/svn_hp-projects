package com.hp.asi.hpic4vc.storage.provider.adapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.hp.asi.hpic4vc.provider.locale.I18NProvider;
import com.hp.asi.hpic4vc.provider.model.BarChartGroupModel;
import com.hp.asi.hpic4vc.provider.model.BarChartModel;
import com.hp.asi.hpic4vc.provider.model.FullSummaryModel;
import com.hp.asi.hpic4vc.provider.model.LabelValueListModel;
import com.hp.asi.hpic4vc.provider.model.SummaryPortletModel;
import com.hp.asi.hpic4vc.storage.provider.locale.I18NStorageProvider;
import com.hp.asi.ui.hpicsm.ws.data.BackupSystemSummaryWSImpl;
import com.hp.asi.ui.hpicsm.ws.data.BackupSystemSummaryWSImpl.BackupSystemInfoWSImpl;
import com.hp.asi.ui.hpicsm.ws.data.BackupSystemSummaryWSImpl.BackupSystemInfoWSImpl.ServiceSetSummaryWSImpl;
import com.hp.asi.ui.hpicsm.ws.data.FullSummaryResult;
import com.hp.asi.ui.hpicsm.ws.data.FullSummaryWSImpl;
import com.hp.asi.ui.hpicsm.ws.data.HostToVmSummaryWSImpl;
import com.hp.asi.ui.hpicsm.ws.data.HostToVmSummaryWSImpl.DatastoreSummaryWSImpl;
import com.hp.asi.ui.hpicsm.ws.data.StorageToHostSummaryWSImpl;
import com.hp.asi.ui.hpicsm.ws.data.StorageToHostSummaryWSImpl.ArrayStoragePoolSummaryWSImpl;
import com.hp.asi.ui.hpicsm.ws.data.StorageToHostSummaryWSImpl.ArrayStoragePoolSummaryWSImpl.StoragePoolSummaryWSImpl;

public class FullSummaryDataAdapter extends AbstractSummaryDataAdapter<FullSummaryResult, FullSummaryModel> {
    private static final String SERVICE_NAME = "services/swd/fullsummary";
    
    public FullSummaryDataAdapter(){
        super(FullSummaryResult.class);
    }

    @Override
    public String getServiceName () {
        return SERVICE_NAME;
    }
    
    @Override
    public FullSummaryModel getEmptyModel () {
        return new FullSummaryModel();
    }
    
    @Override 
    public FullSummaryModel formatData(FullSummaryResult rawData) {
    	return super.makeData(rawData);
    }
    
    @Override
    public FullSummaryModel createModel (FullSummaryWSImpl wsObject) {
        FullSummaryModel model = new FullSummaryModel();
   
        StorageToHostSummaryWSImpl hpToVobject        = wsObject.getStorageToHost();
        HostToVmSummaryWSImpl vObjectToHp             = wsObject.getHostToVm();
        BackupSystemSummaryWSImpl backupSystemSummary = wsObject.getBackupSystemSummary();
        Collection<ArrayStoragePoolSummaryWSImpl> arrayPoolSummaries =
                hpToVobject.getArrayStoragePoolSummaries();
        
        model.hpToVobjectTitle = hpToVobject.getFormattedTitle();
        model.hpToVobject      = createHpToVobjectSummary(hpToVobject);
        if (null == arrayPoolSummaries || arrayPoolSummaries.size() < 1) {
            model.arrayTitle   = I18NStorageProvider.getInstance().getInternationalString
                    (locale, I18NStorageProvider.FS_NO_ARRAYS);
        } else {
            model.arrayTitle   = I18NStorageProvider.getInstance().getInternationalString
                    (locale, I18NStorageProvider.FS_ARRAYS);
            model.arraySummaries = createArraySummaries(hpToVobject); 
        }
        
        model.backupSystemSummaries = createBackupSystemSummaries(backupSystemSummary);
        if (model.backupSystemSummaries.size() < 1) {
            model.backupSystemSummariesTitle = I18NStorageProvider
                    .getInstance()
                    .getInternationalString(locale,
                                            I18NStorageProvider.FS_NO_BACKUP_SYSTEMS);
        } else {
            model.backupSystemSummariesTitle = I18NStorageProvider
                    .getInstance()
                    .getInternationalString(locale,
                                            I18NStorageProvider.FS_BACKUP_SYSTEMS);
        }
        model.vObjectToHpTitle = vObjectToHp.getFormattedTitle();
        model.vObjectToHp      = createVobjectToHpSummary(vObjectToHp);
        model.dsTitle          = I18NStorageProvider.getInstance().getInternationalString
                (locale, I18NStorageProvider.FS_DATASTORES);
        model.dsSummaries      = createDatastoreSummaries(vObjectToHp.getDatastoreSummaries());
        
        return model;
    }

    private List<BarChartGroupModel> createArraySummaries 
    (final StorageToHostSummaryWSImpl hpToVobject) {
        List<BarChartGroupModel> arrayModels = new ArrayList<BarChartGroupModel>();
        
        Collection<ArrayStoragePoolSummaryWSImpl> summaries = 
                hpToVobject.getArrayStoragePoolSummaries();
        for (ArrayStoragePoolSummaryWSImpl arraySummary : summaries) {
            BarChartGroupModel arraySummaryModel = new BarChartGroupModel();
            arraySummaryModel.groupTitle         = arraySummary.getFormattedArrayName();
            arraySummaryModel.barChartData       = createArrayBarChartData
                    (arraySummary.getStoragePoolSummaries());
            arrayModels.add(arraySummaryModel);
        }
        return arrayModels;
    }
    
    private List<BarChartModel> createArrayBarChartData 
    (final Collection<StoragePoolSummaryWSImpl> storagePoolSummaries) {
        List<BarChartModel> barCharts = new ArrayList<BarChartModel>();
        
        for (StoragePoolSummaryWSImpl poolSummary : storagePoolSummaries) {
            BarChartModel barChart = new BarChartModel();
            barChart.info       = poolSummary.getFormattedStoragePoolName();
            barChart.usedSpace  = poolSummary.getRelevantUsedPoolCapacity();
            barChart.freeSpace  = poolSummary.getRelevantTotalPoolCapacity() - barChart.usedSpace;
            barChart.hoverData  = poolSummary.getFormattedRelevantVolumeCount() + "\n" +
                                  poolSummary.getFormattedSavingsSummary();

            barCharts.add(barChart);
        }
        
        if (barCharts.size() <=0) {
            BarChartModel barChart = new BarChartModel();
            barChart.errorMessage = I18NProvider.getInstance().getInternationalString
                    (locale, I18NProvider.Communication_Failure);
            barCharts.add(barChart);
        }
        return barCharts;
    }
    
    private List<BarChartGroupModel> createBackupSystemSummaries 
    (final BackupSystemSummaryWSImpl backupSystemSummaries) {
        List<BarChartGroupModel> backupSystemModels = new ArrayList<BarChartGroupModel>();
        
        Collection<BackupSystemInfoWSImpl> backupSystemInfoSummaries = 
                backupSystemSummaries.getBackupSystems();
        for (BackupSystemInfoWSImpl backupSystemInfoSummary : backupSystemInfoSummaries) {
            BarChartGroupModel backupSystemSummaryModel = new BarChartGroupModel();
            backupSystemSummaryModel.groupTitle   = backupSystemInfoSummary.getBackupSystemType() 
                                                    + " \"" 
                                                    + backupSystemInfoSummary.getBackupSystemName()
                                                    + "\"";
            backupSystemSummaryModel.barChartData = createBackupSystemServiceSetBarChartData
                    (backupSystemInfoSummary.getServiceSetSummaries());
            backupSystemModels.add(backupSystemSummaryModel);
        }
        return backupSystemModels;
    }
    
    private List<BarChartModel> createBackupSystemServiceSetBarChartData 
    (final Collection<ServiceSetSummaryWSImpl> serviceSetSummaries) {
        List<BarChartModel> barCharts = new ArrayList<BarChartModel>();
        
        for (ServiceSetSummaryWSImpl serviceSetSummary : serviceSetSummaries) {
            BarChartModel barChart = new BarChartModel();
            barChart.info       = serviceSetSummary.getServiceSetName();
            barChart.usedSpace  = serviceSetSummary.getUsedCapacity();
            barChart.freeSpace  = serviceSetSummary.getFreeCapacity();
            barChart.hoverData  = serviceSetSummary.getDedupeRatio()
                                  + " Deduplication Ratio \n"
                                  + serviceSetSummary.getFormattedUsedCapacity()
                                  + " Used / "
                                  + serviceSetSummary.getFormattedTotalCapacity()
                                  + " Total";

            barCharts.add(barChart);
        }
        
        if (barCharts.size() <=0) {
            BarChartModel barChart = new BarChartModel();
            barChart.errorMessage = I18NProvider.getInstance().getInternationalString
                    (locale, I18NProvider.Communication_Failure);
            barCharts.add(barChart);
        }
        return barCharts;
    }

    private SummaryPortletModel createVobjectToHpSummary (final HostToVmSummaryWSImpl vObjectToHp) {
        SummaryPortletModel portletModel = new SummaryPortletModel();
        portletModel.fieldData           = createVobjectToHpFieldData(vObjectToHp);
        portletModel.pieChartData        = createPieChartData(vObjectToHp.getHostUsed(),
                                                              vObjectToHp.getHostProvisioned()); 
        return portletModel;
    }


    private LabelValueListModel createVobjectToHpFieldData (HostToVmSummaryWSImpl vObjectToHp) {
        LabelValueListModel fieldData   = new LabelValueListModel();
        Collection<String> labels = vObjectToHp.getHostToVmLabels();
        int i = 0;
        for (String str : labels) {
            String label;
            String data;

            switch (i) {
                case 0:  label = I18NStorageProvider.getInstance().getInternationalString
                                 (locale, I18NStorageProvider.Portlet_Volumes);
                         data  = this.removeHtml(str);
                         break;
                case 1:  label = str;
                         data  = vObjectToHp.getFormattedHostProvisioned();
                         break;
                case 2:  label = str;
                         data  = vObjectToHp.getFormattedHostUsed();
                         break;
                case 3:  label = str;
                         data  = vObjectToHp.getFormattedHostAvailable();
                         break;
                case 4:  label = I18NStorageProvider.getInstance().getInternationalString
                                 (locale, I18NStorageProvider.Portlet_Overprovisioned);
                         data  = str;
                         break;
                default: label = "";
                         data  = null;
            }
            fieldData.addLabelValuePair(label, data);
            i++;
        }        
        return fieldData;
    }

    private List<BarChartGroupModel> createDatastoreSummaries 
    (final Collection<DatastoreSummaryWSImpl> dsSummaries) {
        BarChartGroupModel arraySummaryModel = new BarChartGroupModel();
        arraySummaryModel.groupTitle = I18NStorageProvider.getInstance().getInternationalString
                (locale, I18NStorageProvider.FS_DATASTORES);
        
        for (DatastoreSummaryWSImpl dsSummary : dsSummaries) {
            BarChartModel barChart = new BarChartModel();
            barChart.info          = dsSummary.getFormattedDatastoreName();
            barChart.usedSpace     = dsSummary.getUsedCapacity();
            barChart.freeSpace     = dsSummary.getTotalCapacity() - dsSummary.getUsedCapacity();
            if (dsSummary.getTotalProvisioned() <= 0 && dsSummary.getTotalCapacity() <= 0) {
                barChart.notProvisioned = 1l;
            }
            else if (dsSummary.getTotalProvisioned() > dsSummary.getTotalCapacity()) {
                barChart.overProvisioned = dsSummary.getTotalProvisioned() - dsSummary.getTotalCapacity();
            } else {
                barChart.notProvisioned = dsSummary.getTotalCapacity() - dsSummary.getTotalProvisioned();
            }
            barChart.hoverData     = removeHtml(
            		dsSummary.getFormattedRelevantVolumeCount() + "\n" +
                    dsSummary.getFormattedDSSummary() + "\n" +
                    dsSummary.getFormattedWarning());;
            arraySummaryModel.barChartData.add(barChart);
        }
        return Collections.singletonList(arraySummaryModel);
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
                                   (locale, I18NStorageProvider.Portlet_Volumes);
                         data  = this.removeHtml(str);
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
                case 4:  label = I18NStorageProvider.getInstance().getInternationalString
                                   (locale, I18NStorageProvider.Portlet_Overprovisioned);
                         data  = str;
                         break;
                default: label = "";
                         data  = null;
            }
            fieldData.addLabelValuePair(label, data);
            i++;
        }        
        return fieldData;
    }
 
}
