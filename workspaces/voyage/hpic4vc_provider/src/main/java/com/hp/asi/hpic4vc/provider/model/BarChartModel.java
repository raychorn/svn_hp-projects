package com.hp.asi.hpic4vc.provider.model;

public class BarChartModel extends BaseModel {
    public String info;
    public long   usedSpace;
    public long   freeSpace;
    public long   notProvisioned;
    public long   overProvisioned;
    public String hoverData;
    
    public BarChartModel() {
        super();
        this.info            = null;
        this.usedSpace       = 0l;
        this.freeSpace       = 0l;
        this.notProvisioned  = 0l;
        this.overProvisioned = 0l;
        this.hoverData       = null;
    }

	@Override
	public String toString() {
		return "BarChartModel [info=" + info + ", usedSpace=" + usedSpace
				+ ", freeSpace=" + freeSpace + ", notProvisioned="
				+ notProvisioned + ", overProvisioned=" + overProvisioned
				+ ", hoverData=" + hoverData + ", errorMessage=" + errorMessage
				+ ", informationMessage=" + informationMessage + "]";
	}


}
