/**
 * 
 */
package de.mcs.microservice.schematic.client;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import de.mcs.microservice.application.client.BasicAuthenticator;
import de.mcs.microservice.schematic.SchematicTags;

/**
 * @author w.klaas
 *
 */
public class SchematicTagsClient extends AbstractClient<SchematicTags> {

  public SchematicTagsClient(String baseUrl, String tenant, String username, String password, String apikey)
      throws NoSuchAlgorithmException, KeyManagementException {
    super(baseUrl, tenant, apikey, SchematicTags.class);
    client.register(new BasicAuthenticator(username, password));

    setWebTarget(getBaseWebTarget().path("SchematicApplication/module/SchematicModule/model/Schematic/"));
  }
}
