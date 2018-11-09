/**
 * 
 */
package de.mcs.microservice.schematic.client;

import java.io.File;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

import de.mcs.microservice.application.client.BasicAuthenticator;
import de.mcs.microservice.schematic.Schematic;

/**
 * @author w.klaas
 *
 */
public class SchematicClient extends AbstractClient<Schematic> {

  public SchematicClient(String baseUrl, String tenant, String username, String password, String apikey)
      throws NoSuchAlgorithmException, KeyManagementException {
    super(baseUrl, tenant, apikey, Schematic.class);
    client.register(new BasicAuthenticator(username, password));

    setWebTarget(getBaseWebTarget().path("SchematicApplication/module/SchematicModule/model/Schematic/"));
  }

  public Schematic postFile(Schematic model, File file, Map<String, Object> properties) {
    return postBlob(model, "file", file, properties);
  }

  public InputStream getFile(Schematic model) {
    return getBlob(model, "file");
  }

}
