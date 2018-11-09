package de.mcs.microservice.schematic;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;

import de.mcs.microservice.application.api.LogLevel;
import de.mcs.microservice.application.api.ServerAPI;
import de.mcs.microservice.application.core.AbstractRestDataModelHooks;
import de.mcs.microservice.application.core.model.Context;
import de.mcs.microservice.application.core.model.RestDataModelHooks;
import de.mcs.microservice.application.query.SimpleQuery;

public class SchematicHooks extends AbstractRestDataModelHooks<Schematic> implements RestDataModelHooks<Schematic> {

  @Override
  public Schematic beforeCreate(Schematic dataModel, Context context) {
    ServerAPI serverApi = MyService.getServerApi();
    serverApi.log(LogLevel.INFO, context, "beforeCreate");
    List<String> tags = dataModel.getTags();
    for (String tag : tags) {
      serverApi.log(LogLevel.INFO, context, tag);
      SimpleQuery simpleQuery = new SimpleQuery();
      simpleQuery.set("tag", "*");
      try {
        Context newContext = context.clone();
        newContext.setModelName(SchematicTags.class.getSimpleName());
        List<SchematicTags> dbTags = serverApi.find(simpleQuery.toJson(), newContext, SchematicTags.class);
        if (dbTags != null) {
          for (SchematicTags schematicTags : dbTags) {
            serverApi.log(LogLevel.INFO, context, tag);
          }
        }
      } catch (JsonProcessingException e) {
        serverApi.logError(LogLevel.ERROR, context, "exception creating json", e);
      }
    }

    return null;
  }

  @Override
  public Schematic afterCreate(Schematic dataModel, Context context) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String beforeRead(String id, Context context) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Schematic afterRead(Schematic dataModel, Context context) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Schematic beforeUpdate(Schematic dataModel, Context context) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Schematic afterUpdate(Schematic dataModel, Context context) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String beforeDelete(String id, Context context) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Schematic afterDelete(Schematic dataModel, Context context) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String beforeFind(String query, Context context) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<Schematic> afterFind(String query, List<Schematic> list, Context context) {
    // TODO Auto-generated method stub
    return null;
  }
}
