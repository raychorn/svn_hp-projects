package com.hp.asi.hpic4vc.server.provider.adapter;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPut;

import com.hp.asi.hpic4vc.provider.adapter.PostAdapter;
import com.hp.asi.hpic4vc.provider.model.StringModel;


public class PutRediscoverNodeAdatapter extends
        PostAdapter<String, StringModel> {
	
	private static final String SERVICE_NAME = "services/host/rediscovernode";


	@Override
	public List<NameValuePair> getPostParams() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StringModel getEmptyModel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getServiceName() {
		return SERVICE_NAME;
	}
	
	@Override
    public HttpPut getHttpRequest(final String urlString) throws UnsupportedEncodingException {
        HttpPut httpPut   = new HttpPut(urlString);
        return httpPut;
    }
	
	@Override
	public StringModel formatData(final String result) {
		 StringModel model = new StringModel();
		 model.data = result; 
		 return model;
	}

}
