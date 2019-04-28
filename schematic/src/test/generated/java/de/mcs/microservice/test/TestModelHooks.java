package de.mcs.microservice.test;

import java.util.*;
import de.mcs.microservice.application.core.AbstractRestDataModelHooks;
import de.mcs.microservice.application.core.model.Context;
import de.mcs.microservice.application.core.model.RestDataModelHooks;
import de.mcs.microservice.test.TestModel;

public class TestModelHooks extends AbstractRestDataModelHooks<TestModel>
    implements RestDataModelHooks<TestModel> {

  @Override
  public TestModel beforeCreate(TestModel dataModel, Context context) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public TestModel afterCreate(TestModel dataModel, Context context) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String beforeRead(String id, Context context) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public TestModel afterRead(TestModel dataModel, Context context) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public TestModel beforeUpdate(TestModel dataModel, Context context) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public TestModel afterUpdate(TestModel dataModel, Context context) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String beforeDelete(String id, Context context) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public TestModel afterDelete(TestModel dataModel, Context context) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String beforeFind(String query, Context context) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<TestModel> afterFind(String query, List<TestModel> list, Context context) {
    // TODO Auto-generated method stub
    return null;
  }
}