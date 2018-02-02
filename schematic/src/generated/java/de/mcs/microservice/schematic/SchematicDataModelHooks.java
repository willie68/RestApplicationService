package de.mcs.microservice.schematic;

import java.util.*;
import de.mcs.microservice.application.core.AbstractRestDataModelHooks;
import de.mcs.microservice.application.core.model.Context;
import de.mcs.microservice.application.core.model.RestDataModelHooks;
import de.mcs.microservice.schematic.SchematicDataModel;

public class SchematicDataModelHooks extends AbstractRestDataModelHooks<SchematicDataModel>
    implements RestDataModelHooks<SchematicDataModel> {

  @Override
  public SchematicDataModel beforeCreate(SchematicDataModel dataModel, Context context) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public SchematicDataModel afterCreate(SchematicDataModel dataModel, Context context) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String beforeRead(String id, Context context) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public SchematicDataModel afterRead(SchematicDataModel dataModel, Context context) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public SchematicDataModel beforeUpdate(SchematicDataModel dataModel, Context context) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public SchematicDataModel afterUpdate(SchematicDataModel dataModel, Context context) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String beforeDelete(String id, Context context) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public SchematicDataModel afterDelete(SchematicDataModel dataModel, Context context) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String beforeFind(String query, Context context) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<SchematicDataModel> afterFind(String query, List<SchematicDataModel> list, Context context) {
    // TODO Auto-generated method stub
    return null;
  }
}