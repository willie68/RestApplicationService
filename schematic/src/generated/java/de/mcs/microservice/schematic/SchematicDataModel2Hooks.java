package de.mcs.microservice.schematic;

import java.io.FileNotFoundException;
import java.util.List;

import de.mcs.microservice.application.api.LogLevel;
import de.mcs.microservice.application.api.ServerAPI;
import de.mcs.microservice.application.core.AbstractRestDataModelHooks;
import de.mcs.microservice.application.core.model.Context;
import de.mcs.microservice.application.core.model.RestDataModelHooks;

public class SchematicDataModel2Hooks extends AbstractRestDataModelHooks<SchematicDataModel2>
    implements RestDataModelHooks<SchematicDataModel2> {

  @Override
  public SchematicDataModel2 beforeCreate(SchematicDataModel2 dataModel, Context context) {
    ServerAPI serverApi = MyService.getServerApi();
    serverApi.log(LogLevel.INFO, context, "this is a log message.");
    serverApi.logError(LogLevel.ERROR, context, "this is a log message.", new FileNotFoundException("file not found"));
    return null;
  }

  @Override
  public SchematicDataModel2 afterCreate(SchematicDataModel2 dataModel, Context context) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String beforeRead(String id, Context context) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public SchematicDataModel2 afterRead(SchematicDataModel2 dataModel, Context context) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public SchematicDataModel2 beforeUpdate(SchematicDataModel2 dataModel, Context context) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public SchematicDataModel2 afterUpdate(SchematicDataModel2 dataModel, Context context) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String beforeDelete(String id, Context context) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public SchematicDataModel2 afterDelete(SchematicDataModel2 dataModel, Context context) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String beforeFind(String query, Context context) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<SchematicDataModel2> afterFind(String query, List<SchematicDataModel2> list, Context context) {
    // TODO Auto-generated method stub
    return null;
  }
}
