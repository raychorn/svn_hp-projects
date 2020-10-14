package com.hp.asi.hpic4vc.server.provider.data;

public class OaClusterInfraPowerSummary {
	
	private String redundancy;

	public String getRedundancy() {
		return redundancy;
	}

	public void setRedundancy(String redundancy) {
		this.redundancy = redundancy;
	}
	
	
	private int powerConsumed;

	public int getPowerConsumed()
	{
		return powerConsumed;
	}

	public void setPowerConsumed(int powerConsumed)
	{
		this.powerConsumed = powerConsumed;
	}

}
