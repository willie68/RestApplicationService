/**
 * MCS Media Computer Software
 * Copyright 2018 by Wilfried Klaas
 * Project: schematic
 * File: SimplyQuery.java
 * EMail: W.Klaas@gmx.de
 * Created: 14.01.2018 wklaa_000
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
package de.mcs.microservice.application.query;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.core.JsonProcessingException;

import de.mcs.microservice.utils.JacksonUtils;

/**
 * @author wklaa_000
 *
 */
public class SimpleQuery {

  // and then "other" stuff:
  protected Map<String, Object> data = new HashMap<>();

  public Object get(String name) {
    return data.get(name);
  }

  // "any getter" needed for serialization
  @JsonAnyGetter
  public Map<String, Object> any() {
    return data;
  }

  @JsonAnySetter
  public void set(String name, Object value) {
    data.put(name, value);
  }

  public String toJson() throws JsonProcessingException {
    return JacksonUtils.getJsonMapper().writeValueAsString(this);
  }

}
