package com.hp.asi.hpic4vc.provider.adapter;

import com.hp.asi.hpic4vc.provider.model.StringModel;

public class AddVCCredentialPostAdapter extends BaseVCCredentialsPostAdapter {

    public AddVCCredentialPostAdapter (final String host,
                                       final String username,
                                       final String password) {
        super(ACTION.ADD);
        this.hostName = host;
        this.userName = username;
        this.passWord = password;
        this.id       = EMPTY_ID;
    }


    @Override
    public StringModel formatData (final String result) {
        StringModel model = new StringModel();
        if (result.equalsIgnoreCase(UIM_SUCCESS_MSG)) {
            model.data = this.i18nProvider.getInternationalString
                    (locale, ACTION.ADD.getI18nSuccessMsg());
        } else {
            model.data = result;
        }
        return model;
    }
}
