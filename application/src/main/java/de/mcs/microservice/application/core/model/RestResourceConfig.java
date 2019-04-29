package de.mcs.microservice.application.core.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.mcs.microservice.application.annotations.RestResource;

public class RestResourceConfig {
  public static RestResourceConfig create() {
    return new RestResourceConfig();
  }

  @JsonProperty
  private String className;
  @JsonIgnore
  private RestResource annotation;

  private RestResourceConfig() {

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
  public RestResourceConfig setClassName(String className) {
    this.className = className;
    return this;
  }

  public RestResourceConfig setRestResourceAnnotation(RestResource annotation) {
    this.annotation = annotation;
    return this;
  }

  @JsonProperty
  public String getModuleName() {
    return annotation.moduleName();
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
    return String.format("{ moduleName = \"%s\", description = \"%s\", className = \"%s\" }", getModuleName(),
        getDescription(), className, getModuleName());
  }

}
