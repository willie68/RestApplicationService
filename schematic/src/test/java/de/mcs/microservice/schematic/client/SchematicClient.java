/**
 * 
 */
package de.mcs.microservice.schematic.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

import de.mcs.microservice.application.api.BlobDescription;
import de.mcs.microservice.application.client.BasicAuthenticator;
import de.mcs.microservice.schematic.SchematicDataModel;

/**
 * @author w.klaas
 *
 */
public class SchematicClient {

  private static final String REST_URI = "https://127.0.0.1:8443/rest/v1/apps/SchematicApplication/module/SchematicModule/model/SchematicDataModel/";
  private String apikey;
  private Client client;

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

  private WebTarget schematicWebTarget;
  private String tenant;

  public static class TrustAllHostNameVerifier implements HostnameVerifier {

    public boolean verify(String hostname, SSLSession session) {
      return true;
    }

  }

  public SchematicClient(String baseUrl, String tenant, String username, String password, String apikey)
      throws NoSuchAlgorithmException, KeyManagementException {
    SSLContext ctx = SSLContext.getInstance("SSL");
    ctx.init(null, certs, new SecureRandom());

    client = ClientBuilder.newBuilder().hostnameVerifier(new TrustAllHostNameVerifier()).sslContext(ctx).build();
    client.register(new BasicAuthenticator(username, password));
    client.register(MultiPartFeature.class);

    WebTarget webTarget = client.target(baseUrl).path("/rest/v1/apps/");
    schematicWebTarget = webTarget.path("SchematicApplication/module/SchematicModule/model/SchematicDataModel/");
    this.apikey = apikey;
    this.tenant = tenant;
  }

  public SchematicDataModel get(String id) {
    return addHeader(schematicWebTarget.path(id).request(MediaType.APPLICATION_JSON)).get(SchematicDataModel.class);
  }

  public SchematicDataModel post(SchematicDataModel schematicDataModel) {
    String id = null;
    Response response = addHeader(schematicWebTarget.request(MediaType.APPLICATION_JSON))
        .post(Entity.entity(schematicDataModel, MediaType.APPLICATION_JSON));
    if (response.getStatus() == 201) {
      return response.readEntity(SchematicDataModel.class);
    } else {
      String readEntity = response.readEntity(String.class);
      System.out.println(response.getStatusInfo());
      System.out.println(readEntity);

      throw new ProcessingException("model not created.");
    }
  }

  private Builder addHeader(Builder builder) {
    builder = builder.header("X-mcs-apikey", apikey);
    if (StringUtils.isNotEmpty(tenant)) {
      builder = builder.header("X-mcs-tenant", tenant);
    }
    return builder;
  }

  public SchematicDataModel put(SchematicDataModel model) {
    String id = null;
    Response response = addHeader(schematicWebTarget.path(model.getId()).request(MediaType.APPLICATION_JSON))
        .put(Entity.entity(model, MediaType.APPLICATION_JSON));
    if (response.getStatus() == 200) {
      SchematicDataModel entity = response.readEntity(SchematicDataModel.class);
      return entity;
    } else {
      String readEntity = response.readEntity(String.class);
      System.out.println(response.getStatusInfo());
      System.out.println(readEntity);

      throw new ProcessingException("model not updated.");
    }
  }

  public SchematicDataModel postBlob(SchematicDataModel model, String fieldname, File file,
      Map<String, Object> properties) {
    String id = null;
    try (InputStream in = new FileInputStream(file)) {
      FormDataMultiPart multiPartEntity = new FormDataMultiPart().field("file", in,
          MediaType.APPLICATION_OCTET_STREAM_TYPE);

      Builder builder = addHeader(
          schematicWebTarget.path(model.getId()).path(fieldname).request(MediaType.APPLICATION_JSON));
      if (properties != null) {
        for (Entry<String, Object> property : properties.entrySet()) {
          builder.header("X-mcs-" + property.getKey(), property.getValue());
        }
      }
      Response response = builder.post(Entity.entity(multiPartEntity, multiPartEntity.getMediaType()));
      if (response.getStatus() == 201) {
        BlobDescription entity = response.readEntity(BlobDescription.class);
        model.setFile(entity);
        return put(model);
      } else {
        throw new ProcessingException("model not updated.");
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  public InputStream getBlob(SchematicDataModel model, String fieldname) {
    return addHeader(schematicWebTarget.path(model.getId()).path(fieldname).request(MediaType.APPLICATION_OCTET_STREAM))
        .get(InputStream.class);
  }

  public List<SchematicDataModel> find(String query) {
    try {
      String queryURL;
      queryURL = URLEncoder.encode(query, "UTF-8");
      SchematicDataModel[] schematicDataModels = addHeader(
          schematicWebTarget.queryParam("q", queryURL).request(MediaType.APPLICATION_JSON))
              .get(SchematicDataModel[].class);
      return Arrays.asList(schematicDataModels);
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return null;
  }
}
