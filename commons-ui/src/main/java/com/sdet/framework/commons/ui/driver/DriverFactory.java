package com.sdet.framework.commons.ui.driver;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Tracing;
import com.sdet.framework.commons.config.EnvironmentConfig;
import java.nio.file.Path;
import lombok.extern.slf4j.Slf4j;

/** Thread-scoped Playwright lifecycle, so suites stay safe under {@code parallel="methods"}. */
@Slf4j
public final class DriverFactory {
  private static final String TEST_ID_ATTRIBUTE = "data-test";

  private static final ThreadLocal<Playwright> PLAYWRIGHT = new ThreadLocal<>();
  private static final ThreadLocal<Browser> BROWSER = new ThreadLocal<>();
  private static final ThreadLocal<BrowserContext> CONTEXT = new ThreadLocal<>();
  private static final ThreadLocal<Page> PAGE = new ThreadLocal<>();
  private static final ThreadLocal<Boolean> TRACING = ThreadLocal.withInitial(() -> false);
  private static final ThreadLocal<Boolean> TRACE_KEPT = ThreadLocal.withInitial(() -> false);

  private DriverFactory() {}

  public static Page start(EnvironmentConfig config) {
    Playwright playwright = Playwright.create();
    // SauceDemo marks elements with data-test, so getByTestId resolves against that attribute.
    playwright.selectors().setTestIdAttribute(TEST_ID_ATTRIBUTE);
    PLAYWRIGHT.set(playwright);

    Browser browser = launch(playwright, config);
    BROWSER.set(browser);

    BrowserContext context = browser.newContext();
    CONTEXT.set(context);
    TRACE_KEPT.set(false);
    TRACING.set(config.getFramework().isTracingEnabled());
    if (TRACING.get()) {
      context
          .tracing()
          .start(
              new Tracing.StartOptions().setScreenshots(true).setSnapshots(true).setSources(true));
    }

    Page page = context.newPage();
    page.setDefaultTimeout(config.getFramework().getTimeoutMs());
    PAGE.set(page);
    return page;
  }

  private static Browser launch(Playwright playwright, EnvironmentConfig config) {
    BrowserType.LaunchOptions options =
        new BrowserType.LaunchOptions().setHeadless(config.getFramework().isHeadless());
    return switch (config.getFramework().getBrowser().toLowerCase()) {
      case "firefox" -> playwright.firefox().launch(options);
      case "webkit" -> playwright.webkit().launch(options);
      default -> playwright.chromium().launch(options);
    };
  }

  public static Page page() {
    if (PAGE.get() == null) throw new IllegalStateException("Browser is not started");
    return PAGE.get();
  }

  public static Page pageIfPresent() {
    return PAGE.get();
  }

  /** Stops tracing and writes the archive, for a test that has just failed. */
  public static Path saveTrace(Path output) {
    BrowserContext context = CONTEXT.get();
    if (context == null || !TRACING.get() || TRACE_KEPT.get()) return null;
    context.tracing().stop(new Tracing.StopOptions().setPath(output));
    TRACE_KEPT.set(true);
    return output;
  }

  public static void stop() {
    BrowserContext context = CONTEXT.get();
    if (context != null) {
      // A passing test's trace is discarded; stopping twice would throw.
      if (TRACING.get() && !TRACE_KEPT.get()) context.tracing().stop();
      context.close();
    }
    if (BROWSER.get() != null) BROWSER.get().close();
    if (PLAYWRIGHT.get() != null) PLAYWRIGHT.get().close();
    PAGE.remove();
    CONTEXT.remove();
    BROWSER.remove();
    PLAYWRIGHT.remove();
    TRACE_KEPT.remove();
    TRACING.remove();
  }
}
