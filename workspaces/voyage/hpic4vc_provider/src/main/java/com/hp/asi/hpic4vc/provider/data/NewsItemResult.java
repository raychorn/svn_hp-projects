/**
 * Copyright 2011 Hewlett-Packard Development Company, L.P.
 */
package com.hp.asi.hpic4vc.provider.data;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="newsItem")
public class NewsItemResult {

    private boolean error;
    private String errorMessage;
    private NewsItemData[] result;
        
    public NewsItemResult () {
        
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
    
    public void setResult (NewsItemData[] result) {
        this.result = result;
    }

    @XmlElement(name="result")
    public NewsItemData[] getResult () {
        return result;
    }

    @Override
    public String toString () {
        return "NewsItemResult [error=" + error + ", errorMessage="
                + errorMessage + ", result=" + Arrays.toString(result) + "]";
    }
}
