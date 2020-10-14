package com.hp.asi.hpic4vc.provider.model;


public class RefreshCacheModel extends BaseModel {
    
    private boolean isPopulating;
    private String remainingTime;
    private String estimatedTimeLabel;
    private String summary;
    
    public RefreshCacheModel () {
        super();
        // TODO Auto-generated constructor stub
    }

    public RefreshCacheModel (boolean isPopulating,
                         String remainingTime,
                         String estimatedTimeLabel,
                         String summary) {
        super();
        this.isPopulating = isPopulating;
        this.remainingTime = remainingTime;
        this.estimatedTimeLabel = estimatedTimeLabel;
        this.summary = summary;
    }

    public boolean isPopulating () {
        return isPopulating;
    }

    public void setPopulating (boolean isPopulating) {
        this.isPopulating = isPopulating;
    }

    public String getRemainingTime () {
        return remainingTime;
    }

    public void setRemainingTime (String remainingTime) {
        this.remainingTime = remainingTime;
    }

    public String getEstimatedTimeLabel () {
        return estimatedTimeLabel;
    }

    public void setEstimatedTimeLabel (String estimatedTimeLabel) {
        this.estimatedTimeLabel = estimatedTimeLabel;
    }

    public String getSummary () {
        return summary;
    }

    public void setSummary (String summary) {
        this.summary = summary;
    }

	@Override
	public String toString() {
		return "RefreshCacheModel [isPopulating=" + isPopulating
				+ ", remainingTime=" + remainingTime + ", estimatedTimeLabel="
				+ estimatedTimeLabel + ", summary=" + summary
				+ ", errorMessage=" + errorMessage + ", informationMessage="
				+ informationMessage + "]";
	}
}
