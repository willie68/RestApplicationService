package de.mcs.microservice.application.storage;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.dizitart.no2.Document;
import org.dizitart.no2.Filter;
import org.dizitart.no2.IndexOptions;
import org.dizitart.no2.IndexType;
import org.dizitart.no2.Nitrite;
import org.dizitart.no2.NitriteCollection;
import org.dizitart.no2.WriteResult;
import org.dizitart.no2.filters.Filters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import de.mcs.jmeasurement.MeasureFactory;
import de.mcs.jmeasurement.Monitor;
import de.mcs.microservice.application.ConfigStorageConfig;
import de.mcs.microservice.application.api.BlobDescription;
import de.mcs.microservice.application.core.AbstractDataStorage;
import de.mcs.microservice.application.core.AbstractRestDataModel;
import de.mcs.microservice.application.core.model.ApplicationConfig;
import de.mcs.microservice.application.core.model.Context;
import de.mcs.microservice.application.core.model.DataModelConfig;
import de.mcs.microservice.application.core.model.DataStorage;
import de.mcs.microservice.application.core.model.FieldConfig;
import de.mcs.microservice.application.core.model.ModuleConfig;
import de.mcs.microservice.application.core.model.RestDataModel;
import de.mcs.microservice.application.query.QuerySytaxException;
import de.mcs.microservice.application.query.SimpleQuery;
import de.mcs.microservice.application.query.Tokenizer;
import de.mcs.microservice.application.query.Tokenizer.TOKEN_OP;
import de.mcs.microservice.application.query.Tokenizer.Token;
import de.mcs.microservice.utils.JacksonUtils;
import de.mcs.utils.StreamHelper;

public class NitriteDataStorage<T extends RestDataModel> extends AbstractDataStorage<T> implements DataStorage<T> {

  public static final String KEY_STORAGE_PATH = "storagePath";
  public static final String KEY_STORAGE_USER = "user";
  public static final String KEY_STORAGE_PASSWORD = "password";

  private static Map<String, Nitrite> databases = new HashMap<>();
  private static final String FIELD_DATA = "data";
  private static final String FIELD_NAME = "name";
  private static final String FIELD_ID = "id";
  private static final String FIELD_ID_NAME = "idName";
  private static final String FIELD_CONTENT_LENGTH = "contentLength";
  private static final String FIELD_CONTENT_TYPE = "contentType";
  private static final String FIELD_CREATION_DATE = "creationDate";
  private static final String FIELD_FILENAME = "filename";

  private static File baseStorage;

  private Logger log = LoggerFactory.getLogger(this.getClass());
  private ApplicationConfig appConfig;
  private Class dataType;
  private boolean initialised = false;
  private String dbUser;
  private String dbPassword;

  public NitriteDataStorage() {
  }

  public void initialise(ConfigStorageConfig config, ApplicationConfig appConfig, Class dataType) {
    this.dataType = dataType;
    this.appConfig = appConfig;
    String storageDbStr = (String) config.any().getOrDefault(KEY_STORAGE_PATH, "storage");
    dbUser = (String) config.any().getOrDefault(KEY_STORAGE_USER, "appUser");
    dbPassword = (String) config.any().getOrDefault(KEY_STORAGE_PASSWORD, "f7jMA6OxoL");

    String appName = appConfig.getName();
    if (baseStorage == null) {
      baseStorage = new File(storageDbStr, appName);
      baseStorage.mkdirs();
    }
    initialised = true;
  }

