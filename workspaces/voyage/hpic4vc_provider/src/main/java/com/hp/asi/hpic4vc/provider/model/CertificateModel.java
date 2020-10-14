package com.hp.asi.hpic4vc.provider.model;

public class CertificateModel extends BaseModel {

    public boolean hasCertificate = false;
    public boolean selfSigned     = false;
    public String certificate     = null;
    
    public CertificateModel() {
    }

    public boolean getHasCertificate () {
        return hasCertificate;
    }

    public void setHasCertificate (boolean hasCertificate) {
        this.hasCertificate = hasCertificate;
    }

    public boolean isSelfSigned () {
        return selfSigned;
    }

    public void setIsSelfSigned (boolean selfSigned) {
        this.selfSigned = selfSigned;
    }

    public String getCertificate () {
        return certificate;
    }

    public void setCertificate (String certificate) {
        this.certificate = certificate;
    }

    @Override
    public String toString () {
        return "CertificateModel [hasCertificate=" + hasCertificate
                + ", selfSigned=" + selfSigned + ", certificate=" + certificate
                + ", errorMessage=" + errorMessage + ", informationMessage="
                + informationMessage + "]";
    }
    
    
}
