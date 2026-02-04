package runners;

import io.cucumber.testng.*;
import org.testng.annotations.DataProvider;

@CucumberOptions(
    features = "src/test/resources/features",
    glue = {"steps"},
    plugin = {
      "pretty",
      "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:"
    },
    tags = "@TS_03",
    monochrome = true
)
public class TS_03_Runner extends AbstractTestNGCucumberTests {
  @Override
  @DataProvider(parallel = false)
  public Object[][] scenarios() {
    return super.scenarios();
  }
}