package com.hp.asi.hpic4vc.provider.adapter;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

import com.hp.asi.hpic4vc.provider.model.StringModel;

public class InstallCertificatePostAdapter extends PostAdapter<String, StringModel> {
	private static final String SERVICE_NAME       = "certificate/json";
	private static final String ERROR_DELIMITER    = "\"error\": \"";
	private static final String CR_NEWLINE_COMBO   = "\r\n";
	private static final String CARRIAGE_RETURN    = "\r";
	private static final String NEW_LINE           = "\n";
	private static final String DELIMITED_NEW_LINE = "\\n";
	
	private String certificate;
	
	public InstallCertificatePostAdapter(final String certificate) {
		super();
		this.certificate = certificate;
	}

	@Override
	public List<NameValuePair> getPostParams() {
		return null;
	}

	@Override
	public String getServiceName() {
		return SERVICE_NAME;
	}
	
    @Override
    public StringModel getEmptyModel () {
        return new StringModel();
    }
	
    @Override
    public HttpPost getHttpRequest(final String urlString) throws UnsupportedEncodingException {
        HttpPost httpPost   = new HttpPost(urlString);
        StringEntity params = new StringEntity(generateRequestBody());
        httpPost.addHeader("content-type", "application/x-www-form-urlencoded");
        httpPost.addHeader("Accept", "application/json, text/javascript, */*; q=0.01");
        httpPost.setEntity(params);
        return httpPost;
    }	
	
	String generateRequestBody() {
		String cert = normalizeEOL();
		StringBuilder sb = new StringBuilder();
		sb.append("{\"cert\":\"");
		sb.append(cert);
		sb.append("\"");
		sb.append(",\"method\":\"upload\"}");
		return sb.toString();
	}
	
	private String normalizeEOL() {
		String cert = certificate;
		if (certificate.contains(CR_NEWLINE_COMBO)) {
			cert = certificate.replace(CR_NEWLINE_COMBO, DELIMITED_NEW_LINE);
		} else if (certificate.contains(CARRIAGE_RETURN)) {
			cert = certificate.replace(CARRIAGE_RETURN, DELIMITED_NEW_LINE);
		} else if (certificate.contains(NEW_LINE)) {
			cert = certificate.replace(NEW_LINE, DELIMITED_NEW_LINE);
		}
		return cert;
	}
	
	@Override
	public StringModel formatData(final String result) {
	    StringModel model = new StringModel();
		if (result.contains(ERROR_DELIMITER)) {
			int loc1 = result.indexOf(ERROR_DELIMITER) + ERROR_DELIMITER.length();
			int loc2 = result.lastIndexOf("\"");
			model.data = result.substring(loc1, loc2);
		} else {
			model.data = result;
		}
		return model;
	}
}
