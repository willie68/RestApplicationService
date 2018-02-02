package de.mcs.microservice.schematic;

import java.util.*;
import de.mcs.microservice.application.core.AbstractDataStorage;
import de.mcs.microservice.application.core.model.Context;
import de.mcs.microservice.application.core.model.DataStorage;
import de.mcs.microservice.schematic.SchematicDataModel;

public class SchematicNitritStorage extends AbstractDataStorage<SchematicDataModel> implements DataStorage<SchematicDataModel> {

  @Override
  public String create(SchematicDataModel model, Context context) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public SchematicDataModel read(String id, Context context) {
    // TODO Auto-generated method stub
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