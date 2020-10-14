package com.hp.asi.hpic4vc.provider.utils.ssl;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;

/**
 * Trust manager that can accept certificates based on thumbprints.
 */

public class ThumbprintTrustManager implements X509TrustManager {

	private static final Log LOG = LogFactory
			.getLog(ThumbprintTrustManager.class);

	private final X509TrustManager _defaultTrustManager;

	private final SslThumbprintVerifier _thumbprintVerifier;

	/**
	 * Enables checkServerTrusted() to communicate cert details to the
	 * HostnameVerifier.
	 */
	private static class ChainInfo {
		X509Certificate[] _chain;
		String _thumbprint;
		SslThumbprintVerifier.Result _verifyResult;
		boolean _isTrusted;

		ChainInfo(X509Certificate[] chain, String thumbprint,
				SslThumbprintVerifier.Result verifyResult, boolean isTrusted) {
			_chain = chain;
			_thumbprint = thumbprint;
			_verifyResult = verifyResult;
			_isTrusted = isTrusted;
		}
	}

	private final ThreadLocal<ChainInfo> _tlsChainInfo;

	/**
	 * Constructor.
	 * 
	 * @param trustStore
	 *            The (loaded) store of trusted certs to use.
	 * @param verifier
	 *            The thumbprint verifier to use.
	 */
	public ThumbprintTrustManager(KeyStore trustStore,
			SslThumbprintVerifier verifier) throws KeyStoreException {

		_defaultTrustManager = createDefaultTrustManger(trustStore);
		_thumbprintVerifier = verifier;
		_tlsChainInfo = new ThreadLocal<ChainInfo>();
	}

	private static X509TrustManager createDefaultTrustManger(KeyStore trustStore)
			throws KeyStoreException {

		TrustManagerFactory factory;
		try {
			factory = TrustManagerFactory.getInstance("PKIX");
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException(
					"Unable to create trust manager factory", e);
		}
		factory.init(trustStore);

		for (TrustManager trustManager : factory.getTrustManagers()) {
			if (trustManager instanceof X509TrustManager) {
				return (X509TrustManager) trustManager;
			}
		}

		throw new IllegalStateException("Unable to find default trust manager");
	}

	/**
	 * Computes certificate trust information for subsequent consumption by
	 * HostnameVerifier.
	 * 
	 * Validates a server certificate chain against the configured trust store,
	 * or using the configured thumbprint verifier. Makes details available to
	 * the HostnameVerifier in thread local storage.
	 */
	@Override
	public void checkServerTrusted(X509Certificate[] chain, String authType)
			throws CertificateException {
		String certThumbprint = null;
		SslThumbprintVerifier.Result verifyResult = SslThumbprintVerifier.Result.UNKNOWN;
		boolean isTrusted = false;
		try {
			try {
				certThumbprint = computeCertificateThumbprint(chain[0]);
			} catch (Exception e) {
				throw new CertificateException(
						"Unable to compute server certificate thumbprint", e);
			}
			if (_thumbprintVerifier != null) {
				verifyResult = _thumbprintVerifier.verify(certThumbprint);
			}
			_defaultTrustManager.checkServerTrusted(chain, authType);
			isTrusted = true;
			if (LOG.isDebugEnabled()) {
				LOG.debug("Server certificate chain is trusted");
			}
		} catch (Exception e) {
			// not trusted by trust store
			isTrusted = false;
			if (LOG.isDebugEnabled()) {
				LOG.debug("Server certificate chain is not trusted");
			}
			if (verifyResult != SslThumbprintVerifier.Result.MATCH) {
				// make cert chain and thumbprint available to HostnameVerifier
				_tlsChainInfo.set(new ChainInfo(chain, certThumbprint,
						verifyResult, isTrusted));
				throw new CertificateException(
						"Server certificate chain is not trusted "
								+ "and thumbprint doesn't match", e);
			}
			if (LOG.isDebugEnabled()) {
				LOG.debug("Server certificate chain is not trusted "
						+ "but thumbprint matches");
			}
		}
		// make cert chain and thumbprint available to HostnameVerifier
		_tlsChainInfo.set(new ChainInfo(chain, certThumbprint, verifyResult,
				isTrusted));
	}

	@Override
	public void checkClientTrusted(X509Certificate[] chain, String authType)
			throws CertificateException {
		_defaultTrustManager.checkClientTrusted(chain, authType);
	}

	@Override
	public X509Certificate[] getAcceptedIssuers() {
		return _defaultTrustManager.getAcceptedIssuers();
	}

	public static String computeCertificateThumbprint(X509Certificate cert)
			throws NoSuchAlgorithmException, CertificateEncodingException {
		String HEX = "0123456789ABCDEF";
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		byte[] digest = md.digest(cert.getEncoded());

		StringBuilder thumbprint = new StringBuilder();
		for (int i = 0, len = digest.length; i < len; ++i) {
			if (i > 0) {
				thumbprint.append(':');
			}
			byte b = digest[i];
			thumbprint.append(HEX.charAt((b & 0xF0) >> 4));
			thumbprint.append(HEX.charAt(b & 0x0F));
		}
		return thumbprint.toString();
	}

