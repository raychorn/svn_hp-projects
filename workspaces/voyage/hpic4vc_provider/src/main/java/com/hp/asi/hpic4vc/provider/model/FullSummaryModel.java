package com.hp.asi.hpic4vc.provider.model;

import java.util.List;

public class FullSummaryModel extends BaseModel {
    public String                   hpToVobjectTitle;
    public SummaryPortletModel      hpToVobject;
    public String                   arrayTitle;
    public List<BarChartGroupModel> arraySummaries;

    public String                   backupSystemSummariesTitle;
    public List<BarChartGroupModel> backupSystemSummaries;
    
    public String                   vObjectToHpTitle;
    public SummaryPortletModel      vObjectToHp;
    public String                   dsTitle;
    public List<BarChartGroupModel> dsSummaries;

    public FullSummaryModel() {
        super();
        this.hpToVobjectTitle           = null;
        this.hpToVobject                = null;
        this.arrayTitle                 = null;
        this.arraySummaries             = null;
        
        this.backupSystemSummariesTitle = null;
        this.backupSystemSummaries      = null;
        
        this.vObjectToHpTitle           = null;
        this.vObjectToHp                = null;
        this.dsTitle                    = null;
        this.dsSummaries                = null;
    }

	@Override
	public String toString() {
		return "FullSummaryModel [hpToVobjectTitle=" + hpToVobjectTitle
				+ ", hpToVobject=" + hpToVobject + ", arrayTitle=" + arrayTitle
				+ ", arraySummaries=" + arraySummaries
				+ ", backupSystemSummariesTitle=" + backupSystemSummariesTitle
				+ "backupSystemSummaries=" + backupSystemSummaries
				+ ", vObjectToHpTitle=" + vObjectToHpTitle
				+ ", vObjectToHp=" + vObjectToHp
				+ ", dsTitle=" + dsTitle + ", dsSummaries=" + dsSummaries
				+ ", errorMessage=" + errorMessage + ", informationMessage="
				+ informationMessage + "]";
	}


}
