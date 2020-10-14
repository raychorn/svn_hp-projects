package com.hp.asi.hpic4vc.server.provider.data;

public class ClusterSummaryResult extends ResultBase {
	
	private String cluster_name;
	private String blade;
	private String hosts;
	private String vms;
	private String blades;
	
	public String getCluster_name() {
		return cluster_name;
	}
	public void setCluster_name(String cluster_name) {
		this.cluster_name = cluster_name;
	}
	public String getBlade() {
		return blade;
	}
	public void setBlade(String blade) {
		this.blade = blade;
	}
	public String getHosts() {
		return hosts;
	}
	public void setHosts(String hosts) {
		this.hosts = hosts;
	}
	public String getVms() {
		return vms;
	}
	public void setVms(String vms) {
		this.vms = vms;
	}
	public String getBlades() {
		return blades;
	}
	public void setBlades(String blades) {
		this.blades = blades;
	}
	 
	

}
