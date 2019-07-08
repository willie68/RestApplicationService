/**
 * 
 */
package de.mcs.microservice.utils;

import java.io.IOException;
import java.io.OutputStream;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

/**
 * @author w.klaas
 *
 */
public class JacksonUtils {

  private static ObjectMapper ymlObjectMapper;

  public static ObjectMapper getYmlMapper() {
    if (ymlObjectMapper == null) {
      ymlObjectMapper = new ObjectMapper(new YAMLFactory());
      ymlObjectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      ymlObjectMapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
      ymlObjectMapper.setSerializationInclusion(Include.NON_NULL);

    }
    return ymlObjectMapper;
  }

  public static void writeYAMLObject(Object root, OutputStream fos) throws JsonProcessingException, IOException {
    YAMLFactory yf = new YAMLFactory();
    yf.createGenerator(fos).writeObject(root);
  }

  private static ObjectMapper jsonObjectMapper;

  public static ObjectMapper getJsonMapper() {
    if (jsonObjectMapper == null) {
      jsonObjectMapper = new ObjectMapper();
      jsonObjectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
      jsonObjectMapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
      jsonObjectMapper.configure(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED, false);
      jsonObjectMapper.setVisibility(PropertyAccessor.ALL, Visibility.NONE);
      jsonObjectMapper.setVisibility(PropertyAccessor.FIELD, Visibility.NONE);
      jsonObjectMapper.setSerializationInclusion(Include.NON_NULL);
    }
    return jsonObjectMapper;
  }
}
