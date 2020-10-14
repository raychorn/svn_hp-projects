package com.hp.asi.hpic4vc.server.provider.model;

import com.hp.asi.hpic4vc.provider.model.BaseModel;

public class QueueClusterModel extends BaseModel {
	
	private String package_url;
	private String host;

	public String getPackage_url() {
		return package_url;
	}

	public void setPackage_url(String package_url) {
		this.package_url = package_url;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

}
