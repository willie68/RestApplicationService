/**
 * 
 */
package de.mcs.microservice.schematic.client;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

import de.mcs.microservice.application.api.BaseModel;
import de.mcs.microservice.schematic.client.AbstractClient.TrustAllHostNameVerifier;

/**
 * @author w.klaas
 *
 */
public class ConfigClient {

  private String apikey;
  private String tenant;

  private WebTarget schematicWebTarget;
  private Client client;

  public ConfigClient(String baseUrl) throws NoSuchAlgorithmException, KeyManagementException {
    SSLContext ctx = SSLContext.getInstance("SSL");
    ctx.init(null, AbstractClient.certs, new SecureRandom());

    client = ClientBuilder.newBuilder().hostnameVerifier(new TrustAllHostNameVerifier()).sslContext(ctx).build();
    client.register(MultiPartFeature.class);

    WebTarget webTarget = getWebTarget(baseUrl);
    schematicWebTarget = webTarget.path("/service/config/apps/");
  }

  WebTarget getWebTarget(String baseUrl) {
    return client.target(baseUrl);
  }

  public List<String> getAppNames() {
    return addHeader(schematicWebTarget.request(MediaType.APPLICATION_JSON)).get(List.class);
  }

  public BaseModel getApp(String appName) {
    return addHeader(schematicWebTarget.path(appName).request(MediaType.APPLICATION_JSON)).get(BaseModel.class);
  }

  public Response addTenant(String appName, String tenant) {
    BaseModel tenantModel = new BaseModel();
    tenantModel.setKeyValue("tenant", tenant);
    return addHeader(schematicWebTarget.path(appName).path("tenants").request(MediaType.APPLICATION_JSON))
        .post(Entity.entity(tenantModel, MediaType.APPLICATION_JSON));
  }

  private Builder addHeader(Builder builder) {
    builder = builder.header("X-mcs-apikey", apikey);
    if (StringUtils.isNotEmpty(tenant)) {
      builder = builder.header("X-mcs-tenant", tenant);
    }
    return builder;
  }

}
