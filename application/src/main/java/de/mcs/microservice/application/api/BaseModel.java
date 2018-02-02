package de.mcs.microservice.application.api;
/**
 * 
 */


import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

/**
 * @author w.klaas
 */
public class BaseModel {

  private Map<String, Object> values;

  /**
   * 
   */
  public BaseModel() {
    values = new HashMap<>();
  }

  /**
   * @param name
   *          name of the field
   * @param value
   *          value of the field
   */
  @JsonAnySetter
  public void setKeyValue(String name, Object value) {
    values.put(name, value);
  }

  /**
   * @return a map with all fields
   */
  @JsonAnyGetter
  public Map<String, Object> getAny() {
    return values;
  }

  /**
   * getting the value of a single field with the desired name
   * 
   * @param name
   *          the name of the field to get
   * @return value the value of the field, or <code>null</code>
   */
  public Object getField(String name) {
    return values.get(name);
  }

  /**
   * getting the value of a single field with the desired name
   * 
   * @param name
   *          the name of the field to get
   * @return value the value of the field as a string, or <code>null</code>
   */
  public String getFieldValueAsString(String name) {
    Object fieldValue = getField(name);
    if (fieldValue != null) {
      return fieldValue.toString();
    }
    return null;
  }

  @Override
  public String toString() {
    return values.toString();
  }

}