  private Nitrite getDb(Context context) {
    String appName = context.getApplicationName();
    String tenant = context.getTenant();
    String dbKey = appName;
    if (StringUtils.isNotEmpty(tenant)) {
      dbKey = String.format("%s_%s", appName, tenant);
    }
    if (databases.containsKey(dbKey)) {
      return databases.get(dbKey);
    }

    File storageDb = baseStorage;
    if (StringUtils.isNotEmpty(tenant)) {
      storageDb = new File(baseStorage, tenant);
    }
    storageDb.mkdirs();

    File lucenePath = new File(storageDb, String.format("%s_lucene", appName));
    lucenePath.mkdirs();

    File storageDbFile = new File(storageDb, String.format("data.db", appName));

    Nitrite db = Nitrite.builder().filePath(storageDbFile).textIndexingService(new LuceneService(lucenePath))
        .openOrCreate(dbUser, dbPassword);
    db.compact();
    createDefaultIndexes(db);
    createFulltextIndexes(db);

    Timer timer = new Timer("background", true);
    timer.schedule(new TimerTask() {

      @Override
      public void run() {
        db.commit();
      }
    }, 10000, 10000);
    databases.put(dbKey, db);
    return db;
  }

  private NitriteCollection getCollection(Context context) {
    Nitrite db = getDb(context);
    NitriteCollection collection = db.getCollection(dataType.getSimpleName());
    return collection;
  }

  private NitriteCollection getBlobCollection(Context context) {
    Nitrite db = getDb(context);
    NitriteCollection blobCollection = db.getCollection(String.format("blob_%s", dataType.getSimpleName()));
    return blobCollection;
  }

  private File getBlobPath(Context context) {
    File blobPath = baseStorage;
    if (StringUtils.isNotEmpty(context.getTenant())) {
      blobPath = new File(blobPath, context.getTenant());
    }
    blobPath = new File(blobPath, "blob");
    blobPath.mkdirs();
    return blobPath;
  }

  private void createDefaultIndexes(Nitrite db) {
    NitriteCollection collection = db.getCollection(dataType.getSimpleName());
    NitriteCollection blobCollection = db.getCollection(String.format("blob_%s", dataType.getSimpleName()));
    if (!collection.hasIndex(FIELD_ID)) {
      collection.createIndex(FIELD_ID, IndexOptions.indexOptions(IndexType.Unique));
    }
    for (String index : AbstractRestDataModel.INDEXES) {
      if (!collection.hasIndex(index)) {
        collection.createIndex(index, IndexOptions.indexOptions(IndexType.NonUnique));
      }
    }
    if (!blobCollection.hasIndex(FIELD_ID_NAME)) {
      blobCollection.createIndex(FIELD_ID_NAME, IndexOptions.indexOptions(IndexType.Unique));
    }
  }

  private void createFulltextIndexes(Nitrite db) {
    try {
      NitriteCollection collection = db.getCollection(dataType.getSimpleName());
      RestDataModel testmodel = (T) dataType.newInstance();
      Map<String, ModuleConfig> modules = appConfig.getModules();
      ModuleConfig module = modules.get(testmodel.getModuleName());
      if (module != null) {
        Map<String, DataModelConfig> models = module.getDataModels();
        DataModelConfig model = models.get(testmodel.getModelName());
        if (model != null) {
          List<FieldConfig> indexFields = model.getIndexFields();
          for (FieldConfig field : indexFields) {
            if (!collection.hasIndex(field.getName())) {
              if (field.isFulltext()) {
                collection.createIndex(field.getName(), IndexOptions.indexOptions(IndexType.Fulltext, false));
              } else {
                collection.createIndex(field.getName(), IndexOptions.indexOptions(IndexType.NonUnique, false));
              }
            }
          }
        }
      }
    } catch (InstantiationException | IllegalAccessException e) {
      e.printStackTrace();
    }
  }

  @Override
  public String create(T model, Context context) {
    Monitor monitor = MeasureFactory.start(this, "create");
    try {
      model.setId(UUID.randomUUID().toString());
      Document document = convertDataModelToDocument(model);
      NitriteCollection collection = getCollection(context);
      try {
        WriteResult insertResult = collection.insert(document);
        return model.getId();
      } finally {
        collection.close();
      }
    } finally {
      monitor.stop();
    }
  }

