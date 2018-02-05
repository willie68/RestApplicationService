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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

import de.mcs.microservice.application.api.BaseModel;
import de.mcs.microservice.application.api.BlobDescription;
import de.mcs.microservice.schematic.SchematicDataModel;

/**
 * @author w.klaas
 *
 */
public class ConfigClient extends AbstractClient {

  private String apikey;
  private String tenant;

  private WebTarget schematicWebTarget;

  public ConfigClient(String baseUrl) throws NoSuchAlgorithmException, KeyManagementException {
    super();
    // client.register(new BasicAuthenticator(username, password));

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
