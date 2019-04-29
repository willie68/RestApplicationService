/**
 * 
 */
package de.mcs.microservice.application.storage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.mcs.jmeasurement.MeasureFactory;
import de.mcs.jmeasurement.Monitor;
import de.mcs.jmeasurement.renderer.DefaultHTMLRenderer;
import de.mcs.microservice.application.ConfigStorageConfig;
import de.mcs.utils.Files;

/**
 * @author w.klaas
 *
 */
public class TestNitriteStorage {

  private static NitriteStorage storage;

  /**
   * @throws java.lang.Exception
   */
  @BeforeClass
  public static void beforeClass() throws Exception {
    org.apache.log4j.BasicConfigurator.configure();
    File tmpData = new File("tmp/data");
    Files.remove(tmpData, true);

    storage = new NitriteStorage();
    ConfigStorageConfig config = new ConfigStorageConfig();
    config.setStorageClass(NitriteStorage.class.getName());
    config.set(NitriteStorage.KEY_STORAGE_PATH, "tmp/data/");
    config.set(NitriteStorage.KEY_STORAGE_USER, "appUser");
    config.set(NitriteStorage.KEY_STORAGE_PASSWORD, "f7jMA6OxoL");
    storage.initialise(config);
  }

  /**
   * @throws java.lang.Exception
   */
  @AfterClass
  public static void afterClass() throws Exception {
    storage.close();
    String report = MeasureFactory.getReport(new DefaultHTMLRenderer());
    Files.writeStringToFile(new File("report.html"), report);
  }

  @Test
  public void testSimpleCRUD() {
    String name = "testValue1";
    Object object = storage.get(name);
    assertNull(object);

    storage.save(name, "testvalue1");

    object = storage.get(name);
    assertNotNull(object);
    assertEquals("testvalue1", object.toString());

    storage.save(name, "testvalue2");

    object = storage.get(name);
    assertNotNull(object);
    assertEquals("testvalue2", object.toString());

    Object delete = storage.delete(name);
    assertNotNull(delete);
    assertEquals("testvalue2", delete.toString());

    object = storage.get(name);
    assertNull(object);
  }

  @Test
  public void testGetEmpty() {
    String name = "testGetEmpty";
    Object object = storage.get(name);
    assertNull(object);
  }

  @Test
  public void testChangeType() {
    String name = "testChangeType";
    storage.save(name, "testvalue1");

    Object object = storage.get(name);
    assertNotNull(object);
    assertEquals("testvalue1", object.toString());

    storage.save(name, new Long(123456789l));

    object = storage.get(name);
    assertNotNull(object);
    assertEquals(123456789l, object);
  }

  @Test
  public void testPerformaceSimple() {

    Monitor startPerformace = MeasureFactory.start("testPerformaceSimple");
    try {
      System.out.println("write");
      Monitor writeAll = MeasureFactory.start("writeAll");
      try {
        for (int i = 0; i < 100000; i++) {
          String name = String.format("key%05d", i);
          Monitor writeOnce = MeasureFactory.start("writeOnce");
          storage.save(name, name);
          writeOnce.stop();
        }
      } finally {
        writeAll.stop();
      }

      System.out.println("read");
      Monitor readAll = MeasureFactory.start("readAll");
      try {
        for (int i = 0; i < 100000; i++) {
          String name = String.format("key%05d", i);
          Monitor readOnce = MeasureFactory.start("readOnce");
          Object object = storage.get(name);
          readOnce.stop();
          assertNotNull(object);
        }
      } finally {
        readAll.stop();
      }

      System.out.println("change");
      Monitor changeAll = MeasureFactory.start("changeAll");
      try {
        for (int i = 0; i < 100000; i++) {
          String name = String.format("key%05d", i);
          String value = String.format("key%05dnew", i);
          Monitor changeOnce = MeasureFactory.start("changeOnce");
          storage.save(name, value);
          changeOnce.stop();
        }
      } finally {
        changeAll.stop();
      }

      System.out.println("delete");
      Monitor deleteAll = MeasureFactory.start("deleteAll");
      try {
        for (int i = 0; i < 100000; i++) {
          String name = String.format("key%05d", i);
          Monitor deleteOnce = MeasureFactory.start("deleteOnce");
          Object object = storage.delete(name);
          deleteOnce.stop();
          assertNotNull(object);
        }
      } finally {
        deleteAll.stop();
      }
    } finally {
      startPerformace.stop();
    }
  }

}
