package de.mcs.microservice.application.resources;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.internal.util.Base64;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;

import de.mcs.jmeasurement.MeasureFactory;
import de.mcs.jmeasurement.Monitor;
import de.mcs.microservice.application.ConfigStorageConfig;
import de.mcs.microservice.application.RestApplicationService;
import de.mcs.microservice.application.annotations.Application.TenantType;
import de.mcs.microservice.application.api.BlobDescription;
import de.mcs.microservice.application.core.AbstractRestDataModel;
import de.mcs.microservice.application.core.DynamicDataModel;
import de.mcs.microservice.application.core.model.ApplicationConfig;
import de.mcs.microservice.application.core.model.Authenticator;
import de.mcs.microservice.application.core.model.DataModelConfig;
import de.mcs.microservice.application.core.model.DataStorage;
import de.mcs.microservice.application.core.model.ModuleConfig;
import de.mcs.microservice.application.core.model.RestDataModel;
import de.mcs.microservice.application.core.model.RestDataModelHooks;
import de.mcs.microservice.application.storage.NitriteDataStorage;
import de.mcs.microservice.utils.JacksonUtils;
import de.mcs.utils.StreamHelper;

/**
 * Server Side Implementation for Registry this is the service interface
 * 
 * @since 1.0.0
 */
@Path("/rest/v1/apps/{appName}/module/{moduleName}/model/{modelName}")
public class DataModelResource {

  Logger log = LoggerFactory.getLogger(this.getClass());
  @Context
  UriInfo uriInfo;

  @Context
  HttpHeaders headers;

  @Context
  SecurityContext securityContext;

  @PathParam(value = "appName")
  String appName;

  @PathParam(value = "moduleName")
  String moduleName;

  @PathParam(value = "modelName")
  String modelName;

  private ApplicationConfig application;

  private ModuleConfig module;

  private DataModelConfig dataModelConfig;
  private String applicationTenant;

  @Path("/")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response getAll(@QueryParam("q") String query) {
    checkApplication(true);

    try {
      List<RestDataModel> myModels = doBackendFind(query, buildContext());
      String json = JacksonUtils.getJsonMapper().writeValueAsString(myModels);
      return Response.status(Status.OK).entity(json).build();
    } catch (WebApplicationException e) {
      throw e;
    } catch (Exception e) {
      throw new WebApplicationException("unknown error occured", e, Status.INTERNAL_SERVER_ERROR);
    }
  }

  private List<RestDataModel> doBackendFind(String query, de.mcs.microservice.application.core.model.Context context) {
    try {
      RestDataModelHooks hooks = dataModelConfig.getDataModelHooks();

      String newQuery = hooks.beforeFind(query, context);
      if (newQuery == null) {
        newQuery = query;
      }

      DataStorage storage = initStorage(dataModelConfig.getDataStorage());
      List<RestDataModel> list = storage.find(newQuery, context);

      List<RestDataModel> newList = hooks.afterFind(newQuery, list, context);
      if (newList == null) {
        newList = list;
      }
      return newList;
    } catch (InstantiationException | IllegalAccessException e) {
      log.error("unknown error occured", e);
      throw new WebApplicationException("unknown error occured", e, Status.INTERNAL_SERVER_ERROR);
    }
  }

  @Path("/")
  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response create(DynamicDataModel model) throws JsonProcessingException {
    checkApplication(true);

    if (model == null) {
      throw new WebApplicationException("the data model should not be null.", Status.BAD_REQUEST);
    }

    model.setModuleName(moduleName);
    model.setModelName(modelName);

    try {
      RestDataModel myModel = convertDynamicModelIntoDataModelClass(model, dataModelConfig.getClassName());

      myModel = doBackendCreate(myModel, buildContext());
      String json = JacksonUtils.getJsonMapper().writeValueAsString(myModel);
      return Response.status(Status.CREATED).entity(json).build();
    } catch (WebApplicationException e) {
      throw e;
    } catch (Exception e) {
      throw new WebApplicationException("unknown error occured", e, Status.INTERNAL_SERVER_ERROR);
    }
  }

