package com.hp.asi.hpic4vc.provider.model;


public class PieChartModel extends BaseModel {
    public int percentUsed;
    public int percentFree;
    
    public PieChartModel() {
        super();
        percentUsed = 0;
        percentFree = 0;
    }

	@Override
	public String toString() {
		return "PieChartModel [percentUsed=" + percentUsed + ", percentFree="
				+ percentFree + ", errorMessage=" + errorMessage
				+ ", informationMessage=" + informationMessage + "]";
	}

}
