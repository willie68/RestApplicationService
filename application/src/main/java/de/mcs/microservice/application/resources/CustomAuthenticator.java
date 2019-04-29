package de.mcs.microservice.application.resources;

import java.util.Optional;

import de.mcs.microservice.application.core.model.CustomAuthUser;
import de.mcs.microservice.application.core.model.CustomCredentials;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;

public class CustomAuthenticator implements Authenticator<CustomCredentials, CustomAuthUser> {

  public CustomAuthenticator() {
  }

  @Override
  public Optional authenticate(CustomCredentials credentials) throws AuthenticationException {
    CustomAuthUser authenticatedUser = null;
    if (credentials.getUsername().equals("w.klaas@gmx.de")) {
      if (credentials.getPassword().equals("akteon00")) {
        authenticatedUser = new CustomAuthUser(credentials.getUsername());
      }
    }
    return Optional.ofNullable(authenticatedUser);
  }
}
