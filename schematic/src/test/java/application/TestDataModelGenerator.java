package application;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import de.mcs.microservice.application.generator.DataModelGenerator;
import de.mcs.utils.Files;

public class TestDataModelGenerator {

  private String[] generatedFiles = new String[] { "de/mcs/microservice/test/MyTestService.java",
      "de/mcs/microservice/test/MyTestServiceConfig.java", "de/mcs/microservice/test/TestModel.java",
      "de/mcs/microservice/test/TestModelHooks.java", "de/mcs/microservice/test/TestModule.java",
      "de/mcs/microservice/test/TestTags.java" };

  @Test
  public void test() throws Exception {
    File srcFolder = new File("src/test/generated/java");
    if (srcFolder.exists()) {
      Files.remove(srcFolder, true);
    }
    DataModelGenerator.main(new String[] { "TestModel", "server", "src/test/generated/java" });

    for (String testFileName : generatedFiles) {
      System.out.printf("testing file: %s%n", testFileName);
      File testFile = new File(srcFolder, testFileName);
      assertTrue(testFile.exists());
    }
  }

}
