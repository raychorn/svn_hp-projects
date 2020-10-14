/**
 * Copyright 2012 Hewlett-Packard Development Company, L.P.
 */
package com.hp.asi.hpic4vc.provider.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author bhavsart
 *
 */
public class TableModel extends BaseModel {
    public List<String>          columnNames;
    public List<String>          columnToolTips;
    public List<String>          columnWidth;
    public List<List<LinkModel>> columnRightClickMenu;
    public List<List<String>>    rowFormattedData;
    public List<List<String>>    rowIds;
    public List<List<String>>    rowRawData;
    
	public List<Boolean> 	     hasIcons;       // Indicates if the column needs an icon.  This ArrayCollection is a List<Boolean> that will indicate if the column needs an icon.
	public List<Boolean> 	     isColumnVisible;// List of Boolean values to specify whether to display the column or not.
	public List<Boolean> 	     isColumnNumeric;// List of Boolean values to specify whether the column needs a numeric sort or not.
	public List<List<String>> 	 rowIcons;       // List<List<String>> where each String is a class defined in IconRepository.as class in hpic4vc_ui component.  Each new icon should be defined in IconRepository. 
    
    /**
     * An empty constructor is required for the AMF serialization to work!
     */
    public TableModel() {
        super();
        this.columnNames          = new ArrayList<String>();
        this.columnToolTips       = new ArrayList<String>();
        this.columnWidth          = new ArrayList<String>();
        this.columnRightClickMenu = new ArrayList<List<LinkModel>>();
        this.rowFormattedData     = new ArrayList<List<String>>();
        this.rowIds               = new ArrayList<List<String>>();
        this.rowRawData           = new ArrayList<List<String>>();
        this.hasIcons			  = new ArrayList<Boolean>();
        this.isColumnVisible	  = new ArrayList<Boolean>();
        this.isColumnNumeric	  = new ArrayList<Boolean>();
        this.rowIcons			  = new ArrayList<List<String>>();
    }    
        
    public TableModel(List<String>           columnNames,
                      List<String>           columnToolTips,
                      List<String>           columnWidth,
                      List<List<LinkModel>>  rightClickMenu,
                      List<List<String>>     rowFormattedData,
                      List<List<String>>     rowIds,
                      List<List<String>>     rowRawData,
                      List<Boolean> 	 	 hasIcons,
				      List<Boolean> 	     isColumnVisible,
					  List<Boolean> 	     isColumnNumeric,
					  List<List<String>> 	 rowIcons
					  ) {
        super();
        this.columnNames          = columnNames;
        this.columnToolTips       = columnToolTips;
        this.columnWidth          = columnWidth;
        this.columnRightClickMenu = rightClickMenu;
        this.rowFormattedData     = rowFormattedData;
        this.rowIds               = rowIds;
        this.rowRawData           = rowRawData;
        this.hasIcons			  = hasIcons;
        this.isColumnVisible	  = isColumnVisible;
        this.isColumnNumeric	  = isColumnNumeric;
        this.rowIcons			  = rowIcons;
    }

    /**
     * Adds a column to the table.
     * @param columnName     	The name of the column.
     * @param columnTooltip  	The tool tip associated with the column.
     * @param columnWidth    	The desried width of the column in pixels.
     * @param rightClickMenu 	The right click menu for the column.
     */
    public void addColumn(final String columnName, 
                          final String columnTooltip,
                          final String columnWidth,
                          final List<LinkModel> rightClickMenu) {
        
        checkColumnsForNullLists();
        
        this.columnNames.add(columnName);
        this.columnToolTips.add(columnTooltip);
        this.columnWidth.add(columnWidth);
        this.columnRightClickMenu.add(rightClickMenu);
        
        checkColumnsForUnevenSize();
    }