  private RestDataModel doBackendCreate(RestDataModel myModel,
      de.mcs.microservice.application.core.model.Context context) {
    try {
      RestDataModelHooks hooks = dataModelConfig.getDataModelHooks();

      RestDataModel beforeCreate = hooks.beforeCreate(myModel, context);
      if (beforeCreate != null) {
        myModel = beforeCreate;
      }

      DataStorage storage = initStorage(dataModelConfig.getDataStorage());

      storage.create(myModel, context);

      RestDataModel afterCreate = hooks.afterCreate(myModel, context);
      if (afterCreate != null) {
        myModel = afterCreate;
      }
      return myModel;
    } catch (InstantiationException | IllegalAccessException e) {
      log.error("unknown error occured", e);
      throw new WebApplicationException("unknown error occured", e, Status.INTERNAL_SERVER_ERROR);
    }
  }

  @Path("{id}")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response read(@PathParam(value = "id") String id) {
    checkApplication(true);

    RestDataModel myModel = null;
    try {
      myModel = doBackendRead(id, buildContext());
      if (myModel == null) {
        throw new WebApplicationException(String.format("Data model with id \"%s\" not found.", id), Status.NOT_FOUND);
      }
      String json = JacksonUtils.getJsonMapper().writeValueAsString(myModel);
      return Response.status(Status.OK).entity(json).build();
    } catch (WebApplicationException e) {
      throw e;
    } catch (Exception e) {
      throw new WebApplicationException("unknown error occured", e, Status.INTERNAL_SERVER_ERROR);
    }
  }

  private RestDataModel doBackendRead(String id, de.mcs.microservice.application.core.model.Context context) {
    try {
      RestDataModelHooks<RestDataModel> hooks = dataModelConfig.getDataModelHooks();

      String newId = hooks.beforeRead(id, context);
      if (newId == null) {
        newId = id;
      }

      DataStorage storage = initStorage(dataModelConfig.getDataStorage());
      RestDataModel myModel = storage.read(newId, context);

      if (myModel != null) {
        hooks.afterRead(myModel, context);
      }
      return myModel;
    } catch (InstantiationException | IllegalAccessException e) {
      log.error("unknown error occured", e);
      throw new WebApplicationException("unknown error occured", e, Status.INTERNAL_SERVER_ERROR);
    }
  }

  @Path("{id}")
  @PUT
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  public Response update(@PathParam(value = "id") String id, DynamicDataModel model) throws JsonProcessingException {
    checkApplication(true);

    if (StringUtils.isEmpty(id)) {
      throw new WebApplicationException("null or empty data model id not allowed on update.", Status.BAD_REQUEST);
    }
    if (model == null) {
      throw new WebApplicationException(String.format("null or empty data model not allowed on update. Id: \"%s\"", id),
          Status.BAD_REQUEST);
    }
    de.mcs.microservice.application.core.model.Context context = buildContext();

    RestDataModel dbModel = doBackendRead(id, context);
    if (dbModel == null) {
      throw new WebApplicationException(String.format("Data model with id \"%s\" not found.", id), Status.NOT_FOUND);
    }

    if (model != null) {
      model.setModuleName(moduleName);
      model.setModelName(modelName);
      model.setId(id);
    }

    try {
      RestDataModel myModel = convertDynamicModelIntoDataModelClass(model, dataModelConfig.getClassName());
      RestDataModel returnModel = doBackendUpdate(myModel, dbModel, context);
      String json = JacksonUtils.getJsonMapper().writeValueAsString(returnModel);
      return Response.status(Status.OK).entity(json).build();
    } catch (WebApplicationException e) {
      throw e;
    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
      log.error("unknown error occured", e);
      throw new WebApplicationException("unknown error occured", e, Status.INTERNAL_SERVER_ERROR);
    }

  }

