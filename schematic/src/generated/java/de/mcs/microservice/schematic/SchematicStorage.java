package de.mcs.microservice.schematic;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.mcs.microservice.application.core.AbstractDataStorage;
import de.mcs.microservice.application.core.model.Context;
import de.mcs.microservice.application.core.model.DataStorage;
import de.mcs.microservice.utils.JacksonUtils;

public class SchematicStorage extends AbstractDataStorage<SchematicDataModel>
    implements DataStorage<SchematicDataModel> {

  private File baseFolder;
  private ObjectMapper jsonMapper;

  public SchematicStorage() {
    baseFolder = new File("tmp/data/");
    baseFolder.mkdirs();
    jsonMapper = JacksonUtils.getJsonMapper();
  }

  @Override
  public String create(SchematicDataModel model, Context context) {
    // TODO Auto-generated method stub
    model.setId(UUID.randomUUID().toString());
    try {
      jsonMapper.writeValue(new File(baseFolder, String.format("%s.json", model.getId())), model);
      return model.getId();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public SchematicDataModel read(String id, Context context) {
    // TODO Auto-generated method stub
    File file = new File(baseFolder, String.format("%s.json", id));
    if (file.exists()) {
      try {
        SchematicDataModel model = jsonMapper.readValue(file, SchematicDataModel.class);
        return model;
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return null;
  }

  @Override
  public boolean update(SchematicDataModel model, Context context) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public SchematicDataModel delete(String id, Context context) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<SchematicDataModel> find(String query, Context context) {
    // TODO Auto-generated method stub
    return null;
  }
}
