package de.mcs.microservice.schematic;

import java.util.*;
import de.mcs.microservice.application.annotations.DataModel;
import de.mcs.microservice.application.annotations.Index;
import de.mcs.microservice.application.api.BlobDescription;
import de.mcs.microservice.application.core.model.RestDataModel;
import de.mcs.microservice.application.core.AbstractRestDataModel;


@DataModel(name = "Manufacturer", description = "holds the manufacturer of a schematic file", moduleName = "SchematicModule"
  , dataStorage = de.mcs.microservice.application.storage.NitriteDataStorage.class )
public class Manufacturer extends AbstractRestDataModel implements RestDataModel {

  @Override
  public String getModuleName() {
    return "SchematicModule";
  }
  
  @Override
  public String getModelName() {
    return "Manufacturer";
  }
  

  @Index(name = "manufacturerName", type = "String"  ) 
  public String getManufacturerName() {
    return (String) this.any().get("manufacturerName");
  }
  
  public void setManufacturerName(String manufacturerName) {
    this.setValue("manufacturerName", manufacturerName);
  }

  @Index(name = "description", type = "String"  , fulltext = true ) 
  public String getDescription() {
    return (String) this.any().get("description");
  }
  
  public void setDescription(String description) {
    this.setValue("description", description);
  }
}