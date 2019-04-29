package de.mcs.microservice.schematic;

import java.util.*;
import de.mcs.microservice.application.core.AbstractRestDataModelHooks;
import de.mcs.microservice.application.core.model.Context;
import de.mcs.microservice.application.core.model.RestDataModelHooks;
import de.mcs.microservice.schematic.AttachmentDataModel;

public class AttachmentDataModelHooks extends AbstractRestDataModelHooks<AttachmentDataModel>
    implements RestDataModelHooks<AttachmentDataModel> {

  @Override
  public AttachmentDataModel beforeCreate(AttachmentDataModel dataModel, Context context) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public AttachmentDataModel afterCreate(AttachmentDataModel dataModel, Context context) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String beforeRead(String id, Context context) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public AttachmentDataModel afterRead(AttachmentDataModel dataModel, Context context) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public AttachmentDataModel beforeUpdate(AttachmentDataModel dataModel, Context context) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public AttachmentDataModel afterUpdate(AttachmentDataModel dataModel, Context context) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String beforeDelete(String id, Context context) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public AttachmentDataModel afterDelete(AttachmentDataModel dataModel, Context context) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String beforeFind(String query, Context context) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<AttachmentDataModel> afterFind(String query, List<AttachmentDataModel> list, Context context) {
    // TODO Auto-generated method stub
    return null;
  }
}