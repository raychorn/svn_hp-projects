package com.hp.asi.hpic4vc.server.provider.model;

import java.util.ArrayList;
import java.util.List;

import com.hp.asi.hpic4vc.provider.model.BaseModel;

public class ClusterInfrastructureDetailModel extends BaseModel{
	
	private String enclosureName;
	private String rackName;
	private int powerConsumed;
	private String powerRedundancy;
	private String thermalRedundancy;
	private int fansPresent;
	private int powerSuppliesPresent;
	private int powerSupplyBays;
	private int fanBays;
	private String fanBaysOutOfFansPresent;
	private String powerSuppliesOutOfpowerSuplliesPresent;
	
	private List<InfrastructureDetailModel> clusterInfrastructureDetails;

	public List<InfrastructureDetailModel> getClusterInfrastructureDetails() {
		return clusterInfrastructureDetails;
	}

	public void setClusterInfrastructureDetails(
			List<InfrastructureDetailModel> clusterInfrastructureDetails) {
		this.clusterInfrastructureDetails = clusterInfrastructureDetails;
	}

	public ClusterInfrastructureDetailModel() {
		super();
		this.clusterInfrastructureDetails = new ArrayList<InfrastructureDetailModel>();
	}

	public String getEnclosureName() {
		return enclosureName;
	}

	public void setEnclosureName(String enclosureName) {
		this.enclosureName = enclosureName;
	}

	public String getRackName() {
		return rackName;
	}

	public void setRackName(String rackName) {
		this.rackName = rackName;
	}

	public int getPowerConsumed() {
		return powerConsumed;
	}

	public void setPowerConsumed(int powerConsumed) {
		this.powerConsumed = powerConsumed;
	}

	public String getPowerRedundancy() {
		return powerRedundancy;
	}

	public void setPowerRedundancy(String powerRedundancy) {
		this.powerRedundancy = powerRedundancy;
	}

	public String getThermalRedundancy() {
		return thermalRedundancy;
	}

	public void setThermalRedundancy(String thermalRedundancy) {
		this.thermalRedundancy = thermalRedundancy;
	}

	public int getFansPresent() {
		return fansPresent;
	}

	public void setFansPresent(int fansPresent) {
		this.fansPresent = fansPresent;
	}

	public int getPowerSuppliesPresent() {
		return powerSuppliesPresent;
	}

	public void setPowerSuppliesPresent(int powerSuppliesPresent) {
		this.powerSuppliesPresent = powerSuppliesPresent;
	}

	public int getPowerSupplyBays() {
		return powerSupplyBays;
	}

	public void setPowerSupplyBays(int powerSupplyBays) {
		this.powerSupplyBays = powerSupplyBays;
	}

	public int getFanBays() {
		return fanBays;
	}

	public void setFanBays(int fanBays) {
		this.fanBays = fanBays;
	}

	public String getFanBaysOutOfFansPresent() {
		return (Integer.toString(this.fansPresent) + " " + "of" + " " + Integer.toString(this.fanBays)) ;
	}

	public String getPowerSuppliesOutOfpowerSuplliesPresent() {
		return (Integer.toString(this.powerSuppliesPresent) + " " + "of" + " " + Integer.toString(this.powerSupplyBays)) ;
	}

	public void setPowerSuppliesOutOfpowerSuplliesPresent(
			String powerSuppliesOutOfpowerSuplliesPresent) {
		this.powerSuppliesOutOfpowerSuplliesPresent = powerSuppliesOutOfpowerSuplliesPresent;
	}

	public void setFanBaysOutOfFansPresent(String fanBaysOutOfFansPresent) {
		this.fanBaysOutOfFansPresent = fanBaysOutOfFansPresent;
	}

	
	
	
	

}
