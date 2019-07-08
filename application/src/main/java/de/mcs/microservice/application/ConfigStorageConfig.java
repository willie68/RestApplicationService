/**
 * 
 */
package de.mcs.microservice.application;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author w.klaas
 *
 */
public class ConfigStorageConfig {
  @JsonProperty
  private String storageClass = "de.mcs.microservice.application.storage.NitriteStorage";

  @JsonIgnore
  private Map<String, Object> data = new HashMap<>();

  public String getStorageClass() {
    return storageClass;
  }

  public void setStorageClass(String storageClass) {
    this.storageClass = storageClass;
  }

  @JsonAnyGetter
  public Map<String, Object> any() {
    return data;
  }

  @JsonAnySetter
  public void set(String name, Object value) {
    data.put(name, value);
  }

}
