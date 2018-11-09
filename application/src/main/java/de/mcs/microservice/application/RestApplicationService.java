package de.mcs.microservice.application;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.Path;

import org.apache.commons.lang.StringUtils;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.mcs.jmeasurement.MeasureFactory;
import de.mcs.microservice.application.annotations.DynamicPath;
import de.mcs.microservice.application.api.ServerAPI;
import de.mcs.microservice.application.core.AbstractRestResource;
import de.mcs.microservice.application.core.AnnotationScanner;
import de.mcs.microservice.application.core.impl.RestServerApi;
import de.mcs.microservice.application.core.model.ApplicationConfig;
import de.mcs.microservice.application.core.model.DataModelConfig;
import de.mcs.microservice.application.core.model.FieldConfig;
import de.mcs.microservice.application.core.model.ModuleConfig;
import de.mcs.microservice.application.core.model.RestResourceConfig;
import de.mcs.microservice.application.health.BaseHealthCheck;
import de.mcs.microservice.application.resources.ConfigResource;
import de.mcs.microservice.application.resources.CustomAuthFilter;
import de.mcs.microservice.application.resources.CustomAuthenticator;
import de.mcs.microservice.application.resources.DataModelResource;
import de.mcs.microservice.application.resources.InfoResource;
import de.mcs.microservice.application.resources.ServiceResource;
import de.mcs.microservice.application.storage.ConfigStorage;
import io.dropwizard.Application;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.forms.MultiPartBundle;
import io.dropwizard.jersey.DropwizardResourceConfig;
import io.dropwizard.jersey.setup.JerseyContainerHolder;
import io.dropwizard.jersey.setup.JerseyEnvironment;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

/**
 * Abstract base class for the RestApplicationService
 * 
 * @author w.klaas
 *
 */
public abstract class RestApplicationService<T extends AppServiceConfig> extends Application<T> {

  private Logger log;
  private Map<String, ApplicationConfig> applications;
  private T configuration;
  private ServerAPI serverApi;

  private static RestApplicationService<?> instance;

  public static final RestApplicationService<?> getInstance() {
    return instance;
  }

  public RestApplicationService() {
    MeasureFactory.setApplicationName(this.getName());
    log = LoggerFactory.getLogger(RestApplicationService.class);
    instance = this;
  }

  @Override
  public void initialize(final Bootstrap<T> bootstrap) {
    bootstrap.addBundle(new MultiPartBundle());
  }

  private void scanAnnotationsAndBuildRestApplication() throws Error {
    log.info("scanning class path for application definition.");
    AnnotationScanner scanner = new AnnotationScanner();
    List<ApplicationConfig> installedApps = scanner.getInstalledApps();
    List<ModuleConfig> installedModules = scanner.getInstalledModules();
    List<DataModelConfig> installedDataModels = scanner.getInstalledDataModels();
    List<RestResourceConfig> installedResources = scanner.getInstalledRestResources();

    for (ApplicationConfig app : installedApps) {
      log.info(String.format("Application found: %s", app.getName()));
    }
    for (ModuleConfig module : installedModules) {
      log.info(String.format("Module found: %s", module.getName()));
    }
    for (DataModelConfig model : installedDataModels) {
      log.info(String.format("Data model found: %s", model.getName()));
    }
    for (RestResourceConfig resource : installedResources) {
      log.info(String.format("Resource found: %s, class: %s", resource.getModuleName(), resource.getClassName()));
    }

    log.info("wire up modules and applications");
    Map<String, ModuleConfig> modules = new HashMap<>();
    for (ModuleConfig module : installedModules) {
      modules.put(module.getName(), module);
    }
    for (DataModelConfig model : installedDataModels) {
      String moduleName = model.getModuleName();
      if (modules.keySet().contains(moduleName)) {
        ModuleConfig moduleConfig = modules.get(moduleName);
        moduleConfig.addDataModel(model);
      }
    }
    for (RestResourceConfig resource : installedResources) {
      String moduleName = resource.getModuleName();
      if (modules.keySet().contains(moduleName)) {
        ModuleConfig moduleConfig = modules.get(moduleName);
        moduleConfig.addRestResource(resource);
      }
    }

    applications = new HashMap<>();
    for (ApplicationConfig app : installedApps) {
      applications.put(app.getName(), app);
      String[] usedModulesNames = app.getUsedModulesNames();
      for (String usedModuleName : usedModulesNames) {
        if (modules.containsKey(usedModuleName)) {
          app.addModule(modules.get(usedModuleName));
        } else {
          throw new Error(String.format("can't find used Module \"%s\"", usedModuleName));
        }
      }
      app.setApikey(generateApikey(app));
      setTenants(app);
    }
  }

  private void setTenants(ApplicationConfig app) {
    String key = getAppTenantsKey(app.getName());
    String[] tenants = (String[]) getConfigStorage().get(key);
    if ((tenants != null) && (tenants.length > 0)) {
      for (String tenant : tenants) {
        app.addTenant(tenant);
      }
    }
  }

  public static String getAppTenantsKey(String appName) {
    String key = String.format("%s.tenants", appName);
    return key;
  }

  /**
   * @param app
   * @return
   */
  private String generateApikey(ApplicationConfig app) {
    String key = String.format("%s.apikey", app.getName());
    String apikey = (String) getConfigStorage().get(key);
    if (org.apache.commons.lang3.StringUtils.isEmpty(apikey)) {
      apikey = UUID.randomUUID().toString();
      getConfigStorage().save(key, apikey);
    }
    return apikey;
  }

