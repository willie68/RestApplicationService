package application;

import static org.junit.Assert.assertNotNull;

import java.util.Set;

import org.junit.Test;
import org.reflections.Reflections;

public class TestAnnotationScanner {

  @Test
  public void test() {
    Reflections reflections = new Reflections("de.mcs.microservice");
    Set<Class<?>> applicationTypes = reflections
        .getTypesAnnotatedWith(de.mcs.microservice.application.annotations.Application.class);

    assertNotNull(applicationTypes);
  }

}
