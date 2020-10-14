package com.hp.asi.hpic4vc.provider.model;

public class HealthModel extends BaseModel {

    public String consolidatedStatus;
    public String infoCount;
    public String okCount;
    public String warnCount;
    public String errorCount;
    
    /**
     * An empty constructor is required for the AMF serialization to work!
     */
    public HealthModel() {
        super();
        this.consolidatedStatus   = new String();
        this.infoCount            = new String();
        this.okCount              = new String();
        this.warnCount            = new String();
        this.errorCount           = new String();
    }    
        
    public HealthModel(String consolidatedStatus,
                       String numInfo,
                       String numOk,
                       String numWarn,
                       String numError) {
        super();
        this.consolidatedStatus     = consolidatedStatus;
        this.infoCount              = numInfo;
        this.okCount                = numOk;
        this.warnCount              = numWarn;
        this.errorCount             = numError;
    }

	@Override
	public String toString() {
		return "HealthModel [consolidatedStatus=" + consolidatedStatus
				+ ", infoCount=" + infoCount + ", okCount=" + okCount
				+ ", warnCount=" + warnCount + ", errorCount=" + errorCount
				+ ", errorMessage=" + errorMessage + ", informationMessage="
				+ informationMessage + "]";
	}

}
