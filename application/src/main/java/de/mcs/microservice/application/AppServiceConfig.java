/**
 * 
 */
package de.mcs.microservice.application;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.dropwizard.Configuration;

/**
 * @author w.klaas
 *
 */
public class AppServiceConfig extends Configuration {

  @JsonProperty("internalDatastore")
  private ConfigStorageConfig internalDatastoreConfig;

  public ConfigStorageConfig getInternalDatastoreConfig() {
    return internalDatastoreConfig;
  }

  public void setInternalDatastoreConfig(ConfigStorageConfig internalDatastore) {
    this.internalDatastoreConfig = internalDatastore;
  }

}
