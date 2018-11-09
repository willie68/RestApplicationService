/**
 * 
 */
package de.mcs.microservice.application.core.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mcs.microservice.application.ConfigStorageConfig;
import de.mcs.microservice.application.RestApplicationService;
import de.mcs.microservice.application.api.LogLevel;
import de.mcs.microservice.application.api.ServerAPI;
import de.mcs.microservice.application.core.model.ApplicationConfig;
import de.mcs.microservice.application.core.model.Context;
import de.mcs.microservice.application.core.model.DataModelConfig;
import de.mcs.microservice.application.core.model.DataStorage;
import de.mcs.microservice.application.core.model.ModuleConfig;
import de.mcs.microservice.application.core.model.RestDataModel;
import de.mcs.microservice.application.core.model.RestDataModelHooks;
import de.mcs.microservice.application.storage.NitriteDataStorage;

/**
 * @author w.klaas
 *
 */
public class RestServerApi implements ServerAPI {

  private Logger log = LoggerFactory.getLogger(getClass());

  /* (non-Javadoc)
   * @see de.mcs.microservice.application.api.ServerAPI#create(java.lang.Class)
   */
  @Override
  public <T extends RestDataModel> T create(Context context, Class<T> t) {
    // TODO Auto-generated method stub
    return null;
  }

  /* (non-Javadoc)
   * @see de.mcs.microservice.application.api.ServerAPI#find(java.lang.String, java.lang.Class)
   */
  @Override
  public <T extends RestDataModel> List<T> find(String query, Context context, Class<T> t) {
    List<T> list = new ArrayList<>();
    processContext(context);
    List<RestDataModel> doBackendFind = doBackendFind(query, context);
    return list;
  }

  private void processContext(Context context) {
    // rebuild app configs
    Map<String, ApplicationConfig> installedApps = RestApplicationService.getInstance().getInstalledApps();
    ApplicationConfig applicationConfig = installedApps.get(context.getApplicationName());
    context.setApplicationConfig(applicationConfig);

    Map<String, ModuleConfig> modules = applicationConfig.getModules();
    ModuleConfig moduleConfig = modules.get(context.getModuleName());
    context.setModuleConfig(moduleConfig);

    Map<String, DataModelConfig> models = moduleConfig.getDataModels();
    DataModelConfig modelConfig = models.get(context.getModelName());
    context.setDataModelConfig(modelConfig);

  }

  @Override
  public void log(LogLevel level, Context context, String message) {
    String myMessage = formatMessage(context, message);
    switch (level) {
    case TRACE:
      log.trace(myMessage);
      break;
    case DEBUG:
      log.debug(myMessage);
      break;
    case INFO:
      log.info(myMessage);
      break;
    case WARN:
      log.warn(myMessage);
      break;
    case ERROR:
      log.error(myMessage);
      break;
    default:
      break;
    }
  }

  @Override
  public void logError(LogLevel level, Context context, String message, Throwable throwable) {
    String myMessage = formatMessage(context, message);
    switch (level) {
    case TRACE:
      log.trace(myMessage, throwable);
      break;
    case DEBUG:
      log.debug(myMessage, throwable);
      break;
    case INFO:
      log.info(myMessage, throwable);
      break;
    case WARN:
      log.warn(myMessage, throwable);
      break;
    case ERROR:
      log.error(myMessage, throwable);
      break;
    default:
      break;
    }
  }

  private String formatMessage(Context context, String message) {
    String myMessage = String.format("%s#%s via %s (%s): %s", context.getModuleName(), context.getModelName(),
        context.getApplicationName(), context.getTenant(), message);
    return myMessage;
  }

  @Override
  public DataStorage initStorage(DataStorage storage, Context context) {
    if (storage instanceof NitriteDataStorage) {
      NitriteDataStorage nitriteStorage = (NitriteDataStorage) storage;
      if (!nitriteStorage.isInitialised()) {
        ConfigStorageConfig config = RestApplicationService.getInstance().getConfiguration()
            .getInternalDatastoreConfig();
        try {
          Class dataType = Thread.currentThread().getContextClassLoader()
              .loadClass(context.getDataModelConfig().getClassName());
          nitriteStorage.initialise(config, context.getApplicationConfig(), dataType);
        } catch (ClassNotFoundException e) {
          e.printStackTrace();
        }
      }
    }
    return storage;
  }

