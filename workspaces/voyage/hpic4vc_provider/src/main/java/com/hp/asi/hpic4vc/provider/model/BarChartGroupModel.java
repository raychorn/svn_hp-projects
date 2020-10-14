package com.hp.asi.hpic4vc.provider.model;

import java.util.ArrayList;
import java.util.List;

public class BarChartGroupModel extends BaseModel {
    public String groupTitle;
    public List<BarChartModel> barChartData;
    
    public BarChartGroupModel() {
        super();
        this.groupTitle    = null;
        this.barChartData = new ArrayList<BarChartModel>();
    }
    
    public BarChartGroupModel(final String arrayName,
                             final List<BarChartModel> arrayData) {
        super();
        this.groupTitle    = arrayName;
        this.barChartData = arrayData;
    }

	@Override
	public String toString() {
		return "BarChartGroupModel [groupTitle=" + groupTitle
				+ ", barChartData=" + barChartData + ", errorMessage="
				+ errorMessage + ", informationMessage=" + informationMessage
				+ "]";
	}


}