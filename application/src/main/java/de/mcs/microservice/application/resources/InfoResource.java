package de.mcs.microservice.application.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

/**
 * Server Side Implementation for Registry this is the service interface
 * 
 * @since 1.0.0
 */
@javax.ws.rs.Path("/rest/v1")
public class InfoResource {

  @PathParam("id")
  String id;

  @Path("/info2/{id}")
  @GET
  public Response info2() {
    return Response.ok(String.format("Service working. %s", id)).build();
  }

  @Path("/info")
  @GET
  public Response info() {

    return Response.ok("Service working.").build();
  }

}
