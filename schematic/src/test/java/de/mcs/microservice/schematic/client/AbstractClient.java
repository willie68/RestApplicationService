package de.mcs.microservice.schematic.client;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.glassfish.jersey.media.multipart.MultiPartFeature;

public class AbstractClient {
  TrustManager[] certs = new TrustManager[] { new X509TrustManager() {
    @Override
    public X509Certificate[] getAcceptedIssuers() {
      return null;
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
    }
  } };

  public static class TrustAllHostNameVerifier implements HostnameVerifier {

    public boolean verify(String hostname, SSLSession session) {
      return true;
    }

  }

  Client client;

  AbstractClient() throws NoSuchAlgorithmException, KeyManagementException {
    SSLContext ctx = SSLContext.getInstance("SSL");
    ctx.init(null, certs, new SecureRandom());

    client = ClientBuilder.newBuilder().hostnameVerifier(new TrustAllHostNameVerifier()).sslContext(ctx).build();
    client.register(MultiPartFeature.class);

  }

  WebTarget getWebTarget(String baseUrl) {
    return client.target(baseUrl).path("/rest/v1/apps/");
  }

}