  private RestDataModel doBackendUpdate(RestDataModel model, RestDataModel dbModel,
      de.mcs.microservice.application.core.model.Context context) {
    try {
      RestDataModelHooks hooks = dataModelConfig.getDataModelHooks();

      RestDataModel beforeUpdate = hooks.beforeUpdate(model, context);
      if (beforeUpdate != null) {
        model = beforeUpdate;
      }

      DataStorage storage = initStorage(dataModelConfig.getDataStorage());
      storage.update(model, context);

      RestDataModel afterUpdate = hooks.afterUpdate(model, context);
      if (afterUpdate != null) {
        model = afterUpdate;
      }
      return model;
    } catch (InstantiationException | IllegalAccessException e) {
      log.error("unknown error occured", e);
      throw new WebApplicationException("unknown error occured", e, Status.INTERNAL_SERVER_ERROR);
    }
  }

  @Path("{id}")
  @DELETE
  @Produces(MediaType.APPLICATION_JSON)
  public Response delete(@PathParam(value = "id") String id) throws JsonProcessingException {
    checkApplication(true);

    if (StringUtils.isEmpty(id)) {
      throw new WebApplicationException("null or empty data model id not allowed on delete.", Status.BAD_REQUEST);
    }

    de.mcs.microservice.application.core.model.Context context = buildContext();

    RestDataModel dbModel = doBackendRead(id, context);
    if (dbModel == null) {
      throw new WebApplicationException(String.format("Data model with id \"%s\" not found.", id), Status.NOT_FOUND);
    }

    RestDataModel returnModel = doBackendDelete(id, context);
    String json = JacksonUtils.getJsonMapper().writeValueAsString(returnModel);
    return Response.status(Status.OK).entity(json).build();
  }

  private RestDataModel doBackendDelete(String id, de.mcs.microservice.application.core.model.Context context) {
    try {
      RestDataModelHooks hooks = dataModelConfig.getDataModelHooks();

      String newId = hooks.beforeDelete(id, context);
      if (newId == null) {
        newId = id;
      }

      DataStorage storage = initStorage(dataModelConfig.getDataStorage());
      RestDataModel deleteModel = storage.delete(newId, context);

      RestDataModel afterUpdate = hooks.afterUpdate(deleteModel, context);
      if (afterUpdate != null) {
        deleteModel = afterUpdate;
      }
      return deleteModel;
    } catch (InstantiationException | IllegalAccessException e) {
      log.error("unknown error occured", e);
      throw new WebApplicationException("unknown error occured", e, Status.INTERNAL_SERVER_ERROR);
    }
  }

  private void checkApplication(boolean checkModel) {
    determineApplicationConfig(checkModel);

    checkVisibility();

    checkApplicationParameter();

    authenticate();
  }

  private void authenticate() {
    if (application.hasAuthenticator()) {
      String authHeader = headers.getHeaderString("Authorization");
      if (authHeader.startsWith("Basic")) {
        authHeader = authHeader.substring("Basic".length()).trim();
        authHeader = Base64.decodeAsString(authHeader);
        String[] split = authHeader.split(":");
        if (split.length == 2) {
          String password = split[1];
          String username = split[0];

          Authenticator authenticator;
          try {
            authenticator = application.getAuthenticator();
          } catch (RuntimeException e) {
            throw new WebApplicationException("error in authentication.", e, Status.NOT_FOUND);
          }
          if (!authenticator.authenticate(username, password)) {
            throw new WebApplicationException("authentication failed", Status.UNAUTHORIZED);
          }
        }
      }
    }
  }

