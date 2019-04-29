/**
 * 
 */
package de.mcs.microservice.application.core;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 * @author w.klaas
 *
 */
@Path("")
public abstract class AbstractRestResource {

  public static final String RESOURCEPATH = "/rest/v1/apps/{appName}/module/{moduleName}/moduleresource";
  @PathParam("appName")
  private String appName;

  @PathParam("moduleName")
  private String moduleName;

  public String getModuleName() {
    return moduleName;
  }

  public String getAppName() {
    return appName;
  }

}
