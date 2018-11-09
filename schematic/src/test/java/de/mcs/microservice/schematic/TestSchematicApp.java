/**
 * 
 */
package de.mcs.microservice.schematic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;

import de.mcs.microservice.application.api.BaseModel;
import de.mcs.microservice.schematic.client.ConfigClient;
import de.mcs.microservice.schematic.client.Connection;
import de.mcs.microservice.schematic.client.SchematicClient;

/**
 * @author w.klaas
 *
 */
public class TestSchematicApp {

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

  @Test
  public void testPostAddTags() throws JsonProcessingException, InterruptedException {
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
}
