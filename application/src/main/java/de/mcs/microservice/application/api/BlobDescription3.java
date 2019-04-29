/**
 * 
 */
package de.mcs.microservice.application.api;

import java.util.Date;
import java.util.HashMap;

/**
 * @author wklaa_000
 */
public class BlobDescription3 extends HashMap<String, Object> {

  public BlobDescription3() {
    setCreationDate(new Date());
  }

  // @JsonAnySetter
  // public void set(String fieldName, Object value) {
  // this.properties.put(fieldName, value);
  // }
  //
  // @JsonAnyGetter
  // public Map<String, Object> properties() {
  // return properties;
  // }
  //
  // public Object get(String fieldName) {
  // return this.properties.get(fieldName);
  // }

  public long getContentLength() {
    return (long) get("contentLength");
  }

  public void setContentLength(long size) {
    put("contentLength", size);
  }

  public void setCreationDate(Date creationDate) {
    put("creationDate", creationDate);
  }

  public Date getCreationDate() {
    return (Date) get("creationDate");
  }

  public void setContentType(String type) {
    put("contentType", type);
  }

  public String getContentType() {
    return (String) get("contentType");
  }

  public void setFilename(String filename) {
    put("filename", filename);
  }

  public String getFilename() {
    return (String) get("filename");
  }

  public void setBlobID(String id) {
    put("blobID", id);
  }

  public String getBlobID() {
    return (String) get("blobID");
  }
}
