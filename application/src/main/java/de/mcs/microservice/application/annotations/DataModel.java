package de.mcs.microservice.application.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import de.mcs.microservice.application.core.impl.NullDataModelHooks;
import de.mcs.microservice.application.core.impl.NullDataStorage;
import de.mcs.microservice.application.core.model.DataStorage;
import de.mcs.microservice.application.core.model.RestDataModelHooks;

/**
 * this is the annoation to mark a data model class
 * 
 * @author wklaa_000
 *
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface DataModel {
  /**
   * @return name of the data model
   */
  String name();

  /**
   * 
   * @return name of the parent module
   */
  String moduleName();

  /**
   * 
   * @return some deascritpion for this data model
   */
  String description() default "";

  /**
   * 
   * @return storage class for this data model
   */
  Class<? extends DataStorage> dataStorage() default NullDataStorage.class;

  /**
   * 
   * @return the data hooks to do your magic with the data models
   */
  Class<? extends RestDataModelHooks> dataHooks() default NullDataModelHooks.class;

  /**
   * 
   * @return <code>true</code>> if this data model is visible in the interface, otherwise <code>false</code>
   */
  boolean visible() default true;
}
