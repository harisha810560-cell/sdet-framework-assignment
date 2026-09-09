package com.sdet.framework.commons.ui.base;

import com.microsoft.playwright.Page;
import com.sdet.framework.commons.config.EnvironmentConfig;
import com.sdet.framework.commons.config.EnvironmentConfigLoader;
import com.sdet.framework.commons.ui.driver.DriverFactory;
import com.sdet.framework.commons.ui.listeners.UiEvidenceListener;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;

/**
 * Browser lifecycle for UI suites. A fresh browser per method keeps tests independent. The evidence
 * listener is attached here rather than in Maven, so it applies however the suite is run.
 */
@Listeners(UiEvidenceListener.class)
public abstract class BaseUiTest {
  protected static final EnvironmentConfig CONFIG = EnvironmentConfigLoader.load();

  @BeforeMethod(alwaysRun = true)
  public void startBrowser() {
    DriverFactory.start(CONFIG);
  }

  @AfterMethod(alwaysRun = true)
  public void stopBrowser() {
    DriverFactory.stop();
  }

  protected Page page() {
    return DriverFactory.page();
  }
}
