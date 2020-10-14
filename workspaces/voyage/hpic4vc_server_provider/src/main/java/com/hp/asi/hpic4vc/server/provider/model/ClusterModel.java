package com.hp.asi.hpic4vc.server.provider.model;

import java.util.ArrayList;
import java.util.List;

import com.hp.asi.hpic4vc.provider.model.BaseModel;

public class ClusterModel extends BaseModel {
	
	private String total_memory;
	private String total_nics;
	private String ilo_server_name;
	private String total_cpu;
	private String total_storage;
	private String product_name;
	private String vmware_name;
	private String power_cost_advantage;

	public List<ClusterHostDetail> children; 
	
	public ClusterModel(){
		super();
		this.children = new ArrayList<ClusterHostDetail>();
	}

	public String getTotal_memory() {
		return total_memory;
	}

	public void setTotal_memory(String total_memory) {
		this.total_memory = total_memory;
	}


	public String getTotal_nics() {
		return total_nics;
	}

	public void setTotal_nics(String total_nics) {
		this.total_nics = total_nics;
	}

	public String getIlo_server_name() {
		return ilo_server_name;
	}

	public void setIlo_server_name(String ilo_server_name) {
		this.ilo_server_name = ilo_server_name;
	}


	public String getTotal_cpu() {
		return total_cpu;
	}

	public void setTotal_cpu(String total_cpu) {
		this.total_cpu = total_cpu;
	}


	public String getTotal_storage() {
		return total_storage;
	}

	public void setTotal_storage(String total_storage) {
		this.total_storage = total_storage;
	}


	public String getProduct_name() {
		return product_name;
	}

	public void setProduct_name(String product_name) {
		this.product_name = product_name;
	}


	public String getVmware_name() {
		return vmware_name;
	}

	public void setVmware_name(String vmware_name) {
		this.vmware_name = vmware_name;
	}

	public String getPower_cost_advantage() {
		return power_cost_advantage;
	}

	public void setPower_cost_advantage(String power_cost_advantage) {
		this.power_cost_advantage = power_cost_advantage;
	}

	
}
