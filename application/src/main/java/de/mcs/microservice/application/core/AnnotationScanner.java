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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.reflections.Reflections;

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
  private String searchPackage;

  public AnnotationScanner(String searchPackage) {
    this.searchPackage = searchPackage;
    installedApps = new ArrayList<>();
    installedModules = new ArrayList<>();
    installedDataModels = new ArrayList<>();
    installedRestResources = new ArrayList<>();
    getAllAnnotatedClasses();
  }

  private void getAllAnnotatedClasses() {
    ClassLoader cl = Thread.currentThread().getContextClassLoader();

    Reflections reflections = new Reflections(searchPackage);
    scanInstalledApps(reflections);
    scanInstalledModules(reflections);
    scanInstalledDataModels(reflections);
    scanInstalledRestResources(reflections);
  }

  private void scanInstalledApps(Reflections reflections) {
    Set<Class<?>> applicationTypes = reflections
        .getTypesAnnotatedWith(de.mcs.microservice.application.annotations.Application.class);

    for (Class<?> applicationClass : applicationTypes) {
      Class<?> loadClass = applicationClass;
      Class<?>[] interfaces = loadClass.getInterfaces();
      boolean found = false;
      for (Class<?> myInterface : interfaces) {
        found = found || myInterface.equals(RestApplication.class);
      }
      if (found) {
        if (loadClass.isAnnotationPresent(Application.class)) {
          Application appAnnotation = loadClass.getAnnotation(Application.class);
          ApplicationConfig application = ApplicationConfig.create().setClassName(applicationClass.getName())
              .setAppAnnotation(appAnnotation);
          installedApps.add(application);
        }
      }
    }
  }

  private void scanInstalledModules(Reflections reflections) {
    Set<Class<?>> moduleTypes = reflections
        .getTypesAnnotatedWith(de.mcs.microservice.application.annotations.Module.class);

    for (Class<?> moduleClass : moduleTypes) {
      Class<?> loadClass = moduleClass;
      Class<?>[] interfaces = loadClass.getInterfaces();
      boolean found = false;
      for (Class<?> myInterface : interfaces) {
        found = found || myInterface.equals(RestModule.class);
      }
      if (found) {
        if (loadClass.isAnnotationPresent(Module.class)) {
          Module modAnnotation = loadClass.getAnnotation(Module.class);
          ModuleConfig module = ModuleConfig.create().setClassName(moduleClass.getName())
              .setModuleAnnotation(modAnnotation);
          installedModules.add(module);
        }
      }
    }
  }

  private void scanInstalledDataModels(Reflections reflections) {
    Set<Class<?>> dataModelTypes = reflections
        .getTypesAnnotatedWith(de.mcs.microservice.application.annotations.DataModel.class);

    for (Class<?> datamodelClass : dataModelTypes) {
      Class<?> loadClass = datamodelClass;
      Class<?>[] interfaces = loadClass.getInterfaces();
      boolean found = false;
      for (Class<?> myInterface : interfaces) {
        found = found || myInterface.equals(RestDataModel.class);
      }
      if (found) {
        if (loadClass.isAnnotationPresent(DataModel.class)) {
          DataModel datAnnotation = loadClass.getAnnotation(DataModel.class);
          DataModelConfig dataModel = DataModelConfig.create().setClassName(datamodelClass.getName())
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
    }
  }

  private void scanInstalledRestResources(Reflections reflections) {
    Set<Class<?>> restResourceTypes = reflections
        .getTypesAnnotatedWith(de.mcs.microservice.application.annotations.RestResource.class);

    for (Class<?> restResourceClass : restResourceTypes) {
      Class<?> loadClass = restResourceClass;
      Class<?>[] interfaces = loadClass.getInterfaces();
      if (loadClass.isAnnotationPresent(RestResource.class)) {
        RestResource resourceAnnotation = loadClass.getAnnotation(RestResource.class);
        RestResourceConfig restResource = RestResourceConfig.create().setClassName(restResourceClass.getName())
            .setRestResourceAnnotation(resourceAnnotation);
        installedRestResources.add(restResource);
      }
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
