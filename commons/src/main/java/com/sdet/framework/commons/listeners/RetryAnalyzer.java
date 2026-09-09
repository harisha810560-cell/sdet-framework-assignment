package com.sdet.framework.commons.listeners;

import lombok.extern.slf4j.Slf4j;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * Retries a failed test up to {@code -Dtest.retryCount} times. The default is zero, so a failure
 * stays a failure unless a run explicitly opts in — a retry that is on by default hides real
 * defects behind an intermittent pass.
 */
@Slf4j
public class RetryAnalyzer implements IRetryAnalyzer {
  private static final int MAX_RETRIES = Integer.getInteger("test.retryCount", 0);

  private int attempts;

  @Override
  public boolean retry(ITestResult result) {
    if (attempts >= MAX_RETRIES) return false;
    attempts++;
    log.warn("RETRY {} (attempt {} of {})", TestLogListener.name(result), attempts, MAX_RETRIES);
    return true;
  }
}
