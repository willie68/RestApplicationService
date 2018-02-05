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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataMultiPart;

import com.fasterxml.jackson.core.JsonProcessingException;

import de.mcs.microservice.application.api.BlobDescription;
import de.mcs.microservice.application.client.BasicAuthenticator;
import de.mcs.microservice.schematic.Schematic;
import de.mcs.microservice.schematic.SchematicTags;
import de.mcs.microservice.utils.JacksonUtils;

/**
 * @author w.klaas
 *
 */
public class SchematicTagsClient extends AbstractClient<SchematicTags> {

  public SchematicTagsClient(String baseUrl, String tenant, String username, String password, String apikey)
      throws NoSuchAlgorithmException, KeyManagementException {
    super(baseUrl, tenant, apikey, SchematicTags.class);
    client.register(new BasicAuthenticator(username, password));

    setWebTarget(getBaseWebTarget().path("SchematicApplication/module/SchematicModule/model/Schematic/"));
  }

  public Schematic post(Schematic schematic) {
    String id = null;
    try {
      String json = JacksonUtils.getJsonMapper().writeValueAsString(schematic);
      Response response = addHeader(getWebTarget().request(MediaType.APPLICATION_JSON))
          .post(Entity.entity(json, MediaType.APPLICATION_JSON));
      if (response.getStatus() == 201) {
        return response.readEntity(Schematic.class);
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

  public Schematic put(Schematic model) {
    String id = null;
    try {
      String json = JacksonUtils.getJsonMapper().writeValueAsString(model);
      Response response = addHeader(getWebTarget().path(model.getId()).request(MediaType.APPLICATION_JSON))
          .put(Entity.entity(json, MediaType.APPLICATION_JSON));
      if (response.getStatus() == 200) {
        Schematic entity = response.readEntity(Schematic.class);
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

  public Schematic postBlob(Schematic model, String fieldname, File file, Map<String, Object> properties) {
    String id = null;
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

  public InputStream getBlob(Schematic model, String fieldname) {
    return addHeader(getWebTarget().path(model.getId()).path(fieldname).request(MediaType.APPLICATION_OCTET_STREAM))
        .get(InputStream.class);
  }

  public List<Schematic> find(String query) {
    try {
      String queryURL;
      queryURL = URLEncoder.encode(query, "UTF-8");
      Schematic[] schematic = addHeader(getWebTarget().queryParam("q", queryURL).request(MediaType.APPLICATION_JSON))
          .get(Schematic[].class);
      return Arrays.asList(schematic);
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return null;
  }

}
