/**
 * MCS Media Computer Software
 * Copyright 2018 by Wilfried Klaas
 * Project: application
 * File: CustomAuthFilter.java
 * EMail: W.Klaas@gmx.de
 * Created: 08.01.2018 wklaa_000
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
package de.mcs.microservice.application.resources;

import java.io.IOException;
import java.util.Optional;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.internal.util.Base64;

import de.mcs.microservice.application.core.model.CustomCredentials;
import io.dropwizard.auth.AuthFilter;
import io.dropwizard.auth.AuthenticationException;

/**
 * @author wklaa_000
 *
 */
@Priority(Priorities.AUTHENTICATION)
public class CustomAuthFilter extends AuthFilter {
  private CustomAuthenticator authenticator;

  public CustomAuthFilter(CustomAuthenticator authenticator) {
    this.authenticator = authenticator;
  }

  @Override
  public void filter(ContainerRequestContext requestContext) throws IOException {
    Optional authenticatedUser;
    try {
      CustomCredentials credentials = getCredentials(requestContext);
      authenticatedUser = authenticator.authenticate(credentials);
    } catch (AuthenticationException e) {
      throw new WebApplicationException("Unable to validate credentials", Response.Status.UNAUTHORIZED);
    }

    if (!authenticatedUser.isPresent()) {
      throw new WebApplicationException("Credentials not valid", Response.Status.UNAUTHORIZED);
    }
  }

  private CustomCredentials getCredentials(ContainerRequestContext requestContext) {
    CustomCredentials credentials = new CustomCredentials();

    try {
      String authHeader = requestContext.getHeaderString("Authorization");
      if (authHeader.startsWith("Basic")) {
        authHeader = authHeader.substring("Basic".length()).trim();
        authHeader = Base64.decodeAsString(authHeader);
        String[] split = authHeader.split(":");
        if (split.length == 2) {
          String password = split[1];
          String username = split[0];
          String tenantHeader = requestContext.getHeaderString("X-tenant");
          credentials.setUsername(username);
          credentials.setPassword(password);
          credentials.setTenant(tenantHeader);
        }
      }

    } catch (Exception e) {
      throw new WebApplicationException("Unable to parse credentials", Response.Status.UNAUTHORIZED);
    }

    return credentials;

  }
}
