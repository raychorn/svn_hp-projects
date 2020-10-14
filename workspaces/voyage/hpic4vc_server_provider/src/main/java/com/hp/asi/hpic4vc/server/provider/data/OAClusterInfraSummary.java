package com.hp.asi.hpic4vc.server.provider.data;

public class OAClusterInfraSummary {
	
	private OaClusterThermal thermal;
	private OaClusterInfraEncloserSummary enclosure_info;
	private OaClusterInfraPowerSummary power;
	
	public OAClusterInfraSummary() {
		super();
		// TODO Auto-generated constructor stub
		this.thermal = new OaClusterThermal();
		this.enclosure_info = new OaClusterInfraEncloserSummary();
		this.power = new OaClusterInfraPowerSummary();
		
		
	}
	
	public OaClusterInfraEncloserSummary getEnclosure_info() {
		return enclosure_info;
	}
	public void setEnclosure_info(OaClusterInfraEncloserSummary enclosure_info) {
		this.enclosure_info = enclosure_info;
	}
	
	public OaClusterThermal getThermal() {
		return thermal;
	}
	public void setThermal(OaClusterThermal thermal) {
		this.thermal = thermal;
	}
	public OaClusterInfraPowerSummary getPower() {
		return power;
	}
	public void setPower(OaClusterInfraPowerSummary power) {
		this.power = power;
	}
	
	

	

	
	

	

	

	
	

}
