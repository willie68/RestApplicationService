package de.mcs.microservice.test;

import java.util.*;
import de.mcs.microservice.application.annotations.DataModel;
import de.mcs.microservice.application.annotations.Index;
import de.mcs.microservice.application.api.BlobDescription;
import de.mcs.microservice.application.core.model.RestDataModel;
import de.mcs.microservice.application.core.AbstractRestDataModel;


@DataModel(name = "TestModel", description = "this is the data model for the test app", moduleName = "TestModule"
  , dataHooks = de.mcs.microservice.test.TestModelHooks.class   , dataStorage = de.mcs.microservice.application.storage.NitriteDataStorage.class )
public class TestModel extends AbstractRestDataModel implements RestDataModel {

  @Override
  public String getModuleName() {
    return "TestModule";
  }
  
  @Override
  public String getModelName() {
    return "TestModel";
  }
  

  @Index(name = "testName", type = "String"  , fulltext = true ) 
  public String getTestName() {
    return (String) this.any().get("testName");
  }
  
  public void setTestName(String testName) {
    this.setValue("testName", testName);
  }

  @Index(name = "tags", type = "List<String>"  , fulltext = true ) 
  public List<String> getTags() {
    return (List<String>) this.any().get("tags");
  }
  
  public void setTags(List<String> tags) {
    this.setValue("tags", tags);
  }

  public BlobDescription getFile() {
    return (BlobDescription) this.any().get("file");
  }
  
  public void setFile(BlobDescription file) {
    this.setValue("file", file);
  }

  @Index(name = "testDate", type = "Date"  ) 
  public Date getTestDate() {
    return (Date) this.any().get("testDate");
  }
  
  public void setTestDate(Date testDate) {
    this.setValue("testDate", testDate);
  }

  @Index(name = "testBoolean", type = "boolean"  ) 
  public boolean getTestBoolean() {
    return (boolean) this.any().get("testBoolean");
  }
  
  public void setTestBoolean(boolean testBoolean) {
    this.setValue("testBoolean", testBoolean);
  }
}