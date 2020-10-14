package com.hp.asi.hpic4vc.provider.model;

public class FirmwareModel extends BaseModel {    
    public TableModel softwareTable;
    public String     softwareTableTitle;
    
    public TableModel firmwareTable;
    public String     firmwareTableTitle;
    
    public FirmwareModel() {
    	super();
    	this.softwareTable      = new TableModel();
    	this.softwareTableTitle = "";
    	
    	this.firmwareTable      = new TableModel();
    	this.firmwareTableTitle = "";
    }
    
    public FirmwareModel(TableModel softwareTable,
                         String     softwareTableTitle,
                         TableModel firmwareTable,
                         String     firmwareTableTitle
                         ) {
        super();
        this.softwareTable      = softwareTable;
        this.softwareTableTitle = softwareTableTitle;
        
        this.firmwareTable      = firmwareTable;
        this.firmwareTableTitle = firmwareTableTitle;
    }
    
    public void addSoftwareTableAndTitle(TableModel softwareTable,
                                         String     softwareTableTitle) {
           this.softwareTable      = softwareTable;
           this.softwareTableTitle = softwareTableTitle;
    }
    
    public void addFirmwarewareTableAndTitle(TableModel firmwareTable,
                                             String     firmwareTableTitle) {
           this.firmwareTable      = firmwareTable;
           this.firmwareTableTitle = firmwareTableTitle;
    }
    
    public TableModel getSoftwareTable() {
        return this.softwareTable;
    }
    
    public String getSoftwareTableTitle() {
        return this.softwareTableTitle;
    }
    
    public TableModel getFirmwareTable() {
        return this.firmwareTable;
    }
    
    public String getFirmwareTableTitle() {
        return this.firmwareTableTitle;
    }

	@Override
	public String toString() {
		return "FirmwareModel [softwareTable=" + softwareTable
				+ ", softwareTableTitle=" + softwareTableTitle
				+ ", firmwareTable=" + firmwareTable + ", firmwareTableTitle="
				+ firmwareTableTitle + ", errorMessage=" + errorMessage
				+ ", informationMessage=" + informationMessage + "]";
	}


}
