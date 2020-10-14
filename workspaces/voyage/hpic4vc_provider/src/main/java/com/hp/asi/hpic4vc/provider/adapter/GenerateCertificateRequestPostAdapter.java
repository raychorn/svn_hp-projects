package com.hp.asi.hpic4vc.provider.adapter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

import com.hp.asi.hpic4vc.provider.model.StringModel;

public class GenerateCertificateRequestPostAdapter extends PostAdapter <String, StringModel> {
	private static final String SERVICE_NAME = "certificate/json";
	private static final String NEW_LINE     = "\\\\n";
	private static final String CERT_BEGIN   = "-----BEGIN CERTIFICATE-----";
	private static final String CERT_END     = "-----END CERTIFICATE-----";
	private static final String CERT_REQUEST_BEGIN  = 
			"-----BEGIN CERTIFICATE REQUEST-----";
	private static final String CERT_REQUEST_END    =
			"-----END CERTIFICATE REQUEST-----";
	private static final Map <Integer, CertEnum> EnumMap;
	static {
		EnumMap = new HashMap<Integer, CertEnum>();
		for (CertEnum certEnum : CertEnum.values()) {
			EnumMap.put(certEnum.getOrdinal(), certEnum);
		}
	}
	
	final String  userArgs[];
	final boolean selfSigned;
	
	public GenerateCertificateRequestPostAdapter(String[] args, boolean selfSigned) {
		super();
		this.userArgs   = args;
		this.selfSigned = selfSigned;
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
        httpPost.setEntity(params);
        return httpPost;
    }
	    
	
	@Override
	public StringModel formatData(final String result) {
	    StringModel model = new StringModel();
		if (selfSigned) {
		    String data = extractCertificate(result, CERT_BEGIN, CERT_END);
		    if (null != data) {
		        model.data = data.replaceAll(NEW_LINE, "\n");
		    }
		} else {
		    String data = extractCertificate(result, CERT_REQUEST_BEGIN, CERT_REQUEST_END);
		    if (null != data) {
		        model.data = data.replaceAll(NEW_LINE, "");
		    }
		}
		return model;
	}
	
	private String extractCertificate(final String result, 
			final String startDelimiter, 
			final String endDelimiter) {
	    String extractedString = null;
		if (result.contains(startDelimiter) && result.contains(endDelimiter)) {
			int loc1 = result.indexOf(startDelimiter);
			int loc2 = result.indexOf(endDelimiter) + endDelimiter.length();
			extractedString = result.substring(loc1, loc2);
		} 
		
		return extractedString;
	}

	String generateRequestBody() {
		StringBuilder sb = new StringBuilder();
		sb.append("{\"certinfo\":");
		sb.append(generateCertInfo());
		sb.append(",\"selfsigned\":");
		sb.append(this.selfSigned);
		sb.append(",\"method\":\"generate\"}");
		return sb.toString();
	}
	
	String generateCertInfo() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		
		for (int i=0; i<userArgs.length; i++) {
			if (EnumMap.containsKey(i)) {
				sb.append(EnumMap.get(i).getJsonString());
				sb.append("\"");
				sb.append(userArgs[i]);
				sb.append("\"");
			}
		}

		sb.append("}");
		return sb.toString();
	}

	enum CertEnum {
		C     (0, "\"C\":"),
		ST    (1, ",\"ST\":"),
		L     (2, ",\"L\":"),
		O     (3, ",\"O\":"),
		CN    (4, ",\"CN\":"),
		OU    (5, ",\"OU\":"),
		Email (6, ",\"Email\":"),
		SN    (7, ",\"SN\":"),
		GN    (8, ",\"GN\":");
		
		private int    ordinal;
		private String jsonString;
		
		CertEnum(final int pos, final String str) {
			this.ordinal    = pos;
			this.jsonString = str;
		}
		
		String getJsonString() {
			return this.jsonString;
		}
		
		int getOrdinal() {
			return this.ordinal;
		}
		
	};
}
