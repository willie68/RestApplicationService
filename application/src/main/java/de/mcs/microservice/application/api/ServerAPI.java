/**
 * 
 */
package de.mcs.microservice.application.api;

import java.util.List;

import de.mcs.microservice.application.core.model.Context;
import de.mcs.microservice.application.core.model.DataStorage;
import de.mcs.microservice.application.core.model.RestDataModel;

/**
 * @author w.klaas
 *
 */
public interface ServerAPI {

  <T extends RestDataModel> T create(Context context, Class<T> t);

  <T extends RestDataModel> List<T> find(String query, Context context, Class<T> t);

  void log(LogLevel level, Context context, String message);

  void logError(LogLevel level, Context context, String message, Throwable throwable);

  List<RestDataModel> doBackendFind(String query, Context buildContext);

  DataStorage initStorage(DataStorage storage, Context context);

  RestDataModel doBackendCreate(RestDataModel myModel, Context buildContext);

  RestDataModel doBackendRead(String id, Context context);

  RestDataModel doBackendDelete(String id, Context context);

  RestDataModel doBackendUpdate(RestDataModel model, RestDataModel dbModel, Context context);
}
