/**
 * MCS Media Computer Software
 * Copyright 2017 by Wilfried Klaas
 * Project: schematic
 * File: SchematicApplication.java
 * EMail: W.Klaas@gmx.de
 * Created: 31.12.2017 wklaa_000
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
package de.mcs.microservice.schematic;

import de.mcs.microservice.application.annotations.Application;
import de.mcs.microservice.application.annotations.Application.TenantType;
import de.mcs.microservice.application.core.model.RestApplication;

/**
 * @author wklaa_000
 *
 */
@Application(name = "SchematicApplication2", description = "this is the second main app for the schematic server.", tenantType = TenantType.SINGLE_TENANT, usedModules = {
    "SchematicModule" })
public class SchematicApplication2 implements RestApplication {

  private String field;

}
