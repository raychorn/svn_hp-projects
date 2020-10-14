package com.hp.asi.hpic4vc.server.provider.data;

public class OaClusterInfraEncloserSummary extends OaEnclosure {
	
	private int bladesPresent;
	private int fansPresent;
	private int oaBays;
	private int powerSuppliesPresent;
	
	public int getOaBays() {
		return oaBays;
	}

	public void setOaBays(int oaBays) {
		this.oaBays = oaBays;
	}

	public int getBladesPresent()
	{
		return bladesPresent;
	}

	public void setBladesPresent(int bladesPresent)
	{
		this.bladesPresent = bladesPresent;
	}

	public int getFansPresent()
	{
		return fansPresent;
	}

	public void setFansPresent(int fansPresent)
	{
		this.fansPresent = fansPresent;
	}

	public int getPowerSuppliesPresent()
	{
		return powerSuppliesPresent;
	}

	public void setPowerSuppliesPresent(int powerSuppliesPresent)
	{
		this.powerSuppliesPresent = powerSuppliesPresent;
	}

}
