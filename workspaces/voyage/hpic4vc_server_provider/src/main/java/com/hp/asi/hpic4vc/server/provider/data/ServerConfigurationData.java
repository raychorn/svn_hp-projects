package com.hp.asi.hpic4vc.server.provider.data;

public class ServerConfigurationData {

    private String param_name;
    private String param_value;
    
    public ServerConfigurationData () {
        this.setParam_name(null);
        this.setParam_value(null);
    }

    public String getParam_name() {
	    return param_name;
    }

	public void setParam_name(String param_name) {
	    this.param_name = param_name;
    }

	public String getParam_value() {
	    return param_value;
    }

	public void setParam_value(String param_value) {
	    this.param_value = param_value;
    }

	@Override
    public String toString () {
        return "VCCredentialsData [username=" + param_name + ", host=" + param_value + "]";
    }
}
