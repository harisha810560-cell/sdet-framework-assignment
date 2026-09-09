package com.sdet.framework.e2e;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import com.sdet.framework.commons.config.EnvironmentConfig;
import com.sdet.framework.commons.config.EnvironmentConfigLoader;
import org.testng.annotations.Test;

/**
 * Fails the cross-module gate early when the selected environment cannot supply what the UI and API
 * suites need, so a misconfigured run reports a clear cause.
 */
public class FrameworkSmokeTest {

  @Test(
      description = "Selected environment supplies a usable UI and API configuration",
      groups = {"e2e", "smoke"})
  public void selectedEnvironmentIsValid() {
    EnvironmentConfig config = EnvironmentConfigLoader.load();
    assertNotNull(config.getUi().getBaseUrl(), "UI base URL should be configured");
    assertNotNull(config.getApi().getBaseUrl(), "API base URL should be configured");
    assertTrue(config.getApi().getRequestTimeoutMs() > 0, "API request timeout should be positive");
    assertNotNull(config.getFramework().getBrowser(), "A browser should be configured");
  }
}
