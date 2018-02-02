/**
 * MCS Media Computer Software
 * Copyright 2018 by Wilfried Klaas
 * Project: application
 * File: AbstractDataModelHooks.java
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
package de.mcs.microservice.application.core;

import java.util.List;

import de.mcs.microservice.application.core.model.Context;
import de.mcs.microservice.application.core.model.RestDataModel;
import de.mcs.microservice.application.core.model.RestDataModelHooks;

/**
 * @author wklaa_000
 *
 */
public abstract class AbstractRestDataModelHooks<T extends RestDataModel> implements RestDataModelHooks<T> {

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.mcs.microservice.application.core.model.RestDataModelHooks#beforeCreate(
   * de.mcs.microservice.application.core.model.RestDataModel,
   * de.mcs.microservice.application.core.model.Context)
   */
  @Override
  public T beforeCreate(T dataModel, Context context) {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.mcs.microservice.application.core.model.RestDataModelHooks#afterCreate(
   * de.mcs.microservice.application.core.model.RestDataModel,
   * de.mcs.microservice.application.core.model.Context)
   */
  @Override
  public T afterCreate(T dataModel, Context context) {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.mcs.microservice.application.core.model.RestDataModelHooks#beforeRead(
   * java.lang.String, de.mcs.microservice.application.core.model.Context)
   */
  @Override
  public String beforeRead(String id, Context context) {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.mcs.microservice.application.core.model.RestDataModelHooks#afterRead(de.
   * mcs.microservice.application.core.model.RestDataModel,
   * de.mcs.microservice.application.core.model.Context)
   */
  @Override
  public T afterRead(T dataModel, Context context) {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.mcs.microservice.application.core.model.RestDataModelHooks#beforeUpdate(
   * de.mcs.microservice.application.core.model.RestDataModel,
   * de.mcs.microservice.application.core.model.Context)
   */
  @Override
  public T beforeUpdate(T dataModel, Context context) {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.mcs.microservice.application.core.model.RestDataModelHooks#afterUpdate(
   * de.mcs.microservice.application.core.model.RestDataModel,
   * de.mcs.microservice.application.core.model.Context)
   */
  @Override
  public T afterUpdate(T dataModel, Context context) {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.mcs.microservice.application.core.model.RestDataModelHooks#beforeDelete(
   * java.lang.String, de.mcs.microservice.application.core.model.Context)
   */
  @Override
  public String beforeDelete(String id, Context context) {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.mcs.microservice.application.core.model.RestDataModelHooks#afterDelete(
   * de.mcs.microservice.application.core.model.RestDataModel,
   * de.mcs.microservice.application.core.model.Context)
   */
  @Override
  public T afterDelete(T dataModel, Context context) {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.mcs.microservice.application.core.model.RestDataModelHooks#beforeFind(
   * java.lang.String, de.mcs.microservice.application.core.model.Context)
   */
  @Override
  public String beforeFind(String query, Context context) {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.mcs.microservice.application.core.model.RestDataModelHooks#afterFind(
   * java.lang.String, java.util.List,
   * de.mcs.microservice.application.core.model.Context)
   */
  @Override
  public List<T> afterFind(String query, List<T> list, Context context) {
    // TODO Auto-generated method stub
    return null;
  }

}
