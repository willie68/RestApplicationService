package de.mcs.microservice.schematic;

import de.mcs.microservice.application.annotations.Application;
import de.mcs.microservice.application.annotations.Application.TenantType;
import de.mcs.microservice.application.core.model.RestApplication;

@Application(name = "SchematicApplication", description = "this is the data storage for the schematic files", tenantType = TenantType.MULTI_TENANT, 
  usedModules = {  "SchematicModule",  "SchematicModule2",  }, 
  authClass = SchematicAuthenticator.class)
public class SchematicApplication  implements RestApplication {

}