package de.mcs.microservice.application.core.impl;

import de.mcs.microservice.application.core.model.Authenticator;

public class NullAuthenticator implements Authenticator {

  @Override
  public boolean authenticate(String username, String password) {
    return true;
  }

}
