/**
 * EASY SPIRIT Copyright (c) EASY SOFTWARE AG, 2015 - 2015 All rights reserved.
 */
package de.mcs.microservice.application.resources;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import javax.inject.Singleton;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import de.mcs.jmeasurement.MeasureFactory;
import de.mcs.jmeasurement.exception.RendererMustNotBeNullException;
import de.mcs.jmeasurement.renderer.DefaultHTMLRenderer;
import de.mcs.microservice.application.api.MeasureData;
import de.mcs.microservice.application.api.MeasureDataFactory;

/*
 * @author wklaa_000
 */
@Singleton
@Path("service")
public class ServiceResource {

  /**
   * @param applicationContext
   *          The application context
   */
  public ServiceResource() {
    super();
  }

  /**
   * @return The service statistics.
   */
  @GET
  @Path("callreport")
  @Produces(MediaType.APPLICATION_JSON)
  public List<MeasureData> getStatisticsJson() {
    return MeasureDataFactory.transform(MeasureFactory.getMeasurePoints(null));
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
  @Path("callreport")
  @Produces(MediaType.TEXT_HTML)
  public String getCallReport(@Context HttpServletResponse response)
      throws IOException, de.mcs.jmeasurement.exception.RendererMustNotBeNullException {
    StringWriter sw = new StringWriter();
    MeasureFactory.getReport(null, new DefaultHTMLRenderer(), sw);
    return sw.toString();
  }

}
