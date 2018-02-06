package de.mcs.microservice.schematic.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
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
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

import com.fasterxml.jackson.core.JsonProcessingException;

import de.mcs.microservice.application.api.BlobDescription;
import de.mcs.microservice.application.core.model.RestDataModel;
import de.mcs.microservice.utils.JacksonUtils;

public class AbstractClient<T extends RestDataModel> {

  public static TrustManager[] certs = new TrustManager[] { new X509TrustManager() {
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
  private String baseUrl;
  private String apikey;
  private String tenant;
  private Class<T> dataclass;

  AbstractClient(String baseUrl, String tenant, String apikey, Class<T> dataclass)
      throws NoSuchAlgorithmException, KeyManagementException {
    SSLContext ctx = SSLContext.getInstance("SSL");
    ctx.init(null, certs, new SecureRandom());

    client = ClientBuilder.newBuilder().hostnameVerifier(new TrustAllHostNameVerifier()).sslContext(ctx).build();
    client.register(MultiPartFeature.class);

    this.baseUrl = baseUrl;
    this.apikey = apikey;
    this.tenant = tenant;
    this.dataclass = dataclass;
  }

  public WebTarget getBaseWebTarget() {
    return client.target(baseUrl).path("/rest/v1/apps/");
  }

  private WebTarget webTarget;

  /**
   * @return the webTarget
   */
  public WebTarget getWebTarget() {
    return webTarget;
  }

  /**
   * @param webTarget
   *          the webTarget to set
   */
  public void setWebTarget(WebTarget webTarget) {
    this.webTarget = webTarget;
  }

  public Builder addHeader(Builder builder) {
    builder = builder.header("X-mcs-apikey", apikey);
    if (StringUtils.isNotEmpty(tenant)) {
      builder = builder.header("X-mcs-tenant", tenant);
    }
    return builder;
  }

  public T get(String id) {
    return addHeader(getWebTarget().path(id).request(MediaType.APPLICATION_JSON)).get(dataclass);
  }

  public T delete(String id) {
    return addHeader(getWebTarget().path(id).request(MediaType.APPLICATION_JSON)).delete(dataclass);
  }

  public T post(T model) {
    try {
      String json = JacksonUtils.getJsonMapper().writeValueAsString(model);
      Response response = addHeader(getWebTarget().request(MediaType.APPLICATION_JSON))
          .post(Entity.entity(json, MediaType.APPLICATION_JSON));
      if (response.getStatus() == 201) {
        return response.readEntity(dataclass);
      } else {
        String readEntity = response.readEntity(String.class);
        System.out.println(response.getStatusInfo());
        System.out.println(readEntity);
        throw new ProcessingException("model not created.");
      }
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return null;
  }

  public T put(T model) {
    try {
      String json = JacksonUtils.getJsonMapper().writeValueAsString(model);
      Response response = addHeader(getWebTarget().path(model.getId()).request(MediaType.APPLICATION_JSON))
          .put(Entity.entity(json, MediaType.APPLICATION_JSON));
      if (response.getStatus() == 200) {
        T entity = response.readEntity(dataclass);
        return entity;
      } else {
        String readEntity = response.readEntity(String.class);
        System.out.println(response.getStatusInfo());
        System.out.println(readEntity);

        throw new ProcessingException("model not updated.");
      }
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return null;
  }

  public T postBlob(T model, String fieldname, File file, Map<String, Object> properties) {
    try (InputStream in = new FileInputStream(file)) {
      FormDataMultiPart multiPartEntity = new FormDataMultiPart().field("file", in,
          MediaType.APPLICATION_OCTET_STREAM_TYPE);

      Builder builder = addHeader(
          getWebTarget().path(model.getId()).path(fieldname).request(MediaType.APPLICATION_JSON));
      if (properties != null) {
        for (Entry<String, Object> property : properties.entrySet()) {
          builder.header("X-mcs-" + property.getKey(), property.getValue());
        }
      }
      Response response = builder.post(Entity.entity(multiPartEntity, multiPartEntity.getMediaType()));
      if (response.getStatus() == 201) {
        BlobDescription entity = response.readEntity(BlobDescription.class);
        model.set(fieldname, entity);
        return put(model);
      } else {
        throw new ProcessingException("model not updated.");
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  public InputStream getBlob(T model, String fieldname) {
    return addHeader(getWebTarget().path(model.getId()).path(fieldname).request(MediaType.APPLICATION_OCTET_STREAM))
        .get(InputStream.class);
  }

  public List<T> find(String query) {
    try {
      String queryURL;
      queryURL = URLEncoder.encode(query, "UTF-8");
      ParameterizedType parameterizedGenericType = new ParameterizedType() {
        public Type[] getActualTypeArguments() {
          return new Type[] { dataclass };
        }

        public Type getRawType() {
          return List.class;
        }

        public Type getOwnerType() {
          return List.class;
        }
      };

      GenericType<List<T>> gt = new GenericType<List<T>>(parameterizedGenericType) {
      };

      List<T> models = addHeader(getWebTarget().queryParam("q", queryURL).request(MediaType.APPLICATION_JSON)).get(gt);
      return models;
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return null;
  }

}
