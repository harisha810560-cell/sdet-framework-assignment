package com.sdet.framework.commons.ui.listeners;

import com.microsoft.playwright.Page;
import com.sdet.framework.commons.config.EnvironmentConfig;
import com.sdet.framework.commons.config.EnvironmentConfigLoader;
import com.sdet.framework.commons.listeners.TestLogListener;
import com.sdet.framework.commons.reporting.ReportAttributes;
import com.sdet.framework.commons.ui.driver.DriverFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.extern.slf4j.Slf4j;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * Captures a screenshot and a Playwright trace when a UI test fails. TestNG invokes this before
 * {@code @AfterMethod} closes the browser, so the page is still live here.
 */
@Slf4j
public class UiEvidenceListener implements ITestListener {

  @Override
  public void onTestFailure(ITestResult result) {
    Page page = DriverFactory.pageIfPresent();
    if (page == null) return;
    EnvironmentConfig.FrameworkOptions options = EnvironmentConfigLoader.load().getFramework();
    String name = TestLogListener.name(result);
    captureScreenshot(result, page, options, name);
    captureTrace(result, options, name);
  }

  private void captureScreenshot(
      ITestResult result, Page page, EnvironmentConfig.FrameworkOptions options, String name) {
    try {
      Path output = prepare(options.getScreenshotDirectory(), name + ".png");
      page.screenshot(new Page.ScreenshotOptions().setPath(output).setFullPage(true));
      result.setAttribute(ReportAttributes.SCREENSHOT, output.toAbsolutePath().toString());
      log.info("Screenshot: {}", output.toAbsolutePath());
    } catch (Exception exception) {
      // Evidence capture must never mask the assertion failure.
      log.warn("Could not capture screenshot for {}", name, exception);
    }
  }

  private void captureTrace(
      ITestResult result, EnvironmentConfig.FrameworkOptions options, String name) {
    try {
      Path saved = DriverFactory.saveTrace(prepare(options.getTraceDirectory(), name + ".zip"));
      if (saved == null) return;
      result.setAttribute(ReportAttributes.TRACE, saved.toAbsolutePath().toString());
      log.info("Trace: {} (open with: npx playwright show-trace {})", saved, saved);
    } catch (Exception exception) {
      log.warn("Could not capture trace for {}", name, exception);
    }
  }

  private Path prepare(String directory, String fileName) throws java.io.IOException {
    Path output = Path.of(directory, fileName);
    Files.createDirectories(output.getParent());
    return output;
  }
}