  private void outputRestApplication() {
    for (ApplicationConfig app : applications.values()) {
      log.info(String.format("  Application: %s", app.getName()));
      log.info(String.format("  Apikey: %s", app.getApikey()));
      log.info(String.format("  Description: %s", app.getDescription()));
      log.info(String.format("  Authenticator: %s", app.getAuthenticatorClass().getName()));
      log.info("  Modules:");

      for (ModuleConfig module : app.getModules().values()) {
        log.info(String.format("    Module: %s", module.getName()));
        log.info(String.format("    Description: %s", module.getDescription()));
        log.info("    Datamodels:");
        for (DataModelConfig model : module.getDataModels().values()) {
          log.info(String.format("      Model: %s", model.getName()));
          log.info(String.format("      Description: %s", model.getDescription()));
          log.info(String.format("      Visible: %s", model.isVisible()));
          log.info(String.format("      Storage: %s", model.getDataStorageClass().getSimpleName()));
          for (FieldConfig indexField : model.getIndexFields()) {
            log.info(String.format("        IndexField: %s", indexField.getName()));
            log.info(String.format("        Type: %s", indexField.getType()));
            log.info(String.format("        Fulltext: %s", Boolean.toString(indexField.isFulltext())));
            log.info("        -----");
          }
          log.info("      -----");
        }
        log.info("    RestResources:");
        for (RestResourceConfig resource : module.getRestResources()) {
          log.info(String.format("      Class: %s", resource.getClassName()));
          log.info(String.format("      Description: %s", resource.getDescription()));
          log.info("      -----");
        }
        log.info("    -----");
      }
      log.info("  -----");
    }
  }

  @Override
  public void run(T configuration, Environment environment) throws Exception {
    this.configuration = configuration;

    initConfigStorage();

    scanAnnotationsAndBuildRestApplication();

    outputRestApplication();

    addingAdminResources(environment);

    addingStaticResources(environment);

    addingRestApplicationResources(environment);

    addingBaseHealthCheck(environment);

    createServerAPI();
  }

  private void createServerAPI() {
    serverApi = new RestServerApi();
  }

  private void addingAdminResources(Environment environment) {
    final DropwizardResourceConfig jerseyConfig = new DropwizardResourceConfig(environment.metrics());
    JerseyContainerHolder jerseyContainerHolder = new JerseyContainerHolder(new ServletContainer(jerseyConfig));
    JerseyEnvironment jerseyEnvironment = new JerseyEnvironment(jerseyContainerHolder, jerseyConfig);

    jerseyEnvironment.register(ServiceResource.class);
    jerseyEnvironment.register(new ConfigResource());
    environment.admin().addServlet("admin jersey resources", jerseyContainerHolder.getContainer())
        .addMapping("/service/*");
  }

  private void addingBaseHealthCheck(Environment environment) {
    environment.healthChecks().register("base", new BaseHealthCheck());
  }

  private void addingRestApplicationResources(Environment environment) throws ClassNotFoundException {
    for (ApplicationConfig app : applications.values()) {
      for (ModuleConfig module : app.getModules().values()) {
        for (RestResourceConfig resource : module.getRestResources()) {
          String className = resource.getClassName();
          Class<?> resourceClass = this.getClass().getClassLoader().loadClass(className);

          Path targetValue = new DynamicPath(AbstractRestResource.RESOURCEPATH);
          alterAnnotationValueJDK8(resourceClass, Path.class, targetValue);

          environment.jersey().register(resourceClass);
        }
      }
    }
  }

  private void addingStaticResources(Environment environment) {
    CustomAuthFilter filter = new CustomAuthFilter(new CustomAuthenticator());
    environment.jersey().register(new AuthDynamicFeature(filter));

    environment.jersey().register(InfoResource.class);
    environment.jersey().register(DataModelResource.class);
  }

  private void initConfigStorage() {
    ConfigStorageConfig internalDatastoreConfig = configuration.getInternalDatastoreConfig();
    if (internalDatastoreConfig == null) {
      throw new RuntimeException("config storage should be set.");
    }
    String storageClass = internalDatastoreConfig.getStorageClass();
    if (StringUtils.isEmpty(storageClass)) {
      throw new RuntimeException("config storage class should not be null or empty");
    }
    try {
      Class<?> loadClass = this.getClass().getClassLoader().loadClass(storageClass);
      if (loadClass != null) {
        Object newInstance = loadClass.newInstance();
        configStorage = (ConfigStorage) newInstance;
        getConfigStorage().initialise(internalDatastoreConfig);
      }
    } catch (Exception e) {
      throw new RuntimeException("config storage class not found or can't be loaded.", e);
    }
  }

  private static final String ANNOTATION_METHOD = "annotationData";
  private static final String ANNOTATION_FIELDS = "declaredAnnotations";
  private static final String ANNOTATIONS = "annotations";
  private ConfigStorage configStorage;

  @SuppressWarnings("unchecked")
  public static void alterAnnotationValueJDK8(Class<?> targetClass, Class<? extends Annotation> targetAnnotation,
      Annotation targetValue) {

    try {
      Method method = Class.class.getDeclaredMethod(ANNOTATION_METHOD, null);
      method.setAccessible(true);

      Object annotationData = method.invoke(targetClass);

      Field annotations = annotationData.getClass().getDeclaredField(ANNOTATIONS);
      annotations.setAccessible(true);

      Map<Class<? extends Annotation>, Annotation> map = (Map<Class<? extends Annotation>, Annotation>) annotations
          .get(annotationData);
      map.put(targetAnnotation, targetValue);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * @return the installedApps
   */
  public Map<String, ApplicationConfig> getInstalledApps() {
    return applications;
  }

  public T getConfiguration() {
    return configuration;
  }

  /**
   * @return the configStorage
   */
  public ConfigStorage getConfigStorage() {
    return configStorage;
  }

  /**
   * @return the serverApi
   */
  public static ServerAPI getServerApi() {
    return getInstance().serverApi;
  }

}
