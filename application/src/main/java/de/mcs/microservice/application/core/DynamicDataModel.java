/**
 * MCS Media Computer Software
 * Copyright 2018 by Wilfried Klaas
 * Project: application
 * File: DynamicDataModel.java
 * EMail: W.Klaas@gmx.de
 * Created: 07.01.2018 wklaa_000
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

import de.mcs.microservice.application.core.model.RestDataModel;

/**
 * @author wklaa_000
 *
 */
public class DynamicDataModel extends AbstractRestDataModel implements RestDataModel {

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.mcs.microservice.application.core.AbstractRestDataModel#getModelName()
   */
  @Override
  public String getModelName() {
    return (String) this.any().get(AbstractRestDataModel.KEY_MODEL_NAME);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.mcs.microservice.application.core.AbstractRestDataModel#getModuleName()
   */
  @Override
  public String getModuleName() {
    return (String) this.any().get(AbstractRestDataModel.KEY_MODULE_NAME);
  }

  public void setModuleName(String moduleName) {
    super.setPrivateModuleName(moduleName);
  }

  public void setModelName(String modelName) {
    super.setPrivateModelName(modelName);
  }

}
