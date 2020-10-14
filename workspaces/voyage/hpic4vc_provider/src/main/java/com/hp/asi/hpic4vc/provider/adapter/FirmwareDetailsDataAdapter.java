package com.hp.asi.hpic4vc.provider.adapter;

import com.hp.asi.hpic4vc.provider.data.SoftwareFirmware;
import com.hp.asi.hpic4vc.provider.data.FirmwareResult;
import com.hp.asi.hpic4vc.provider.impl.SessionInfo.VObjectType;
import com.hp.asi.hpic4vc.provider.locale.I18NProvider;
import com.hp.asi.hpic4vc.provider.model.TableModel;
import com.hp.asi.hpic4vc.provider.model.TableModel.TableRow;

public class FirmwareDetailsDataAdapter extends
        TableDataAdapter<FirmwareResult> {

    public FirmwareDetailsDataAdapter () {
        super(FirmwareResult.class);
    }

    @Override
    public String getServiceName () {
        String serviceName = "";
        if (null != sessionInfo.getVobjectType()) {
            switch (sessionInfo.getVobjectType()) {
                case HOST:
                    serviceName = "agservices/hostswfwdetail"; 
                    break;
                case VM:
                    serviceName = "agservices/vmswfwdetail";
                    break;
                case DATASTORE:
                    serviceName = "agservices/datastoreswfwdetail";
                    break;
                case CLUSTER:
                    serviceName = "agservices/clusterswfwdetail";
                    break;
                default:
                        break;
            }
        }      
        return serviceName;
    }

    @Override
    public TableModel getEmptyModel () {
        return new TableModel();
    }

	public String getErrorMsg(FirmwareResult rawData) {
	    if (null != rawData.getErrorMessage() &&
	            !rawData.getErrorMessage().equals("") &&
	            isResultEmpty(rawData)) {
	        return rawData.getErrorMessage();
	    }
	    return null;
	}
	
    public boolean isResultEmpty (FirmwareResult rawData) {
        if ((null == rawData.getFirmware()) && (null == rawData.getSoftware())) {
            return true;
        }
        return false;
    }

    @Override
    public void setColumns (TableModel table) {
        if (VObjectType.CLUSTER == sessionInfo.getVobjectType()) {
            addClusterColumns(table);
        }
        addNormalColumns(table);
    }
    
    @Override
    public void setRows (TableModel table, FirmwareResult rawData) {
        if (VObjectType.CLUSTER == sessionInfo.getVobjectType()) {
            setClusterRows(table, rawData);
            
        } else {
            setNormalRows(table, rawData);
        }

    }

    private void addNormalColumns(TableModel table) {
        String name = this.i18nProvider.getInternationalString(
                                       locale,
                                       I18NProvider.FirmwareSummary_Name);
       String description = this.i18nProvider.getInternationalString(
                                       locale,
                                       I18NProvider.FirmwareSummary_Description);
       String version = this.i18nProvider.getInternationalString(
                                       locale,
                                       I18NProvider.FirmwareSummary_Version);
       table.addColumn(name,        null, "200", null, true, false, false);
       table.addColumn(description, null, "300", null, true, false, false);
       table.addColumn(version,     null,  "80", null, true, true, false);
    }

    private void addClusterColumns (TableModel table) {
        String host = this.i18nProvider.getInternationalString(
                                          locale,
                                          I18NProvider.FirmwareSummary_Host);
        String type = this.i18nProvider.getInternationalString(
                                          locale,
                                          I18NProvider.FirmwareSummary_Type);

        table.addColumn(host,        null, "200", null, true, false, false);
        table.addColumn(type,        null, "100", null, true, false, false);      
    } 
    
    private void setNormalRows (TableModel table, FirmwareResult rawData) {
        for (SoftwareFirmware softwareData : rawData.getSoftware()) {
            TableRow row = new TableRow(); 
            populateNormalData(row, softwareData);
            table.addRow(row);
        }

        for (SoftwareFirmware firmwareData : rawData.getFirmware()) {
            TableRow row = new TableRow(); 
            populateNormalData(row, firmwareData);
            table.addRow(row);
        }        
    }

    private void setClusterRows (TableModel table, FirmwareResult rawData) {
        for (SoftwareFirmware clusterRowData : rawData.getClusterRows()) {
            TableRow row = new TableRow();
            populateClusterData(row, clusterRowData);
            populateNormalData(row, clusterRowData);
            table.addRow(row);
        }
        
    }

    private void populateNormalData(TableRow row, final SoftwareFirmware swfwData) {
        String id    = null;
        row.addCell(id, swfwData.getName(),       swfwData.getName(), 		 null);
        row.addCell(id, swfwData.getDescription(),swfwData.getDescription(), null);
        row.addCell(id, swfwData.getVersion(),    swfwData.getVersion(), 	 null);   
    }
    
    private void populateClusterData(TableRow row, final SoftwareFirmware swfwData) {
        String id    = null;
        row.addCell(id, swfwData.getHost(),       swfwData.getHost(), null);
        row.addCell(id, swfwData.getType(),       swfwData.getType(), null);     
    }
}
