package de.mcs.microservice.application.core.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.mcs.microservice.application.annotations.Module;

public class ModuleConfig {

  public static ModuleConfig create() {
    return new ModuleConfig();
  }

  @JsonProperty
  private String className;
  @JsonIgnore
  private Module annotation;
  @JsonProperty
  private Map<String, DataModelConfig> dataModels = new HashMap<>();
  @JsonProperty
  private List<RestResourceConfig> restResources = new ArrayList<>();

  private ModuleConfig() {

  }

  /**
   * @return the className
   */
  public String getClassName() {
    return className;
  }

  /**
   * @param className
   *          the className to set
   */
  public ModuleConfig setClassName(String className) {
    this.className = className;
    return this;
  }

  public ModuleConfig setModuleAnnotation(Module modAnnotation) {
    this.annotation = modAnnotation;
    return this;
  }

  @JsonProperty
  public String getName() {
    return annotation.name();
  }

  @JsonProperty
  public String getDescription() {
    return annotation.description();
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    StringBuilder b = new StringBuilder();
    for (String modelName : dataModels.keySet()) {
      b.append(modelName);
      b.append(", ");
    }
    String modelsStr = b.toString();
    if (modelsStr.length() > 2) {
      modelsStr = modelsStr.substring(0, modelsStr.length() - 2);
    }
    return String.format("{ name = \"%s\", description = \"%s\", className = \"%s\", datamodels = { %s }", getName(),
        getDescription(), className, modelsStr);
  }

  public void addDataModel(DataModelConfig model) {
    dataModels.put(model.getName(), model);
  }

  public Map<String, DataModelConfig> getDataModels() {
    return dataModels;
  }

  public void addRestResource(RestResourceConfig resource) {
    restResources.add(resource);
  }

  public List<RestResourceConfig> getRestResources() {
    return restResources;
  }
}
