package com.hp.asi.hpic4vc.server.provider.data;

public class ResultBase
{
	private boolean error;
    private String errorMessage;
    
    public ResultBase()
    {
    	this.error = false;
    	this.errorMessage = null;       	
    }
        
    public boolean hasError() 
    {
        return error;
    }
    
    public void setError(boolean hasException) 
    {
        this.error = hasException;
    }
        
    public String getErrorMessage() 
    {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMsg) 
    {
        this.errorMessage = errorMsg;
    }
}
