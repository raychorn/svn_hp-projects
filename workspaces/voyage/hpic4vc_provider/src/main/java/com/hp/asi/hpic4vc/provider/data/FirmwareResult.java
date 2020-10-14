/**
 * Copyright 2011 Hewlett-Packard Development Company, L.P.
 */
package com.hp.asi.hpic4vc.provider.data;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonProperty;

@XmlRootElement(name="firmware")
public class FirmwareResult {

    private boolean error;
    private String errorMessage;
    private SoftwareFirmware[] firmware;
    private SoftwareFirmware[] software;
    private SoftwareFirmware[] clusterRows;
    
    public FirmwareResult () {
        
        this.error        = false;
        this.errorMessage = null;
        this.errorMessage = null;
        this.firmware     = null;
        this.software     = null;
        this.clusterRows         = null;
    }

    @XmlElement(name="error")
    public boolean hasError () {
        return error;
    }
    
    public void setError (boolean hasException) {
        this.error = hasException;
    }
    
    @XmlElement(name="errorMessage")
    public String getErrorMessage () {
        return errorMessage;
    }
    
    public void setErrorMessage (String errorMsg) {
        this.errorMessage = errorMsg;
    }

    @XmlElement(name="firmware")
    public SoftwareFirmware[] getFirmware () {
    	if (this.firmware == null) {
    		return new SoftwareFirmware[0];
    	}
    	
        return firmware;
    }
    
    public void setFirmware (SoftwareFirmware[] firmware) {
        this.firmware = firmware;
    }
    
    @XmlElement(name="software")
    public SoftwareFirmware[] getSoftware() {
    	if (this.software == null) {
    		return new SoftwareFirmware[0];
    	}
    	
    	return this.software;
    }
    
    public void setSoftware(SoftwareFirmware[] software) {
    	this.software = software;
    }

    @XmlElement(name="rows")
    @JsonProperty("rows")
    public SoftwareFirmware[] getClusterRows() {
        if (this.clusterRows == null) {
            return new SoftwareFirmware[0];
        }
        
        return this.clusterRows;
    }
    
    public void setRows(SoftwareFirmware[] rows) {
        this.clusterRows = rows;
    }

    @Override
    public String toString () {
        return "FirmwareResult [error=" + error + ", errorMessage="
                + errorMessage + ", firmware=" + Arrays.toString(firmware)
                + ", software=" + Arrays.toString(software) + ", clusterRows="
                + Arrays.toString(clusterRows) + "]";
    }
}
