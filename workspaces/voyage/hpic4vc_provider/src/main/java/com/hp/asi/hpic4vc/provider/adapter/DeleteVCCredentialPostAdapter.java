package com.hp.asi.hpic4vc.provider.adapter;

import com.hp.asi.hpic4vc.provider.locale.I18NProvider;
import com.hp.asi.hpic4vc.provider.model.StringModel;

public class DeleteVCCredentialPostAdapter extends BaseVCCredentialsPostAdapter {

    public DeleteVCCredentialPostAdapter (final String host,
                                          final String username,
                                          final String id) {
        super(ACTION.DELETE);
        this.hostName = host;
        this.userName = username;
        this.passWord = PASSWORD;
        this.id       = id;
    }


    @Override
    public StringModel formatData (final String result) {
        StringModel model = new StringModel();
        if (result.equalsIgnoreCase(UIM_SUCCESS_MSG)) {
            model.data =  this.i18nProvider.getInternationalString
                    (locale, ACTION.DELETE.getI18nSuccessMsg());
        }

        else if (result.equalsIgnoreCase(UIM_CANNOT_FIND_CREDENTIAL)) {
            model.data = this.i18nProvider.getInternationalString(locale,
                                            I18NProvider.VCCredentials_NotFound,
                                            userName,
                                            hostName);
        } else {
            model.data = result;
        }

        return model;
    }
}
