package de.mcs.microservice.schematic;

import de.mcs.microservice.application.core.model.Authenticator;

public class SchematicAuthenticator implements Authenticator {

  @Override
  public boolean authenticate(String username, String password) {
    return username.equals("w.klaas@gmx.de") && password.equals("akteon00");
  }

}
