# ParaBank Selenium Automation

Automation assignment for testing ParaBank account registration and login using Selenium WebDriver, Java, Cucumber, JUnit, and Page Object Model.

## Objective

Automate the following flow:

1. Open ParaBank.
2. Register a new user.
3. Sign in using the created account.
4. Print/log the amount displayed after login.

## Tech Stack

- Java 17
- Selenium WebDriver 4
- Cucumber BDD
- JUnit
- Maven
- Page Object Model

## Run Tests

```bash
mvn clean test
```

Run headless:

```bash
mvn clean test -Dheadless=true
```

Run in Chrome explicitly:

```bash
mvn clean test -Dbrowser=chrome
```

## Reports

After execution, the Cucumber HTML report is generated at:

```text
target/cucumber-report.html
```
