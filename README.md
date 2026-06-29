# ParaBank Selenium Automation Framework

Automation framework for the Incubyte-style ParaBank assignment using Selenium WebDriver, Java, Cucumber, JUnit, Maven, Page Object Model, hooks, utilities, framework configuration, reporting, logging, screenshots, and failure evidence.

## Objective

Automate and validate:

1. Open ParaBank.
2. Register a new user.
3. Sign in using the created account.
4. Print/log the amount displayed after login.
5. Validate and log negative-case error messages.

## What was hardened in v11

The earlier framework was functionally correct, but it was still assignment-level. This version adds industry-style quality improvements:

| Area | Improvement |
|---|---|
| Configuration | Added typed validation and resolution order: Maven system property -> environment variable -> config file -> default. |
| Browser setup | Added browser enum validation, stable browser options, insecure cert handling, and thread-safe driver lifecycle. |
| Wait strategy | Changed default implicit wait to `0`; explicit waits are centralized in page objects. |
| Logging | Replaced plain `System.out.println` logging with SLF4J + Logback console and file logging. |
| Secrets handling | Passwords are masked in logs. Usernames are partially masked. |
| Test data | Uses timestamp + UUID + secure random username generation and retries if the shared ParaBank demo database already contains a generated username. |
| Assertions | Negative cases now assert specific expected validation messages, not just “some error exists.” |
| Evidence | Screenshots and page source are attached to Cucumber reports and saved under `target/test-artifacts` on failure. |
| Reporting | Generates HTML, JSON, XML, and rerun files. |
| Maintainability | Added framework utilities such as `BrowserType`, `FrameworkException`, `MoneyUtil`, and `FileUtil`. |
| Page objects | Added page load checks, safer waits, scrolling before actions, and cleaner amount extraction. |

## Tech Stack

- Java 17
- Selenium WebDriver 4
- Cucumber BDD
- JUnit 4
- Maven
- SLF4J + Logback
- Page Object Model

## Project Structure

```text
src/test/java/com/incubyte/parabank
├── hooks
│   └── Hooks.java
├── pages
│   ├── AccountsOverviewPage.java
│   ├── BasePage.java
│   ├── HomePage.java
│   └── RegisterPage.java
├── runners
│   └── TestRunner.java
├── steps
│   └── ParaBankSteps.java
└── utils
    ├── AppConfig.java
    ├── BrowserType.java
    ├── CredentialMasker.java
    ├── DriverManager.java
    ├── FileUtil.java
    ├── FrameworkException.java
    ├── LoggerUtil.java
    ├── MoneyUtil.java
    ├── RegistrationData.java
    ├── ScenarioContext.java
    └── TestData.java

src/test/resources
├── config.properties
├── logback-test.xml
└── features
    └── parabank_signup_login.feature
```

## Configuration

Main config file:

```text
src/test/resources/config.properties
```

Current important properties:

```properties
env=qa
qa.base.url=https://parabank.parasoft.com/parabank/index.htm?ConnType=JDBC
staging.base.url=https://parabank.parasoft.com/parabank/index.htm?ConnType=JDBC

browser=chrome
headless=false
maximize.window=true
window.width=1440
window.height=900

implicit.wait.seconds=0
explicit.wait.seconds=15
page.load.timeout.seconds=30
account.data.timeout.seconds=45
registration.retry.count=3

default.password=Password@123
invalid.username=invalid_user_12345
invalid.password=wrongPassword

screenshot.on.failure=true
page.source.on.failure=true
artifacts.dir=target/test-artifacts
accept.insecure.certs=true
```

Configuration resolution order:

```text
Maven system property -> Environment variable -> config.properties -> AppConfig default
```

Examples:

```bash
mvn clean test -Denv=qa
mvn clean test -Dbrowser=firefox
mvn clean test -Dheadless=true
mvn clean test -Denv=qa -Dbrowser=edge -Dheadless=true
mvn clean test -Dbase.url=https://parabank.parasoft.com/parabank/index.htm?ConnType=JDBC
```

Environment variable examples:

```bash
export BROWSER=chrome
export HEADLESS=true
export BASE_URL=https://parabank.parasoft.com/parabank/index.htm?ConnType=JDBC
mvn clean test
```

## Run Tests

Run all scenarios:

```bash
mvn clean test
```

Run headless:

```bash
mvn clean test -Dheadless=true
```

Run smoke scenario only:

```bash
mvn clean test -Dcucumber.filter.tags="@smoke"
```

Run only registration scenarios:

```bash
mvn clean test -Dcucumber.filter.tags="@registration"
```

