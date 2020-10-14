package com.hp.asi.hpic4vc.provider.model;

import java.util.ArrayList;
import java.util.List;

public class StorageOverviewModel extends BaseModel {

    public List<BarChartGroupModel> arraySummaries;

    public StorageOverviewModel() {
        super();
        arraySummaries = new ArrayList<BarChartGroupModel>();
    }

	@Override
	public String toString() {
		return "StorageOverviewModel [arraySummaries=" + arraySummaries
				+ ", errorMessage=" + errorMessage + ", informationMessage="
				+ informationMessage + "]";
	}
    
}
