package com.hp.asi.hpic4vc.server.provider.data;


public class ServerCommStatusResult extends ResultBase{
    private boolean error;
    private String errorMessage;
    
    private ServerCommStatusData server;
    private ServerCommStatusData ilo;
    private ServerCommStatusData oa;
    private ServerCommStatusData vcm;
    private boolean blade;
    

	public ServerCommStatusResult(){
		super();
        this.error = false;
        this.errorMessage = null;
	}
	
	public ServerCommStatusData getServer(){
		return server;
	}

	public void setServer(ServerCommStatusData server) {
	    this.server = server;
    }

	public ServerCommStatusData getIlo() {
	    return ilo;
    }

	public void setIlo(ServerCommStatusData ilo) {
	    this.ilo = ilo;
    }

	public ServerCommStatusData getOa() {
	    return oa;
    }

	public void setOa(ServerCommStatusData oa) {
	    this.oa = oa;
    }

	public ServerCommStatusData getVcm() {
	    return vcm;
    }

	public void setVcm(ServerCommStatusData vcm) {
	    this.vcm = vcm;
    }
	
	public boolean isBlade() {
		return blade;
	}

	public void setBlade(boolean blade) {
		this.blade = blade;
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

    @Override
    public String toString () {
        return "ServerConfigurationResult [error=" + error + ", errorMessage="
                + errorMessage + ", server=" 
                + "]";
    }
}
