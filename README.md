# SDET Framework Assignment

Java 21 / Maven test framework covering a UI purchase journey on SauceDemo and a REST API suite
against JSONPlaceholder. Playwright drives the browser, REST Assured drives the API, and TestNG
runs both.

## Coverage at a glance

| Module | Tests | What it covers |
|---|---|---|
| `commons` | — | Shared browser-free infrastructure |
| `commons-ui` | — | Playwright driver, base UI test, failure-evidence listener |
| `ui` | 11 | Login, cart and checkout stages plus the full journey, with negative and boundary cases |
| `api` | 17 | GET, POST, PUT, PATCH, DELETE plus negative, boundary and data-driven cases |
| `e2e` | 5 | Cross-module smoke gate, aggregating the `smoke` group from `ui` and `api` |

Requirement-to-test mapping, per-endpoint validation detail and known service deviations are in
[docs/TRACEABILITY.md](docs/TRACEABILITY.md).

Real passing-run reports and console logs are available in
[docs/sample-output](docs/sample-output/README.md).

## Prerequisites

- JDK 21
- Maven 3.9+
- A Chromium build for Playwright (one-time, see below)

## Setup

```bash
# Build every module
mvn -s .mvn/settings.xml clean install -DskipTests

# Install the browser Playwright drives (one time)
mvn -s .mvn/settings.xml -pl ui exec:java -Dexec.args="install chromium"
```

## Running the tests

```bash
# Everything, in dependency order
mvn -s .mvn/settings.xml clean install -Denv=local

# API only
mvn -s .mvn/settings.xml test -pl api -Denv=local

# UI only, headless
mvn -s .mvn/settings.xml test -pl ui -Denv=local -Dframework.headless=true

# UI with a visible browser
mvn -s .mvn/settings.xml test -pl ui -Denv=local -Dframework.headless=false

# Cross-module smoke gate (needs `clean install -DskipTests` first)
mvn -s .mvn/settings.xml test -pl e2e -Denv=local
```

### Running a single group

Every test is tagged with `ui` or `api`, plus `smoke`, `regression`, `negative` or `boundary`.

```bash
mvn -s .mvn/settings.xml test -pl api -Dgroups=smoke
mvn -s .mvn/settings.xml test -pl ui  -Dgroups=negative
mvn -s .mvn/settings.xml test -pl ui  -Dgroups=boundary
```

## Environment configuration

No URL, credential or timeout is hardcoded in Java. The active environment is chosen in this
order:

1. Maven property: `-Denv=qa`
2. OS environment variable: `TEST_ENV=qa`
3. Default: `local`

Three files are merged, later ones overriding earlier ones:

| File | Purpose |
|---|---|
| `config/application.yml` | Defaults shared by every environment |
| `config/environments/<env>.yml` | Per-environment values, committed |
| `config/environments/<env>.local.yml` | Machine-only overrides, untracked |

Values support `${VARIABLE}` (required) and `${VARIABLE:-default}` (optional), so secrets can come
from the environment rather than a file. Two settings can also be overridden directly for a single
run: `-Dframework.browser=firefox` and `-Dframework.headless=false`.

```bash
# Equivalent ways to select an environment
mvn test -Denv=qa
TEST_ENV=qa mvn test
```

## Architecture

```
├── commons/     config loader, REST wrapper, log and retry listeners, HTML reporter
├── commons-ui/  Playwright driver factory, base UI test, failure-evidence listener
├── ui/          SauceDemo page objects, flow helpers, validators
├── api/         JSONPlaceholder endpoints, flow helpers, POJOs, validators, JSON schemas
├── e2e/         cross-module smoke gate
├── config/      base configuration plus per-environment overrides
└── docs/        traceability matrix
```

Each module layers the same way:

**Page object / endpoint** owns *where* things are. Selectors live only in `UiConstants` and are
read only by page objects; URLs are resolved from an enum through `EndpointManager`.

**Helper** owns *what the business flow does*, through a four-phase lifecycle:
`init()` resolves locators or endpoints, `process()` performs the action, `validate()` asserts, and
`test()` runs all three. A helper takes an immutable context in and hands an enriched copy back, so
a later stage can assert against what an earlier stage actually saw — that is how the cart
assertion knows the price the inventory page advertised.

**Validator** owns *what correct looks like*. Assertions live nowhere else.

## Design decisions

**Page objects hold locators, helpers hold flows.** A plain Page Object Model would put both the
selectors and the multi-step business flow in the same class, which makes the page object grow
every time a scenario is added. Splitting them means `LoginPage` changes only when SauceDemo's
markup changes, while `LoginHelper` changes only when the login *flow* changes.

**Locators go through `getByTestId`.** `DriverFactory` points Playwright's test-id engine at
SauceDemo's `data-test` attribute, so pages read `page.getByTestId(UiConstants.USERNAME)` rather
than a CSS string. Test ids are contract, not styling, so they do not move when the markup or
classes change. One exception is documented in `UiConstants`: each add-to-cart button's test id
carries the product slug, so that single case stays a CSS prefix match scoped inside the product
row.

**`commons` is browser-free; `commons-ui` owns Playwright.** If both lived in one module the `api`
module would inherit a browser library it never calls, which slows resolution and blurs the
architecture. The split keeps `api`'s classpath honest — verifiable with
`mvn -pl api dependency:tree`.