  private void checkApplicationParameter() {
    String apikey = headers.getHeaderString("X-mcs-apikey");
    if (StringUtils.isEmpty(apikey)) {
      throw new WebApplicationException("missing apikey.", Status.BAD_REQUEST);
    }
    if (!application.getApikey().equals(apikey)) {
      throw new WebApplicationException("wrong apikey.", Status.BAD_REQUEST);
    }

    if (TenantType.MULTI_TENANT.equals(application.getTenantType())) {
      applicationTenant = headers.getHeaderString("X-mcs-tenant");
      if (StringUtils.isEmpty(applicationTenant)) {
        throw new WebApplicationException("missing tenant.", Status.BAD_REQUEST);
      }
      if (!application.hasTenant(applicationTenant)) {
        throw new WebApplicationException("wrong tenant.", Status.BAD_REQUEST);
      }
    }
  }

  private void determineApplicationConfig(boolean checkModel) {
    RestApplicationService<?> instance = RestApplicationService.getInstance();
    Map<String, ApplicationConfig> installedApps = instance.getInstalledApps();
    if (!installedApps.containsKey(appName)) {
      throw new WebApplicationException(String.format("application \"%s\" not found.", appName), Status.NOT_FOUND);
    }
    application = installedApps.get(appName);
    if (!application.getModules().containsKey(moduleName)) {
      throw new WebApplicationException(String.format("module \"%s\" not found.", moduleName), Status.NOT_FOUND);
    }
    module = application.getModules().get(moduleName);
    if (checkModel && !module.getDataModels().containsKey(modelName)) {
      throw new WebApplicationException(String.format("data model \"%s\" not found.", modelName), Status.NOT_FOUND);
    }
    if (modelName != null) {
      dataModelConfig = module.getDataModels().get(modelName);
    }
  }

  private AbstractRestDataModel convertDynamicModelIntoDataModelClass(DynamicDataModel model, String classname)
      throws ClassNotFoundException, InstantiationException, IllegalAccessException {
    Class<?> loadClass = Thread.currentThread().getContextClassLoader().loadClass(classname);
    Object newInstance = loadClass.newInstance();
    AbstractRestDataModel myModel = (AbstractRestDataModel) newInstance;
    Map<String, Object> any = model.any();
    for (Entry<String, Object> entry : any.entrySet()) {
      myModel.set(entry.getKey(), entry.getValue());
      if (entry.getKey().equals("_id")) {
        myModel.setId(entry.getValue().toString());
      }
    }
    myModel.setId(model.getId());
    return myModel;
  }

  private de.mcs.microservice.application.core.model.Context buildContext() {
    de.mcs.microservice.application.core.model.Context context = de.mcs.microservice.application.core.model.Context
        .create().setApplicationName(appName).setModuleName(moduleName).setModelName(modelName);
    if (TenantType.MULTI_TENANT.equals(application.getTenantType())) {
      context.setTenant(applicationTenant);
    }
    return context;
  }

  private void checkVisibility() {
    if (!dataModelConfig.isVisible()) {
      throw new WebApplicationException(String.format("data model \"%s\" not found.", modelName), Status.NOT_FOUND);
    }
  }

  private DataStorage initStorage(DataStorage storage) {
    if (storage instanceof NitriteDataStorage) {
      NitriteDataStorage nitriteStorage = (NitriteDataStorage) storage;
      if (!nitriteStorage.isInitialised()) {
        ConfigStorageConfig config = RestApplicationService.getInstance().getConfiguration()
            .getInternalDatastoreConfig();
        try {
          Class dataType = Thread.currentThread().getContextClassLoader().loadClass(dataModelConfig.getClassName());
          nitriteStorage.initialise(config, application, dataType);
        } catch (ClassNotFoundException e) {
          e.printStackTrace();
        }
      }
    }
    if (storage instanceof NitriteDataStorage) {
      NitriteDataStorage nitriteStorage = (NitriteDataStorage) storage;
      if (!nitriteStorage.isInitialised()) {
        ConfigStorageConfig config = RestApplicationService.getInstance().getConfiguration()
            .getInternalDatastoreConfig();
        try {
          Class dataType = Thread.currentThread().getContextClassLoader().loadClass(dataModelConfig.getClassName());
          nitriteStorage.initialise(config, application, dataType);
        } catch (ClassNotFoundException e) {
          e.printStackTrace();
        }
      }
    }
    return storage;
  }

