/**
 * MCS Media Computer Software
 * Copyright 2018 by Wilfried Klaas
 * Project: application
 * File: DataModelGenerator.java
 * EMail: W.Klaas@gmx.de
 * Created: 05.01.2018 wklaa_000
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package de.mcs.microservice.application.generator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.mcs.microservice.utils.JacksonUtils;

/**
 * @author wklaa_000
 *
 */
public class DataModelGenerator {
  enum OVERWRITE_MODE {
    overwrite, merge, none
  }

  enum MODE {
    server, client
  }

  private ObjectMapper ymlMapper;
  private GeneratorUtility utility;
  private MODE mode;
  private static File srcRootPath;

  public static void main(String[] args) throws Exception {
    File dataFile = new File("testdata"); // , "SchematicModel.yml");
    if (args.length > 0) {
      dataFile = new File(args[0]);
    }
    if (!dataFile.exists()) {
      throw new FileNotFoundException(String.format("dataFile \"%s\" not found", dataFile.getAbsolutePath()));
    }
    MODE mode = MODE.valueOf(args[1]);
    if (!MODE.server.equals(mode)) {
      throw new Exception("client model generation not implemented yet.");

    }
    srcRootPath = new File("src/generated/java");
    if (args.length > 2) {
      srcRootPath = new File(args[2]);
    }
    DataModelGenerator generator = new DataModelGenerator();
    generator.setMode(mode);
    generator.start(dataFile);
  }

  private void setMode(MODE mode) {
    this.mode = mode;
  }

  public DataModelGenerator() throws IOException {
    Properties p = new Properties();
    p.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("velocity.properties"));
    p.setProperty("file.resource.loader.path", "/templates");
    Velocity.init(p);

    ymlMapper = JacksonUtils.getYmlMapper();
    utility = new GeneratorUtility();
  }

  public void start(File modelFile) throws Exception {
    if (modelFile.isDirectory()) {
      File[] ymlFiles = modelFile.listFiles((File a1, String a2) -> {
        return a2.toLowerCase().endsWith(".yml");
      });
      for (File ymlFile : ymlFiles) {
        HashMap value = ymlMapper.readValue(ymlFile, HashMap.class);
        processFile(ymlFile, value);
      }
    } else {
      HashMap value = ymlMapper.readValue(modelFile, HashMap.class);
      processFile(modelFile, value);
    }
  }

  public void processFile(File ymlFile, HashMap value) throws Exception {
    String templateName = String.format("%s.vm", value.get("type"));
    Template template = Velocity.getTemplate(String.format("templates/%s/%s", mode.name(), templateName));

    String modeStr = (String) value.getOrDefault("mode", "merge");
    OVERWRITE_MODE mode = OVERWRITE_MODE.valueOf(modeStr);

    String className = (String) value.get("name");

    VelocityContext context = new VelocityContext();
    context.put("class", value);
    context.put("utility", utility);

    String packageStr = (String) value.get("package");
    if (StringUtils.isEmpty(packageStr)) {
      throw new Exception(String.format("package must be given. File: \"%s\"", ymlFile.getName()));
    }
    File srcPath = new File(srcRootPath, packageStr.replace('.', '/'));
    srcPath.mkdirs();
    File sourceFile = new File(srcPath, String.format("%s.java", className));
    boolean write = true;
    if (sourceFile.exists()) {
      if (OVERWRITE_MODE.none.equals(mode)) {
        write = false;
      }
      if (OVERWRITE_MODE.merge.equals(mode)) {
        sourceFile = new File(srcPath, String.format("%s.java.merge", className));
      }
    }
    if (write) {
      try (BufferedWriter writer = new BufferedWriter(new FileWriter(sourceFile))) {
        template.merge(context, writer);
        writer.flush();
      }

      System.out.printf("Class \"%s\" generated! File: \"%s\"\n\r", className, sourceFile.getName());
    }
  }

}
