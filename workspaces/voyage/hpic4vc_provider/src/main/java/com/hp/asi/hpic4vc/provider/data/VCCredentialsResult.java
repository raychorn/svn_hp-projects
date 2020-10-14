package com.hp.asi.hpic4vc.provider.data;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlElement;

public class VCCredentialsResult {

    private boolean error;
    private String errorMessage;
    private VCCredentialsData[] result;
    
    public VCCredentialsResult () {
        
        this.error = false;
        this.errorMessage = null;
        this.result = null;
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
    
    public void setPwdb (VCCredentialsData[] result) {
        this.result = result;
    }

    @XmlElement(name="pwdb")
    public VCCredentialsData[] getPwdb () {
        return result;
    }
    
    @Override
    public String toString () {
        return "VCCredentialsResult [error=" + error + ", errorMessage="
                + errorMessage + ", result=" + Arrays.toString(result)
                + "]";
    }
}
