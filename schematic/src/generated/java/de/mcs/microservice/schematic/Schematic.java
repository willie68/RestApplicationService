package de.mcs.microservice.schematic;

import java.util.*;
import de.mcs.microservice.application.annotations.DataModel;
import de.mcs.microservice.application.annotations.Index;
import de.mcs.microservice.application.api.BlobDescription;
import de.mcs.microservice.application.core.model.RestDataModel;
import de.mcs.microservice.application.core.AbstractRestDataModel;


@DataModel(name = "Schematic", description = "this is the data model for the schematic files", moduleName = "SchematicModule"
  , dataHooks = de.mcs.microservice.schematic.SchematicHooks.class   , dataStorage = de.mcs.microservice.application.storage.NitriteDataStorage.class )
public class Schematic extends AbstractRestDataModel implements RestDataModel {

  @Override
  public String getModuleName() {
    return "SchematicModule";
  }
  
  @Override
  public String getModelName() {
    return "Schematic";
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

  @Index(name = "buildIn", type = "Date"  ) 
  public Date getBuildIn() {
    return (Date) this.any().get("buildIn");
  }
  
  public void setBuildIn(Date buildIn) {
    this.setValue("buildIn", buildIn);
  }

  @Index(name = "buildTo", type = "Date"  ) 
  public Date getBuildTo() {
    return (Date) this.any().get("buildTo");
  }
  
  public void setBuildTo(Date buildTo) {
    this.setValue("buildTo", buildTo);
  }

  @Index(name = "model", type = "String"  , fulltext = true ) 
  public String getModel() {
    return (String) this.any().get("model");
  }
  
  public void setModel(String model) {
    this.setValue("model", model);
  }

  @Index(name = "manufacturer", type = "String"  , fulltext = true ) 
  public String getManufacturer() {
    return (String) this.any().get("manufacturer");
  }
  
  public void setManufacturer(String manufacturer) {
    this.setValue("manufacturer", manufacturer);
  }

  @Index(name = "privateFile", type = "boolean"  ) 
  public boolean getPrivateFile() {
    return (boolean) this.any().get("privateFile");
  }
  
  public void setPrivateFile(boolean privateFile) {
    this.setValue("privateFile", privateFile);
  }

  @Index(name = "subtitle", type = "String"  , fulltext = true ) 
  public String getSubtitle() {
    return (String) this.any().get("subtitle");
  }
  
  public void setSubtitle(String subtitle) {
    this.setValue("subtitle", subtitle);
  }
}