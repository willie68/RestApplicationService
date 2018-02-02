/**
 * MCS Media Computer Software
 * Copyright 2017 by Wilfried Klaas
 * Project: application
 * File: Application.java
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
package de.mcs.microservice.application.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import de.mcs.microservice.application.core.impl.NullAuthenticator;
import de.mcs.microservice.application.core.model.Authenticator;

/**
 * @author wklaa_000
 *
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface Application {
  public enum TenantType {
    SINGLE_TENANT, MULTI_TENANT
  }

  String name();

  TenantType tenantType() default TenantType.SINGLE_TENANT;

  String description() default "";

  String[] usedModules() default {};

  Class<? extends Authenticator> authClass() default NullAuthenticator.class;

}
