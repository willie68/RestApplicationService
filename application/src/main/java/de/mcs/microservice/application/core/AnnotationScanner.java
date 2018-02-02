/**
 * MCS Media Computer Software
 * Copyright 2018 by Wilfried Klaas
 * Project: application
 * File: AnnotationScanner.java
 * EMail: W.Klaas@gmx.de
 * Created: 01.01.2018 wklaa_000
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
package de.mcs.microservice.application.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

import de.mcs.microservice.application.annotations.Application;
import de.mcs.microservice.application.annotations.DataModel;
import de.mcs.microservice.application.annotations.Index;
import de.mcs.microservice.application.annotations.Module;
import de.mcs.microservice.application.annotations.RestResource;
import de.mcs.microservice.application.core.model.ApplicationConfig;
import de.mcs.microservice.application.core.model.DataModelConfig;
import de.mcs.microservice.application.core.model.FieldConfig;
import de.mcs.microservice.application.core.model.ModuleConfig;
import de.mcs.microservice.application.core.model.RestApplication;
import de.mcs.microservice.application.core.model.RestDataModel;
import de.mcs.microservice.application.core.model.RestModule;
import de.mcs.microservice.application.core.model.RestResourceConfig;

/**
 * @author wklaa_000
 *
 */
public class AnnotationScanner {
  private List<ApplicationConfig> installedApps;
  private List<ModuleConfig> installedModules;
  private List<DataModelConfig> installedDataModels;
  private List<RestResourceConfig> installedRestResources;

  public AnnotationScanner() {
    installedApps = new ArrayList<>();
    installedModules = new ArrayList<>();
    installedDataModels = new ArrayList<>();
    installedRestResources = new ArrayList<>();
    getAllAnnotatedClasses();
  }

