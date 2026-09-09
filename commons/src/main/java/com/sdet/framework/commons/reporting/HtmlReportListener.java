package com.sdet.framework.commons.reporting;

import com.sdet.framework.commons.config.EnvironmentConfigLoader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import org.testng.IReporter;
import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.xml.XmlSuite;

/** Writes a self-contained HTML summary to the surefire reports directory after each run. */
public class HtmlReportListener implements IReporter {
  private static final String REPORT_FILE = "sdet-test-report.html";

  @Override
  public void generateReport(
      List<XmlSuite> suites, List<ISuite> runSuites, String outputDirectory) {
    List<TestRow> rows = collectRows(runSuites);
    Path report = Path.of(outputDirectory).resolve(REPORT_FILE);
    try {
      Files.createDirectories(report.getParent());
      Files.writeString(report, render(rows, report.getParent()));
    } catch (IOException e) {
      throw new IllegalStateException("Unable to write HTML test report", e);
    }
  }

  private List<TestRow> collectRows(List<ISuite> runSuites) {
    List<TestRow> rows = new ArrayList<>();
    for (ISuite suite : runSuites) {
      for (ISuiteResult suiteResult : suite.getResults().values()) {
        ITestContext context = suiteResult.getTestContext();
        context.getPassedTests().getAllResults().forEach(r -> rows.add(TestRow.of(r, "Passed")));
        context.getFailedTests().getAllResults().forEach(r -> rows.add(TestRow.of(r, "Failed")));
        context.getSkippedTests().getAllResults().forEach(r -> rows.add(TestRow.of(r, "Skipped")));
      }
    }
    rows.sort(Comparator.comparing(TestRow::className).thenComparing(TestRow::methodName));
    return rows;
  }

  private String render(List<TestRow> rows, Path reportDirectory) {
    long passed = rows.stream().filter(row -> row.status().equals("Passed")).count();
    long failed = rows.stream().filter(row -> row.status().equals("Failed")).count();
    long skipped = rows.stream().filter(row -> row.status().equals("Skipped")).count();
    long totalMillis = rows.stream().mapToLong(TestRow::durationMillis).sum();

    StringBuilder html = new StringBuilder();
    html.append("<!doctype html><html lang='en'><head><meta charset='utf-8'>")
        .append("<title>SDET Framework Report</title><style>")
        .append("body{font-family:-apple-system,Segoe UI,Arial,sans-serif;margin:2rem;color:#111}")
        .append("table{border-collapse:collapse;width:100%;margin-top:1rem}")
        .append("td,th{border:1px solid #d4d4d8;padding:.5rem .7rem;text-align:left;")
        .append("font-size:.9rem;vertical-align:top}")
        .append("th{background:#1d4ed8;color:#fff}")
        .append(".summary{display:flex;gap:1rem;flex-wrap:wrap;margin-top:1rem}")
        .append(".card{border:1px solid #d4d4d8;border-radius:.5rem;padding:.8rem 1.2rem}")
        .append(".card strong{display:block;font-size:1.6rem}")
        .append(".Passed{color:#15803d;font-weight:600}.Failed{color:#b91c1c;font-weight:600}")
        .append(".Skipped{color:#a16207;font-weight:600}")
        .append("pre{margin:0;white-space:pre-wrap;font-size:.8rem;color:#7f1d1d}")
        .append("</style></head><body><h1>SDET Framework Report</h1><p>Environment <code>")
        .append(escape(environment()))
        .append("</code> &middot; ")
        .append(totalMillis)
        .append(" ms</p><div class='summary'>")
        .append(card("Total", rows.size()))
        .append(card("Passed", passed))
        .append(card("Failed", failed))
        .append(card("Skipped", skipped))
        .append("</div><table><tr><th>Class</th><th>Test</th><th>Groups</th><th>Status</th>")
        .append("<th>Duration</th><th>Details</th></tr>");

    for (TestRow row : rows) {
      html.append("<tr><td>")
          .append(escape(row.className()))
          .append("</td><td>")
          .append(escape(row.methodName()))
          .append("<br><small>")
          .append(escape(row.description()))
          .append("</small></td><td>")
          .append(escape(row.groups()))
          .append("</td><td class='")
          .append(row.status())
          .append("'>")
          .append(row.status())
          .append("</td><td>")
          .append(row.durationMillis())
          .append(" ms</td><td>")
          .append(details(row, reportDirectory))
          .append("</td></tr>");
    }
    return html.append("</table></body></html>").toString();
  }

  private String details(TestRow row, Path reportDirectory) {
    StringBuilder details = new StringBuilder();
    if (row.failureMessage() != null) {
      details.append("<pre>").append(escape(row.failureMessage())).append("</pre>");
    }
    appendLink(details, reportDirectory, row.screenshot(), "Screenshot");
    appendLink(details, reportDirectory, row.trace(), "Playwright trace");
    return details.isEmpty() ? "&mdash;" : details.toString();
  }

  private void appendLink(
      StringBuilder details, Path reportDirectory, String evidence, String label) {
    if (evidence == null) return;
    details
        .append("<a href='")
        .append(escape(relativize(reportDirectory, Path.of(evidence))))
        .append("'>")
        .append(label)
        .append("</a> ");
  }

  /**
   * Evidence sits in a sibling directory of the report, so the link has to be relative for the
   * report to still work once it is downloaded as a CI artifact.
   */
  private String relativize(Path reportDirectory, Path evidence) {
    try {
      return reportDirectory.toAbsolutePath().relativize(evidence.toAbsolutePath()).toString();
    } catch (IllegalArgumentException differentRoot) {
      return evidence.toUri().toString();
    }
  }

  private String environment() {
    return EnvironmentConfigLoader.activeEnvironment();
  }

  private String card(String label, long value) {
    return "<div class='card'><strong>" + value + "</strong>" + label + "</div>";
  }

  private static String escape(String value) {
    if (value == null) return "";
    return value
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("'", "&#39;");
  }

  private record TestRow(
      String className,
      String methodName,
      String description,
      String groups,
      String status,
      long durationMillis,
      String failureMessage,
      String screenshot,
      String trace) {

    static TestRow of(ITestResult result, String status) {
      return new TestRow(
          result.getTestClass().getRealClass().getSimpleName(),
          result.getMethod().getMethodName(),
          Objects.requireNonNullElse(result.getMethod().getDescription(), ""),
          String.join(", ", result.getMethod().getGroups()),
          status,
          result.getEndMillis() - result.getStartMillis(),
          result.getThrowable() == null ? null : result.getThrowable().getMessage(),
          attribute(result, ReportAttributes.SCREENSHOT),
          attribute(result, ReportAttributes.TRACE));
    }

    private static String attribute(ITestResult result, String key) {
      Object value = result.getAttribute(key);
      return value == null ? null : value.toString();
    }
  }
}
