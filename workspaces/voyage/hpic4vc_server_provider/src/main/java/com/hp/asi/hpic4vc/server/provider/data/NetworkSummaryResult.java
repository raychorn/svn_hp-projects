package com.hp.asi.hpic4vc.server.provider.data;

import java.util.List;

public class NetworkSummaryResult
{
	private boolean error;
    private String errorMessage;
	
    private List<Nic> nics;
    
    public boolean hasError()
	{
		return error;
	}
    
	public void setError(boolean error)
	{
		this.error = error;
	}

	public String getErrorMessage()
	{
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage)
	{
		this.errorMessage = errorMessage;
	}

	public List<Nic> getNics()
	{
		return nics;
	}

	public void setNics(List<Nic> nics)
	{
		this.nics = nics;
	}
    
    
}