    /**
     * Adds a column to the table.
     * @param columnName     	The name of the column.
     * @param columnTooltip  	The tool tip associated with the column.
     * @param columnWidth    	The desired width of the column in pixels.
     * @param rightClickMenu 	The right click menu for the column.
     * @param isColumnVisible 	Value to specify whether to display the column or not.
     * @param isColumnNumeric	Value to specify if the column needs numeric sort.
     * @param hasIcons			Value to specify if the column needs an icon.
     */
    public void addColumn(final String columnName, 
                          final String columnTooltip,
                          final String columnWidth,
                          final List<LinkModel> rightClickMenu,
                          final Boolean isColumnVisible,
                          final Boolean isColumnNumeric,
                          final Boolean hasIcon) {
        
        checkColumnsForNullLists();
        
        this.columnNames.add(columnName);
        this.columnToolTips.add(columnTooltip);
        this.columnWidth.add(columnWidth);
        this.columnRightClickMenu.add(rightClickMenu);
        this.isColumnVisible.add(isColumnVisible);
        this.isColumnNumeric.add(isColumnNumeric);
        this.hasIcons.add(hasIcon);
        
        checkColumnsForUnevenSize(); 
    }
    /**
     * Adds a row of data to the table.
     * @param row  A TableRow object encapsulating one row of data.
     */
    public void addRow(final TableRow row) {
        checkRowsForNullLists();
        checkRowsForUnevenSize();
        
        if (null != row) {
            this.rowIds.add(row.getRowIds());
            this.rowRawData.add(row.getRawData());
            this.rowFormattedData.add(row.getFormattedData());
            this.rowIcons.add(row.getRowIcons());
        }
    }
    

    /**
     * Check if the columnNames is null or columnToolTips is null
     * to avoid null pointer exceptions.
     */
    private void checkColumnsForNullLists() {
        if (null == this.columnNames) {
            this.columnNames    = new ArrayList<String>();
        }
        
        if (null == this.columnToolTips) {
            this.columnToolTips = new ArrayList<String>();
        }
        
        if (null == this.columnWidth) {
            this.columnWidth = new ArrayList<String>();
        }
        
        if (null == this.columnRightClickMenu) {
            this.columnRightClickMenu = new ArrayList<List<LinkModel>>();
        }
        
        if (null == this.isColumnVisible) {
            this.isColumnVisible = new ArrayList<Boolean>();
        }
        
        if (null == this.isColumnNumeric) {
            this.isColumnNumeric = new ArrayList<Boolean>();
        }
        
        if (null == this.hasIcons) {
            this.hasIcons = new ArrayList<Boolean>();
        }
    }
    
    /**
     * Make sure that each list is the same size.  That way adding a new column
     * adds items to the same index in each list so that the elements line-up.
     */
    private void checkColumnsForUnevenSize() {
        int colSize = Math.max(this.columnNames.size(), this.columnToolTips.size());
        colSize = Math.max(colSize, this.columnWidth.size());
        colSize = Math.max(colSize, this.columnRightClickMenu.size());
        colSize = Math.max(colSize, this.isColumnVisible.size());
        colSize = Math.max(colSize, this.isColumnNumeric.size());
        colSize = Math.max(colSize, this.hasIcons.size());
        
        while (this.columnNames.size() < colSize) {
            this.columnNames.add(null);
        }
        
        while (this.columnToolTips.size() < colSize) {
            this.columnToolTips.add(null);
        }
        
        while (this.columnWidth.size() < colSize) {
            this.columnWidth.add(null);
        }
        
        while (this.columnRightClickMenu.size () < colSize) {
            this.columnRightClickMenu.add(null);
        }
        
        while (this.isColumnVisible.size () < colSize) {
            this.isColumnVisible.add(true);
        }
        
        while (this.isColumnNumeric.size () < colSize) {
            this.isColumnNumeric.add(false);
        }
        
        while (this.hasIcons.size () < colSize) {
            this.hasIcons.add(false);
        }
    }

