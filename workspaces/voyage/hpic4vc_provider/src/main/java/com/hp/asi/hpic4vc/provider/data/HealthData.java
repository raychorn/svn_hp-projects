package com.hp.asi.hpic4vc.provider.data;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="health")
public class HealthData {

    private String arrayName;
	private String healthStatus;
	private String healthDetail;
            
    public HealthData () {
        
    	this.arrayName = null;
		this.healthStatus = null;
		this.healthDetail = null;
    }     
    
    public HealthData(String arrayName, String healthStatus,
			String healthDetail) {
		
		this.arrayName = arrayName;
		this.healthStatus = healthStatus;
		this.healthDetail = healthDetail;
	}

	@XmlElement(name="name")
    public String getName() {
        return arrayName;        
    }
	
	public void setName(final String name) {
	    this.arrayName = name;
	}

    @XmlElement(name="healthStatus")
    public String getHealthStatus() {        
        return healthStatus;
    }
    
    public void setHealthStatus(final String status) {
        this.healthStatus = status;
    }
    
    @XmlElement(name="detail")
    public String getDetail() {
    	return healthDetail;
    }
    
    public void setDetail(final String detail) {
        this.healthDetail = detail;
    }

    @Override
    public String toString () {
        return "HealthData [arrayName=" + arrayName + ", healthStatus="
                + healthStatus + ", healthDetail=" + healthDetail + "]";
    }
    
}
