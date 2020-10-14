package com.hp.asi.hpic4vc.provider.adapter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import org.apache.http.entity.StringEntity;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

import com.hp.asi.hpic4vc.provider.data.HPOneViewCredentialsData;
import com.hp.asi.hpic4vc.provider.locale.I18NProvider;
import com.hp.asi.hpic4vc.provider.model.StringModel;


public abstract class BaseHPOneViewCredentialsPostAdapter extends CredentialsPostAdapter<String, StringModel> {
    public static enum ACTION {
        ADD    ("add",  I18NProvider.HPOneViewCredentials_Add_Success), 
        DELETE ("del",  I18NProvider.HPOneViewCredentials_Delete_Success), 
        EDIT   ("edit", I18NProvider.HPOneViewCredentials_Edit_Success);
        
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
    
    private   static final String SERVICE_NAME = "credentials";
    private   static final String TYPE = "hponeview";
    protected static final String PASSWORD = "****************";
    protected static final String EMPTY_ID = "_empty";
    protected static final String UIM_SUCCESS_MSG = "none";
    protected static final String UIM_CANNOT_FIND_CREDENTIAL = "not found";

    private ACTION   action;
    protected String hostName;
    protected String userName;
    protected String passWord;
    protected String id;

    public BaseHPOneViewCredentialsPostAdapter (final ACTION action) {
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
    public StringEntity getPostParams () {
		String json = "";
		ObjectMapper mapper = new ObjectMapper();

		HPOneViewCredentialsData data = new HPOneViewCredentialsData();
		data.setIp(hostName);
		data.setUsername(userName);
		data.setPassword(passWord);
		data.setType(TYPE);
		data.setAction(action.operationString);
		try {
			json = mapper.writeValueAsString(data);
			log.info(json);
		} catch (JsonGenerationException e) {
			log.error("hostName=" + hostName + ", userName=" + userName, e);
			throw new RuntimeException(e);
		} catch (JsonMappingException e) {
			log.error("hostName=" + hostName + ", userName=" + userName, e);
			throw new RuntimeException(e);
		} catch (IOException e) {
			log.error("hostName=" + hostName + ", userName=" + userName, e);
			throw new RuntimeException(e);
		}
		
		StringEntity entity = null;
		try {
			entity = new StringEntity(json);
		} catch (UnsupportedEncodingException e) {
			log.error("json=" + json, e);
			throw new RuntimeException(e);
		}
        return entity;
    }
}
