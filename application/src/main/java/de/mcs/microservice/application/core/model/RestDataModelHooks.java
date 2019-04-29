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

import java.util.List;

/**
 * @author wklaa_000
 *
 */
public interface RestDataModelHooks<T extends RestDataModel> {

  /**
   * this hook will be called before a datamodel will be create in the storage
   * class
   * 
   * @param dataModel
   *          the model to be created
   * @param context
   *          the context
   * @return return the data model that should be created. If <code>null</code>
   *         the original data model will be used.
   */
  T beforeCreate(T dataModel, Context context);

  /**
   * this hook will be called after a new datamodel was created in the storage.
   * And only if the creation process succeeded.
   * 
   * @param dataModel
   *          the model to be created
   * @param context
   *          the context
   * @return return the data model that should be deliverd to the client. If
   *         <code>null</code> the original data model will be used.
   */
  T afterCreate(T dataModel, Context context);

  /**
   * this hook will be called before a datamodel will be readed from the storage
   * class
   * 
   * @param id
   *          the id of the model to be readed from the storage
   * @param context
   *          the context
   * @return the desired data model id
   */
  String beforeRead(String id, Context context);

  /**
   * this hook will be called after a datamodel was readed from the storage. And
   * only if the read process succeeded.
   * 
   * @param dataModel
   *          the model to be created
   * @param context
   *          the context
   * @return the datamodel
   */
  T afterRead(T dataModel, Context context);

  /**
   * this hook will be called before a datamodel will be updated in the storage
   * class
   * 
   * @param dataModel
   *          the model to be updated
   * @param context
   *          the context
   * @return return the data model that should be created. If <code>null</code>
   *         the original data model will be used.
   */
  T beforeUpdate(T dataModel, Context context);

  /**
   * this hook will be called after a datamodel was updated in the storage. And
   * only if the update process succeeded.
   * 
   * @param dataModel
   *          the model to be updated
   * @param context
   *          the context
   * @return return the data model that should be deliverd to the client. If
   *         <code>null</code> the original data model will be used.
   */
  T afterUpdate(T dataModel, Context context);

  /**
   * this hook will be called before a datamodel will be deleted in the storage
   * class
   * 
   * @param id
   *          the id of the model to be deleted
   * @param context
   *          the context
   * @return the desired data model id
   */
  String beforeDelete(String id, Context context);

  /**
   * this hook will be called after a datamodel was deleted in the storage. And
   * only if the delete process succeeded.
   * 
   * @param dataModel
   *          the model to be delete
   * @param context
   *          the context
   * @return the deleted data model
   */
  T afterDelete(T dataModel, Context context);

  /**
   * this hook will be called before a query will be sended to the storage. And
   * only if the delete process succeeded.
   * 
   * @param query
   *          the query of the client
   * @param context
   *          the context
   * @return the query, if return value is <code>null</code>, the original query
   *         will be send.
   */
  String beforeFind(String query, Context context);

  /**
   * 
   * @param query
   *          the query of the client
   * @param list
   *          a list from the storage
   * @param context
   *          the context
   * @return the list of models, if return value is <code>null</code>, the
   *         original list will be send.
   */
  List<T> afterFind(String query, List<T> list, Context context);
}
