package com.hp.asi.hpic4vc.provider.data;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlElement;

public class HPOneViewCredentialsResult {

    private boolean error;
    private String errorMessage;
    private HPOneViewCredentialsData[] result;
    
    public HPOneViewCredentialsResult () {
        
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
    
    public void setPwdb (HPOneViewCredentialsData[] result) {
        this.result = result;
    }

    @XmlElement(name="pwdb")
    public HPOneViewCredentialsData[] getPwdb () {
        return result;
    }
    
    @Override
    public String toString () {
        return "HPOneViewCredentialsResult [error=" + error + ", errorMessage="
                + errorMessage + ", result=" + Arrays.toString(result)
                + "]";
    }
}