Run only negative scenarios:

```bash
mvn clean test -Dcucumber.filter.tags="@negative"
```

## Scenario Order

The invalid login negative scenario is intentionally kept last in the feature file:

```text
1. Register a new user and login successfully
2. Registration with missing mandatory details should fail
3. Registration with duplicate username should fail
4. Login with invalid credentials should fail
```

## Logging

Logs are printed to the console and written to:

```text
target/logs/automation.log
```

The framework logs:

- Runtime configuration without passwords.
- Registration success title/message.
- Amount displayed after successful login.
- Missing mandatory registration field errors.
- Duplicate username error.
- Invalid login error.
- Masked auth details during login actions.

Example masked log:

```text
Logging in with username: aut********a9c and password: ********
```

## Reports and Evidence

After execution:

```text
target/cucumber-report.html
target/cucumber-report.json
target/cucumber-report.xml
target/rerun.txt
target/logs/automation.log
target/test-artifacts/
```

On failure, the framework attaches and saves:

- Screenshot
- Page source HTML

## Notes for Interview Submission

This framework intentionally keeps the stack simple and reviewer-friendly. It does not add heavy reporting libraries or DI containers because the assignment scope is small. The design still demonstrates senior-level practices: config-driven execution, Page Object Model, explicit waits, reusable utilities, masked auth logs, scenario isolation, meaningful assertions, and failure diagnostics.

## Running scenarios by Cucumber tags

The framework uses Cucumber tags in `src/test/resources/features/parabank_signup_login.feature` and a default tag expression in `TestRunner.java`:

```java
 tags = "not @ignore"
```

This means all scenarios run by default except scenarios marked with `@ignore`. For selective execution, pass `cucumber.filter.tags` from Maven.

### Common tag commands

Run all tests except ignored tests:

```bash
mvn clean test
```

Run smoke tests only:

```bash
mvn clean test -Dcucumber.filter.tags="@smoke"
```

Run full regression:

```bash
mvn clean test -Dcucumber.filter.tags="@regression"
```

Run positive scenarios only:

```bash
mvn clean test -Dcucumber.filter.tags="@positive"
```

Run negative scenarios only:

```bash
mvn clean test -Dcucumber.filter.tags="@negative"
```

Run registration tests only:

```bash
mvn clean test -Dcucumber.filter.tags="@registration"
```

Run login tests only:

```bash
mvn clean test -Dcucumber.filter.tags="@login"
```

Run validation tests only:

```bash
mvn clean test -Dcucumber.filter.tags="@validation"
```

Run edge cases only:

```bash
mvn clean test -Dcucumber.filter.tags="@edge"
```

Run negative login scenarios only:

```bash
mvn clean test -Dcucumber.filter.tags="@negative and @login"
```

Run registration tests but exclude edge cases:

```bash
mvn clean test -Dcucumber.filter.tags="@registration and not @edge"
```

Temporarily skip any scenario by adding `@ignore` above it.


## Test case tags

Each scenario has a unique test case ID in both the scenario name and tag.

| Test case | Scenario | Tags |
|---|---|---|
| TC_001 | Register a new user and login successfully | `@TC_001 @smoke @positive @e2e @registration @login` |
| TC_002 | Registration with missing mandatory details should fail | `@TC_002 @negative @validation @registration` |
| TC_003 | Registration with duplicate username should fail | `@TC_003 @negative @edge @validation @registration` |
| TC_004 | Login with invalid credentials should fail | `@TC_004 @negative @validation @login` |

Run a single test case:

```bash
mvn clean test -Dcucumber.filter.tags="@TC_001"
```

## Troubleshooting: blank Accounts Overview amount

Sometimes ParaBank shows the `Accounts Overview` heading and account table headers before the account rows are available. In that state the page is loaded, but the amount cannot be captured yet.

The framework handles this in `AccountsOverviewPage` by waiting specifically for a valid dollar amount in the account table, re-opening the Accounts Overview page during the wait window, and logging a detailed diagnostic message if the amount still does not appear.

The retry timeout is configurable:

```properties
account.data.timeout.seconds=45
```

Override from Maven if the public ParaBank environment is slow:

```bash
mvn clean test -Daccount.data.timeout.seconds=60
```


## Troubleshooting: duplicate username during positive registration

ParaBank is a public demo site and can retain previously registered users in its shared database. If a generated username unexpectedly already exists, the positive registration step now detects the duplicate username message and retries with a new generated username.

The retry count is configurable:

```properties
registration.retry.count=3
```

Override from Maven if needed:

```bash
mvn clean test -Dregistration.retry.count=5
```
