package com.hp.asi.hpic4vc.server.provider.model;

import com.hp.asi.hpic4vc.provider.model.BaseModel;

public class AssociatedDevices extends BaseModel{
	private String server;
	private String ilo;
	private String oa;
	private String vcm;
	public String getServer() {
	    return server;
    }
	public void setServer(String server) {
	    this.server = server;
    }
	public String getIlo() {
	    return ilo;
    }
	public void setIlo(String ilo) {
	    this.ilo = ilo;
    }
	public String getOa() {
	    return oa;
    }
	public void setOa(String oa) {
	    this.oa = oa;
    }
	public String getVcm() {
	    return vcm;
    }
	public void setVcm(String vcm) {
	    this.vcm = vcm;
    }
}
