/**
 * 
 */
package de.mcs.microservice.application.core.model;

/**
 * @author w.klaas
 *
 */
public class FieldConfig {
  public static FieldConfig create(String name, String type, boolean fulltext) {
    return new FieldConfig().setName(name).setType(type).setFulltext(fulltext);
  }

  private String name;
  private boolean fulltext;
  private String type;

  private FieldConfig setName(String name) {
    this.name = name;
    return this;
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  private FieldConfig setType(String type) {
    this.type = type;
    return this;
  }

  /**
   * @return the name
   */
  public String getType() {
    return type;
  }

  private FieldConfig setFulltext(boolean fulltext) {
    this.fulltext = fulltext;
    return this;
  }

  /**
   * @return the fulltext
   */
  public boolean isFulltext() {
    return fulltext;
  }
}
