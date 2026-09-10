package com.sdet.framework.commons.reporting;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.sdet.framework.commons.config.EnvironmentConfigLoader;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import org.testng.IReporter;
import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.xml.XmlSuite;

/** Generates an Extent Spark HTML report after every TestNG suite execution. */
public class ExtentReportListener implements IReporter {
  private static final String REPORT_FILE = "extent-report.html";

  @Override
  public void generateReport(
      List<XmlSuite> suites, List<ISuite> runSuites, String outputDirectory) {
    Path reportPath = Path.of(outputDirectory).resolve(REPORT_FILE);
    ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath.toString());
    sparkReporter.config().setDocumentTitle("SDET Framework Execution Report");
    sparkReporter.config().setReportName("Environment: " + EnvironmentConfigLoader.activeEnvironment());

    ExtentReports extentReports = new ExtentReports();
    extentReports.attachReporter(sparkReporter);
    extentReports.setSystemInfo("Environment", EnvironmentConfigLoader.activeEnvironment());

    for (ISuite suite : runSuites) {
      for (ISuiteResult suiteResult : suite.getResults().values()) {
        logResults(extentReports, suiteResult.getTestContext());
      }
    }
    extentReports.flush();
  }

  private void logResults(ExtentReports extentReports, ITestContext context) {
    context.getPassedTests().getAllResults().forEach(result -> logResult(extentReports, result));
    context.getFailedTests().getAllResults().forEach(result -> logResult(extentReports, result));
    context.getSkippedTests().getAllResults().forEach(result -> logResult(extentReports, result));
  }

  private void logResult(ExtentReports extentReports, ITestResult result) {
    ExtentTest test =
        extentReports
            .createTest(result.getTestClass().getRealClass().getSimpleName())
            .createNode(result.getMethod().getMethodName())
            .assignCategory(result.getMethod().getGroups());

    String description = Objects.requireNonNullElse(result.getMethod().getDescription(), "");
    if (!description.isBlank()) {
      test.info(description);
    }

    switch (result.getStatus()) {
      case ITestResult.SUCCESS -> test.pass("Test passed");
      case ITestResult.FAILURE -> logFailure(test, result);
      case ITestResult.SKIP -> test.skip(result.getThrowable());
      default -> test.warning("Test did not finish with a recognised status");
    }
  }

  private void logFailure(ExtentTest test, ITestResult result) {
    test.fail(result.getThrowable());
    Object screenshot = result.getAttribute(ReportAttributes.SCREENSHOT);
    if (screenshot == null) {
      return;
    }

    try {
      test.fail(
          MediaEntityBuilder.createScreenCaptureFromPath(screenshot.toString()).build());
    } catch (Exception ignored) {
      test.warning("Screenshot could not be attached: " + screenshot);
    }
  }
}
