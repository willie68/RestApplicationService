package de.mcs.microservice.schematic;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import de.mcs.microservice.application.annotations.RestResource;
import de.mcs.microservice.application.core.AbstractRestResource;

@RestResource(moduleName = "SchematicModule", description = "this is the data model for the schematic files")
public class SchematicResource extends AbstractRestResource {

  @Path("/info")
  @GET
  @RolesAllowed({ "admin", "appadmin" })
  public Response info() {
    return Response.ok(String.format("Service working. %s#%s", getAppName(), getModuleName())).build();
  }

}
