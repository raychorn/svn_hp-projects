package com.hp.asi.hpic4vc.provider.data;

public class RefreshResult {
    
    private String errorMsg;
    private boolean repopulating;
    private boolean prepopulating;
    private String formattedRemainingTime;
    private String formattedEstTimeLabel;
    private String formattedSummary;
    
    private String refreshCounter;
    private Double elapsedTimeMS;
    private String formattedElapsedTime;
    private Double remainingTimeMS;
    private Double totalEstimatedTimeMS;
    private String formattedTotalTime;
    private String lastCompletionTimeMS;
    private String formattedLastCompletionTime;
    private String formattedStatus;
    private String formattedElapsedTimeLabel;
    
    public RefreshResult () {
        super();
        // TODO Auto-generated constructor stub
    }

    public RefreshResult (String errorMsg,
                          boolean repopulating,
                          boolean prepopulating,
                          String formattedRemainingTime,
                          String formattedEstTimeLabel,
                          String formattedSummary,
                          String refreshCounter,
                          Double elapsedTimeMS,
                          String formattedElapsedTime,
                          Double remainingTimeMS,
                          Double totalEstimatedTimeMS,
                          String formattedTotalTime,
                          String lastCompletionTimeMS,
                          String formattedLastCompletionTime,
                          String formattedStatus,
                          String formattedElapsedTimeLabel) {
        super();
        this.errorMsg = errorMsg;
        this.repopulating = repopulating;
        this.prepopulating = prepopulating;
        this.formattedRemainingTime = formattedRemainingTime;
        this.formattedEstTimeLabel = formattedEstTimeLabel;
        this.formattedSummary = formattedSummary;
        this.refreshCounter = refreshCounter;
        this.elapsedTimeMS = elapsedTimeMS;
        this.formattedElapsedTime = formattedElapsedTime;
        this.remainingTimeMS = remainingTimeMS;
        this.totalEstimatedTimeMS = totalEstimatedTimeMS;
        this.formattedTotalTime = formattedTotalTime;
        this.lastCompletionTimeMS = lastCompletionTimeMS;
        this.formattedLastCompletionTime = formattedLastCompletionTime;
        this.formattedStatus = formattedStatus;
        this.formattedElapsedTimeLabel = formattedElapsedTimeLabel;
    }

    public String getErrorMsg () {
        return errorMsg;
    }

    public void setErrorMsg (String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public boolean isRepopulating () {
        return repopulating;
    }

    public void setRepopulating (boolean repopulating) {
        this.repopulating = repopulating;
    }

    public boolean isPrepopulating () {
        return prepopulating;
    }

    public void setPrepopulating (boolean prepopulating) {
        this.prepopulating = prepopulating;
    }

    public String getFormattedRemainingTime () {
        return formattedRemainingTime;
    }

    public void setFormattedRemainingTime (String formattedRemainingTime) {
        this.formattedRemainingTime = formattedRemainingTime;
    }

    public String getFormattedEstTimeLabel () {
        return formattedEstTimeLabel;
    }

    public void setFormattedEstTimeLabel (String formattedEstTimeLabel) {
        this.formattedEstTimeLabel = formattedEstTimeLabel;
    }

    public String getFormattedSummary () {
        return formattedSummary;
    }

    public void setFormattedSummary (String formattedSummary) {
        this.formattedSummary = formattedSummary;
    }

    public String getRefreshCounter () {
        return refreshCounter;
    }

    public void setRefreshCounter (String refreshCounter) {
        this.refreshCounter = refreshCounter;
    }

    public Double getElapsedTimeMS () {
        return elapsedTimeMS;
    }

    public void setElapsedTimeMS (Double elapsedTimeMS) {
        this.elapsedTimeMS = elapsedTimeMS;
    }

    public String getFormattedElapsedTime () {
        return formattedElapsedTime;
    }

    public void setFormattedElapsedTime (String formattedElapsedTime) {
        this.formattedElapsedTime = formattedElapsedTime;
    }

    public Double getRemainingTimeMS () {
        return remainingTimeMS;
    }

    public void setRemainingTimeMS (Double remainingTimeMS) {
        this.remainingTimeMS = remainingTimeMS;
    }

    public Double getTotalEstimatedTimeMS () {
        return totalEstimatedTimeMS;
    }

    public void setTotalEstimatedTimeMS (Double totalEstimatedTimeMS) {
        this.totalEstimatedTimeMS = totalEstimatedTimeMS;
    }

    public String getFormattedTotalTime () {
        return formattedTotalTime;
    }

    public void setFormattedTotalTime (String formattedTotalTime) {
        this.formattedTotalTime = formattedTotalTime;
    }

    public String getLastCompletionTimeMS () {
        return lastCompletionTimeMS;
    }

    public void setLastCompletionTimeMS (String lastCompletionTimeMS) {
        this.lastCompletionTimeMS = lastCompletionTimeMS;
    }

    public String getFormattedLastCompletionTime () {
        return formattedLastCompletionTime;
    }

    public void setFormattedLastCompletionTime (String formattedLastCompletionTime) {
        this.formattedLastCompletionTime = formattedLastCompletionTime;
    }

    public String getFormattedStatus () {
        return formattedStatus;
    }

    public void setFormattedStatus (String formattedStatus) {
        this.formattedStatus = formattedStatus;
    }

    public String getFormattedElapsedTimeLabel () {
        return formattedElapsedTimeLabel;
    }

    public void setFormattedElapsedTimeLabel (String formattedElapsedTimeLabel) {
        this.formattedElapsedTimeLabel = formattedElapsedTimeLabel;
    }

    @Override
    public String toString () {
        return "RefreshResult [errorMsg=" + errorMsg + ", repopulating="
                + repopulating + ", prepopulating=" + prepopulating
                + ", formattedRemainingTime=" + formattedRemainingTime
                + ", formattedEstTimeLabel=" + formattedEstTimeLabel
                + ", formattedSummary=" + formattedSummary
                + ", refreshCounter=" + refreshCounter + ", elapsedTimeMS="
                + elapsedTimeMS + ", formattedElapsedTime="
                + formattedElapsedTime + ", remainingTimeMS=" + remainingTimeMS
                + ", totalEstimatedTimeMS=" + totalEstimatedTimeMS
                + ", formattedTotalTime=" + formattedTotalTime
                + ", lastCompletionTimeMS=" + lastCompletionTimeMS
                + ", formattedLastCompletionTime="
                + formattedLastCompletionTime + ", formattedStatus="
                + formattedStatus + ", formattedElapsedTimeLabel="
                + formattedElapsedTimeLabel + "]";
    }
    
    
}
