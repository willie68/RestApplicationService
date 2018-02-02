package de.mcs.microservice.application.core.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.mcs.microservice.application.annotations.Application;
import de.mcs.microservice.application.annotations.Application.TenantType;
import de.mcs.microservice.application.core.impl.NullAuthenticator;

public class ApplicationConfig {

  public static ApplicationConfig create() {
    return new ApplicationConfig();
  }

  @JsonProperty
  private String className;
  @JsonIgnore
  private Application annotation;
  @JsonProperty
  private Map<String, ModuleConfig> modules = new HashMap<>();
  @JsonIgnore
  private Authenticator authenticator;
  @JsonProperty
  private String apikey;
  @JsonProperty
  private List<String> tenants = new ArrayList<>();

  private ApplicationConfig() {
  }

  /**
   * @return the className
   */
  public String getClassName() {
    return className;
  }

  public ApplicationConfig setClassName(String className) {
    this.className = className;
    return this;
  }

  /**
   * @return the tenantType
   */
  @JsonProperty
  public TenantType getTenantType() {
    return annotation.tenantType();
  }

  /**
   * @return the name
   */
  @JsonProperty
  public String getName() {
    return annotation.name();
  }

  /**
   * @return the description
   */
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
    return String.format("{ name = \"%s\", description = \"%s\", className = \"%s\", tenantType = \"%s\" }", getName(),
        getDescription(), className, getTenantType().name());
  }

  public ApplicationConfig setAppAnnotation(Application appAnnotation) {
    this.annotation = appAnnotation;
    return this;
  }

  @JsonProperty
  public String[] getUsedModulesNames() {
    return annotation.usedModules();
  }

  public Class<? extends Authenticator> getAuthenticatorClass() {
    return annotation.authClass();
  }

  @JsonProperty
  public boolean hasAuthenticator() {
    return annotation.authClass() != null;
  }

  public Authenticator getAuthenticator() {
    if (authenticator == null) {
      Class<? extends Authenticator> authenticatorClass = getAuthenticatorClass();
      if (authenticatorClass == null) {
        authenticatorClass = NullAuthenticator.class;
      }
      try {
        authenticator = authenticatorClass.newInstance();
      } catch (InstantiationException | IllegalAccessException e) {
        throw new RuntimeException("can't instanciate authenticator. Please cunsult the log file.", e);
      }
    }
    return authenticator;
  }

  public void addModule(ModuleConfig moduleConfig) {
    modules.put(moduleConfig.getName(), moduleConfig);
  }

  public Map<String, ModuleConfig> getModules() {
    return modules;
  }

  public String getApikey() {
    return apikey;
  }

  public void setApikey(String apikey) {
    this.apikey = apikey;
  }

  public void addTenant(String tenant) {
    tenants.add(tenant);
  }

  public boolean removeTenant(String tenant) {
    return tenants.remove(tenant);
  }

  public boolean hasTenant(String applicationTenant) {
    return tenants.contains(applicationTenant);
  }

}
