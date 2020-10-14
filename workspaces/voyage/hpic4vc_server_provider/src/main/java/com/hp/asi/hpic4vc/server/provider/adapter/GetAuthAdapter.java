package com.hp.asi.hpic4vc.server.provider.adapter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.JsonGenerationException;
import com.hp.asi.hpic4vc.provider.adapter.PostAdapter;
import com.hp.asi.hpic4vc.provider.error.DataMapException;
import com.hp.asi.hpic4vc.provider.error.InitializationException;
import com.hp.asi.hpic4vc.provider.impl.SessionManagerImpl;
import com.hp.asi.hpic4vc.provider.locale.I18NProvider;
import com.hp.asi.hpic4vc.provider.model.StringModel;
import com.hp.asi.hpic4vc.server.provider.data.DeploymentUserData;
import com.hp.asi.hpic4vc.server.provider.data.Deployment.AuthResult;
import com.hp.asi.hpic4vc.server.provider.model.DeploymentUserDataModel;

public class GetAuthAdapter extends PostAdapter<String, StringModel> {

	// private DeploymentUserData userData = new DeploymentUserData();

	private String host;

	private String data = "";

	private String userName;

	private String password;

	private URL _baseurl;

	private DeploymentUserDataModel userData = new DeploymentUserDataModel();

	private static final String SERVICE_NAME = "rest/login-sessions";

	public GetAuthAdapter(String _userName, String _password, String _host) {
		super();

		this.host = _host;
		this.userName = _userName;
		this.password = _password;
		

	}

	@Override
	public String getServiceName() {
		return SERVICE_NAME;
	}

	@Override
	protected String getWsUrl() throws InitializationException {
		String url;
		String developerHostname = "http://localhost:8085/";
		String hostname = SessionManagerImpl.getInstance().getWSURLHostname(
				this.sessionInfo.getSessionId(),
				this.sessionInfo.getServerGuid());
		if (hostname.contains("localhost")) {
			hostname = developerHostname;
		}

		url = "https://" + host + "/" + SERVICE_NAME;

		try {
			_baseurl = new URL(url);
		} catch (MalformedURLException e) {
			log.error(url, e);
			// e.printStackTrace();
		}

		return url;

	}

	@Override
	public List<NameValuePair> getPostParams() {
		String kvPairs[] = data.split(":");
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		for (String kvPair : kvPairs) {
			String kv[] = kvPair.split("=");
			if (kv.length == 2) {
				try {
					params.add(new BasicNameValuePair(kv[0], kv[1]));
				} catch (Exception e) {
					log.debug(e);
				}
			}
		}
		return params;
	}

	@Override
	public StringModel formatData(final String result) {
		StringModel model = new StringModel();
		
		try {
			model.data = getStringObjFromJson(result).getSessionID().toString();
		} catch (DataMapException e) {
			// TODO Auto-generated catch block
			log.error("formatData()" + e);
		}
		
		return model;
	}

	@Override
	public StringModel getEmptyModel() {

		return null;
	}

	private void replaceHttpClient() {
		try {
			if (httpClient != null) {
				httpClient.getConnectionManager().shutdown();
				httpClient = null;
			}

			httpClient = ServerProvisioningAdapter.getInstance();
		} catch (Exception e) {
			log.error("replaceHttpClient()", e);
		}

	}

	 private AuthResult getStringObjFromJson (final String jsonData) throws DataMapException {
		 ObjectMapper mapper = new ObjectMapper();
		 AuthResult auth = new AuthResult();
	        try {
	        	auth = mapper.readValue(jsonData, AuthResult.class);
	            log.info("JSON data mapped to object: " + auth.toString());
	            return auth;
	        } catch (Exception e) {
	            log.error("JSON ERROR: error mapping JSON string to object for service "
	                    + this.getServiceName()
	                    + ":  "
	                    + jsonData
	                    + ".  Throwing a DataMapException.");
	            log.debug(e.getMessage(), e);
	            String message = this.i18nProvider.getInternationalString(locale,
	                                            I18NProvider.Error_CannotMapJson);
	            throw new DataMapException(message);
	        }
	    }
	

	@Override
	public HttpEntityEnclosingRequestBase getHttpRequest(final String urlString)
			throws UnsupportedEncodingException {
		HttpEntityEnclosingRequestBase httpPost = new HttpPost(URI.create(urlString));
		log.info(httpPost.getURI().getHost());
		log.info(httpPost.getURI().getPath());
		
		httpPost.setHeader("Content-type", "application/json");
		log.info(httpPost.getRequestLine().getUri());
		 httpPost.setHeader("Accept", "application/json");
		// List<NameValuePair> params = getPostParams();
		String userDataJson = "";
		ObjectMapper mapper = new ObjectMapper();

		try {
			userDataJson = mapper.writeValueAsString(new DeploymentUserData(
					this.userName, this.password));
			log.info(userDataJson);
		} catch (JsonGenerationException e) {
			log.error(userName + ":" + password, e);
			throw new RuntimeException(e);
		} catch (JsonMappingException e) {
			log.error(userName + ":" + password, e);
			throw new RuntimeException(e);
		} catch (IOException e) {
			log.error(userName + ":" + password, e);
			throw new RuntimeException(e);
		}

		StringEntity entity = new StringEntity(userDataJson);
		httpPost.setEntity(entity);
		log.info(httpPost.getRequestLine().getUri());

		replaceHttpClient();
		return httpPost;
	}

}
