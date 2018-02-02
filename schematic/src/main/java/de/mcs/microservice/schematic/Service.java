package de.mcs.microservice.schematic;
/**
 * EASY MICROSERVICE PLATFORM
 * Copyright (c) EASY SOFTWARE AG 2014 - 2015
 * All rights reserved
 * Project: config-service
 */

import org.apache.log4j.Logger;

import de.mcs.jmeasurement.MeasureFactory;
import de.mcs.microservice.application.RestApplicationService;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

/**
 * Service class for Service-Registry-Server
 * 
 * @author w.klaas
 *
 */
public class Service extends RestApplicationService<ServiceConfig> {

  private Logger log;

  public static void main(String[] args) {
    try {
      new Service().run(args);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public Service() {
    super();
    MeasureFactory.setApplicationName(this.getName());
    log = Logger.getLogger(Service.class);
  }

  @Override
  public void run(ServiceConfig configuration, Environment environment) throws Exception {
    super.run(configuration, environment);
    // System.exit(0);
  }

  @Override
  public String getName() {
    return "MCSSchematicApplication";
  }

  public void terminate() {
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.mcs.microservice.application.RestApplicationService#initialize(io.
   * dropwizard. setup.Bootstrap)
   */
  @Override
  public void initialize(Bootstrap<ServiceConfig> bootstrap) {
    super.initialize(bootstrap);
  }

}
