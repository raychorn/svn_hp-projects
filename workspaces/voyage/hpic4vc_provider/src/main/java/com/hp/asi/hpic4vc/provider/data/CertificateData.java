package com.hp.asi.hpic4vc.provider.data;

import javax.xml.bind.annotation.XmlElement;

public class CertificateData {

    private boolean selfsigned;
    private String cert;
    
    
    public boolean isSelfSigned () {
        return selfsigned;
    }
    
    @XmlElement(name="selfsigned")
    public void setSelfsigned (boolean selfSigned) {
        this.selfsigned = selfSigned;
    }
    
    public String getCert () {
        return cert;
    }
    
    @XmlElement(name="cert")
    public void setCert (String cert) {
        this.cert = cert;
    }
}
