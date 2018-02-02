/**
 * MCS Media Computer Software
 * Copyright 2018 by Wilfried Klaas
 * Project: application
 * File: AbstractDataStorage.java
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
package de.mcs.microservice.application.core;

import java.io.InputStream;
import java.util.List;

import de.mcs.microservice.application.api.BlobDescription;
import de.mcs.microservice.application.core.model.Context;
import de.mcs.microservice.application.core.model.DataStorage;
import de.mcs.microservice.application.core.model.RestDataModel;

/**
 * @author wklaa_000
 *
 */
public abstract class AbstractDataStorage<T extends RestDataModel> implements DataStorage<T> {

  /*
   * (non-Javadoc)
   * 
   * @see de.mcs.microservice.application.core.model.DataStorage#create(de.mcs.
   * microservice.application.core.RestDataModel,
   * de.mcs.microservice.application.core.Context)
   */
  @Override
  public String create(T model, Context context) {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.mcs.microservice.application.core.model.DataStorage#read(java.lang.
   * String, de.mcs.microservice.application.core.Context)
   */
  @Override
  public T read(String id, Context context) {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.mcs.microservice.application.core.model.DataStorage#update(de.mcs.
   * microservice.application.core.RestDataModel,
   * de.mcs.microservice.application.core.Context)
   */
  @Override
  public boolean update(T model, Context context) {
    // TODO Auto-generated method stub
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.mcs.microservice.application.core.model.DataStorage#delete(java.lang.
   * String, de.mcs.microservice.application.core.Context)
   */
  @Override
  public T delete(String id, Context context) {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.mcs.microservice.application.core.model.DataStorage#find(java.lang.
   * String, de.mcs.microservice.application.core.model.Context)
   */
  @Override
  public List<T> find(String query, Context context) {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.mcs.microservice.application.core.model.DataStorage#saveBlob(de.mcs.
   * microservice.application.core.model.RestDataModel, java.lang.String,
   * de.mcs.microservice.application.api.BlobDescription, java.io.InputStream,
   * long, de.mcs.microservice.application.core.model.Context)
   */
  @Override
  public BlobDescription saveBlob(T dbModel, String fieldname, BlobDescription blobDescription,
      InputStream fileInputStream, long contentLength, Context context) {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.mcs.microservice.application.core.model.DataStorage#getBlobDescription(de.
   * mcs.microservice.application.core.model.RestDataModel, java.lang.String,
   * de.mcs.microservice.application.core.model.Context)
   */
  @Override
  public BlobDescription getBlobDescription(T dbModel, String fieldname, Context context) {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see de.mcs.microservice.application.core.model.DataStorage#hasBlob(de.mcs.
   * microservice.application.core.model.RestDataModel, java.lang.String,
   * de.mcs.microservice.application.core.model.Context)
   */
  @Override
  public boolean hasBlob(T dbModel, String fieldname, Context context) {
    // TODO Auto-generated method stub
    return false;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.mcs.microservice.application.core.model.DataStorage#getBlobInputStream(de.
   * mcs.microservice.application.core.model.RestDataModel, java.lang.String,
   * de.mcs.microservice.application.core.model.Context)
   */
  @Override
  public InputStream getBlobInputStream(T dbModel, String fieldname, Context context) {
    // TODO Auto-generated method stub
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.mcs.microservice.application.core.model.DataStorage#deleteBlob(de.mcs.
   * microservice.application.core.model.RestDataModel, java.lang.String,
   * de.mcs.microservice.application.core.model.Context)
   */
  @Override
  public boolean deleteBlob(T dbModel, String fieldname, Context context) {
    // TODO Auto-generated method stub
    return false;
  }

}
