package com.hp.asi.hpic4vc.server.provider.data;

import java.util.Arrays;

import com.hp.asi.hpic4vc.server.provider.data.ServerCredentialsData;

public class ServerCredentialsResult extends ResultBase{
	
    private boolean error;
    private String errorMessage;
    private ServerCredentialsData[] result;
    
    public ServerCredentialsResult () {
        super();
        this.error = false;
        this.errorMessage = null;
        this.result = null;
    }

    public boolean hasError () {
        return error;
    }
    
    public void setError (boolean hasException) {
        this.error = hasException;
    }
    
    public String getErrorMessage () {
        return errorMessage;
    }
    
    public void setErrorMessage (String errorMsg) {
        this.errorMessage = errorMsg;
    }
    
    public void setPwdb (ServerCredentialsData[] result) {
        this.result = result;
    }

    public ServerCredentialsData[] getPwdb () {
        return result;
    }
    
    @Override
    public String toString () {
        return "ServerCredentialsResult [error=" + error + ", errorMessage="
                + errorMessage + ", result=" + Arrays.toString(result)
                + "]";
    }
}
