package de.mcs.microservice.application.core.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.mcs.microservice.application.annotations.DataModel;

public class DataModelConfig {
  public static DataModelConfig create() {
    return new DataModelConfig();
  }

  @JsonProperty
  private String className;
  @JsonIgnore
  private DataModel annotation;
  @JsonIgnore
  private RestDataModelHooks hooks;
  @JsonIgnore
  private DataStorage storage;
  @JsonProperty
  private List<FieldConfig> indexFields = new ArrayList<>();

  private DataModelConfig() {

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
  public DataModelConfig setClassName(String className) {
    this.className = className;
    return this;
  }

  public DataModelConfig setDataModelAnnotation(DataModel annotation) {
    this.annotation = annotation;
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

  @JsonProperty
  public String getModuleName() {
    return annotation.moduleName();
  }

  public Class<? extends RestDataModelHooks> getDataModelHookClass() {
    return annotation.dataHooks();
  }

  public Class<? extends DataStorage> getDataStorageClass() {
    return annotation.dataStorage();
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return String.format("{ name = \"%s\", description = \"%s\", className = \"%s\" }", getName(), getDescription(),
        className, getModuleName());
  }

  @JsonIgnore
  public RestDataModelHooks getDataModelHooks() throws InstantiationException, IllegalAccessException {
    if (hooks == null) {
      hooks = getDataModelHookClass().newInstance();
    }
    return hooks;
  }

  @JsonIgnore
  public DataStorage getDataStorage() throws InstantiationException, IllegalAccessException {
    if (storage == null) {
      storage = getDataStorageClass().newInstance();
    }
    return storage;
  }

  @JsonProperty
  public boolean isVisible() {
    return annotation.visible();
  }

  public void addIndexField(FieldConfig name) {
    indexFields.add(name);
  }

  @JsonProperty
  public List<FieldConfig> getIndexFields() {
    return indexFields;
  }

}
