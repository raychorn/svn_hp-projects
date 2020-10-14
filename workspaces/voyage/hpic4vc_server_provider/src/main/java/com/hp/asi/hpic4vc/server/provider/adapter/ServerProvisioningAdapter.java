package com.hp.asi.hpic4vc.server.provider.adapter;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

import com.hp.asi.hpic4vc.provider.adapter.PostAdapter;

public class ServerProvisioningAdapter {

	public static HttpClient getInstance() throws NoSuchAlgorithmException,
			KeyManagementException, UnrecoverableKeyException,
			KeyStoreException {
		SSLContext sslContext = SSLContext.getInstance("SSL");
		TrustManager promiscuous_trust = new X509TrustManager() {

			@Override
			public void checkClientTrusted(X509Certificate[] arg0, String arg1)
					throws CertificateException {
				// TODO Auto-generated method stub

			}

			@Override
			public void checkServerTrusted(X509Certificate[] arg0, String arg1)
					throws CertificateException {
				// TODO Auto-generated method stub

			}

			@Override
			public X509Certificate[] getAcceptedIssuers() {
				// TODO Auto-generated method stub
				return null;
			}

		};
		sslContext.init(null, new TrustManager[] { promiscuous_trust }, null);
		SSLSocketFactory secureSocketFactory = new SSLSocketFactory(sslContext);
		secureSocketFactory
				.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		SchemeRegistry registry = new SchemeRegistry();
		registry.register(new Scheme("https", secureSocketFactory, 443));
		// registry.register(new Scheme("http", new PlainSocketFactory(), 80));
		HttpParams params = new BasicHttpParams();
		ClientConnectionManager mgr = new ThreadSafeClientConnManager(params,
				registry);

		return new DefaultHttpClient(mgr, params);

	}
}