  @Override
  public T read(String id, Context context) {
    Document document = null;
    Monitor monitor = MeasureFactory.start(this, "read");
    try {
      NitriteCollection collection = getCollection(context);
      try {
        document = collection.find(Filters.eq(FIELD_ID, id)).firstOrDefault();
        if (document == null) {
          return null;
        }
        return convertDocumentToDataModel(document);
      } finally {
        collection.close();
      }
    } finally {
      monitor.stop();
    }
  }

  @Override
  public boolean update(T model, Context context) {
    Monitor monitor = MeasureFactory.start(this, "update");
    try {
      NitriteCollection collection = getCollection(context);
      try {
        Document document = collection.find(Filters.eq(FIELD_ID, model.getId())).firstOrDefault();
        if (document == null) {
          return false;
        }
        for (Entry<String, Object> entry : model.any().entrySet()) {
          if (!entry.getKey().equals("_id")) {
            document.put(entry.getKey(), entry.getValue());
          }
        }
        WriteResult update = collection.update(document);
        return true;
      } finally {
        collection.close();
      }
    } finally {
      monitor.stop();
    }
  }

  @Override
  public T delete(String id, Context context) {
    Monitor monitor = MeasureFactory.start(this, "delete");
    try {
      NitriteCollection collection = getCollection(context);
      try {
        Document document = collection.find(Filters.eq(FIELD_ID, id)).firstOrDefault();
        if (document == null) {
          return null;
        }
        T model = convertDocumentToDataModel(document);
        WriteResult delete = collection.remove(document);
        if (delete.getAffectedCount() == 1) {
          return model;
        }
        return null;
      } finally {
        collection.close();
      }
    } finally {
      monitor.stop();
    }

  }

