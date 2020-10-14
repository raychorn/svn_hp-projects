package com.hp.asi.hpic4vc.provider.adapter;

import com.hp.asi.hpic4vc.provider.locale.I18NProvider;
import com.hp.asi.hpic4vc.provider.model.TableModel;
import com.hp.asi.hpic4vc.provider.model.TableModel.TableRow;
import com.hp.asi.hpic4vc.provider.data.HealthResult;
import com.hp.asi.hpic4vc.provider.data.HealthData;

public class HealthDetailsDataAdapter extends TableDataAdapter<HealthResult> {
    
    public HealthDetailsDataAdapter() {
        super(HealthResult.class);
    }
    
    @Override
    public String getServiceName () {
        String serviceName = "";
        if (null != sessionInfo.getVobjectType()) {
            switch (sessionInfo.getVobjectType()) {
                case HOST:
                    serviceName = "agservices/hoststatus"; 
                    break;
                case VM:
                    serviceName = "agservices/vmstatus";
                    break;
                case DATASTORE:
                    serviceName = "agservices/datastorestatus";
                    break;
                case CLUSTER:
                    serviceName = "agservices/clusterstatus";
                    break;
                default:
                        break;
            }
        }      
        return serviceName;
    }

    @Override
    public String getErrorMsg (HealthResult rawData) {
        return rawData.getErrorMessage();
    }

    @Override
    public boolean isResultEmpty (HealthResult rawData) {
        if (null == rawData.getResult()) {
            return true;
        }
        return false;
    }

    @Override
	public void setColumns (TableModel table) {
        String status      = this.i18nProvider.getInternationalString(locale, I18NProvider.Health_Status);
        String source      = this.i18nProvider.getInternationalString(locale, I18NProvider.Health_Source);
        String description = this.i18nProvider.getInternationalString(locale, I18NProvider.Health_Description);
        
        String statusTT    	= this.i18nProvider.getInternationalString(locale, I18NProvider.Health_Status_Tooltip);
        String sourceTT    	= this.i18nProvider.getInternationalString(locale, I18NProvider.Health_Source_Tooltip);
        String descriptionTT= this.i18nProvider.getInternationalString(locale, I18NProvider.Health_Description_Tooltip);

        table.addColumn(status, 	 statusTT, 		"150", null, true, false, true);
        table.addColumn(source, 	 sourceTT, 		"200", null, true, false, false);       
        table.addColumn(description, descriptionTT, "450", null, true, false, false);
    }

    @Override
    public void setRows (TableModel table, HealthResult rawData) {
        String id    = null;

        for (HealthData healthItem : rawData.getResult()) {
            TableRow row = new TableRow();
            String healthStatus = healthItem.getHealthStatus().toLowerCase();
            // This is done to make sure the correct icon is displayed in the table. This will not alter the Health status displayed.
            if (healthStatus.equalsIgnoreCase("error")) {
            	healthStatus = "failed";
            }
            row.addCell(id, healthItem.getHealthStatus(), healthItem.getHealthStatus(), healthStatus);
            row.addCell(id, healthItem.getName(),         healthItem.getName(), 		null);
            row.addCell(id, healthItem.getDetail(),       healthItem.getDetail(), 		null);
            
            table.addRow(row);
        }
        
    }
}
