package com.hp.asi.hpic4vc.storage.provider.dam.model;

import java.util.ArrayList;
import java.util.List;

import com.hp.asi.hpic4vc.provider.model.BaseModel;

public class HierarchialDataModel extends BaseModel {
    public static final String KEY_TAG             = "Key";
    public static final String VALUE_TAG           = "Value";
    public static final String DEFAULT_KEY_WIDTH   = "100";
    public static final String DEFAULT_VALUE_WIDTH = "250";
    
	public List<String>          columnNames;
    public List<String>          columnWidth;
    public List<HierarchialData> rowFormattedData;    //this will be a list of HierarchialData objects
    public List<List<String>>    rowIds;
    public List<List<String>>    rowRawData;
    
    /**
     * An empty constructor is required for the AMF serialization to work!
     */
    public HierarchialDataModel() {
    	super();
    	this.columnNames         = new ArrayList<String>();
        this.columnWidth         = new ArrayList<String>();
        this.rowFormattedData    = new ArrayList<HierarchialData>();
        this.rowIds              = new ArrayList<List<String>>();
        this.rowRawData          = new ArrayList<List<String>>();
        addDefaultColumns();
        addDefaultColumnWidths();
    }
    
    public void addDefaultColumns() {
        columnNames.add(KEY_TAG);
        columnNames.add(VALUE_TAG);
    }
    
    public void addDefaultColumnWidths() {
        columnWidth.add(DEFAULT_KEY_WIDTH);
        columnWidth.add(DEFAULT_VALUE_WIDTH);
    }
    
    @Override
    public String toString () {
    	return "HierarchialDataModel [columnNames=" + columnNames + ", columnWidth=" + columnWidth
				+ ", columnRighClickMenu=" + ", rowFormattedData=" + rowFormattedData + ", rowIds="
				+ rowIds + ", rowRawData=" + rowRawData + ", errorMessage="
				+ errorMessage + ", informationMessage=" + informationMessage
				+ "]";
    }
}
