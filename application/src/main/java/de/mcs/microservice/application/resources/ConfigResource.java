/**
 * EASY SPIRIT Copyright (c) EASY SOFTWARE AG, 2015 - 2015 All rights reserved.
 */
package de.mcs.microservice.application.resources;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang.StringUtils;

import de.mcs.microservice.application.RestApplicationService;
import de.mcs.microservice.application.annotations.Application.TenantType;
import de.mcs.microservice.application.api.BaseModel;
import de.mcs.microservice.application.core.model.ApplicationConfig;

/**
 * @author wklaa_000
 */
@Singleton
@Path("config")
public class ConfigResource {

  /**
   * @param applicationContext
   *          The application context
   */
  public ConfigResource() {
    super();
  }

  /**
   * @return a the application configuration
   */
  @GET
  @Path("apps/{app}")
  @Produces(MediaType.APPLICATION_JSON)
  public ApplicationConfig getApplication(@PathParam(value = "app") String application) {
    RestApplicationService<?> instance = RestApplicationService.getInstance();
    Map<String, ApplicationConfig> installedApps = instance.getInstalledApps();
    ApplicationConfig applicationConfig = installedApps.get(application);
    if (applicationConfig == null) {
      throw new WebApplicationException(String.format("application \"%s\" not found", application), Status.NOT_FOUND);
    }
    return applicationConfig;
  }

  /**
   * @return a list of all tenants for this application
   */
  @GET
  @Path("apps/{app}/tenants")
  @Produces(MediaType.APPLICATION_JSON)
  public List<String> getTenants(@PathParam(value = "app") String application) {
    List<String> list = new ArrayList<>();
    RestApplicationService<?> instance = RestApplicationService.getInstance();
    Map<String, ApplicationConfig> installedApps = instance.getInstalledApps();
    ApplicationConfig applicationConfig = installedApps.get(application);
    if (applicationConfig == null) {
      throw new WebApplicationException(String.format("application \"%s\" not found", application), Status.NOT_FOUND);
    }
    if (!TenantType.MULTI_TENANT.equals(applicationConfig.getTenantType())) {
      throw new WebApplicationException(String.format("application \"%s\" is not a multi tenant app.", application),
          Status.NOT_FOUND);
    }
    String[] tenants = (String[]) instance.getConfigStorage().get(RestApplicationService.getAppTenantsKey(application));
    if (tenants != null) {
      for (String tenant : tenants) {
        list.add(tenant);
      }
    }
    return list;
  }

  /**
   * @return a list of all tenants for this application
   */
  @POST
  @Path("apps/{app}/tenants")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public List<String> addTenant(@PathParam(value = "app") String application, BaseModel model) {
    List<String> list = getTenants(application);
    if (model == null) {
      throw new WebApplicationException("tenant model should not be null or empty.", Status.BAD_REQUEST);
    }
    String tenant = model.getFieldValueAsString("tenant");
    if (StringUtils.isNotEmpty(tenant)) {
      if (list.contains(tenant)) {
        return list;
      }
      list.add(tenant);

      RestApplicationService<?> instance = RestApplicationService.getInstance();
      instance.getConfigStorage().save(RestApplicationService.getAppTenantsKey(application),
          list.toArray(new String[0]));

      Map<String, ApplicationConfig> installedApps = instance.getInstalledApps();
      ApplicationConfig applicationConfig = installedApps.get(application);
      applicationConfig.addTenant(tenant);
    }
    return list;
  }

  /**
   * @return a list of all tenants for this application
   */
  @DELETE
  @Path("apps/{app}/tenants/{tenant}")
  @Produces(MediaType.APPLICATION_JSON)
  public List<String> removeTenant(@PathParam(value = "app") String application,
      @PathParam(value = "tenant") String tenant) {
    List<String> list = getTenants(application);
    if (StringUtils.isNotEmpty(tenant)) {
      if (list.remove(tenant)) {
        RestApplicationService<?> instance = RestApplicationService.getInstance();
        instance.getConfigStorage().save(RestApplicationService.getAppTenantsKey(application),
            list.toArray(new String[0]));
      }
    }
    return list;
  }
}