	/**
	 * HostnameVerifier that accepts any assertion if the cert has a known
	 * thumbprint.
	 */
	public class HostnameVerifier implements X509HostnameVerifier {

		/**
		 * Verifies that remote host is consistent with the assertions
		 * associated with an SSL socket. This is the method actually called by
		 * the SSLSocketFactory.
		 * 
		 * @param host
		 *            The remote host.
		 * @param socket
		 *            The SSL socket.
		 */
		@Override
		public void verify(String host, SSLSocket socket) throws IOException {
			// @see org.apache.http.conn.ssl.AbstractVerifier

			// calls checkServerTrusted()
			SSLSession session = socket.getSession();
			if (session == null) {
				// force re-negotiation
				InputStream in = socket.getInputStream();
				in.available();
				session = socket.getSession();
				if (session == null) {
					socket.startHandshake();
					session = socket.getSession();
				}
			}
			Certificate[] certificates;
			try {
				certificates = session.getPeerCertificates();
			} catch (SSLPeerUnverifiedException e) {
				// Since the hostname validation failed - invalidate the
				// session. If it is not invalidated a second call to getSession
				// would use the existing session without calling
				// checkServerTrusted() and this will fail the logic of the
				// hostname verifier about the SslThumbprintVerifier.
				session.invalidate();
				ChainInfo chainInfo = _tlsChainInfo.get();
				if (chainInfo != null) {
					_tlsChainInfo.set(null);
					throw new ThumbprintValidationException(
							"Server certificate chain not verified",
							chainInfo._chain, chainInfo._thumbprint,
							chainInfo._isTrusted, e);
				} else {
					throw new SSLException(
							"Server certificate chain not verified (no details)",
							e);
				}
			}
			try {
				verify(host, (X509Certificate) certificates[0]);
			} catch (ThumbprintValidationException cve) {
				// Same reason as the above session.invalidate();
				session.invalidate();
				throw cve;
			}
		}

		/**
		 * Uses strict checking to verify that the remote host is consistent
		 * with the server certificate, or that the certificate has a known
		 * thumbprint. Invokes the {@link SslThumbprintVerifier}'s
		 * {@link SslThumbprintVerifier#onSuccess} callback if appropriate.
		 * 
		 * @param host
		 *            The remote host.
		 * @param cert
		 *            The server certificate.
		 */
		@Override
		public void verify(String host, X509Certificate cert)
				throws SSLException {
			boolean assertionVerified = false;
			ChainInfo chainInfo = _tlsChainInfo.get();
			_tlsChainInfo.set(null);
			try {
				SSLSocketFactory.STRICT_HOSTNAME_VERIFIER.verify(
						formatHost(host), cert);
				if (LOG.isDebugEnabled()) {
					LOG.debug("Server certificate assertion verified");
				}
				assertionVerified = true;
			} catch (SSLException e) {
				/*
				 * If host name verification fails and chainInfo is null then we
				 * are using a cached session with a previously verified
				 * thumbprint.
				 */
				if (null != chainInfo
						&& chainInfo._verifyResult != SslThumbprintVerifier.Result.MATCH) {
					throw new ThumbprintValidationException(
							"Server certificate assertion not verified "
									+ "and thumbprint not matched",
							chainInfo._chain, chainInfo._thumbprint,
							chainInfo._isTrusted, e);
				}
				if (LOG.isDebugEnabled()) {
					LOG.debug("Server certificate assertion not verified "
							+ "but thumbprint matches");
				}
			}
			if (null != chainInfo && _thumbprintVerifier != null) {
				_thumbprintVerifier.onSuccess(chainInfo._chain,
						chainInfo._thumbprint, chainInfo._verifyResult,
						chainInfo._isTrusted, assertionVerified);
			}
		}

		/**
		 * Uses strict checking to verify that the remote host is consistent
		 * with the decomposed certificate assertions. We don't actually expect
		 * this method to be called.
		 * 
		 * @param host
		 *            The remote host.
		 * @param cns
		 *            The common names asserted in the certificate.
		 * @param subjectAlts
		 *            The alternate names asserted in the certificate.
		 */
		@Override
		public void verify(String host, String[] cns, String[] subjectAlts)
				throws SSLException {
			SSLSocketFactory.STRICT_HOSTNAME_VERIFIER.verify(formatHost(host),
					cns, subjectAlts);
		}

		@Override
		public boolean verify(String host, SSLSession session) {
			try {
				Certificate[] certificates = session.getPeerCertificates();
				verify(host, (X509Certificate) certificates[0]);
				return true;
			} catch (SSLException e) {
				return false;
			}
		}

		/**
		 * Remove the surrounding [] from IPv6 host name returned by the
		 * {@link URI#getHost()}
		 * 
		 * @param host
		 *            the host name
		 * @return the formatted host name
		 */
		private String formatHost(String host) {
			if (host != null && host.startsWith("[") && host.endsWith("]")) {
				try {
					return InetAddress.getByName(host).getHostAddress();
				} catch (UnknownHostException e) {
					// if InetAddress bails, this is not, in fact, IPv6 address,
					// so return it as is; shouldn't be possible to happen
					return host;
				}
			}
			return host;
		}
	}
}
