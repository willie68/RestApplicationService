package de.mcs.microservice.schematic;

import java.util.*;
import de.mcs.microservice.application.annotations.DataModel;
import de.mcs.microservice.application.annotations.Index;
import de.mcs.microservice.application.api.BlobDescription;
import de.mcs.microservice.application.core.model.RestDataModel;
import de.mcs.microservice.application.core.AbstractRestDataModel;


@DataModel(name = "AttachmentDataModel", description = "this is the data model for the attachment files", moduleName = "SchematicModule"
  , visible = false   , dataHooks = de.mcs.microservice.schematic.AttachmentDataModelHooks.class   , dataStorage = de.mcs.microservice.application.storage.NitriteDataStorage.class )
public class AttachmentDataModel extends AbstractRestDataModel implements RestDataModel {

  @Override
  public String getModuleName() {
    return "SchematicModule";
  }
  
  @Override
  public String getModelName() {
    return "AttachmentDataModel";
  }
  

  public String getAttachmentName() {
    return (String) this.any().get("attachmentName");
  }
  
  public void setAttachmentName(String attachmentName) {
    this.setValue("attachmentName", attachmentName);
  }

  public String getPath() {
    return (String) this.any().get("path");
  }
  
  public void setPath(String path) {
    this.setValue("path", path);
  }
}