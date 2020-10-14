package com.hp.asi.hpic4vc.storage.provider.adapter;

import com.hp.asi.hpic4vc.provider.adapter.TableDataAdapter;
import com.hp.asi.hpic4vc.provider.impl.SessionInfo.VObjectType;
import com.hp.asi.hpic4vc.provider.model.TableModel;
import com.hp.asi.hpic4vc.provider.model.TableModel.TableRow;
import com.hp.asi.hpic4vc.storage.provider.locale.I18NStorageProvider;
import com.hp.asi.ui.hpicsm.ws.data.HBAResult;
import com.hp.asi.ui.hpicsm.ws.data.HBAWSImpl;

public class HBADataAdapter extends TableDataAdapter<HBAResult>{
    private static final String SERVICE_NAME = "services/swd/hbas";
	
    /**
     * Do this for the AMF.
     */
    public HBADataAdapter () {
        super(HBAResult.class);
    }    
    
    @Override
    public String getServiceName () {
        return SERVICE_NAME;
    }
    
    @Override
    public String getErrorMsg (HBAResult rawData) {
        return rawData.getErrorMessage();
    }

    @Override
	public boolean isResultEmpty (HBAResult rawData) {
        if (null == rawData.getResult()) {
            return true;
        }
        return false;
    }
    
    @Override
    public void setColumns (final TableModel table) {
        String hostNames = I18NStorageProvider.getInstance().getInternationalString(locale, I18NStorageProvider.Hbas_HostNames);
        String type      = I18NStorageProvider.getInstance().getInternationalString(locale, I18NStorageProvider.Hbas_Type);
        String typeHover = I18NStorageProvider.getInstance().getInternationalString(locale, I18NStorageProvider.Hbas_TT_Type);
        String id        = I18NStorageProvider.getInstance().getInternationalString(locale, I18NStorageProvider.Hbas_Id);
        String idHover   = I18NStorageProvider.getInstance().getInternationalString(locale, I18NStorageProvider.Hbas_TT_Id);
        String wwn       = I18NStorageProvider.getInstance().getInternationalString(locale, I18NStorageProvider.Hbas_Wwn);
        String label     = I18NStorageProvider.getInstance().getInternationalString(locale, I18NStorageProvider.Hbas_Label);
        
        table.addColumn(hostNames, 			null,      "250", configData.getVObjectActions(VObjectType.HOST, sessionInfo, false).getMenuItems(), true, false, false);
        table.addColumn(type,     			typeHover, "112", null, true, false, false);       
        table.addColumn(id,        			idHover,   "130", null, true, false, false); 
        table.addColumn(wwn,       			null,      "250", null, true, false, false);
        table.addColumn(label,     			null,      "64",  null, true, false, false);
    }
   
    
    @Override
    public void setRows (final TableModel table, final HBAResult rawData) {
        String id    = null;
        
        for (HBAWSImpl hbaSwImpl : rawData.getResult()) {
            TableRow row = new TableRow();
            row.addCell(VObjectType.HOST.getMoref()+":"+hbaSwImpl.getHostIdentifier(), hbaSwImpl.getHostName(),   hbaSwImpl.getFormattedHostName(), null);
            row.addCell(id,    hbaSwImpl.getType(),       hbaSwImpl.getFormattedType(), 							null);
            row.addCell(id,    hbaSwImpl.getIdentifier(), hbaSwImpl.getFormattedIdentifier(), 						null);
            row.addCell(id,    hbaSwImpl.getWWN(),        hbaSwImpl.getFormattedWWN(), 								null);
            row.addCell(id,    hbaSwImpl.getLabel(),      hbaSwImpl.getFormattedLabel(), 							null);
            table.addRow(row);
        }
    }
}
