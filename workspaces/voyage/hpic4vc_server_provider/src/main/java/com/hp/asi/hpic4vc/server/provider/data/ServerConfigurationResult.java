package com.hp.asi.hpic4vc.server.provider.data;

import java.util.Arrays;

public class ServerConfigurationResult extends ResultBase{
    private boolean error;
    private String errorMessage;
    private ServerConfigurationData[] result;
	
	public ServerConfigurationResult(){
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

    public void setSrvcfg (ServerConfigurationData[] result) {
        this.result = result;
    }

    public ServerConfigurationData[] getSrvcfg () {
        return result;
    }

    @Override
    public String toString () {
        return "ServerConfigurationResult [error=" + error + ", errorMessage="
                + errorMessage + ", result=" + Arrays.toString(result)
                + "]";
    }
}
