/**
 * 
 */
package de.mcs.microservice.application.core.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mcs.microservice.application.api.LogLevel;
import de.mcs.microservice.application.api.ServerAPI;
import de.mcs.microservice.application.core.model.Context;
import de.mcs.microservice.application.core.model.RestDataModel;

/**
 * @author w.klaas
 *
 */
public class RestServerApi implements ServerAPI {

  private Logger logger = LoggerFactory.getLogger(getClass());

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
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void log(LogLevel level, Context context, String message) {
    String myMessage = formatMessage(context, message);
    switch (level) {
    case TRACE:
      logger.trace(myMessage);
      break;
    case DEBUG:
      logger.debug(myMessage);
      break;
    case INFO:
      logger.info(myMessage);
      break;
    case WARN:
      logger.warn(myMessage);
      break;
    case ERROR:
      logger.error(myMessage);
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
      logger.trace(myMessage, throwable);
      break;
    case DEBUG:
      logger.debug(myMessage, throwable);
      break;
    case INFO:
      logger.info(myMessage, throwable);
      break;
    case WARN:
      logger.warn(myMessage, throwable);
      break;
    case ERROR:
      logger.error(myMessage, throwable);
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

}
