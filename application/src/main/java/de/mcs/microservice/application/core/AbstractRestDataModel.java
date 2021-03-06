/**
 * MCS Media Computer Software
 * Copyright 2018 by Wilfried Klaas
 * Project: application
 * File: AbstractRestDataModel.java
 * EMail: W.Klaas@gmx.de
 * Created: 05.01.2018 wklaa_000
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package de.mcs.microservice.application.core;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.mcs.microservice.application.annotations.Id;
import de.mcs.microservice.application.core.model.RestDataModel;

/**
 * @author wklaa_000
 *
 */
@JsonIgnoreProperties
public abstract class AbstractRestDataModel implements RestDataModel {

  public static final String[] INDEXES = { AbstractRestDataModel.KEY_MODEL_NAME, AbstractRestDataModel.KEY_MODULE_NAME,
      AbstractRestDataModel.KEY_OWNER, AbstractRestDataModel.KEY_CREATED_AT,
      AbstractRestDataModel.KEY_LAST_MODIFIED_AT };

  public static final String KEY_MODULE_NAME = "_moduleName";

  public static final String KEY_MODEL_NAME = "_modelName";

  public static final String KEY_OWNER = "_owner";

  public static final String KEY_CREATED_AT = "_createdAt";

  public static final String KEY_LAST_MODIFIED_AT = "_lastModifiedAt";

  @Id
  @JsonProperty
  private String _id;

  @JsonIgnore
  private Map<String, Object> data = new HashMap<>();

  public AbstractRestDataModel() {
    data.put(KEY_MODEL_NAME, getModelName());
    data.put(KEY_MODULE_NAME, getModuleName());
    long time = new Date().getTime();
    data.put(KEY_CREATED_AT, time);
    data.put(KEY_LAST_MODIFIED_AT, time);
  }

  public abstract String getModelName();

  public abstract String getModuleName();

  @JsonAnyGetter
  public Map<String, Object> any() {
    return data;
  }

  @JsonAnySetter
  public void set(String name, Object value) {
    data.put(name, value);
  }

  @JsonIgnore
  public void setValue(String name, Object value) {
    data.put(name, value);
    data.put(KEY_LAST_MODIFIED_AT, new Date().getTime());
  }

  /**
   * @return the _id
   */
  public String getId() {
    return _id;
  }

  /**
   * @param _id
   *          the _id to set
   */
  public void setId(String _id) {
    this._id = _id;
  }

  final public String getOwner() {
    return (String) data.get(KEY_OWNER);
  }

  final public void setOwner(String owner) {
    data.put(KEY_OWNER, owner);
  }

  final public Date getCreatedAt() {
    return new Date((Long) data.get(KEY_CREATED_AT));
  }

  final public Date getlastModifiedAt() {
    return new Date((Long) data.get(KEY_LAST_MODIFIED_AT));
  }

  final void setPrivateModuleName(String moduleName) {
    data.put(KEY_MODULE_NAME, moduleName);
  }

  final void setPrivateModelName(String modelName) {
    data.put(KEY_MODEL_NAME, modelName);
  }

}
