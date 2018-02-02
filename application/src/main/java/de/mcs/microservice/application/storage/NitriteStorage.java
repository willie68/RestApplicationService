/**
 * 
 */
package de.mcs.microservice.application.storage;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import org.dizitart.no2.Document;
import org.dizitart.no2.IndexOptions;
import org.dizitart.no2.IndexType;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.NitriteCollection;
import org.dizitart.no2.filters.Filters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mcs.microservice.application.ConfigStorageConfig;

/**
 * @author w.klaas
 *
 */
public class NitriteStorage implements ConfigStorage {

  public static final String KEY_STORAGE_PATH = "storagePath";
  public static final String KEY_STORAGE_USER = "user";
  public static final String KEY_STORAGE_PASSWORD = "password";

  private static final String FIELD_DATA = "data";
  private static final String FIELD_NAME = "name";
  private NitriteCollection collection;
  private Nitrite db;
  private Logger log = LoggerFactory.getLogger(getClass());

  @Override
  public void initialise(ConfigStorageConfig config) {
    String storageDb = (String) config.any().getOrDefault(KEY_STORAGE_PATH, "storage");
    String dbUser = (String) config.any().getOrDefault(KEY_STORAGE_USER, "appUser");
    String dbPassword = (String) config.any().getOrDefault(KEY_STORAGE_PASSWORD, "f7jMA6OxoL");

    File storageDbFile = new File(storageDb, "config.db");
    storageDbFile.getParentFile().mkdirs();

    db = Nitrite.builder().compressed().filePath(storageDbFile).openOrCreate(dbUser, dbPassword);
    db.compact();
    collection = db.getCollection("config");
    if (!collection.hasIndex(FIELD_NAME)) {
      collection.createIndex(FIELD_NAME, IndexOptions.indexOptions(IndexType.Unique));
    }
    Timer timer = new Timer("background", true);
    timer.schedule(new TimerTask() {

      @Override
      public void run() {
        db.commit();
      }
    }, 10000, 10000);
  }

  @Override
  public void save(String name, Object value) {
    Document document = collection.find(Filters.eq(FIELD_NAME, name)).firstOrDefault();
    if (document != null) {
      document.put(FIELD_DATA, value);
      collection.update(document);
    } else {
      document = new Document();
      document.put(FIELD_NAME, name);
      document.put(FIELD_DATA, value);
      collection.insert(document);
    }
  }

  @Override
  public Object get(String name) {
    Document document = collection.find(Filters.eq(FIELD_NAME, name)).firstOrDefault();
    if (document == null) {
      return null;
    }
    return document.get(FIELD_DATA);
  }

  @Override
  public Object delete(String name) {
    Document document = collection.find(Filters.eq(FIELD_NAME, name)).firstOrDefault();
    if (document == null) {
      return null;
    }
    collection.remove(document);
    return document.get(FIELD_DATA);
  }

  public void close() {
    db.commit();
    db.close();
  }

}
