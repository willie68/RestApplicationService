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
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;

import de.mcs.microservice.schematic.client.SchematicClient;
import de.mcs.utils.Files;
import de.mcs.utils.StreamHelper;

/**
 * @author w.klaas
 *
 */
public class TestSchematicPerformance {

  private static final int DATALENGTH = 1024;
  private SchematicClient client;
  private ThreadPoolExecutor executor;
  private File tmp;

  @Before
  public void before() throws KeyManagementException, NoSuchAlgorithmException, IOException {
    client = new SchematicClient("https://127.0.0.1:8443", "wkla", "w.klaas@gmx.de", "akteon00",
        "cce0ef23-c0bf-4a25-b871-1219f482d863");
    executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
    tmp = new File("tmp");
    if (tmp.exists()) {
      Files.remove(tmp, true);
    }
    tmp.mkdirs();
    assertTrue(tmp.exists());
  }

  @Test
  public void testPostBlob()
      throws KeyManagementException, NoSuchAlgorithmException, IOException, InterruptedException {
    testModel(0);
    Thread.sleep(1000);
    for (int i = 1; i <= 1000; i++) {
      final int count = i;
      executor.execute(new Runnable() {

        @Override
        public void run() {
          try {
            testModel(count);
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      });
    }
    executor.shutdown();
    executor.awaitTermination(10, TimeUnit.MINUTES);
  }

  private void testModel(int i) throws IOException {
    SchematicDataModel schematicDataModel = new SchematicDataModel();
    schematicDataModel.setFilename("filename" + Integer.toString(i));
    schematicDataModel.setSchematicName("schematicName" + Integer.toString(i));
    schematicDataModel.setTags(Arrays.asList(new String[] { "tube", "amp", "count" + Integer.toString(i) }));

    SchematicDataModel newModel = client.post(schematicDataModel);
    assertNotNull(newModel);
    System.out.printf("%d: id:%s\r\n", i, newModel.getId());

    File orgFile = new File(tmp, String.format("%d.bin", i));
    try (OutputStream out = new BufferedOutputStream(new FileOutputStream(orgFile))) {
      Random random = new Random(System.currentTimeMillis());
      byte[] data = new byte[DATALENGTH];
      for (int j = 0; j < data.length; j++) {
        data[i] = (byte) random.nextInt(255);
      }
      out.write(data);
      out.close();
    }

    client.postBlob(newModel, "file", orgFile, null);
    String orgSHAFile = Files.computeSHAFromFile(orgFile);

    File srvFile = new File(tmp, String.format("%d.bin2", i));

    try (InputStream in = client.getBlob(newModel, "file")) {
      try (OutputStream out = new BufferedOutputStream(new FileOutputStream(srvFile))) {
        long written = StreamHelper.copyStream(in, out);
        assertEquals(DATALENGTH, written);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    String srvSHAFile = Files.computeSHAFromFile(srvFile);

    assertEquals(orgSHAFile, srvSHAFile);

    srvFile.delete();
    orgFile.delete();
  }

}
