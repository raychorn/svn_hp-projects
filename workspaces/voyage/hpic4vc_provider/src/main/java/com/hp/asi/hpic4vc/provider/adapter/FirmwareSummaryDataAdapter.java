package com.hp.asi.hpic4vc.provider.adapter;

import com.hp.asi.hpic4vc.provider.data.FirmwareResult;
import com.hp.asi.hpic4vc.provider.data.SoftwareFirmware;
import com.hp.asi.hpic4vc.provider.impl.SessionInfo.VObjectType;
import com.hp.asi.hpic4vc.provider.locale.I18NProvider;
import com.hp.asi.hpic4vc.provider.model.TableModel;
import com.hp.asi.hpic4vc.provider.model.TableModel.TableRow;

public class FirmwareSummaryDataAdapter extends
        TableDataAdapter<FirmwareResult> {

    public FirmwareSummaryDataAdapter () {
        super(FirmwareResult.class);
    }

    @Override
    public String getServiceName () {
        String serviceName = "";
        if (null != sessionInfo.getVobjectType()) {
            switch (sessionInfo.getVobjectType()) {
                case HOST:
                    serviceName = "agservices/hostswfwsummary"; 
                    break;
                case VM:
                    serviceName = "agservices/vmswfwsummary";
                    break;
                case DATASTORE:
                    serviceName = "agservices/datastoreswfwsummary";
                    break;
                case CLUSTER:
                    serviceName = "agservices/clusterswfwsummary";
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

	@Override	
	public String getErrorMsg(FirmwareResult rawData) {
	    if (null != rawData.getErrorMessage() &&
	            !rawData.getErrorMessage().equals("") &&
	            isResultEmpty(rawData)) {
	        return rawData.getErrorMessage();
	    }		
	    return null;
	}
	
    @Override
    public boolean isResultEmpty (FirmwareResult rawData) {
        boolean isFirmwareEmpty = false;
        boolean isSoftwareEmpty = false;
        
        if (null == rawData.getFirmware() || rawData.getFirmware().length < 1) {
            isFirmwareEmpty = true;
        }
        
        if (null == rawData.getSoftware() || rawData.getSoftware().length < 1) {
            isSoftwareEmpty = true;
        }

        return (isFirmwareEmpty && isSoftwareEmpty);
    }

    @Override
    public void setColumns (TableModel table) {
        String name = this.i18nProvider.getInternationalString(locale,
                                        I18NProvider.FirmwareSummary_Name);
        String version = this.i18nProvider.getInternationalString(locale,
                                        I18NProvider.FirmwareSummary_Version);

        table.addColumn(name, null, "200", null);
        table.addColumn(version, null, "50", null);
    }

    @Override
    public void setRows (TableModel table, FirmwareResult rawData) {
        for (SoftwareFirmware softwareData : rawData.getSoftware()) {
            TableRow row = makeRow(softwareData);            
            table.addRow(row);
        }

        for (SoftwareFirmware firmwareData : rawData.getFirmware()) {
            TableRow row = makeRow(firmwareData);            
            table.addRow(row);
        }
    }
    
    private TableRow makeRow(final SoftwareFirmware swfwData) {
        String id    = null;
        TableRow row = new TableRow();
        row.addCell(id, swfwData.getName(), swfwData.getName());
        if (VObjectType.CLUSTER == sessionInfo.getVobjectType()) {
            String versionPrintStr = getPrettyVersionData(swfwData.getVersions());
            row.addCell(id, versionPrintStr, versionPrintStr);
        } else {
            row.addCell(id, swfwData.getVersion(), swfwData.getVersion());
        }
        return row;        
    }
    
    private String getPrettyVersionData(final String[] versions) {
        if (versions.length <= 0) {
            return "";
        }
        
        if (1 == versions.length) {
            return versions[0];
        }
        
        StringBuilder sb = new StringBuilder();
        String delimiter = ", ";
        
        for (String item : versions) {
                sb.append(item);
                sb.append(delimiter);
        }
        sb.delete(sb.length() - delimiter.length(), sb.length());
        
        return sb.toString();
    }

}
