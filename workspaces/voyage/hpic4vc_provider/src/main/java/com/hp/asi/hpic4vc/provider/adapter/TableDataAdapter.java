package com.hp.asi.hpic4vc.provider.adapter;

import com.hp.asi.hpic4vc.provider.locale.I18NProvider;
import com.hp.asi.hpic4vc.provider.model.TableModel;

public abstract class TableDataAdapter<T> extends DataAdapter<T, TableModel> {
    public TableDataAdapter(final Class<T> clazz) {
        super(clazz);
    }
    
    @Override
    public TableModel formatData(final T rawData) {        
        TableModel table   = new TableModel();
        setColumns(table);
                
        String errorMessage = getErrorMsg(rawData);
        if (errorMessage != null && !errorMessage.equals("")) {
            table.errorMessage = errorMessage;
            log.info("The data has an error message - setting the error message in table model.");
        }
        
        if (isResultEmpty(rawData)) {
            table.informationMessage = this.i18nProvider.getInternationalString
                    (locale, I18NProvider.Info_NoRecords);
            log.info("The data had no records.  Returning a table with an error message set.");
            return table;
        }

        setRows(table, rawData);
        return table;
    }
    
    @Override
    public TableModel getEmptyModel() {
        return new TableModel();
    }
    
    /**
     * Looks for an error message.  Returns null if none is found.
     * @param rawData  The data retrieved from the web service.
     * @return         The error message if one is found in the data, null otherwise.
     */
    public abstract String getErrorMsg(final T rawData);
    
    /**
     * Checks if the result in the data is null or empty.
     * @param rawData  The data retrieved from the web service.
     * @return         True if there is no data to report.
     */
    public abstract boolean isResultEmpty(final T rawData);
    
    public abstract void setColumns(final TableModel table);
    public abstract void setRows(final TableModel table, final T rawData); 

}
