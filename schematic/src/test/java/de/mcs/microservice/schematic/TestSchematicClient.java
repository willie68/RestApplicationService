/**
 * 
 */
package de.mcs.microservice.schematic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;

import de.mcs.microservice.application.api.BaseModel;
import de.mcs.microservice.application.query.SimpleQuery;
import de.mcs.microservice.schematic.client.ConfigClient;
import de.mcs.microservice.schematic.client.Connection;
import de.mcs.microservice.schematic.client.SchematicClient;
import de.mcs.microservice.utils.JacksonUtils;
import de.mcs.utils.Files;
import de.mcs.utils.StreamHelper;

/**
 * @author w.klaas
 *
 */
public class TestSchematicClient {

  private SchematicClient client;
  private ConfigClient configClient;

  @Before
  public void before() throws KeyManagementException, NoSuchAlgorithmException {
    configClient = new ConfigClient("https://127.0.0.1:8444");
    List<String> appNames = configClient.getAppNames();
    assertTrue(appNames.contains("SchematicApplication"));
    BaseModel appConfig = configClient.getApp("SchematicApplication");
    String apikey = appConfig.getFieldValueAsString("apikey");
    List<String> tenants = (List<String>) appConfig.getField("tenants");
    if ((tenants == null) || (tenants.size() == 0) || !tenants.contains(Connection.TENANT)) {
      Response addTenant = configClient.addTenant("SchematicApplication", Connection.TENANT);
      assertNotNull(addTenant);
    }
    client = new SchematicClient(Connection.BASE_URL, Connection.TENANT, "w.klaas@gmx.de", "akteon00", apikey);
  }

  @Test(expected = NotFoundException.class)
  public void testGet() {
    Schematic schematic = client.get("e78d2252-3a50-421e-abc2-98031c999ed7");
  }

  @Test
  public void testPost() throws JsonProcessingException, InterruptedException {
    Random random = new Random(System.currentTimeMillis());
    Schematic schematic = new Schematic();
    schematic.setFilename("filename");
    schematic.setSchematicName("schematicName");
    schematic.setTags(Arrays.asList(new String[] { "tube", "amp" }));

    Schematic newModel = client.post(schematic);
    assertNotNull(newModel);
    System.out.printf("id:%s\r\n", newModel.getId());

    schematic = newModel;
    schematic.setFilename("filename2");
    schematic.setSchematicName("schematicName2");
    schematic.setTags(Arrays.asList(new String[] { "tube2", "amp2" }));

    Schematic putModel = client.put(schematic);
    assertNotNull(putModel);
    System.out.printf("id:%s\r\n", putModel.getId());

    Schematic model2 = client.get(schematic.getId());
    assertNotNull(model2);
    System.out.printf("id:%s\r\n", model2.getId());
    assertEquals(schematic.getId(), model2.getId());
    assertEquals(schematic.getFilename(), model2.getFilename());
    assertEquals(schematic.getSchematicName(), model2.getSchematicName());

    Schematic delModel = client.delete(schematic.getId());
    assertNotNull(delModel);
    System.out.printf("id:%s\r\n", delModel.getId());
    assertEquals(schematic.getId(), delModel.getId());
    assertEquals(schematic.getFilename(), delModel.getFilename());
    assertEquals(schematic.getSchematicName(), delModel.getSchematicName());

  }

  @Test
  public void testPostBlob() {
    Schematic schematic = new Schematic();
    schematic.setFilename("filename");
    schematic.setSchematicName("schematicName");
    schematic.setTags(Arrays.asList(new String[] { "tube", "amp" }));

    Schematic newModel = client.post(schematic);
    assertNotNull(newModel);
    System.out.printf("id:%s\r\n", newModel.getId());

    File orgFile = new File("Bild.pdf");
    newModel = client.postBlob(newModel, "file", orgFile, null);
    String orgSHAFile = Files.computeSHAFromFile(orgFile);
    System.out.printf("id:%s\r\n", newModel.getId());

    newModel = client.get(newModel.getId());
    System.out.printf("id:%s\r\n", newModel.getId());
    File srvFile = new File("blob.pdf");

    try (InputStream in = client.getBlob(newModel, "file")) {
      try (OutputStream out = new BufferedOutputStream(new FileOutputStream(srvFile))) {
        long written = StreamHelper.copyStream(in, out);
        System.out.printf("%d bytes written.\r\n", written);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    String srvSHAFile = Files.computeSHAFromFile(srvFile);

    assertEquals(orgSHAFile, srvSHAFile);

    Schematic delModel = client.delete(newModel.getId());
    assertNotNull(delModel);
    System.out.printf("id:%s\r\n", delModel.getId());
  }

  @Test
  public void testSearch() throws JsonProcessingException, InterruptedException {
    Random random = new Random(System.currentTimeMillis());
    int nextInt = random.nextInt();
    String filename = String.format("file%d", nextInt);
    String schematicName = String.format("schematic%d", nextInt);
    Schematic schematic = new Schematic();
    schematic.setFilename(filename);
    schematic.setSchematicName(schematicName);
    schematic.setTags(Arrays.asList(new String[] { "tube", "amp", Integer.toString(nextInt) }));

    Schematic newModel = client.post(schematic);
    assertNotNull(newModel);
    System.out.printf("id:%s\r\n", newModel.getId());

    Thread.sleep(1000);

    SimpleQuery simpleQuery = new SimpleQuery();
    simpleQuery.set("schematicName", schematic.getSchematicName());
    String query = JacksonUtils.getJsonMapper().writeValueAsString(simpleQuery);

    List<Schematic> models = client.find(query);
    assertNotNull(models);
    assertEquals(1, models.size());

    assertEquals(newModel.getId(), models.get(0).getId());
    assertEquals(newModel.getFilename(), models.get(0).getFilename());
    assertEquals(newModel.getSchematicName(), models.get(0).getSchematicName());

    simpleQuery = new SimpleQuery();
    simpleQuery.set("filename", schematic.getFilename());
    query = JacksonUtils.getJsonMapper().writeValueAsString(simpleQuery);

    models = client.find(query);
    assertNotNull(models);
    assertEquals(1, models.size());

    assertEquals(newModel.getId(), models.get(0).getId());

    simpleQuery = new SimpleQuery();
    simpleQuery.set("schematicName", schematic.getSchematicName());
    simpleQuery.set("filename", schematic.getFilename());
    query = JacksonUtils.getJsonMapper().writeValueAsString(simpleQuery);

    models = client.find(query);
    assertNotNull(models);
    assertEquals(1, models.size());

    assertEquals(newModel.getId(), models.get(0).getId());
  }
}
