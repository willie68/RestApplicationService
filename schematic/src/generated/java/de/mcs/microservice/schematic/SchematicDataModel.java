package de.mcs.microservice.schematic;

import java.util.*;
import de.mcs.microservice.application.annotations.DataModel;
import de.mcs.microservice.application.annotations.Index;
import de.mcs.microservice.application.api.BlobDescription;
import de.mcs.microservice.application.core.model.RestDataModel;
import de.mcs.microservice.application.core.AbstractRestDataModel;


@DataModel(name = "SchematicDataModel", description = "this is the data model for the schematic files", moduleName = "SchematicModule"
  , dataHooks = de.mcs.microservice.schematic.SchematicDataModelHooks.class )
public class SchematicDataModel extends AbstractRestDataModel implements RestDataModel {

  @Override
  public String getModuleName() {
    return "SchematicModule";
  }
  
  @Override
  public String getModelName() {
    return "SchematicDataModel";
  }
  

  @Index(name = "schematicName", type = "String"  , fulltext = true ) 
  public String getSchematicName() {
    return (String) this.any().get("schematicName");
  }
  
  public void setSchematicName(String schematicName) {
    this.setValue("schematicName", schematicName);
  }

  @Index(name = "tags", type = "List<String>"  , fulltext = true ) 
  public List<String> getTags() {
    return (List<String>) this.any().get("tags");
  }
  
  public void setTags(List<String> tags) {
    this.setValue("tags", tags);
  }

  @Index(name = "filename", type = "String"  , fulltext = true ) 
  public String getFilename() {
    return (String) this.any().get("filename");
  }
  
  public void setFilename(String filename) {
    this.setValue("filename", filename);
  }

  public BlobDescription getFile() {
    return (BlobDescription) this.any().get("file");
  }
  
  public void setFile(BlobDescription file) {
    this.setValue("file", file);
  }
}