**Shared versions are pinned in `dependencyManagement`.** Maven resolves conflicts by picking the
*nearest* declaration, not the newest. TestNG ships a transitive `slf4j-api` 1.7, which is nearer
than this project's 2.x once a module sits two levels deep — and 1.7 silently turns Logback into a
no-op logger, losing all logging without failing the build. Pinning the versions centrally removes
that class of failure.

**Assertions are isolated in validators.** Keeping them out of the pages and helpers means one
scenario can reuse a flow with a different expectation. `RejectedLoginHelper` covers unregistered
credentials, blank fields and a locked-out account because the expected message is a parameter
rather than something baked into the flow.

**Immutable context objects with Lombok builders.** `UiTestContext` and `ApiTestContext` are
`@Value @Builder(toBuilder = true)`, so passing state between stages cannot accidentally mutate a
shared object. This is what makes `parallel="methods"` safe.

**Explicit waits only, driven by configuration.** `BasePage.waitUntilVisible` wraps Playwright's
`waitFor`, and the timeout comes from `framework.timeoutMs`. There is no `Thread.sleep` in the
codebase — a fixed sleep is either too short on a slow run or wasted time on a fast one.

**One shared REST Assured specification.** `SpecFactory` owns the base URI, JSON content
negotiation and timeouts. Helpers never touch `RestAssured` directly, so changing a timeout or
adding an auth header is a single edit.

**Requests and responses are POJOs, not maps or strings.** `PostRequest` serializes with
`NON_NULL` so a PATCH sends only the fields it changes, and `PostResponse` deserializes with
`@Jacksonized`, so a renamed field is a compile error rather than a null at runtime.

**Responses are validated three ways.** Status code via a shared response spec, structure via JSON
schema in `api/src/main/resources/schemas`, and business fields via explicit assertions. A response
cannot pass on its status code alone.

**A hand-rolled HTML reporter.** ExtentReports and Allure both add a dependency, and Allure needs a
separate CLI to render. `HtmlReportListener` writes a self-contained file with per-test status,
groups, duration, failure message and a link to the failure screenshot, which is enough for this
scope and keeps the build dependency-free. Surefire's XML is still produced, so CI gets native
JUnit reporting for free.

**The `e2e` module is a gate, not a third test suite.** It owns no scenarios beyond a configuration
check. It depends on the `ui` and `api` *test* jars and runs only their `smoke` group, so one
command answers "is this build shippable" without duplicating a single scenario or running the full
regression pack.

**Parallel execution.** Both suites run `parallel="methods"`. `DriverFactory` keeps Playwright in
thread-locals and `BaseUiTest` opens a fresh browser per method, so no browser state crosses
threads. The API tests each build their own context, so there is no shared state to race on.

## Reporting and failure evidence

| Output | Location |
|---|---|
| HTML summary | `<module>/target/surefire-reports/sdet-test-report.html` |
| JUnit XML | `<module>/target/surefire-reports/TEST-*.xml` |
| Failure screenshots | `<module>/target/screenshots/<Class>-<method>.png` |
| Failure traces | `<module>/target/traces/<Class>-<method>.zip` |
| Console and request logs | stdout via SLF4J / Logback |

Open a trace with `npx playwright show-trace <path>` for a step-by-step replay of the failure,
including DOM snapshots and the network log.

`TestLogListener` logs every test transition. `UiEvidenceListener` captures a full-page screenshot
and a Playwright trace when a UI test fails, recording both paths on the result so the HTML report
links them relatively — the links still resolve after the report is downloaded as a CI artifact.
Capture is wrapped so it can never mask the original assertion failure. `RestUtils` logs each
request as a runnable `curl` command alongside the response status, timing, headers and body, so an
API failure can be reproduced from the log alone.

Traces are recorded for every UI test but kept only for failures, so a passing run leaves nothing
behind. Set `-Dframework.tracing=false` to switch recording off.

### Flaky-test retries

Retries are **off by default**, because a retry that is on by default hides real defects behind an
intermittent pass. Opt in per run, and every attempt is logged:

```bash
mvn -s .mvn/settings.xml test -pl ui -Dtest.retryCount=1
```

### Continuous integration

`api-tests` and `ui-tests` run in parallel on Chromium, a `ui-cross-browser` matrix runs the smoke
group on Firefox and WebKit, then `smoke-gate` runs the cross-module gate. Every job publishes the
reports, screenshots and traces as artifacts, and the Surefire XML as GitLab JUnit reports.

## Assumptions

- **JSONPlaceholder is a mock.** Writes are not persisted, so `POST`, `PUT`, `PATCH` and `DELETE`
  are validated against the response contract rather than by reading the resource back. The seeded
  data set is treated as fixed at 100 posts.
- **Two mock behaviours deviate from a production API** — a malformed body returns 500 instead of
  400, and a payload with no mandatory fields is accepted. The tests assert the observed behaviour
  and the gap is recorded in the traceability notes rather than failing the suite over a defect
  this project does not own.
- **No authentication coverage.** JSONPlaceholder exposes no authenticated endpoint, so there is no
  401/403 case to assert.
- **SauceDemo credentials in `local.yml` are the public demo credentials** published by the site.
  Real credentials belong in an untracked `<env>.local.yml` or in environment variables
  (`UI_BASE_URL`, `UI_USERNAME`, `UI_PASSWORD`).
- **SauceDemo has no stock model and no empty-cart guard**, so out-of-stock and empty-cart
  rejection cases are not covered — there is no rule to assert against.
- **Validators live in `src/main`** so they can be reused across modules, which is why TestNG is a
  compile-scoped dependency rather than test-scoped.
