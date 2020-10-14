package com.hp.asi.hpic4vc.provider.adapter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.entity.StringEntity;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;

import com.hp.asi.hpic4vc.provider.data.HPOneViewCredentialsData;
import com.hp.asi.hpic4vc.provider.locale.I18NProvider;
import com.hp.asi.hpic4vc.provider.model.StringModel;

public class DeleteHPOneViewCredentialPostAdapter extends BaseHPOneViewCredentialsPostAdapter {

    public DeleteHPOneViewCredentialPostAdapter (final String host,
                                          final String username,
                                          final String id) {
        super(ACTION.DELETE);
        this.hostName = host;
        this.userName = username;
        this.passWord = PASSWORD;
        this.id       = id;
    }

    @Override
    public StringEntity getPostParams () {
		String json = "";
		ObjectMapper mapper = new ObjectMapper().setVisibility(JsonMethod.FIELD, Visibility.ANY);

		HPOneViewCredentialsData data = new HPOneViewCredentialsData();
		data.setIp(hostName);
		data.setUsername(userName);
		data.setAction("delete");
		data.setType("hponeview");
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
