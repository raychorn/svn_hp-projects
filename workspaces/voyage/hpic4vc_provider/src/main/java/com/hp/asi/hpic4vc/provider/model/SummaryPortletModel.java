package com.hp.asi.hpic4vc.provider.model;

public class SummaryPortletModel extends BaseModel {

    public LabelValueListModel fieldData;
    public PieChartModel       pieChartData;
    
    public SummaryPortletModel() {
        super();
        fieldData    = new LabelValueListModel();
        pieChartData = new PieChartModel();
    }
    
    public SummaryPortletModel(final LabelValueListModel fieldData, 
                               final PieChartModel pieChartData) {
        super();
        this.fieldData    = fieldData;
        this.pieChartData = pieChartData;
    }

	@Override
	public String toString() {
		return "SummaryPortletModel [fieldData=" + fieldData
				+ ", pieChartData=" + pieChartData + ", errorMessage="
				+ errorMessage + ", informationMessage=" + informationMessage
				+ "]";
	}

}
