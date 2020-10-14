package com.hp.asi.hpic4vc.provider.adapter;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.hp.asi.hpic4vc.provider.adapter.PostAdapter;
import com.hp.asi.hpic4vc.provider.locale.I18NProvider;
import com.hp.asi.hpic4vc.provider.model.StringModel;


public abstract class BaseVCCredentialsPostAdapter extends PostAdapter<String, StringModel> {
    public static enum ACTION {
        ADD    ("add",  I18NProvider.VCCredentials_Add_Success), 
        DELETE ("del",  I18NProvider.VCCredentials_Delete_Success), 
        EDIT   ("edit", I18NProvider.VCCredentials_Edit_Success);
        
        private String operationString;
        private String i18nSuccessMsg;
        
        private ACTION(final String operation,
                       final String successMsg) {
            this.operationString = operation;
            this.i18nSuccessMsg  = successMsg;
        }
        
        public String getI18nSuccessMsg() {
            return this.i18nSuccessMsg;
        }
    };
    
    private   static final String SERVICE_NAME = "settings/vcpassword";
    private   static final String TYPE = "vCenter";
    protected static final String PASSWORD = "****************";
    protected static final String EMPTY_ID = "_empty";
    protected static final String UIM_SUCCESS_MSG = "none";
    protected static final String UIM_CANNOT_FIND_CREDENTIAL = "not found";

    private ACTION   action;
    protected String hostName;
    protected String userName;
    protected String passWord;
    protected String id;

    public BaseVCCredentialsPostAdapter (final ACTION action) {
        super();
        this.action   = action;
    }

    @Override
    public String getServiceName () {
        return SERVICE_NAME;
    }
    
    @Override
    public StringModel getEmptyModel () {
        return new StringModel();
    }

    @Override
    public List<NameValuePair> getPostParams () {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("host",     hostName));
        params.add(new BasicNameValuePair("username", userName));
        params.add(new BasicNameValuePair("password", passWord));
        params.add(new BasicNameValuePair("type",     TYPE));
        params.add(new BasicNameValuePair("oper",     action.operationString));
        params.add(new BasicNameValuePair("id",       id));

        return params;
    }
}
