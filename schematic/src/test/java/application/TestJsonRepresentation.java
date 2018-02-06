package application;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.mcs.microservice.schematic.Schematic;
import de.mcs.microservice.utils.JacksonUtils;

public class TestJsonRepresentation {

  @Test
  public void test() throws IOException, InterruptedException {
    ObjectMapper jsonMapper = JacksonUtils.getJsonMapper();

    Schematic model = new Schematic();
    model.setSchematicName("schematic");
    assertEquals("schematic", model.getSchematicName());

    String[] tags = { "Tube", "Amp" };
    model.setTags(Arrays.asList(tags));

    String json = jsonMapper.writeValueAsString(model);
    System.out.println(json);

    Thread.sleep(1000);
    Schematic model2 = jsonMapper.readValue(json, Schematic.class);
    assertEquals("schematic", model2.getSchematicName());

    assertEquals(model.getCreatedAt().getTime(), model2.getCreatedAt().getTime());
    assertEquals(model.getlastModifiedAt().getTime(), model2.getlastModifiedAt().getTime());

    String json2 = jsonMapper.writeValueAsString(model2);
    System.out.println(json2);
  }

}
