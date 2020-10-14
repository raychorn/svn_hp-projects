package com.hp.asi.hpic4vc.provider.data;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="health")
public class HealthResult {

    private boolean error;
    private String errorMessage;
    private HealthData[] result;
        
    public HealthResult () {
        
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
    
    public void setResult (HealthData[] result) {
        this.result = result;
    }

    @XmlElement(name="result")
    public HealthData[] getResult () {
        return result;
    }

    @Override
    public String toString () {
        return "HealthResult [error=" + error + ", errorMessage="
                + errorMessage + ", result=" + Arrays.toString(result) + "]";
    }
}
