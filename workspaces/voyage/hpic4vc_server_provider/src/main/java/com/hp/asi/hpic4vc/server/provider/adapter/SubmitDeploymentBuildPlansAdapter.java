package com.hp.asi.hpic4vc.server.provider.adapter;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.codehaus.jackson.map.ObjectMapper;

import com.hp.asi.hpic4vc.provider.adapter.PostAdapter;
import com.hp.asi.hpic4vc.provider.error.CommunicationException;
import com.hp.asi.hpic4vc.provider.error.DataMapException;
import com.hp.asi.hpic4vc.provider.error.InitializationException;
import com.hp.asi.hpic4vc.provider.error.NullDataException;
import com.hp.asi.hpic4vc.provider.impl.SessionManagerImpl;
import com.hp.asi.hpic4vc.provider.locale.I18NProvider;
import com.hp.asi.hpic4vc.provider.model.StringModel;
import com.hp.asi.hpic4vc.server.provider.data.Deployment.ServerProvisioningJobResult;

public class SubmitDeploymentBuildPlansAdapter extends
		PostAdapter<String, StringModel> {

	private static final String SERVICE_NAME = "rest/os-deployment-build-plans";

	private static final String PERSONALIZED_RUN = "personalizeandrun";

	

	private String data;

	private URL _baseurl;

	private String auth;

	private String host;

	private String buildPlanID;

	public SubmitDeploymentBuildPlansAdapter(String data, String buildId,
			String auth, String host) {
		super();
		this.data = data;
		this.auth = auth;
		this.host = host;
		this.buildPlanID = buildId;

		// getObjectFromXML(data);
	}

	@Override
	public String getServiceName() {
		return SERVICE_NAME;
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
	public HttpEntityEnclosingRequestBase getHttpRequest(final String urlString)
			throws UnsupportedEncodingException {
		HttpEntityEnclosingRequestBase httpPut = new HttpPut(
				URI.create(urlString));
		log.info(httpPut.getURI().getHost());
		log.info(httpPut.getURI().getPath());

		httpPut.setHeader("Content-type", "application/json");
		httpPut.setHeader("auth", auth);
		log.info(httpPut.getRequestLine().getUri());
		httpPut.setHeader("Accept", "application/json");

		StringEntity entity = new StringEntity(data);
		httpPut.setEntity(entity);
		log.info(httpPut.getRequestLine().getUri());

		replaceHttpClient();
		return httpPut;

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

		url = "https://" + host + buildPlanID + "/" + PERSONALIZED_RUN;

	

		try {
			_baseurl = new URL(url);
		} catch (MalformedURLException e) {
			log.error(url, e);
			
		}

		return url;

	}

	@Override
	protected void checkHttpResponseForErrors(final HttpResponse response)
			throws CommunicationException, NullDataException {
		String errorMsg = i18nProvider.getInternationalString(locale,
				I18NProvider.Error_NoWsResponse);

		String httpGetMsg = "HttpResponse for " + this.getServiceName();

		if (null == response) {
			log.error(httpGetMsg
					+ " had a null response.  Throwing a NullDataException.");
			throw new NullDataException(errorMsg);
		}

		if (null == response.getStatusLine()) {
			log.error(httpGetMsg + " had a null statusLine in the response.  "
					+ "Throwing a NullDataException.");
			throw new NullDataException(errorMsg);
		}

		if (response.getStatusLine().getStatusCode() != HttpStatus.SC_ACCEPTED) {
			log.error(httpGetMsg + " had status code "
					+ response.getStatusLine().getStatusCode()
					+ " Throwing a CommunicationException.");
			throw new CommunicationException(errorMsg);
		}

		if (null == response.getEntity()) {
			log.info(httpGetMsg
					+ " had a null entity, throwing a NullDataException.");
			throw new NullDataException(errorMsg);
		}

	}

	private ServerProvisioningJobResult getStringObjFromJson(
			final String jsonData) throws DataMapException {
		ObjectMapper mapper = new ObjectMapper();
		ServerProvisioningJobResult result = new ServerProvisioningJobResult();
		try {
			result = mapper.readValue(jsonData,
					ServerProvisioningJobResult.class);
			log.info("JSON data mapped to object: " + auth.toString());
			return result;
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
	public StringModel formatData(final String result) {
		StringModel model = new StringModel();

		try {
			model.data = getStringObjFromJson(result).getUri().toString();
		} catch (DataMapException e) {
			// TODO Auto-generated catch block
			log.error("formatData()" + e);
		}

		return model;
	}

	@Override
	public StringModel getEmptyModel() {
		// TODO Auto-generated method stub
		return new StringModel();
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

	

}