  @POST
  @Path("/{id}/{fieldname}")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.APPLICATION_JSON)
  public Response createBlob(@PathParam(value = "id") String id, @PathParam(value = "fieldname") String fieldname,
      final @FormDataParam("file") InputStream fileInputStream, @FormDataParam("file") FormDataBodyPart body,
      @Context HttpHeaders headers) {
    Monitor monitor = MeasureFactory.start(this, "createBlob()");
    checkApplication(true);

    if (StringUtils.isEmpty(id)) {
      throw new WebApplicationException("null or empty data model id not allowed on delete.", Status.BAD_REQUEST);
    }

    de.mcs.microservice.application.core.model.Context context = buildContext();

    RestDataModel dbModel = doBackendRead(id, context);
    if (dbModel == null) {
      throw new WebApplicationException(String.format("Data model with id \"%s\" not found.", id), Status.NOT_FOUND);
    }

    try {
      BlobDescription blobDescription = buildBlobDescription(body);
      MultivaluedMap<String, String> requestHeaders = headers.getRequestHeaders();

      for (Entry<String, List<String>> entry : requestHeaders.entrySet()) {
        if (entry.getKey().startsWith("X-mcs")) {
          blobDescription.put(entry.getKey(), entry.getValue());
        }
      }

      DataStorage storage = initStorage(dataModelConfig.getDataStorage());
      blobDescription = storage.saveBlob(dbModel, fieldname, blobDescription, fileInputStream,
          blobDescription.getContentLength(), context);
      return Response.ok(blobDescription).status(201).build();
    } catch (WebApplicationException e) {
      throw e;
    } catch (Exception e) {
      monitor.setException(e);
      log.error("", e);
      throw new WebApplicationException("internal server error", Status.INTERNAL_SERVER_ERROR);
    } finally {
      monitor.stop();
    }
  }

  private BlobDescription buildBlobDescription(FormDataBodyPart body) {
    BlobDescription blobDescription = new BlobDescription();
    blobDescription.setFilename(body.getContentDisposition().getFileName());
    blobDescription.setContentLength(body.getContentDisposition().getSize());
    blobDescription.setContentType(body.getMediaType().toString());
    blobDescription.setCreationDate(body.getContentDisposition().getCreationDate());
    return blobDescription;
  }

  @GET
  @Path("{id}/{fieldname}")
  @Produces(MediaType.APPLICATION_OCTET_STREAM)
  public StreamingOutput readBlob(final @PathParam("id") String id, final @PathParam("fieldname") String fieldname,
      @Context HttpServletResponse servletResponse) {
    Monitor monitor = MeasureFactory.start(this, "readBlob");
    checkApplication(true);

    if (StringUtils.isEmpty(id)) {
      throw new WebApplicationException("null or empty data model id not allowed on delete.", Status.BAD_REQUEST);
    }

    de.mcs.microservice.application.core.model.Context context = buildContext();

    RestDataModel dbModel = doBackendRead(id, context);
    if (dbModel == null) {
      throw new WebApplicationException(String.format("Data model with id \"%s\" not found.", id), Status.NOT_FOUND);
    }

    try {
      DataStorage storage = initStorage(dataModelConfig.getDataStorage());

      if (storage.hasBlob(dbModel, fieldname, context)) {
        BlobDescription blobDescription = storage.getBlobDescription(dbModel, fieldname, context);

        for (Entry<String, Object> entry : blobDescription.properties().entrySet()) {
          if (entry.getValue() instanceof List) {
            List<String> valueList = (List<String>) entry.getValue();
            for (String value : valueList) {
              servletResponse.setHeader(entry.getKey(), value);
            }
          }
        }
        StreamingOutput output = new StreamingOutput() {

          @Override
          public void write(OutputStream outputStream) throws IOException, WebApplicationException {
            try {
              try (InputStream inputStream = storage.getBlobInputStream(dbModel, fieldname, context)) {
                long written = StreamHelper.copyStream(inputStream, outputStream);
              } catch (FileNotFoundException e) {
                log.error("file not found", e);
                throw new WebApplicationException("blob not found", Status.NOT_FOUND);
              }
            } finally {
              outputStream.close();
            }
          }
        };
        return output;
      } else {
        throw new WebApplicationException("blob not found", Status.NOT_FOUND);
      }
    } catch (WebApplicationException e) {
      throw e;
    } catch (InstantiationException | IllegalAccessException e) {
      log.error("unknown error occured", e);
      throw new WebApplicationException("unknown error occured", e, Status.INTERNAL_SERVER_ERROR);
    } finally {
      monitor.stop();
    }
  }

  @GET
  @Path("{id}/{fieldname}/info")
  @Produces(MediaType.APPLICATION_JSON)
  public BlobDescription readBlobDescription(@PathParam("id") String id, @PathParam("fieldname") String fieldname) {
    Monitor monitor = MeasureFactory.start(this, "readBlob");
    checkApplication(true);

    if (StringUtils.isEmpty(id)) {
      throw new WebApplicationException("null or empty data model id not allowed on delete.", Status.BAD_REQUEST);
    }

    de.mcs.microservice.application.core.model.Context context = buildContext();

    RestDataModel dbModel = doBackendRead(id, context);
    if (dbModel == null) {
      throw new WebApplicationException(String.format("Data model with id \"%s\" not found.", id), Status.NOT_FOUND);
    }

    try {
      DataStorage storage = initStorage(dataModelConfig.getDataStorage());

      if (storage.hasBlob(dbModel, fieldname, context)) {
        BlobDescription blobDescription = storage.getBlobDescription(dbModel, fieldname, context);
        return blobDescription;
      } else {
        throw new WebApplicationException("blob not found", Status.NOT_FOUND);
      }
    } catch (WebApplicationException e) {
      throw e;
    } catch (InstantiationException | IllegalAccessException e) {
      log.error("unknown error occured", e);
      throw new WebApplicationException("unknown error occured", e, Status.INTERNAL_SERVER_ERROR);
    } finally {
      monitor.stop();
    }
  }

  @DELETE
  @Path("{id}/{fieldname}")
  public Response deleteBlob(@PathParam("id") String id, @PathParam("fieldname") String fieldname) {
    Monitor monitor = MeasureFactory.start(this, "deleteBlob");
    checkApplication(true);

    if (StringUtils.isEmpty(id)) {
      throw new WebApplicationException("null or empty data model id not allowed on delete.", Status.BAD_REQUEST);
    }

    de.mcs.microservice.application.core.model.Context context = buildContext();

    RestDataModel dbModel = doBackendRead(id, context);
    if (dbModel == null) {
      throw new WebApplicationException(String.format("Data model with id \"%s\" not found.", id), Status.NOT_FOUND);
    }

    try {
      DataStorage storage = initStorage(dataModelConfig.getDataStorage());
      if (!storage.hasBlob(dbModel, fieldname, context)) {
        throw new WebApplicationException("blob not found", Status.NOT_FOUND);
      }
      if (storage.deleteBlob(dbModel, fieldname, context)) {
        return Response.ok().build();
      }
    } catch (WebApplicationException e) {
      throw e;
    } catch (InstantiationException | IllegalAccessException e) {
      log.error("unknown error occured", e);
      throw new WebApplicationException("unknown error occured", e, Status.INTERNAL_SERVER_ERROR);
    } finally {
      monitor.stop();
    }
    return Response.ok().build();
  }
}
