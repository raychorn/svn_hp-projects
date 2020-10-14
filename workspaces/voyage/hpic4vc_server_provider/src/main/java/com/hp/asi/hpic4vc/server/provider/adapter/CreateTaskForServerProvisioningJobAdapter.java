/**
 * 
 */
package com.hp.asi.hpic4vc.server.provider.adapter;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;

import com.hp.asi.hpic4vc.provider.adapter.PostAdapter;
import com.hp.asi.hpic4vc.provider.model.StringModel;

/**
 * @author naikvin
 * 
 */
public class CreateTaskForServerProvisioningJobAdapter extends
		PostAdapter<String, StringModel> {

	private static final String SERVICE_NAME = "services/host/icsp";

	private String data;

	public CreateTaskForServerProvisioningJobAdapter(String data) {
		super();

		this.data = data;
	}

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
	public StringModel formatData(final String result) {

		StringModel model = new StringModel();

		return model;
	}
	
	
	@Override
	public HttpEntityEnclosingRequestBase getHttpRequest(final String urlString)
			throws UnsupportedEncodingException {
		HttpEntityEnclosingRequestBase httpPost = new HttpPost(
				URI.create(urlString));
		log.info(httpPost.getURI().getHost());
		log.info(httpPost.getURI().getPath());

		httpPost.setHeader("Content-type", "application/json");
		log.info(httpPost.getRequestLine().getUri());
		httpPost.setHeader("Accept", "application/json");

		StringEntity entity = new StringEntity(data);
		httpPost.setEntity(entity);
		log.info(httpPost.getRequestLine().getUri());

		
		return httpPost;

	}

}