    /**
     * Check to make sure that none of the lists are null.
     */
    private void checkRowsForNullLists() {
        if (null == this.rowFormattedData) {
            this.rowFormattedData = new ArrayList<List<String>>();
        }
        
        if (null == this.rowIds) {
            this.rowIds = new ArrayList<List<String>>();
        }
        
        if (null == this.rowRawData) {
            this.rowRawData = new ArrayList<List<String>>();
        }
        
        if (null == this.rowIcons) {
            this.rowIcons = new ArrayList<List<String>>();
        }
    }

    /**
     * Check to make sure that each list of the same size so that the
     * data matches up.
     */
    private void checkRowsForUnevenSize () {
        if (this.rowFormattedData.size() != this.rowRawData.size() ||
            this.rowFormattedData.size() != this.rowIds.size() ||
            this.rowRawData.size()       != this.rowIds.size()) {
            int size = Math.max(rowFormattedData.size(), rowRawData.size());
            size = Math.max(size, rowIds.size());
            size = Math.max(size, rowIcons.size());
            
            while (rowFormattedData.size() < size) {
                rowFormattedData.add(null);
            }
            while (rowRawData.size() < size) {
                rowRawData.add(null);
            }
            while (rowIds.size() < size) {
                rowIds.add(null);
            }
            while (rowIcons.size() < size) {
                rowIcons.add(null);
            }
        }        
    }
    
    /**
     *  Class to encapsulate one row of data.  
     */
    public static class TableRow {
        private List<String>    ids;
        private List<String>    rawData;
        private List<String>    formattedData;
        private List<String>    rowIcons;
        
        public TableRow() {
            this.ids           = new ArrayList<String>();
            this.rawData       = new ArrayList<String>();
            this.formattedData = new ArrayList<String>();
            this.rowIcons	   = new ArrayList<String>();
        }

		/**
         * Adds one cell of data to the row.  A cell of data contains:
         * @param id             The row's ID.
         * @param rawData        The raw data for the cell.
         * @param formattedData  The data formatted for display in the UI.
         */
        public void addCell (final String id, 
        					 final String rawData, 
        					 final String formattedData) {
            this.ids.add(id);
            this.rawData.add(rawData);
            this.formattedData.add(formattedData);
        }
        
        /**
         * Adds one cell of data to the row.  A cell of data contains:
         * @param id             The row's ID.
         * @param rawData        The raw data for the cell.
         * @param formattedData  The data formatted for display in the UI.
         * @param rowIcon		 The icon that gets displayed in the cell.  
         * 						 Each String is a class defined in IconRepository.as 
         * 						 in hpic4vc_ui component.  Example: "error".
         * 						 Each new icon should be defined in IconRepository. 
         */
        public void addCell (final String id, 
        					 final String rawData, 
        					 final String formattedData,
        					 final String rowIcon) {
            this.ids.add(id);
            this.rawData.add(rawData);
            this.formattedData.add(formattedData);
            this.rowIcons.add(rowIcon);
        }
        
        List<String> getRowIds() {
            return this.ids;
        }
        
        List<String> getRawData() {
            return this.rawData;
        }
        
        List<String> getFormattedData() {
            return this.formattedData;
        }
        
        List<String> getRowIcons() {
			return this.rowIcons;
		}

    }

	@Override
	public String toString() {
		return "TableModel [columnNames=" + columnNames + ", columnToolTips="
				+ columnToolTips + ", columnWidth=" + columnWidth
				+ ", isColumnVisible=" + isColumnVisible
				+ ", isColumnNumeric=" + isColumnNumeric
				+ ", hasIcons=" + hasIcons
				+ ", columnRighClickMenu=" + columnRightClickMenu
				+ ", rowFormattedData=" + rowFormattedData + ", rowIds="
				+ rowIds + ", rowRawData=" + rowIcons + ", rowIcons=" 
				+ rowRawData + ", errorMessage="
				+ errorMessage + ", informationMessage=" + informationMessage
				+ "]";
	}

}