  @Override
  public List<RestDataModel> doBackendFind(String query, Context context) {
    try {
      RestDataModelHooks hooks = context.getDataModelConfig().getDataModelHooks();

      String newQuery = hooks.beforeFind(query, context);
      if (newQuery == null) {
        newQuery = query;
      }

      DataStorage storage = initStorage(context.getDataModelConfig().getDataStorage(), context);
      List<RestDataModel> list = storage.find(newQuery, context);

      List<RestDataModel> newList = hooks.afterFind(newQuery, list, context);
      if (newList == null) {
        newList = list;
      }
      return newList;
    } catch (InstantiationException | IllegalAccessException e) {
      log.error("unknown error occured", e);
      throw new WebApplicationException("unknown error occured", e, Status.INTERNAL_SERVER_ERROR);
    }
  }

  @Override
  public RestDataModel doBackendCreate(RestDataModel myModel, Context context) {
    try {
      RestDataModelHooks hooks = context.getDataModelConfig().getDataModelHooks();

      RestDataModel beforeCreate = hooks.beforeCreate(myModel, context);
      if (beforeCreate != null) {
        myModel = beforeCreate;
      }

      DataStorage storage = RestApplicationService.getServerApi()
          .initStorage(context.getDataModelConfig().getDataStorage(), context);

      storage.create(myModel, context);

      RestDataModel afterCreate = hooks.afterCreate(myModel, context);
      if (afterCreate != null) {
        myModel = afterCreate;
      }
      return myModel;
    } catch (InstantiationException | IllegalAccessException e) {
      log.error("unknown error occured", e);
      throw new WebApplicationException("unknown error occured", e, Status.INTERNAL_SERVER_ERROR);
    }
  }

  @Override
  public RestDataModel doBackendRead(String id, Context context) {
    try {
      RestDataModelHooks<RestDataModel> hooks = context.getDataModelConfig().getDataModelHooks();

      String newId = hooks.beforeRead(id, context);
      if (newId == null) {
        newId = id;
      }

      DataStorage storage = RestApplicationService.getServerApi()
          .initStorage(context.getDataModelConfig().getDataStorage(), context);
      RestDataModel myModel = storage.read(newId, context);

      if (myModel != null) {
        hooks.afterRead(myModel, context);
      }
      return myModel;
    } catch (InstantiationException | IllegalAccessException e) {
      log.error("unknown error occured", e);
      throw new WebApplicationException("unknown error occured", e, Status.INTERNAL_SERVER_ERROR);
    }
  }

  @Override
  public RestDataModel doBackendUpdate(RestDataModel model, RestDataModel dbModel, Context context) {
    try {
      RestDataModelHooks hooks = context.getDataModelConfig().getDataModelHooks();

      RestDataModel beforeUpdate = hooks.beforeUpdate(model, context);
      if (beforeUpdate != null) {
        model = beforeUpdate;
      }

      DataStorage storage = RestApplicationService.getServerApi()
          .initStorage(context.getDataModelConfig().getDataStorage(), context);
      storage.update(model, context);

      RestDataModel afterUpdate = hooks.afterUpdate(model, context);
      if (afterUpdate != null) {
        model = afterUpdate;
      }
      return model;
    } catch (InstantiationException | IllegalAccessException e) {
      log.error("unknown error occured", e);
      throw new WebApplicationException("unknown error occured", e, Status.INTERNAL_SERVER_ERROR);
    }
  }

  @Override
  public RestDataModel doBackendDelete(String id, Context context) {
    try {
      RestDataModelHooks hooks = context.getDataModelConfig().getDataModelHooks();

      String newId = hooks.beforeDelete(id, context);
      if (newId == null) {
        newId = id;
      }

      DataStorage storage = RestApplicationService.getServerApi()
          .initStorage(context.getDataModelConfig().getDataStorage(), context);
      RestDataModel deleteModel = storage.delete(newId, context);

      RestDataModel afterUpdate = hooks.afterUpdate(deleteModel, context);
      if (afterUpdate != null) {
        deleteModel = afterUpdate;
      }
      return deleteModel;
    } catch (InstantiationException | IllegalAccessException e) {
      log.error("unknown error occured", e);
      throw new WebApplicationException("unknown error occured", e, Status.INTERNAL_SERVER_ERROR);
    }
  }

}
