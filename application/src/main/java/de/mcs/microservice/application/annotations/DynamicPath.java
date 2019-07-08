package de.mcs.microservice.application.annotations;

import java.lang.annotation.Annotation;

public class DynamicPath implements javax.ws.rs.Path {

  private String path;

  public DynamicPath(String path) {
    this.path = path;
  }

  @Override
  public Class<? extends Annotation> annotationType() {
    return DynamicPath.class;
  }

  @Override
  public String value() {
    return path;
  }

}