  @Override
  public List<T> find(String query, Context context) {
    Monitor monitor = MeasureFactory.start(this, "find");
    try {
      try {
        Filter filter = null;
        if (query != null) {
          if (query.startsWith("{")) {
            filter = parseSimpleQuery(query, filter, context);
          } else {
            filter = parseQueryToNitriteFilter(query);
          }
        }
        NitriteCollection collection = getCollection(context);
        try {
          List<Document> documents = collection.find(filter).toList();
          List<T> list = new ArrayList<>();
          documents.forEach(d -> list.add(convertDocumentToDataModel(d)));
          return list;
        } finally {
          collection.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      } catch (QuerySytaxException e) {
        e.printStackTrace();
      }
      return null;
    } finally {
      monitor.stop();
    }
  }

  private Filter parseQueryToNitriteFilter(String query) throws QuerySytaxException {
    Filter filter = null;
    List<Token> tokens = Tokenizer.tokenize(query);
    int level = 0;
    for (Token token : tokens) {
      if (token.is(TOKEN_OP.CP)) {
        level--;
      }
      if (token.is(TOKEN_OP.OP)) {
        level++;
      }
      if (token.is(TOKEN_OP.OP)) {
      } else if (token.is(TOKEN_OP.CP)) {
      } else {
      }
      if (token.is(TOKEN_OP.TEXT)) {
      }
    }
    return filter;
  }

  private Filter parseSimpleQuery(String query, Filter filter, Context context)
      throws IOException, JsonParseException, JsonMappingException {
    Map<String, FieldConfig> indexFields = getIndexFields(context);
    SimpleQuery simpleQuery = JacksonUtils.getJsonMapper().readValue(query, SimpleQuery.class);
    for (Entry<String, Object> entry : simpleQuery.any().entrySet()) {

      String fieldname = entry.getKey();
      Filter fieldFilter = null;

      if (indexFields.containsKey(fieldname)) {
        FieldConfig field = indexFields.get(fieldname);
        if (field.isFulltext()) {
          fieldFilter = Filters.text(fieldname, entry.getValue().toString());
        } else {
          fieldFilter = Filters.eq(fieldname, entry.getValue().toString());
        }
      }

      if (fieldFilter != null) {
        if (filter != null) {
          filter = Filters.and(filter, fieldFilter);
        } else {
          filter = fieldFilter;
        }
      }
    }
    return filter;
  }

  /**
   * @param context
   * @param indexFields
   * @return
   */
  private Map<String, FieldConfig> getIndexFields(Context context) {
    Map<String, FieldConfig> indexFields = new HashMap<>();
    try {
      RestDataModel testmodel = (T) dataType.newInstance();
      if (appConfig != null) {
        Map<String, ModuleConfig> modules = appConfig.getModules();
        ModuleConfig module = modules.get(testmodel.getModuleName());
        if (module != null) {
          Map<String, DataModelConfig> models = module.getDataModels();
          DataModelConfig model = models.get(testmodel.getModelName());
          if (model != null) {
            for (FieldConfig field : model.getIndexFields()) {
              indexFields.put(field.getName(), field);
            }
          }
        }
      }
    } catch (InstantiationException | IllegalAccessException e) {
      e.printStackTrace();
    }
    return indexFields;
  }

  public boolean isInitialised() {
    return initialised;
  }

  private Document convertDataModelToDocument(T model) {
    Document document = new Document();
    document.put(FIELD_ID, model.getId());
    for (Entry<String, Object> entry : model.any().entrySet()) {
      document.put(entry.getKey(), entry.getValue());
    }
    return document;
  }

  private T convertDocumentToDataModel(Document document) {
    try {
      T model = (T) dataType.newInstance();
      for (Entry<String, Object> entry : document.entrySet()) {
        if (entry.getKey().equals("_id")) {
          model.set("nitrite_id", entry.getValue());
        } else if (entry.getKey().equals("id")) {
          model.setId(entry.getValue().toString());
          model.set("_id", entry.getValue());
        } else {
          model.set(entry.getKey(), entry.getValue());
        }
      }
      return model;
    } catch (InstantiationException | IllegalAccessException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public BlobDescription saveBlob(T dbModel, String fieldname, BlobDescription blobDescription,
      InputStream fileInputStream, long contentLength, Context context) {
    Monitor monitor = MeasureFactory.start(this, "saveBlob");
    try {
      BlobDescription orgBlobDescription = getBlobDescription(dbModel, fieldname, context);
      if (orgBlobDescription != null) {
        deleteBlob(dbModel, fieldname, context);
      }
      String idName = buildBlobId(dbModel, fieldname);
      blobDescription.setBlobID(idName);
      Document document = convertBlobDescriptionToDocument(idName, blobDescription);
      NitriteCollection blobCollection = getBlobCollection(context);
      try {
        WriteResult insert = blobCollection.insert(document);

        if (insert.getAffectedCount() == 1) {
          File modelPath = new File(getBlobPath(context), dbModel.getId());
          modelPath.mkdirs();
          File blobFile = new File(modelPath, String.format("%s.bin", fieldname));
          try (OutputStream out = new BufferedOutputStream(new FileOutputStream(blobFile))) {
            StreamHelper.copyStream(fileInputStream, out);
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
        return blobDescription;
      } finally {
        blobCollection.close();
      }
    } finally {
      monitor.stop();
    }
  }

  @Override
  public BlobDescription getBlobDescription(T dbModel, String fieldname, Context context) {
    Monitor monitor = MeasureFactory.start(this, "getBlobDescription");
    try {
      String idName = buildBlobId(dbModel, fieldname);
      NitriteCollection blobCollection = getBlobCollection(context);
      try {
        Document document = blobCollection.find(Filters.eq(FIELD_ID_NAME, idName)).firstOrDefault();
        if (document == null) {
          return null;
        }
        return convertDocumentToBlobDescription(document);
      } finally {
        blobCollection.close();
      }
    } finally {
      monitor.stop();
    }
  }

  @Override
  public boolean hasBlob(T dbModel, String fieldname, Context context) {
    Monitor monitor = MeasureFactory.start(this, "hasBlob");
    try {
      String idName = buildBlobId(dbModel, fieldname);
      NitriteCollection blobCollection = getBlobCollection(context);
      try {
        Document document = blobCollection.find(Filters.eq(FIELD_ID_NAME, idName)).firstOrDefault();
        if (document == null) {
          return false;
        }
        return true;
      } finally {
        blobCollection.close();
      }
    } finally {
      monitor.stop();
    }
  }

  @Override
  public InputStream getBlobInputStream(T dbModel, String fieldname, Context context) {
    Monitor monitor = MeasureFactory.start(this, "getBlobInputStream");
    try {
      String idName = buildBlobId(dbModel, fieldname);
      File modelPath = new File(getBlobPath(context), dbModel.getId());
      modelPath.mkdirs();
      File blobFile = new File(modelPath, String.format("%s.bin", fieldname));
      if (blobFile.exists()) {
        try {
          return new BufferedInputStream(new FileInputStream(blobFile));
        } catch (FileNotFoundException e) {
          e.printStackTrace();
        }
      }
      return null;
    } finally {
      monitor.stop();
    }
  }

  @Override
  public boolean deleteBlob(T dbModel, String fieldname, Context context) {
    Monitor monitor = MeasureFactory.start(this, "deleteBlob");
    try {
      String idName = buildBlobId(dbModel, fieldname);
      NitriteCollection blobCollection = getBlobCollection(context);
      try {
        Document document = blobCollection.find(Filters.eq(FIELD_ID_NAME, idName)).firstOrDefault();
        if (document == null) {
          return false;
        }
        blobCollection.remove(document);
      } finally {
        blobCollection.close();
      }
      File modelPath = new File(getBlobPath(context), dbModel.getId());
      modelPath.mkdirs();
      File blobFile = new File(modelPath, String.format("%s.bin", fieldname));
      if (blobFile.exists()) {
        blobFile.delete();
      }
      return true;
    } finally {
      monitor.stop();
    }
  }

  private String buildBlobId(T dbModel, String fieldname) {
    return String.format("%s-%s", dbModel.getId(), fieldname);
  }

  private Document convertBlobDescriptionToDocument(String idName, BlobDescription blobDescription) {
    Document document = new Document();
    document.put(FIELD_ID_NAME, idName);
    document.put(FIELD_CONTENT_LENGTH, blobDescription.getContentLength());
    document.put(FIELD_CONTENT_TYPE, blobDescription.getContentType());
    document.put(FIELD_CREATION_DATE, blobDescription.getCreationDate());
    document.put(FIELD_FILENAME, blobDescription.getFilename());
    for (Entry<String, Object> entry : blobDescription.properties().entrySet()) {
      document.put(entry.getKey(), entry.getValue());
    }
    return document;
  }

  private BlobDescription convertDocumentToBlobDescription(Document document) {
    BlobDescription description = new BlobDescription();
    for (Entry<String, Object> entry : document.entrySet()) {
      if (entry.getKey().equals("id")) {
        if (entry.getValue() != null)
          description.put("nitrite_id", entry.getValue().toString());
      } else if (entry.getKey().equals(FIELD_ID_NAME)) {
        if (entry.getValue() != null)
          description.setBlobID(entry.getValue().toString());
      } else if (entry.getKey().equals(FIELD_CONTENT_LENGTH)) {
        if (entry.getValue() != null)
          description.setContentLength((long) entry.getValue());
      } else if (entry.getKey().equals(FIELD_CONTENT_TYPE)) {
        if (entry.getValue() != null)
          description.setContentType(entry.getValue().toString());
      } else if (entry.getKey().equals(FIELD_CREATION_DATE)) {
        if (entry.getValue() != null)
          description.setCreationDate((Date) entry.getValue());
      } else if (entry.getKey().equals(FIELD_FILENAME)) {
        if (entry.getValue() != null)
          description.setFilename(entry.getValue().toString());
      } else {
        if (entry.getValue() != null)
          description.put(entry.getKey(), entry.getValue());
      }
    }
    return description;
  }
}
