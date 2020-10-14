package com.hp.asi.hpic4vc.provider.adapter;

import com.hp.asi.hpic4vc.provider.data.CertificateData;
import com.hp.asi.hpic4vc.provider.model.CertificateModel;

public class InstallCertificateDataAdapter extends DataAdapter<CertificateData, CertificateModel> {
    private static final String SERVICE_NAME = "certificate/json";
    
    public InstallCertificateDataAdapter () {
        super(CertificateData.class);
    }

    @Override
    public CertificateModel getEmptyModel () {
        return new CertificateModel();
    }

    @Override
    public String getServiceName () {
        return SERVICE_NAME;
    }

    @Override
    public CertificateModel formatData (CertificateData rawData) {
        CertificateModel model = new CertificateModel();
        
        if (null != rawData.getCert() && !rawData.getCert().equals("")) {
            model.setHasCertificate(true);
            model.setIsSelfSigned(rawData.isSelfSigned());
            model.setCertificate(rawData.getCert());
        }
        
        return model;
    } 

}
