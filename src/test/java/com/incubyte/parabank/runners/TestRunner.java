package com.incubyte.parabank.runners;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"com.incubyte.parabank.steps", "com.incubyte.parabank.hooks"},
        tags = "not @ignore",
        plugin = {
                "pretty",
                "html:target/cucumber-report.html",
                "json:target/cucumber-report.json",
                "junit:target/cucumber-report.xml",
                "rerun:target/rerun.txt"
        },
        monochrome = true,
        publish = false
)
public class TestRunner {
}
