package com.hp.asi.hpic4vc.server.provider.data;

public class Cpu {

	private String vendor;
	private String name;
	private String threads;
	private String cores;
	private String speed;
	private String description;
	public String getVendor() {
		return vendor;
	}
	public void setVendor(String vendor) {
		this.vendor = vendor;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getThreads() {
		return threads;
	}
	public void setThreads(String threads) {
		this.threads = threads;
	}
	public String getCores() {
		return cores;
	}
	public void setCores(String cores) {
		this.cores = cores;
	}
	public String getSpeed() {
		return speed;
	}
	public void setSpeed(String speed) {
		this.speed = speed;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
}
