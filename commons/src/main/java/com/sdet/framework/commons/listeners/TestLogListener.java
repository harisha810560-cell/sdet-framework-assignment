package com.sdet.framework.commons.listeners;

import lombok.extern.slf4j.Slf4j;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * Logs the lifecycle of every test. Carries no browser dependency, so it applies to all modules.
 */
@Slf4j
public class TestLogListener implements ITestListener {

  @Override
  public void onTestStart(ITestResult result) {
    log.info("START {}", name(result));
  }

  @Override
  public void onTestSuccess(ITestResult result) {
    log.info("PASS  {}", name(result));
  }

  @Override
  public void onTestFailure(ITestResult result) {
    log.error("FAIL  {}", name(result), result.getThrowable());
  }

  @Override
  public void onTestSkipped(ITestResult result) {
    log.warn("SKIP  {}", name(result));
  }

  public static String name(ITestResult result) {
    return result.getTestClass().getRealClass().getSimpleName()
        + "-"
        + result.getMethod().getMethodName();
  }
}