  private void getAllAnnotatedClasses() {
    for (URL url : getRootUrls()) {
      File f = new File(url.getPath());
      try {
        if (f.isDirectory()) {
          visitFile(f);
        } else {
          visitJar(url);
        }
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }

  }

  private List<URL> getRootUrls() {
    List<URL> result = new ArrayList<>();

    ClassLoader cl = Thread.currentThread().getContextClassLoader();
    while (cl != null) {
      if (cl instanceof URLClassLoader) {
        URL[] urls = ((URLClassLoader) cl).getURLs();
        result.addAll(Arrays.asList(urls));
      }
      cl = cl.getParent();
    }
    return result;
  }

  private void visitFile(File f) throws IOException {
    if (f.isDirectory()) {
      final File[] children = f.listFiles();
      if (children != null) {
        for (File child : children) {
          visitFile(child);
        }
      }
    } else if (f.getName().endsWith(".class")) {
      try (FileInputStream in = new FileInputStream(f)) {
        handleClass(in);
      }
    }
  }

  private void visitJar(URL url) throws IOException {
    try (InputStream urlIn = url.openStream(); JarInputStream jarIn = new JarInputStream(urlIn)) {
      JarEntry entry;
      while ((entry = jarIn.getNextJarEntry()) != null) {
        if (entry.getName().endsWith(".class")) {
          byte[] b = new byte[4096];
          ByteArrayOutputStream out = new ByteArrayOutputStream();
          int len = 0;
          while (len != -1) {
            len = jarIn.read(b);
            if (len > 0) {
              out.write(b, 0, len);
            }
          }
          out.flush();
          out.close();
          ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
          handleClass(in);
        }
      }
    }
  }

  private void handleClass(InputStream in) throws IOException {
    MyClassVisitor cv = new MyClassVisitor();
    new ClassReader(in).accept(cv, 0);

    ClassLoader cl = Thread.currentThread().getContextClassLoader();
    if (cv.hasApplicationAnnotation) {
      try {
        Class<?> loadClass = cl.loadClass(cv.className);
        Class<?>[] interfaces = loadClass.getInterfaces();
        boolean found = false;
        for (Class<?> myInterface : interfaces) {
          found = found || myInterface.equals(RestApplication.class);
        }
        if (found) {
          if (loadClass.isAnnotationPresent(Application.class)) {
            Application appAnnotation = loadClass.getAnnotation(Application.class);
            ApplicationConfig application = ApplicationConfig.create().setClassName(cv.className)
                .setAppAnnotation(appAnnotation);
            installedApps.add(application);
          }
        }

      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      }
    }
    if (cv.hasModuleAnnotation) {
      try {
        Class<?> loadClass = cl.loadClass(cv.className);
        Class<?>[] interfaces = loadClass.getInterfaces();
        boolean found = false;
        for (Class<?> myInterface : interfaces) {
          found = found || myInterface.equals(RestModule.class);
        }
        if (found) {
          if (loadClass.isAnnotationPresent(Module.class)) {
            Module modAnnotation = loadClass.getAnnotation(Module.class);
            ModuleConfig module = ModuleConfig.create().setClassName(cv.className).setModuleAnnotation(modAnnotation);
            installedModules.add(module);
          }
        }
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      }
    }
    if (cv.hasDataModelAnnotation) {
      try {
        Class<?> loadClass = cl.loadClass(cv.className);
        Class<?>[] interfaces = loadClass.getInterfaces();
        boolean found = false;
        for (Class<?> myInterface : interfaces) {
          found = found || myInterface.equals(RestDataModel.class);
        }
        if (found) {
          if (loadClass.isAnnotationPresent(DataModel.class)) {
            DataModel datAnnotation = loadClass.getAnnotation(DataModel.class);
            DataModelConfig dataModel = DataModelConfig.create().setClassName(cv.className)
                .setDataModelAnnotation(datAnnotation);
            installedDataModels.add(dataModel);

            Method[] methods = loadClass.getMethods();
            for (Method method : methods) {
              if (method.isAnnotationPresent(Index.class)) {
                Index idxAnnotation = method.getAnnotation(Index.class);
                if (idxAnnotation.name() != null) {
                  dataModel.addIndexField(
                      FieldConfig.create(idxAnnotation.name(), idxAnnotation.type(), idxAnnotation.fulltext()));
                }
              }
            }
          }
        }
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      }
    }
    if (cv.hasResourceAnnotation) {
      try {
        Class<?> loadClass = cl.loadClass(cv.className);
        Class<?>[] interfaces = loadClass.getInterfaces();
        if (loadClass.isAnnotationPresent(RestResource.class)) {
          RestResource resourceAnnotation = loadClass.getAnnotation(RestResource.class);
          RestResourceConfig restResource = RestResourceConfig.create().setClassName(cv.className)
              .setRestResourceAnnotation(resourceAnnotation);
          installedRestResources.add(restResource);
        }
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      }
    }
  }

  private class MyClassVisitor extends ClassVisitor {
    public boolean hasDataModelAnnotation = false;
    public boolean hasApplicationAnnotation = false;
    public boolean hasModuleAnnotation = false;
    public boolean hasResourceAnnotation = false;
    public String className;

    MyClassVisitor() {
      super(Opcodes.ASM6);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
      className = name.replace('/', '.');
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
      if (desc.equals("Lde/mcs/microservice/application/annotations/Application;")) {
        hasApplicationAnnotation = true;
      }
      if (desc.equals("Lde/mcs/microservice/application/annotations/Module;")) {
        hasModuleAnnotation = true;
      }
      if (desc.equals("Lde/mcs/microservice/application/annotations/DataModel;")) {
        hasDataModelAnnotation = true;
      }
      if (desc.equals("Lde/mcs/microservice/application/annotations/RestResource;")) {
        hasResourceAnnotation = true;
      }
      return null;
    }

  }

  /**
   * @return the installedApps
   */
  public List<ApplicationConfig> getInstalledApps() {
    return installedApps;
  }

  /**
   * @return the installedModules
   */
  public List<ModuleConfig> getInstalledModules() {
    return installedModules;
  }

  /**
   * @return the installedModules
   */
  public List<DataModelConfig> getInstalledDataModels() {
    return installedDataModels;
  }

  public List<RestResourceConfig> getInstalledRestResources() {
    return installedRestResources;
  }
}
