package de.mcs.microservice.schematic;

import java.io.FileNotFoundException;
import java.util.List;

import de.mcs.microservice.application.api.LogLevel;
import de.mcs.microservice.application.api.ServerAPI;
import de.mcs.microservice.application.core.AbstractRestDataModelHooks;
import de.mcs.microservice.application.core.model.Context;
import de.mcs.microservice.application.core.model.RestDataModelHooks;

public class SchematicDataModelHooks extends AbstractRestDataModelHooks<SchematicDataModel>
    implements RestDataModelHooks<SchematicDataModel> {

  @Override
  public SchematicDataModel beforeCreate(SchematicDataModel dataModel, Context context) {
    ServerAPI serverApi = MyService.getServerApi();
    serverApi.log(LogLevel.INFO, context, "before create: this is a log message.");
    serverApi.logError(LogLevel.ERROR, context, "this is a log message.", new FileNotFoundException("file not found"));
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
