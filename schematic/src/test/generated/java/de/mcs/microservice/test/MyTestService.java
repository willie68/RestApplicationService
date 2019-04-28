package de.mcs.microservice.test;
/**
 * MyTestService
 * this is the test application for testing the generator
 * Copyright (c) MCS 2019
 */

import de.mcs.jmeasurement.MeasureFactory;
import de.mcs.microservice.application.RestApplicationService;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

/**
 * Service class for
 */
public class MyTestService extends RestApplicationService<MyTestServiceConfig> {

  public static void main(String[] args) {
    try {
      new MyTestService().run(args);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public MyTestService() {
    super();
    MeasureFactory.setApplicationName(this.getName());
  }

  @Override
  public void run(MyTestServiceConfig configuration, Environment environment) throws Exception {
    super.run(configuration, environment);
  }

  @Override
  public String getName() {
    return "MCSTestApplication";
  }

  public void terminate() {
  }

  @Override
  public void initialize(Bootstrap<MyTestServiceConfig> bootstrap) {
    super.initialize(bootstrap);
  }

}
