/**
 * MCS Media Computer Software
 * Copyright 2018 by Wilfried Klaas
 * Project: application
 * File: Context.java
 * EMail: W.Klaas@gmx.de
 * Created: 03.01.2018 wklaa_000
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
package de.mcs.microservice.application.core.model;

/**
 * @author wklaa_000
 *
 */
public class Context {

  public static Context create() {
    return new Context();
  }

  private String applicationName;
  private String moduleName;
  private String modelName;
  private String tenant;

  public Context setApplicationName(String applicationName) {
    this.applicationName = applicationName;
    return this;
  }

  public String getApplicationName() {
    return applicationName;
  }

  /**
   * @return the moduleName
   */
  public String getModuleName() {
    return moduleName;
  }

  /**
   * @param moduleName
   *          the moduleName to set
   * @return
   */
  public Context setModuleName(String moduleName) {
    this.moduleName = moduleName;
    return this;
  }

  /**
   * @return the modelName
   */
  public String getModelName() {
    return modelName;
  }

  /**
   * @param modelName
   *          the modelName to set
   * @return
   */
  public Context setModelName(String modelName) {
    this.modelName = modelName;
    return this;
  }

  /**
   * setting the tenant for this call
   * 
   * @param tenant
   *          the tenant of the call
   * @return the Context
   */
  public Context setTenant(String tenant) {
    this.tenant = tenant;
    return this;
  }

  /**
   * getting the actual tenant for this call. <code>null</code> if the application is not a multi tenant app.
   * 
   * @return tenant
   */
  public String getTenant() {
    return this.tenant;
  }

}
