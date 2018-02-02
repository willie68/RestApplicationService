/**
 * MCS Media Computer Software
 * Copyright 2018 by Wilfried Klaas
 * Project: application
 * File: DataStorage.java
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

import java.io.InputStream;
import java.util.List;

import de.mcs.microservice.application.api.BlobDescription;

/**
 * @author wklaa_000
 *
 */
public interface DataStorage<T extends RestDataModel> {

  /**
   * creating a new data model. Returns the id
   * 
   * @param model
   *          the model to create
   * @param context
   *          the context
   * @return the model id
   */
  String create(T model, Context context);

  /**
   * reading a data model from the storage
   * 
   * @param id
   *          the id of the model to read
   * @param context
   *          the context
   * @return the data model
   */
  T read(String id, Context context);

  /**
   * updating a data model
   * 
   * @param model
   *          the model to update
   * @param context
   *          the context
   * @return <code>true</code> if the update was performed, otherwise <code>false</code>
   */
  boolean update(T model, Context context);

  /**
   * deleting the datamodel
   * 
   * @param id
   *          the id of the model
   * @param context
   *          the context
   * @return the deleted data model, if present
   */
  T delete(String id, Context context);

  /**
   * finding all data models that matches the query
   * 
   * @param query
   *          the query
   * @param context
   *          the context
   * @return List of RestDataModel
   */
  List<T> find(String query, Context context);

  /**
   * saving a blob to a datamodel field
   * 
   * @param fieldname
   * @param dbModel
   * 
   * @param blobDescription
   * @param fileInputStream
   * @param contentLength
   * @param context
   * @return
   */
  BlobDescription saveBlob(T dbModel, String fieldname, BlobDescription blobDescription, InputStream fileInputStream,
      long contentLength, Context context);

  /**
   * 
   * @param dbModel
   * @param fieldname
   * @param context
   * @return
   */
  BlobDescription getBlobDescription(T dbModel, String fieldname, Context context);

  /**
   * 
   * @param dbModel
   * @param fieldname
   * @param context
   * @return
   */
  boolean hasBlob(T dbModel, String fieldname, Context context);

  /**
   * 
   * @param dbModel
   * @param fieldname
   * @param context
   * @return
   */
  InputStream getBlobInputStream(T dbModel, String fieldname, Context context);

  /**
   * 
   * @param dbModel
   * @param fieldname
   * @param context
   * @return
   */
  boolean deleteBlob(T dbModel, String fieldname, Context context);
}
