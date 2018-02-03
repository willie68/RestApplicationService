package de.mcs.microservice.application.health;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.health.HealthCheck;

import de.mcs.microservice.application.RestApplicationService;
import de.mcs.microservice.application.core.model.ApplicationConfig;
import de.mcs.microservice.application.storage.ConfigStorage;

public class BaseHealthCheck extends HealthCheck {
  private Logger log = LoggerFactory.getLogger(this.getClass());

  @Override
  protected Result check() throws Exception {
    boolean healthy = true;
    StringBuilder b = new StringBuilder();
    healthy = healthy & checkConfigStore(b);
    healthy = healthy & checkInstalledApps(b);

    if (healthy) {
      return Result.healthy("healthy: " + b.toString());
    } else {
      return Result.unhealthy("unhealthy: " + b.toString());
    }
  }

  private boolean checkInstalledApps(StringBuilder b) {
    try {
      b.append("\r\nchecking installed apps: ");
      RestApplicationService service = RestApplicationService.getInstance();
      Map<String, ApplicationConfig> installedApps = service.getInstalledApps();
      if ((installedApps == null) || (installedApps.size() == 0)) {
        b.append(", no apps installed!");
        return false;
      }
      b.append("[");
      installedApps.values().stream().forEach(a -> {
        b.append(a.getName());
        b.append(',');
      });
      b.deleteCharAt(b.length() - 1);
      b.append("]");
      b.append(", result: ok");
      return true;
    } catch (Exception e) {
      b.append(", Exception occured:" + e.getMessage());
      log.error("exception in healthcheck", e);
      return false;
    }
  }

  /**
   * @param healthy
   * @param b
   * @return
   */
  private boolean checkConfigStore(StringBuilder b) {
    try {
      b.append("\r\nchecking config store: ");
      RestApplicationService service = RestApplicationService.getInstance();
      ConfigStorage configStorage = service.getConfigStorage();
      configStorage.save("test.check", true);
      boolean isItTrue = (boolean) configStorage.get("test.check");
      b.append(isItTrue);
      configStorage.delete("test.check");
      Object object = configStorage.get("test.check");
      if (object != null) {
        b.append(", error deleting value!");
        return false;
      }
      if (!isItTrue) {
        b.append(", value not true!");
        return false;
      }
      return true;
    } catch (Exception e) {
      b.append(", Exception occured:" + e.getMessage());
      log.error("exception in healthcheck", e);
      return false;
    }
  }

}
