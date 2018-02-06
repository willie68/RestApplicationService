/**
 * MCS Media Computer Software
 * Copyright 2018 by Wilfried Klaas
 * Project: schematic
 * File: TestSerialiser.java
 * EMail: W.Klaas@gmx.de
 * Created: 01.02.2018 wklaa_000
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
package de.mcs.microservice.schematic;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.mcs.microservice.application.api.BlobDescription;
import de.mcs.microservice.utils.JacksonUtils;

/**
 * @author wklaa_000
 *
 */
public class TestSerialiser {

  @Test
  public void test() throws IOException {
    ObjectMapper jsonMapper = JacksonUtils.getJsonMapper();
    Schematic model = new Schematic();
    {
      String json = jsonMapper.writeValueAsString(model);
      assertNotNull(json);
      System.out.println(json);
      Schematic jsonModel = jsonMapper.readValue(json, Schematic.class);
      assertNotNull(jsonModel);
    }

    model.setFilename("filename");
    model.setSchematicName("schematicName");
    model.setOwner("me");
    model.setTags(new ArrayList<>());
    model.getTags().add("tube");
    model.getTags().add("amp");

    {
      String json = jsonMapper.writeValueAsString(model);
      assertNotNull(json);
      System.out.println(json);
      Schematic jsonModel = jsonMapper.readValue(json, Schematic.class);
      assertNotNull(jsonModel);
    }

    BlobDescription description = new BlobDescription();
    description.put("hallo", "murks");
    model.setFile(description);

    {
      String json = jsonMapper.writeValueAsString(model);
      assertNotNull(json);
      System.out.println(json);
      Schematic jsonModel = jsonMapper.readValue(json, Schematic.class);
      assertNotNull(jsonModel);
    }

  }

}
