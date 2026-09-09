package com.sdet.framework.commons.reporting;

/** Keys used to hand failure evidence from a listener to the reporter via {@code ITestResult}. */
public final class ReportAttributes {
  public static final String SCREENSHOT = "screenshot";
  public static final String TRACE = "trace";

  private ReportAttributes() {}
}
