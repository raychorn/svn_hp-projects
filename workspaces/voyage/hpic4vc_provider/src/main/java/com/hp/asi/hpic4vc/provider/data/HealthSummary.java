package com.hp.asi.hpic4vc.provider.data;

import org.codehaus.jackson.annotate.JsonProperty;


public class HealthSummary {

    @JsonProperty("INFORMATION")
    private int infoCount;
    @JsonProperty("OK")
    private int okCount;
    @JsonProperty("WARNING")
    private int warnCount;
    @JsonProperty("ERROR")
    private int errorCount;
    @JsonProperty("consolidated_status")
    private String cStatus;
    
    public HealthSummary() {
        this.infoCount  = 0;
        this.okCount    = 0;
        this.warnCount  = 0;
        this.errorCount = 0;
        this.cStatus    = null;
    }

    public int getInfoCount () {
        return this.infoCount;
    }

    @JsonProperty("INFORMATION")
    public void setInformation (final int information) {
        this.infoCount = information;
    }

    public int getOkCount () {
        return okCount;
    }

    @JsonProperty("OK")
    public void setOk (final int ok) {
        this.okCount = ok;
    }

    public int getWarnCount () {
        return warnCount;
    }

    @JsonProperty("WARNING")
    public void setWarning (final int warning) {
        this.warnCount = warning;
    }

    public int getErrorCount () {
        return errorCount;
    }

    @JsonProperty("ERROR")
    public void setError (final int error) {
        this.errorCount = error;
    }

    public String getcStatus () {
        return cStatus;
    }

    public void setconsolidated_status (String cStatus) {
        this.cStatus = cStatus;
    }

    @Override
    public String toString () {
        return "HealthSummary [infoCount=" + infoCount + ", okCount=" + okCount
                + ", warnCount=" + warnCount + ", errorCount=" + errorCount
                + ", cStatus=" + cStatus + "]";
    }
}
