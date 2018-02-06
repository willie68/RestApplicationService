package de.mcs.microservice.schematic;

import java.util.*;
import de.mcs.microservice.application.core.AbstractDataStorage;
import de.mcs.microservice.application.core.model.Context;
import de.mcs.microservice.application.core.model.DataStorage;
import de.mcs.microservice.schematic.SchematicDataModel2;

public class SchematicStorage2 extends AbstractDataStorage<SchematicDataModel2> implements DataStorage<SchematicDataModel2> {

  @Override
  public String create(SchematicDataModel2 model, Context context) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public SchematicDataModel2 read(String id, Context context) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean update(SchematicDataModel2 model, Context context) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public SchematicDataModel2 delete(String id, Context context) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<SchematicDataModel2> find(String query, Context context) {
    // TODO Auto-generated method stub
    return null;
  }

}