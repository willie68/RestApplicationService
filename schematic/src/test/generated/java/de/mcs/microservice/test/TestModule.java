package de.mcs.microservice.test;

import de.mcs.microservice.application.annotations.Module;
import de.mcs.microservice.application.core.model.RestModule;

/**
 * @author wklaa_000
 *
 */
@Module(name = "TestModule", description = "this is the main test module")
public class TestModule implements RestModule {

}
