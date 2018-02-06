package de.mcs.microservice.schematic;

import java.util.*;

import de.mcs.microservice.application.api.LogLevel;
import de.mcs.microservice.application.core.AbstractRestDataModelHooks;
import de.mcs.microservice.application.core.model.Context;
import de.mcs.microservice.application.core.model.RestDataModelHooks;
import de.mcs.microservice.schematic.Schematic;

public class SchematicHooks extends AbstractRestDataModelHooks<Schematic> implements RestDataModelHooks<Schematic> {

  @Override
  public Schematic beforeCreate(Schematic dataModel, Context context) {
    MyService.getServerApi().log(LogLevel.INFO, context, "beforeCreate");
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
