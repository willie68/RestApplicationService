package de.mcs.microservice.schematic;
/**
 * MyService
 * this is the data storage for the schematic files
 * Copyright (c) MCS 2018
 */

import de.mcs.jmeasurement.MeasureFactory;
import de.mcs.microservice.application.RestApplicationService;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

/**
 * Service class for
 */
public class MyService extends RestApplicationService<MyServiceConfig> {

  public static void main(String[] args) {
    try {
      new MyService().run(args);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public MyService() {
    super();
    MeasureFactory.setApplicationName(this.getName());
  }

  @Override
  public void run(MyServiceConfig configuration, Environment environment) throws Exception {
    super.run(configuration, environment);
  }

  @Override
  public String getName() {
    return "MCSSchematicApplication";
  }

  public void terminate() {
  }

  @Override
  public void initialize(Bootstrap<MyServiceConfig> bootstrap) {
    super.initialize(bootstrap);
  }

}
