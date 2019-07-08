/**
 * 
 */
package de.mcs.microservice.application.storage;

import de.mcs.microservice.application.ConfigStorageConfig;

/**
 * @author w.klaas
 *
 */
public interface ConfigStorage {

  void initialise(ConfigStorageConfig config);

  void save(String name, Object value);

  Object get(String name);

  Object delete(String name);

}
