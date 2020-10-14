package com.hp.asi.hpic4vc.provider.adapter;

import com.hp.asi.hpic4vc.provider.data.HPOneViewCredentialsData;
import com.hp.asi.hpic4vc.provider.data.HPOneViewCredentialsResult;
import com.hp.asi.hpic4vc.provider.locale.I18NProvider;
import com.hp.asi.hpic4vc.provider.model.TableModel;
import com.hp.asi.hpic4vc.provider.model.TableModel.TableRow;


public class HPOneViewCredentialsDataAdapter extends TableDataAdapter<HPOneViewCredentialsResult>{

    private static final String SERVICE_NAME = "credentials";
    
    public HPOneViewCredentialsDataAdapter() {
        super(HPOneViewCredentialsResult.class);
    }
    
    @Override
    public String getServiceName () {
        return SERVICE_NAME;
    }

    @Override
    public TableModel getEmptyModel () {
        return new TableModel();
    }

    @Override
    public String getErrorMsg(HPOneViewCredentialsResult rawData) {
        return rawData.getErrorMessage();
    }

    @Override
    public boolean isResultEmpty(HPOneViewCredentialsResult rawData) {
        return false;
    }
    
    @Override
    public void setColumns(TableModel table) {
        String hostname = this.i18nProvider.getInternationalString(locale, I18NProvider.VCCredentials_Hostname);
        String username = this.i18nProvider.getInternationalString(locale, I18NProvider.VCCredentials_Username);
        String rowId    = this.i18nProvider.getInternationalString(locale, I18NProvider.VCCredentials_RowId);
        String type    =  this.i18nProvider.getInternationalString(locale, I18NProvider.VCCredentials_Type);

        table.addColumn(hostname, null, "200", null, true,  false, false);
        table.addColumn(username, null, "200", null, true,  false, false);
        table.addColumn(rowId,    null, "0",   null, false, false, false);
        table.addColumn(type,     null, "0",   null, false, false, false);
    }

    @Override
    public void setRows(TableModel table, HPOneViewCredentialsResult rawData) {
        String id = null;
        for (HPOneViewCredentialsData HPOneViewCredentialsData : rawData.getPwdb()) {
            TableRow row = new TableRow();
            row.addCell(id, HPOneViewCredentialsData.getIp(),     HPOneViewCredentialsData.getIp(), 		null);
            row.addCell(id, HPOneViewCredentialsData.getUsername(), HPOneViewCredentialsData.getUsername(), 	null);
            row.addCell(id, HPOneViewCredentialsData.getId(),       HPOneViewCredentialsData.getId(), 		null);
            row.addCell(id, HPOneViewCredentialsData.getType(),     HPOneViewCredentialsData.getType(), 		null);
            table.addRow(row);
        }
    }
}
