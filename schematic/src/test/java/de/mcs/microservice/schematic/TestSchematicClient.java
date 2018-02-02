/**
 * 
 */
package de.mcs.microservice.schematic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;

import de.mcs.microservice.application.query.SimpleQuery;
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

  @Before
  public void before() throws KeyManagementException, NoSuchAlgorithmException {
    client = new SchematicClient("https://127.0.0.1:8443", "wkla", "w.klaas@gmx.de", "akteon00",
        "cce0ef23-c0bf-4a25-b871-1219f482d863");
  }

  @Test(expected = NotFoundException.class)
  public void testGet() {
    SchematicDataModel schematicDataModel = client.get("e78d2252-3a50-421e-abc2-98031c999ed7");
  }

  @Test
  public void testPost() throws JsonProcessingException, InterruptedException {
    Random random = new Random(System.currentTimeMillis());
    SchematicDataModel schematicDataModel = new SchematicDataModel();
    schematicDataModel.setFilename("filename");
    schematicDataModel.setSchematicName("schematicName");
    schematicDataModel.setTags(Arrays.asList(new String[] { "tube", "amp" }));

    SchematicDataModel newModel = client.post(schematicDataModel);
    assertNotNull(newModel);
    System.out.printf("id:%s\r\n", newModel.getId());

    schematicDataModel = newModel;
    schematicDataModel.setFilename("filename2");
    schematicDataModel.setSchematicName("schematicName2");
    schematicDataModel.setTags(Arrays.asList(new String[] { "tube2", "amp2" }));

    SchematicDataModel putModel = client.put(schematicDataModel);
    assertNotNull(putModel);
    System.out.printf("id:%s\r\n", putModel.getId());

    SchematicDataModel model2 = client.get(schematicDataModel.getId());
    assertNotNull(model2);
    System.out.printf("id:%s\r\n", model2.getId());
    assertEquals(schematicDataModel.getId(), model2.getId());
    assertEquals(schematicDataModel.getFilename(), model2.getFilename());
    assertEquals(schematicDataModel.getSchematicName(), model2.getSchematicName());
  }

  @Test
  public void testPostBlob() {
    SchematicDataModel schematicDataModel = new SchematicDataModel();
    schematicDataModel.setFilename("filename");
    schematicDataModel.setSchematicName("schematicName");
    schematicDataModel.setTags(Arrays.asList(new String[] { "tube", "amp" }));

    SchematicDataModel newModel = client.post(schematicDataModel);
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
  }

  @Test
  public void testSearch() throws JsonProcessingException, InterruptedException {
    Random random = new Random(System.currentTimeMillis());
    int nextInt = random.nextInt();
    String filename = String.format("file%d", nextInt);
    String schematicName = String.format("schematic%d", nextInt);
    SchematicDataModel schematicDataModel = new SchematicDataModel();
    schematicDataModel.setFilename(filename);
    schematicDataModel.setSchematicName(schematicName);
    schematicDataModel.setTags(Arrays.asList(new String[] { "tube", "amp", Integer.toString(nextInt) }));

    SchematicDataModel newModel = client.post(schematicDataModel);
    assertNotNull(newModel);
    System.out.printf("id:%s\r\n", newModel.getId());

    Thread.sleep(1000);

    SimpleQuery simpleQuery = new SimpleQuery();
    simpleQuery.set("filename", schematicDataModel.getFilename());
    String query = JacksonUtils.getJsonMapper().writeValueAsString(simpleQuery);

    List<SchematicDataModel> models = client.find(query);
    assertNotNull(models);
    assertEquals(1, models.size());

    assertEquals(newModel.getId(), models.get(0).getId());
    assertEquals(newModel.getFilename(), models.get(0).getFilename());
    assertEquals(newModel.getSchematicName(), models.get(0).getSchematicName());

    simpleQuery = new SimpleQuery();
    simpleQuery.set("schematicName", schematicDataModel.getSchematicName());
    query = JacksonUtils.getJsonMapper().writeValueAsString(simpleQuery);

    models = client.find(query);
    assertNotNull(models);
    assertEquals(1, models.size());

    assertEquals(newModel.getId(), models.get(0).getId());

    simpleQuery = new SimpleQuery();
    simpleQuery.set("schematicName", schematicDataModel.getSchematicName());
    simpleQuery.set("filename", schematicDataModel.getFilename());
    query = JacksonUtils.getJsonMapper().writeValueAsString(simpleQuery);

    models = client.find(query);
    assertNotNull(models);
    assertEquals(1, models.size());

    assertEquals(newModel.getId(), models.get(0).getId());
  }
}
