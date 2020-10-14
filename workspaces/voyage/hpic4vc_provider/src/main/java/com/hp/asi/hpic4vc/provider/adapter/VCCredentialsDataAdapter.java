package com.hp.asi.hpic4vc.provider.adapter;

import com.hp.asi.hpic4vc.provider.data.VCCredentialsData;
import com.hp.asi.hpic4vc.provider.data.VCCredentialsResult;
import com.hp.asi.hpic4vc.provider.locale.I18NProvider;
import com.hp.asi.hpic4vc.provider.model.TableModel;
import com.hp.asi.hpic4vc.provider.model.TableModel.TableRow;


public class VCCredentialsDataAdapter extends TableDataAdapter<VCCredentialsResult>{

    private static final String SERVICE_NAME = "settings/vcpassword";
    
    public VCCredentialsDataAdapter() {
        super(VCCredentialsResult.class);
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
    public String getErrorMsg(VCCredentialsResult rawData) {
        return rawData.getErrorMessage();
    }

    @Override
    public boolean isResultEmpty(VCCredentialsResult rawData) {
        if (null == rawData.getPwdb()) {
            return true;
        }
        return false;
    }
    
    @Override
    public void setColumns(TableModel table) {
        String hostname = this.i18nProvider.getInternationalString(locale, I18NProvider.VCCredentials_Hostname);
        String username = this.i18nProvider.getInternationalString(locale, I18NProvider.VCCredentials_Username);
        String password = this.i18nProvider.getInternationalString(locale, I18NProvider.VCCredentials_Password);
        String rowId    = this.i18nProvider.getInternationalString(locale, I18NProvider.VCCredentials_RowId);
        String type    =  this.i18nProvider.getInternationalString(locale, I18NProvider.VCCredentials_Type);

        table.addColumn(hostname, null, "200", null, true,  false, false);
        table.addColumn(username, null, "200", null, true,  false, false);
        table.addColumn(password, null, "200", null, true,  false, false);
        table.addColumn(rowId,    null, "0",   null, false, false, false);
        table.addColumn(type,     null, "0",   null, false, false, false);
    }

    @Override
    public void setRows(TableModel table, VCCredentialsResult rawData) {
        String id = null;
        for (VCCredentialsData vcCredentialsData : rawData.getPwdb()) {
            TableRow row = new TableRow();
            row.addCell(id, vcCredentialsData.getHost(),     vcCredentialsData.getHost(), 		null);
            row.addCell(id, vcCredentialsData.getUsername(), vcCredentialsData.getUsername(), 	null);
            row.addCell(id, vcCredentialsData.getPassword(), vcCredentialsData.getPassword(), 	null);
            row.addCell(id, vcCredentialsData.getId(),       vcCredentialsData.getId(), 		null);
            row.addCell(id, vcCredentialsData.getType(),     vcCredentialsData.getType(), 		null);
            table.addRow(row);
        }
    }
}
