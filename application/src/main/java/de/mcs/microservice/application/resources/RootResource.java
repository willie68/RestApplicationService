/**
 * EASY SPIRIT Copyright (c) EASY SOFTWARE AG, 2015 - 2015 All rights reserved.
 */
package de.mcs.microservice.application.resources;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Singleton;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import com.google.common.base.Charsets;

import de.mcs.jmeasurement.exception.RendererMustNotBeNullException;
import de.mcs.microservice.application.RestApplicationService;
import de.mcs.microservice.application.core.model.ApplicationConfig;

/**
 * EASY SPIRIT
 * 
 * @author s.goppelt
 */
@Singleton
@Path("")
public class RootResource {

  /**
   * simple constructor
   */
  public RootResource() {
    super();
  }

  /**
   * Class for holding service link data
   * 
   * @author w.klaas
   */
  public static class ServiceLink {
    /**
     * name of the link
     */
    public String name;

    /**
     * short description
     */
    public String description;

    /**
     * the link itself
     */
    public String link;

    /**
     * @param name
     *          name of the link
     * @param description
     *          short description
     * @param link
     *          the link itself
     */
    public ServiceLink(String name, String description, String link) {
      this.name = name;
      this.description = description;
      this.link = link;
    }

    /**
     * @return the link
     */
    public String getLink() {
      return link;
    }

    /**
     * @return the description
     */
    public String getDescription() {
      return description;
    }

    /**
     * @return the name
     */
    public String getName() {
      return name;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
      return name;
    }
  }

  /**
   * @param response
   *          The http servlet response.
   * @return The call report.
   * @throws IOException
   *           On IO errors.
   * @throws RendererMustNotBeNullException
   *           If the renderer is null.
   */
  @GET
  @Path("")
  @Produces(MediaType.TEXT_HTML)
  public Response getServiceLinks(@Context HttpServletResponse response)
      throws IOException, RendererMustNotBeNullException {
    StreamingOutput stream = new StreamingOutput() {
      @Override
      public void write(OutputStream os) throws IOException, WebApplicationException {
        Writer writer = new BufferedWriter(new OutputStreamWriter(os, Charsets.UTF_8));
        List<ServiceLink> serviceLinks = new ArrayList<RootResource.ServiceLink>();
        serviceLinks.add(new ServiceLink("callreport", "it's a jmeasurement callreport in html", "service/callreport"));

        VelocityEngine ve = new VelocityEngine();
        ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        ve.init();
        VelocityContext context = new VelocityContext();
        context.put("links", serviceLinks);
        Map<String, ApplicationConfig> installedApps = RestApplicationService.getInstance().getInstalledApps();
        List<ApplicationConfig> appList = new ArrayList<>();
        for (ApplicationConfig applicationConfig : installedApps.values()) {
          appList.add(applicationConfig);
        }
        context.put("apps", appList);
        Template template = ve.getTemplate("templates/rootresource.vm");
        template.merge(context, writer);
        writer.flush();
      }
    };
    return Response.ok(stream).build();
  }

}
