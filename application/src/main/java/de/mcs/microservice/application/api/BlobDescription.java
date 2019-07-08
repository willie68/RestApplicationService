/**
 * 
 */
package de.mcs.microservice.application.api;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author wklaa_000
 */
public class BlobDescription {

  @JsonProperty
  private long contentLength;
  @JsonProperty
  private String contentType;
  @JsonProperty
  private Date creationDate = new Date();
  @JsonProperty
  private String filename;
  @JsonProperty
  private String blobID;

  private Map<String, Object> properties = new HashMap<>();

  @JsonAnySetter
  public void put(String fieldName, Object value) {
    this.properties.put(fieldName, value);
  }

  @JsonAnyGetter
  public Map<String, Object> properties() {
    return properties;
  }

  public Object get(String fieldName) {
    return this.properties.get(fieldName);
  }

  public long getContentLength() {
    return contentLength;
  }

  public void setContentLength(long size) {
    this.contentLength = size;
  }

  public void setCreationDate(Date creationDate) {
    this.creationDate = creationDate;
  }

  public Date getCreationDate() {
    return creationDate;
  }

  public void setContentType(String type) {
    this.contentType = type;
  }

  public String getContentType() {
    return contentType;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }

  public String getFilename() {
    return filename;
  }

  public void setBlobID(String id) {
    this.blobID = id;
  }

  public String getBlobID() {
    return blobID;
  }
}
