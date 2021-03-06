package de.mcs.microservice.test;

import java.util.*;
import de.mcs.microservice.application.annotations.DataModel;
import de.mcs.microservice.application.annotations.Index;
import de.mcs.microservice.application.api.BlobDescription;
import de.mcs.microservice.application.core.model.RestDataModel;
import de.mcs.microservice.application.core.AbstractRestDataModel;


@DataModel(name = "TestTags", description = "tagging system", moduleName = "TestModule"
  , dataStorage = de.mcs.microservice.application.storage.NitriteDataStorage.class )
public class TestTags extends AbstractRestDataModel implements RestDataModel {

  @Override
  public String getModuleName() {
    return "TestModule";
  }
  
  @Override
  public String getModelName() {
    return "TestTags";
  }
  

  @Index(name = "tag", type = "String"  ) 
  public String getTag() {
    return (String) this.any().get("tag");
  }
  
  public void setTag(String tag) {
    this.setValue("tag", tag);
  }

  @Index(name = "description", type = "String"  , fulltext = true ) 
  public String getDescription() {
    return (String) this.any().get("description");
  }
  
  public void setDescription(String description) {
    this.setValue("description", description);
  }
}