package com.hp.asi.hpic4vc.server.provider.adapter;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.hp.asi.hpic4vc.provider.adapter.PostAdapter;
import com.hp.asi.hpic4vc.provider.model.StringModel;

public class UpdateConfigurationAdapter extends PostAdapter<String, StringModel> {
    private static final String SERVICE_NAME = "settings/config";
    
    public String   data;
    
    public UpdateConfigurationAdapter () {
    	super();
    }

    public UpdateConfigurationAdapter (String data) {
    	super();
    	this.data = data;
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
    	String kvPairs[] = data.split(":");
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        for (String kvPair:kvPairs){
        	String kv[] = kvPair.split("=");
        	if (kv.length == 2){
            	params.add(new BasicNameValuePair(kv[0], kv[1]));
        	}
        }
        return params;
    }

    @Override
    public StringModel formatData (final String result) {
        StringModel model = new StringModel();
        model.data = result;
        return model;
    }

}
